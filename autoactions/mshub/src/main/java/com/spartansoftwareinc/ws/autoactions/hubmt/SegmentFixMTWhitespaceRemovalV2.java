package com.spartansoftwareinc.ws.autoactions.hubmt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.component.WSParameter;
import com.idiominc.wssdk.component.WSParameterFactory;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.workflow.WSTask;
import com.spartansoftwareinc.ws.autoactions.hubmt.config.SegmentWhitespaceFixYAMLConfig;

/**
 * <p>During Machine Translation, white spaces next to tags can be removed. This Auto Action can be added after a Machine Translation to fix those whitespaces</p>
 *
 * <p>Example: {@literal Hello <b>my</b> darling} --> {@literal Salut<b>disque</b>Darling!}</p>
 * <p>
 * These whitespaces need to be reinserted.
 */
public class SegmentFixMTWhitespaceRemovalV2 extends WSTaskAutomaticAction {

    private final Logger LOG = Logger.getLogger(SegmentFixMTWhitespaceRemovalV2.class);

    private static final String RETURN_DONE = "Done";
    private static final String INCLUDED_CONFIG_RESOURCES_FILE_NAME = "mshub_autoaction_whitespace_fix_v2.yml";
    private static final String DEFAULT_CONFIG_WS_LOCATION =
        "/Customization/Spartan/mshub_autoaction_whitespace_fix_v2.yml";
    private static final String CONFIG_WS_FOLDER = "/Customization/Spartan/";

    // This determines what to look for. The first and third capture groups determine what to restore from source,
    // and the second capture group is used to compare equality.
    private Pattern regexPatternTokens;

    // Parameters
    private static final String PARAM_IGNORE_ICE = "ignore_ice";
    private static final String PARAM_IGNORE_100_PERCENT = "ignore_100";
    private static final String PARAM_CONFIG_LOCATION = "config_location";

    protected SegmentWhitespaceFixYAMLConfig config;

    @Override
    public String getDescription() {
        return
            "Restores whitespaces around placeholders in the target translation, that were originally present in the source. Uses Regex from a configuration file, which is generated if one does not exist."
                + "\nExample of missing whitespaces: Hello <b>my</b> darling --> Salut<b>mon</b>chéri!"
                + "\nFixed: Hello <b>my</b> darling --> Salut <b>mon</b> chéri!" + "\nDefault Config location: "
                + DEFAULT_CONFIG_WS_LOCATION;
    }

    @Override
    public String getName() {
        return "Restore Segment's Placeholder Whitespace v2";
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
        return new String[] {RETURN_DONE};
    }

