package com.spartansoftwareinc.ws.autoactions.hubmt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.ais.WSNodeType;
import com.idiominc.wssdk.asset.WSAssetTask;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.component.WSParameter;
import com.idiominc.wssdk.component.WSParameterFactory;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.workflow.WSTask;
import com.spartansoftwareinc.ws.autoactions.hubmt.config.SimplePostEditAutoActionYAMLConfig;
import com.spartansoftwareinc.ws.autoactions.hubmt.util.Intervals;

/**
 * <p>An Auto Action that does configurable pattern matching and replacement on Target Segments.</p>
 */
public class SimplePostEditAutoAction extends WSTaskAutomaticAction {

    private final Logger LOG = Logger.getLogger(SimplePostEditAutoAction.class);

    private static final String INCLUDED_CONFIG_RESOURCES_FILE_NAME = "mshub_simple_post_edit_aa.yml";
    private static final String DEFAULT_CONFIG_WS_LOCATION = "/Customization/Spartan/mshub_simple_post_edit_aa.yml";
    private static final String CONFIG_WS_FOLDER = "/Customization/Spartan/";

    // Parameters
    private static final String PARAM_IGNORE_ICE = "ignore_ice";
    private static final String PARAM_IGNORE_100_PERCENT = "ignore_100";
    private static final String PARAM_CONFIG_LOCATION = "config_location";

    @Override
    public String getDescription() {
        return "Updates the target segments using REGEX pattern matching." + "\nConfig location: "
            + DEFAULT_CONFIG_WS_LOCATION;
    }

