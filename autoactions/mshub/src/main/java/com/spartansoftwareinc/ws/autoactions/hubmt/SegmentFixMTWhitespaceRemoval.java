package com.spartansoftwareinc.ws.autoactions.hubmt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.ais.WSNodeType;
import com.idiominc.wssdk.asset.WSAssetTask;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.workflow.WSTask;
import com.spartansoftwareinc.ws.autoactions.Version;
import com.spartansoftwareinc.ws.autoactions.hubmt.config.SegmentWhitespaceFixYAMLConfig;

/**
 * <p>During Machine Translation, white spaces next to tags can be removed. This Auto Action can be added after a Machine Translation to fix those whitespaces</p>
 *
 * <p>Example: {@literal Hello <b>my</b> darling} --> {@literal Salut<b>disque</b>Darling!}</p>
 * <p>
 * These whitespaces need to be reinserted.
 */
public class SegmentFixMTWhitespaceRemoval extends WSTaskAutomaticAction {

    private final Logger LOG = Logger.getLogger(SegmentFixMTWhitespaceRemoval.class);

    private static final String RETURN_DONE = "Done";
    private static final String CONFIG_FILE_NAME = "mshub_autoaction_whitespace_fix.yml";
    private static final String CONFIG_WS_LOCATION = "/Customization/Spartan/mshub_autoaction_whitespace_fix.yml";
    private static final String CONFIG_WS_FOLDER = "/Customization/Spartan/";

    // This determines what to look for. The first and third capture groups determine what to restore from source,
    // and the second capture group is used to compare equality.
    private Pattern regexPatternTokens;
    // Specifies the capture group to compare for validity. The specified capture group must match exactly.
    private int captureGroupCompare;

    @Override
    public String getDescription() {
        return "Restores whitespaces around placeholders in the target translation, that were originally present in the source. Uses Regex from a configuration file, which is generated if one does not exist." +
                "\nExample of missing whitespaces: Hello <b>my</b> darling --> Salut<b>mon</b>chéri!" +
                "\nFixed: Hello <b>my</b> darling --> Salut <b>mon</b> chéri!" +
                "\nConfig location: " + CONFIG_WS_LOCATION;
    }

    @Override
    public String getName() {
        return "Restore Segment's Placeholder Whitespace";
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public String[] getReturns() {
        return new String[] {RETURN_DONE};
    }

    @Override
    public WSActionResult execute(WSContext context, Map parameters, WSTask task) throws WSException {
        WSAssetTask assetTask;
        Iterator textSegmentsIterator;
        String resultMessage;

        setConfig(context.getAisManager());
        int numberOfTargetsModified = 0;

        try {
            // cast task to asset task
            if (task instanceof WSAssetTask) {
                assetTask = (WSAssetTask) task;
            } else {
                throw new WSException("Task was not an Asset Task");
            }

            // Grab the previous step's source and target translations
            textSegmentsIterator = assetTask.getAssetTranslation().textSegmentIterator();

            // Compare each translation and add in missing whitespaces
            while (textSegmentsIterator.hasNext()) {
                Object segmentObject = textSegmentsIterator.next();
                if (!(segmentObject instanceof WSTextSegmentTranslation)) {
                    continue;
                }
                WSTextSegmentTranslation segment = (WSTextSegmentTranslation) segmentObject;
                String source = segment.getSource();
                String target = segment.getTarget();
                String fixed = fixWhitespace(source, target);
                if (!target.equals(fixed)) {
                    segment.setTarget(fixed);
                    numberOfTargetsModified += 1;
                }
            }

            resultMessage = String.format("%d segments had their placeholder whitespace restored.", numberOfTargetsModified);

        } catch (Exception e) {
            resultMessage = e.getMessage();
            LOG.error("Unable to complete " + getName(), e);
            return new WSActionResult(WSActionResult.ERROR, resultMessage);

        }
        return new WSActionResult(RETURN_DONE, resultMessage);
    }


    /**
     * Compares the source to the target with the Regular Expression {@link #regexPatternTokens}. If they are different
     * in any way, the source's string replaces the target's string in that specific spot. If the value of the token differs,
     * a warning is thrown and no replacement will be made.
     *
     * @param source The string to compare to
     * @param target The string that is being checked for differences
     * @return The target string with the updates to the placeholders
     */
    public String fixWhitespace(String source, String target) {

        Matcher source_matcher = regexPatternTokens.matcher(source);
        Matcher target_matcher = regexPatternTokens.matcher(target);

        StringBuffer final_target = new StringBuffer();

        while (source_matcher.find() && target_matcher.find()) {

            String source_item = source_matcher.group();
            String target_item = target_matcher.group();

            String source_value = source_matcher.group(captureGroupCompare);
            String target_value = target_matcher.group(captureGroupCompare);

            if (!source_value.equals(target_value)) {

                LOG.warn(String.format("Source value %s did not match target value %s when comparing strings", source_item, target_item));
            } else if (!source_item.equals(target_item)) {
                target_matcher.appendReplacement(final_target, Matcher.quoteReplacement(source_item));
            }

        }

        target_matcher.appendTail(final_target);

        return final_target.toString();
    }

    protected void setConfig(WSAisManager aisManager) throws WSException {

        InputStream wsConfigFile;

        final String FILE_TYPE = "Filesystem File";

        if (aisManager != null) {
            WSNode configFile = aisManager.getNode(CONFIG_WS_LOCATION);

            // If config file is missing from server, copy the default config from the resources directory
            if (configFile == null) {
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
                    throw new WSException("Could not find WSNodeType \"" + FILE_TYPE +
                            "\"to create missing config file. Node types found: " + nodeTypesList);
                }

                // Copy file from resources to Worldserver
                InputStream defaultConfigFile = loadConfigFromResources();
                configFile = aisManager.create(CONFIG_WS_LOCATION, fileNodeType);
                try {
                    FileUtils.copyInputStreamToFile(defaultConfigFile, configFile.getFile());
                } catch (IOException e) {
                    throw new WSException(e);
                }

                LOG.warn("Config file not found in Worldserver. Created one at \"" + CONFIG_WS_LOCATION + "\"");
            }

            wsConfigFile = configFile.getInputStream();
        } else {
            // If not AIS manager (ran outside of Worldserver), just use resources file
            wsConfigFile = loadConfigFromResources();
        }

        setConfiguration(wsConfigFile);

        try {
            wsConfigFile.close();
        } catch (IOException e) {
            throw new WSException(e);
        }


    }


    protected InputStream loadConfigFromResources() throws WSException {
        InputStream resource = getClass().getResourceAsStream(CONFIG_FILE_NAME);
        if (resource == null) {
            throw new WSException(new FileNotFoundException("Unable to load Resource " + CONFIG_FILE_NAME
                    + " stored in package resources."));
        }

        return resource;
    }

    private void setConfiguration(InputStream inputStream) throws WSException {

        SegmentWhitespaceFixYAMLConfig config = new Yaml().
                loadAs(inputStream, SegmentWhitespaceFixYAMLConfig.class);

        String pattern = config.getRegex();
        if (pattern == null) {
            throw new WSException("Unable to load \"regex\" from config file located at " + CONFIG_WS_LOCATION);
        }
        regexPatternTokens = Pattern.compile(pattern);


        Integer group = config.getCaptureGroup();
        if (group == null) {
            throw new WSException("Unable to load \"captureGroup\" from config file located at " + CONFIG_WS_LOCATION);
        }
        captureGroupCompare = group;

    }


}
