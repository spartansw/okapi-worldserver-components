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
public class SegmentFixMTWhitespaceRemoval extends WSTaskAutomaticAction {

    private final Logger LOG = Logger.getLogger(SegmentFixMTWhitespaceRemoval.class);

    private static final String RETURN_DONE = "Done";
    private static final String CONFIG_FILE_NAME = "mshub_autoaction_whitespace_fix.yml";
    private static final String CONFIG_WS_LOCATION = "/Customization/Spartan/mshub_autoaction_whitespace_fix.yml";
    private static final String CONFIG_WS_FOLDER = "/Customization/Spartan/";

    // This determines what to look for. The first and third capture groups determine what to restore from source,
    // and the second capture group is used to compare equality.
    private Pattern regexPatternTokens;

    protected SegmentWhitespaceFixYAMLConfig config;

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

        final Matcher source_matcher = regexPatternTokens.matcher(source);
        final Matcher target_matcher = regexPatternTokens.matcher(target);
        final int leftCaptureGroup = config.getLeftCaptureGroup();
        final int leftIgnoreCaptureGroup = config.getLeftIgnoreCaptureGroup();
        final int centerCaptureGroup = config.getCenterCaptureGroup();
        final int compareCaptureGroup = config.getCompareCaptureGroup();
        final int rightIgnoreCaptureGroup = config.getRightIgnoreCaptureGroup();
        final int rightCaptureGroup = config.getRightCaptureGroup();

        final Map<String, String[]> sourceMatches = new HashMap<>();

        // Create source map. Each match has an array of of Capture groups. 0 = entire match, 1-n = Capture groups
        while (source_matcher.find()) {
            final int numberOfGroups = source_matcher.groupCount();
            final String[] groups = new String[numberOfGroups + 1];
            for (int i = 0; i <= numberOfGroups; i++) {
                groups[i] = source_matcher.group(i);
            }
            sourceMatches.put(groups[compareCaptureGroup], groups);
        }


        final StringBuffer final_target = new StringBuffer();
        while (target_matcher.find()) {

            final String target_match = target_matcher.group(0);
            final String target_value = target_matcher.group(compareCaptureGroup);


            // If the values are different, then apply this fix.
            if (!sourceMatches.containsKey(target_value)) {
                LOG.warn(String.format("The target contained the placeholder {%s}, which was not found in the source: \"%s\". No fix will be applied.", target_match, source));

            } else if (!sourceMatches.get(target_value)[0].equals(target_match)) {

                final StringBuilder replacement = new StringBuilder();

                final String target_left = target_matcher.group(leftCaptureGroup);
                final String target_ignore_left = target_matcher.group(leftIgnoreCaptureGroup);
                final String target_center = target_matcher.group(centerCaptureGroup);
                final String target_ignore_right = target_matcher.group(rightIgnoreCaptureGroup);
                final String target_right = target_matcher.group(rightCaptureGroup);

                final String[] sourceGroups = sourceMatches.get(target_value);
                final String source_match = sourceGroups[0];
                final String source_left = sourceGroups[leftCaptureGroup];
                final String source_ignore_left = sourceGroups[leftIgnoreCaptureGroup];
                final String source_value = sourceGroups[compareCaptureGroup];
                final String source_ignore_right = sourceGroups[rightIgnoreCaptureGroup];
                final String source_right = sourceGroups[rightCaptureGroup];


                // Replace the left side's whitespace if nothing in the ignore capture group was detected hugging the placeholder
                if (target_ignore_left.length() == 0) {
                    replacement.append(source_left);
                } else {
                    replacement.append(target_ignore_left).append(target_left);
                }

                // Add the placeholder
                replacement.append(target_center);

                // Replace the right side's whitespace if nothing in the ignore capture group was detected hugging the placeholder
                if (target_ignore_right.length() == 0) {
                    replacement.append(source_right);
                } else {
                    replacement.append(target_ignore_right).append(target_right);
                }

                target_matcher.appendReplacement(final_target, Matcher.quoteReplacement(replacement.toString()));
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
        Integer leftCaptureGroup = config.getLeftCaptureGroup();
        Integer centerCaptureGroup = config.getCenterCaptureGroup();
        Integer compareCaptureGroup = config.getCompareCaptureGroup();
        Integer rightCaptureGroup = config.getRightCaptureGroup();
        if (pattern == null) {
            throw new WSException("Unable to load \"regex\" from config file located at " + CONFIG_WS_LOCATION);
        }
        if (leftCaptureGroup == null || centerCaptureGroup == null ||
                compareCaptureGroup == null || rightCaptureGroup == null) {
            throw new WSException(String.format("Unable to load all capture groups from config file located at " + CONFIG_WS_LOCATION + ": leftCaptureGroup = %d, centerCaptureGroup = %d, compareCaptureGroup = %d, rightCaptureGroup = %d", leftCaptureGroup, centerCaptureGroup, compareCaptureGroup, rightCaptureGroup));
        }

        regexPatternTokens = Pattern.compile(pattern);
        this.config = config;

    }


}