    @Override
    public String getName() {
        return "Simple Post Edit";
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSParameter[] getParameters() {
        return new WSParameter[] {
            WSParameterFactory.createBooleanParameter(PARAM_IGNORE_ICE, "Ignore ICE Segments", false),
            WSParameterFactory.createBooleanParameter(PARAM_IGNORE_100_PERCENT, "Ignore 100% Segments", false),
            WSParameterFactory.createStringParameter(PARAM_CONFIG_LOCATION, "Configuration File",
                DEFAULT_CONFIG_WS_LOCATION)};
    }


    @Override
    public String[] getReturns() {
        return new String[] {WSActionResult.DONE_RETURN_VALUE};
    }

    @Override
    public WSActionResult execute(WSContext context, Map parameters, WSTask task) throws WSException {
        WSAssetTask assetTask;
        Iterator textSegmentsIterator;
        String resultMessage;

        int numberOfTargetsModified = 0, numberOfIceIgnored = 0, numberOf100Ignored = 0;

        try {

            // Grab parameters
            final boolean ignoreIce = parameters.get(PARAM_IGNORE_ICE).equals("Yes");
            final boolean ignore100 = parameters.get(PARAM_IGNORE_100_PERCENT).equals("Yes");
            final String configFileLocation = (String) parameters.get(PARAM_CONFIG_LOCATION);

            // cast task to asset task
            if (task instanceof WSAssetTask) {
                assetTask = (WSAssetTask) task;
            } else {
                throw new WSException("Task was not an Asset Task");
            }

            final SimplePostEditAutoActionYAMLConfig config = getConfig(context.getAisManager(), configFileLocation);

            // Grab the language and use the Actions associated with that language
            final String targetLanguage = assetTask.getProject().getTargetLocale().getLanguage().getName();
            List<SimplePostEditAutoActionYAMLConfig.Action> actions = config.getActionsForLanguage(targetLanguage);

            // Only perform this operation if Actions are specified
            if (actions == null) {
                String configPath = configFileLocation.isEmpty() ? DEFAULT_CONFIG_WS_LOCATION : configFileLocation;
                String message = "Locale " + targetLanguage + " not specified in " + configPath + ". Skipping...";
                LOG.warn(message);
                resultMessage = message;
            } else if (actions.size() > 0) {
                // Grab the previous step's source and target translations
                textSegmentsIterator = assetTask.getAssetTranslation().textSegmentIterator();

                // Go through each target and update
                while (textSegmentsIterator.hasNext()) {
                    Object segmentObject = textSegmentsIterator.next();
                    if (!(segmentObject instanceof WSTextSegmentTranslation)) {
                        continue;
                    }

                    final WSTextSegmentTranslation segment = (WSTextSegmentTranslation) segmentObject;
                    final WSTranslationType translationType = segment.getTranslationType();

                    // Skip ICE and 100% matches if options are set to Yes in the AA
                    if (ignoreIce && translationType == WSTranslationType.ICE_MATCH_TM_TRANSLATION) {
                        numberOfIceIgnored += 1;
                        continue;
                    }
                    if (ignore100 && translationType == WSTranslationType.EXACT_OR_100_MATCH_TM_TRANSLATION) {
                        numberOf100Ignored += 1;
                        continue;
                    }

                    // Perform the Action, and replace the target only if there the string has been altered
                    String target = segment.getTarget();
                    String fixed = updateString(target, actions);
                    if (!target.equals(fixed)) {
                        segment.setTarget(fixed);
                        numberOfTargetsModified += 1;
                        LOG.debug(String.format("Original Target: %s\nNew Target: %s", target, fixed));
                    }
                }

                // Produce an completion message, including the number of ignored and modified
                final String ignoreIceMsg =
                    numberOfIceIgnored > 0 ? String.format(" %d ICE matched segments ignored.", numberOfIceIgnored) :
                        "";
                final String ignore100Msg =
                    numberOf100Ignored > 0 ? String.format(" %d 100%% matched segments ignored.", numberOf100Ignored) :
                        "";
                resultMessage =
                    String.format("%d segments modified.%s%s", numberOfTargetsModified, ignoreIceMsg, ignore100Msg);
            } else {
                resultMessage = String.format("Skipped, due to no actions for %s", targetLanguage);
            }
        } catch (Exception e) {
            resultMessage = e.getMessage();
            LOG.error("Unable to complete " + getName(), e);
            return new WSActionResult(WSActionResult.ERROR, resultMessage);

        }
        return new WSActionResult(WSActionResult.DONE_RETURN_VALUE, resultMessage);
    }


    /**
     * Updates the provided string using the given actions. An Action consists of a REGEX pattern match followed by a
     * replacement.
     *
     * @param input   The original string to update
     * @param actions The actions to perform on the string
     * @return The updated string
     */
    String updateString(String input, List<SimplePostEditAutoActionYAMLConfig.Action> actions) {

        String newTarget = input;
        final Intervals noLongerReplace = new Intervals();
        for (SimplePostEditAutoActionYAMLConfig.Action action : actions) {
            final Matcher matcher = action.getPatternCompiled().matcher(newTarget);
            final String replacement = action.getReplace();

            final StringBuffer builtTarget = new StringBuffer();
            while (matcher.find()) {
                final boolean canReplace = !noLongerReplace.containsInterval(matcher.start(), matcher.end());
                boolean replaced = false;

                if (replacement != null && canReplace) {
                    matcher.appendReplacement(builtTarget, replacement);
                    replaced = true;
                }

                if (!action.getAllowFurtherReplacements()) {
                    final int endPosition = replaced ?  builtTarget.length() : matcher.end();
                    noLongerReplace.addInterval(matcher.start(), endPosition);
                }
            }
            matcher.appendTail(builtTarget);
            newTarget = builtTarget.toString();
        }
        return newTarget;
    }

    SimplePostEditAutoActionYAMLConfig getConfig(WSAisManager aisManager, String configFileLocation)
        throws WSException {

        InputStream wsConfigFile;

        final String FILE_TYPE = "Filesystem File";

        // If config file is blank, load default
        if (configFileLocation.isEmpty()) {
            configFileLocation = DEFAULT_CONFIG_WS_LOCATION;
            LOG.warn("No config file specified. Used default " + DEFAULT_CONFIG_WS_LOCATION);
        }

        if (aisManager != null) {
            WSNode configFile = aisManager.getNode(configFileLocation);

            // If default config file is missing from server, load the default config from the resources
            // directory. Otherwise, if a config file is specified but not found, throw an exception
            if (configFile == null && configFileLocation.equals(DEFAULT_CONFIG_WS_LOCATION)) {
                WSNodeType fileNodeType = null;
                WSNodeType[] nodeTypes = aisManager.getPossibleChildTypes(CONFIG_WS_FOLDER);
                StringBuilder nodeTypesList = new StringBuilder();

                for (WSNodeType nodeType : nodeTypes) {
                    String name = nodeType.getName(Locale.ENGLISH);

                    nodeTypesList.append("\"");
                    nodeTypesList.append(name);
                    nodeTypesList.append("\" ");
                    if (FILE_TYPE.equals(name)) {
                        fileNodeType = nodeType;
                    }
                }

                if (fileNodeType == null) {
                    throw new WSException("Could not find WSNodeType \"" + FILE_TYPE
                        + "\"to create missing config file. Node types found: " + nodeTypesList);
                }

                // Copy file from resources to Worldserver
                InputStream defaultConfigFile = loadConfigFromResources();
                configFile = aisManager.create(DEFAULT_CONFIG_WS_LOCATION, fileNodeType);
                try {
                    FileUtils.copyInputStreamToFile(defaultConfigFile, configFile.getFile());
                } catch (IOException e) {
                    throw new WSException(e);
                }

                LOG.warn("Config file not found in Worldserver. Created one at \"" + DEFAULT_CONFIG_WS_LOCATION + "\"");
            } else if (configFile == null) {
                throw new WSException("Could not find config file at \"" + configFileLocation + "\"");
            }

            wsConfigFile = configFile.getInputStream();
        } else {
            // If not AIS manager (ran outside of Worldserver), just use resources file
            wsConfigFile = loadConfigFromResources();
        }

        // Load the config file then construct it
        final SimplePostEditAutoActionYAMLConfig config = new Yaml().
            loadAs(wsConfigFile, SimplePostEditAutoActionYAMLConfig.class);
        try {
            wsConfigFile.close();
        } catch (IOException e) {
            throw new WSException(e);
        }

        return config;

    }


    private InputStream loadConfigFromResources() throws WSException {
        InputStream resource = getClass().getResourceAsStream(INCLUDED_CONFIG_RESOURCES_FILE_NAME);
        if (resource == null) {
            throw new WSException(new FileNotFoundException(
                "Unable to load Resource " + INCLUDED_CONFIG_RESOURCES_FILE_NAME + " stored in package resources."));
        }
        return resource;
    }


}