    @Override
    public WSActionResult execute(WSContext context, Map parameters, WSTask task) throws WSException {
        final WSAssetTask assetTask;
        final Iterator textSegmentsIterator;
        String resultMessage;

        int numberOfTargetsModified = 0, numberOfIceIgnored = 0, numberOf100Ignored = 0;

        try {
            // Grab parameters
            final boolean ignoreIce = parameters.get(PARAM_IGNORE_ICE).equals("Yes");
            final boolean ignore100 = parameters.get(PARAM_IGNORE_100_PERCENT).equals("Yes");
            final String configFileLocation = (String) parameters.get(PARAM_CONFIG_LOCATION);

            // Load config file
            final SegmentWhitespaceFixYAMLConfig config = getConfig(context.getAisManager(), configFileLocation);

            // cast task to asset task
            if (task instanceof WSAssetTask) {
                assetTask = (WSAssetTask) task;
            } else {
                throw new WSException("Task was not an Asset Task");
            }

            // Get the target language
            final String targetLanguage = assetTask.getProject().getTargetLocale().getName();

            // Grab the previous step's source and target translations
            textSegmentsIterator = assetTask.getAssetTranslation().textSegmentIterator();

            // Go through each Worldserver Segment and make changes based on config file
            while (textSegmentsIterator.hasNext()) {
                Object segmentObject = textSegmentsIterator.next();
                if (!(segmentObject instanceof WSTextSegmentTranslation)) {
                    continue;
                }
                final WSTextSegmentTranslation segment = (WSTextSegmentTranslation) segmentObject;
                final WSTranslationType translationType = segment.getTranslationType();

                // Skip ICE and 100% matches if options are checked
                if (ignoreIce && translationType == WSTranslationType.ICE_MATCH_TM_TRANSLATION) {
                    numberOfIceIgnored += 1;
                    continue;
                }
                if (ignore100 && translationType == WSTranslationType.EXACT_OR_100_MATCH_TM_TRANSLATION) {
                    numberOf100Ignored += 1;
                    continue;
                }

                // Perform the the actual changes
                final String source = segment.getSource();
                final String target = segment.getTarget();
                final String fixed = fixSegment(source, target, config, targetLanguage);
                if (!target.equals(fixed)) {
                    segment.setTarget(fixed);
                    numberOfTargetsModified += 1;
                }
            }

            // Produce an Action complete message
            final String ignoreIceMsg =
                numberOfIceIgnored > 0 ? String.format(" %d ICE matched segments ignored.", numberOfIceIgnored) : "";
            final String ignore100Msg =
                numberOf100Ignored > 0 ? String.format(" %d 100%% matched segments ignored.", numberOf100Ignored) : "";
            resultMessage = String
                .format("%d segments modified.%s%s", numberOfTargetsModified,
                    ignoreIceMsg, ignore100Msg);

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
    public String fixSegment(String source, String target, SegmentWhitespaceFixYAMLConfig config,
        String targetLanguage) {

        String currentTarget = target;
        final Map<String, Boolean> processedLeftTargets = new HashMap<>(), processedRightTargets = new HashMap<>();

        // Loop through every pattern group in the config
        for(SegmentWhitespaceFixYAMLConfig.SegmentRule segmentRule : config.getSegmentRules()) {
            final Matcher source_matcher = segmentRule.getSourcePatternComplete().matcher(source);
            final Matcher targetMatcher = segmentRule.getTargetPatternComplete().matcher(currentTarget);
            final int sourceCaptureGroup = segmentRule.getSourceCaptureGroupPlaceholder()+1;
            final int targetCaptureGroup = segmentRule.getTargetCaptureGroupPlaceholder()+1;
            final Map<String, String[]> sourceMatches = new HashMap<>();

            // If target languages are specified, skip if this doesn't match any of those languages.
            if (segmentRule.getTargetLanguages() != null && !segmentRule.getTargetLanguages().isEmpty()
                && !segmentRule.getTargetLanguages().contains(targetLanguage)) {
                continue;
            }

            // Loop through the source and build all the matches. We will then connect them with the target via their
            // placeholder value
            while (source_matcher.find()) {
                final int numberOfGroups = source_matcher.groupCount();
                final String[] groups = new String[numberOfGroups + 1];
                for (int i = 0; i <= numberOfGroups; i++) {
                    groups[i] = source_matcher.group(i);
                }
                final String placeholderId = groups[sourceCaptureGroup];
                sourceMatches.put(placeholderId, groups);
            }

            // Loop through each placeholder in the target
            final StringBuffer builtTarget = new StringBuffer();
            while (targetMatcher.find()) {

                final String targetFullMatch = targetMatcher.group(0);
                final String wholePlaceholder = targetMatcher.group(segmentRule.getTargetCaptureGroupPlaceholder());
                final String placeholderValue = targetMatcher.group(targetCaptureGroup);

                // If the placeholder values are different, then produce a warning and skip.
                if (!sourceMatches.containsKey(placeholderValue)) {
                    LOG.warn(String.format(
                        "The target contained the placeholder {%s}, which was not found in the source: \"%s\". No fix will be applied.",
                        targetFullMatch, source));
                    continue;
                }

                final String[] sourceGroups = sourceMatches.get(placeholderValue);


                // Both source and target matching expressions need to match for a replacement to occur
                final boolean leftSideMatch = sourceGroups[segmentRule.getSourceCaptureGroupLeft()] != null
                    && targetMatcher.group(segmentRule.getTargetCaptureGroupLeft()) != null;
                final boolean rightSideMatch = sourceGroups[segmentRule.getSourceCaptureGroupRight()] != null
                    && targetMatcher.group(segmentRule.getTargetCaptureGroupRight()) != null;

                // Check if processed
                final boolean leftSideProcessed =
                    processedLeftTargets.containsKey(placeholderValue) && processedLeftTargets.get(placeholderValue);
                final boolean rightSideProcessed =
                    processedRightTargets.containsKey(placeholderValue) && processedRightTargets.get(placeholderValue);

                // Decide whether or not to replace based on:
                //  - Is there a replacement pattern
                //  - Is there a match on that side
                //  - If not independent, is there a match on other side
                //  - Has it already been marked as processed
                final boolean replaceLeft = segmentRule.getReplacementLeft() != null && leftSideMatch && (
                    segmentRule.isLeftRightIndependent() || rightSideMatch) && !leftSideProcessed;
                final boolean replaceRight = segmentRule.getReplacementRight() != null && rightSideMatch && (
                    segmentRule.isLeftRightIndependent() || leftSideMatch) && !rightSideProcessed;

                final StringBuilder placeholderFixed = new StringBuilder();

                // Replace the left side
                if (replaceLeft) {


                    final String side = replaceSide(sourceGroups, targetMatcher, segmentRule.getReplacementLeft(),
                        segmentRule.getSourceCaptureGroupLeft(), segmentRule.getTargetCaptureGroupLeft());

                    placeholderFixed.append(side);

                    // Mark placeholder as processed
                    if (!segmentRule.isAllowFurtherReplacementsLeft()) {
                        processedLeftTargets.put(placeholderValue, true);
                    }

                } else if (targetMatcher.group(segmentRule.getTargetCaptureGroupLeft()) != null) {
                    placeholderFixed.append(targetMatcher.group(segmentRule.getTargetCaptureGroupLeft()));
                }

                // Add the placeholder
                placeholderFixed.append(wholePlaceholder);

                // Replace the right side
                if (replaceRight) {

                    final String side = replaceSide(sourceGroups, targetMatcher, segmentRule.getReplacementRight(),
                        segmentRule.getSourceCaptureGroupRight(), segmentRule.getTargetCaptureGroupRight());
                    placeholderFixed.append(side);

                    // Mark placeholder as processed
                    if (!segmentRule.isAllowFurtherReplacementsRight()) {
                        processedRightTargets.put(placeholderValue, true);
                    }

                } else if (targetMatcher.group(segmentRule.getTargetCaptureGroupRight()) != null) {
                    placeholderFixed.append(targetMatcher.group(segmentRule.getTargetCaptureGroupRight()));
                }


                // Append the new target
                targetMatcher.appendReplacement(builtTarget, Matcher.quoteReplacement(placeholderFixed.toString()));
            }

            // Finish with the last of the data
            targetMatcher.appendTail(builtTarget);

            currentTarget = builtTarget.toString();

        }

        return currentTarget;
    }

    /**
     * Reads the source, target, and repalcement pattern, and then returns a string from that.
     *
     * @param sourceGroups The capture groups for the source matched placeholder
     * @param targetMatcher  The matcher currently on the same placeholder
     * @param replacementPattern The REGEX pattern used to generate the new String
     * @param sourceCaptureGroup The capture group the source string rests in in sourceGroups
     * @param targetCaptureGroup The capture group the target string rests in in targetMatcher
     * @return The newly formed String from the replacementPattern
     */
    private String replaceSide(String[] sourceGroups, Matcher targetMatcher, String replacementPattern,
        int sourceCaptureGroup,
        int targetCaptureGroup){
        final Pattern captureReplacement = Pattern.compile("\\$((s)|(t))(\\d+)");
        final StringBuffer generatedString = new StringBuffer();
        final Matcher sideMatcher = captureReplacement.matcher(replacementPattern);
        while (sideMatcher.find()) {
            final int groupNumber = Integer.parseInt(sideMatcher.group(4));
            String replacementText = null;
            if (sideMatcher.group(2) != null) {
                replacementText = sourceGroups[sourceCaptureGroup + groupNumber];
            } else if (sideMatcher.group(3) != null) {
                replacementText =
                    targetMatcher.group(targetCaptureGroup + groupNumber);
            }
            if (replacementText == null) {
                replacementText = "";
            }

            sideMatcher.appendReplacement(generatedString, Matcher.quoteReplacement(replacementText));
        }
        return generatedString.toString();
    }

    protected SegmentWhitespaceFixYAMLConfig getConfig(WSAisManager aisManager, String configFileLocation)
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
        final SegmentWhitespaceFixYAMLConfig config = new Yaml().
            loadAs(wsConfigFile, SegmentWhitespaceFixYAMLConfig.class);
        config.construct();

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
