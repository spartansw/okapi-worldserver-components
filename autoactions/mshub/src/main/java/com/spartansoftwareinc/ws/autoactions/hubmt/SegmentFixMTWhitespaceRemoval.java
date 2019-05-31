package com.spartansoftwareinc.ws.autoactions.hubmt;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.asset.WSAssetTask;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.workflow.WSTask;
import com.spartansoftwareinc.ws.autoactions.Version;

/**
 * <p>During Machine Translation, white spaces next to tags can be removed. This Auto Action can be added after a Machine Translation to fix those whitespaces</p>
 *
 * <p>Example: {@literal Hello <b>my</b> darling} --> {@literal Salut<b>disque</b>Darling!}</p>
 * <p>
 * These whitespaces need to be reinserted.
 */
public class SegmentFixMTWhitespaceRemoval extends WSTaskAutomaticAction {

    private final Logger LOG = Logger.getLogger(SegmentFixMTWhitespaceRemoval.class);

    // This determines what to look for. The first and third capture groups determine what to restore from source,
    // and the second capture group is used to compare equality.
    private final static Pattern REGEX_PATTERN_TOKENS = Pattern.compile("(\\s)*\\{(\\d+)\\}(\\s)*");

    private final String RETURN_DONE = "Done";

    @Override
    public String getDescription() {
        return "Restores whitespaces around placeholders in the target translation, that were originally present in the source." +
                "\nExample of missing whitespaces: Hello <b>my</b> darling --> Salut<b>disque</b>chéri!" +
                "\nFixed: Hello <b>my</b> darling --> Salut <b>mon</b> chéri!";
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
     * Compares the source to the target with the Regular Expression {@link #REGEX_PATTERN_TOKENS}. If they are different
     * in any way, the source's string replaces the target's string in that specific spot. If the value of the token differs,
     * a warning is thrown and no replacement will be made.
     *
     * @param source The string to compare to
     * @param target The string that is being checked for differences
     * @return The target string with the updates to the placeholders
     */
    public String fixWhitespace(String source, String target) {

        Matcher source_matcher = REGEX_PATTERN_TOKENS.matcher(source);
        Matcher target_matcher = REGEX_PATTERN_TOKENS.matcher(target);

        StringBuffer final_target = new StringBuffer();

        while (source_matcher.find() && target_matcher.find()) {

            String source_item = source_matcher.group();
            String target_item = target_matcher.group();

            String source_value = source_matcher.group(2);
            String target_value = target_matcher.group(2);

            if (!source_value.equals(target_value)) {

                LOG.warn(String.format("Source value %s did not match target value %s when comparing strings", source_item, target_item));
            } else if (!source_item.equals(target_item)) {
                target_matcher.appendReplacement(final_target, Matcher.quoteReplacement(source_item));
            }

        }

        target_matcher.appendTail(final_target);

        return final_target.toString();
    }


}
