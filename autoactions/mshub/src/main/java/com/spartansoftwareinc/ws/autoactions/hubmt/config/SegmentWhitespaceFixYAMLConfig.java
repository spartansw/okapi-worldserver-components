package com.spartansoftwareinc.ws.autoactions.hubmt.config;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SegmentWhitespaceFixYAMLConfig {

    public List<SegmentRule> getSegmentRules() {
        return segmentRules;
    }

    public void setSegmentRules(List<SegmentRule> segmentRules) {
        this.segmentRules = segmentRules;
    }

    private List<SegmentRule> segmentRules;

    private static final String PLACEHOLDER_REGEX = "(\\{(\\d+)\\})";


    public SegmentWhitespaceFixYAMLConfig() {
    }

    public void construct() {
        for (SegmentRule segmentRule : segmentRules) {
            segmentRule.construct();
        }
    }


    public static class SegmentRule {

        // Data from YAML
        private String sourceLeft;
        private String targetLeft;
        private String replacementLeft;
        private String sourceRight;
        private String targetRight;
        private String replacementRight;
        private Boolean leftRightIndependent;
        private Boolean allowFurtherReplacementsLeft;
        private Boolean allowFurtherReplacementsRight;
        private Set<String> targetLanguages;

        public String getSourceLeft() {
            return sourceLeft;
        }

        public void setSourceLeft(String sourceLeft) {
            this.sourceLeft = sourceLeft;
        }

        public String getTargetLeft() {
            return targetLeft;
        }

        public void setTargetLeft(String targetLeft) {
            this.targetLeft = targetLeft;
        }

        public String getReplacementLeft() {
            return replacementLeft;
        }

        public void setReplacementLeft(String replacementLeft) {
            this.replacementLeft = replacementLeft;
        }

        public String getSourceRight() {
            return sourceRight;
        }

        public void setSourceRight(String sourceRight) {
            this.sourceRight = sourceRight;
        }

        public String getTargetRight() {
            return targetRight;
        }

        public void setTargetRight(String targetRight) {
            this.targetRight = targetRight;
        }

        public String getReplacementRight() {
            return replacementRight;
        }

        public void setReplacementRight(String replacementRight) {
            this.replacementRight = replacementRight;
        }

        public boolean isLeftRightIndependent() {
            return leftRightIndependent == null || leftRightIndependent;
        }

        public void setLeftRightIndependent(boolean leftRightIndependent) {
            this.leftRightIndependent = leftRightIndependent;
        }

        public boolean isAllowFurtherReplacementsLeft() {
            return allowFurtherReplacementsLeft == null || allowFurtherReplacementsLeft;
        }

        public void setAllowFurtherReplacementsLeft(boolean allowFurtherReplacementsLeft) {
            this.allowFurtherReplacementsLeft = allowFurtherReplacementsLeft;
        }

        public boolean isAllowFurtherReplacementsRight() {
            return allowFurtherReplacementsRight == null || allowFurtherReplacementsRight;
        }

        public void setAllowFurtherReplacementsRight(boolean allowFurtherReplacementsRight) {
            this.allowFurtherReplacementsRight = allowFurtherReplacementsRight;
        }

        public Set<String> getTargetLanguages() {
            return targetLanguages;
        }

        public void setTargetLanguages(Set<String> targetLanguages) {
            this.targetLanguages = targetLanguages;
        }

        public Pattern getTargetPatternRight() {
            return targetPatternRight;
        }

        public void setTargetPatternRight(Pattern targetPatternRight) {
            this.targetPatternRight = targetPatternRight;
        }

        public Pattern getTargetPatternLeft() {
            return targetPatternLeft;
        }

        public void setTargetPatternLeft(Pattern targetPatternLeft) {
            this.targetPatternLeft = targetPatternLeft;
        }

        public Pattern getSourcePatternComplete() {
            return sourcePatternComplete;
        }

        public void setSourcePatternComplete(Pattern sourcePatternComplete) {
            this.sourcePatternComplete = sourcePatternComplete;
        }

        public Pattern getTargetPatternComplete() {
            return targetPatternComplete;
        }

        public void setTargetPatternComplete(Pattern targetPatternComplete) {
            this.targetPatternComplete = targetPatternComplete;
        }

        public int getSourceCaptureGroupLeft() {
            return sourceCaptureGroupLeft;
        }

        public void setSourceCaptureGroupLeft(int sourceCaptureGroupLeft) {
            this.sourceCaptureGroupLeft = sourceCaptureGroupLeft;
        }

        public int getSourceCaptureGroupPlaceholder() {
            return sourceCaptureGroupPlaceholder;
        }

        public void setSourceCaptureGroupPlaceholder(int sourceCaptureGroupPlaceholder) {
            this.sourceCaptureGroupPlaceholder = sourceCaptureGroupPlaceholder;
        }

        public int getSourceCaptureGroupRight() {
            return sourceCaptureGroupRight;
        }

        public void setSourceCaptureGroupRight(int sourceCaptureGroupRight) {
            this.sourceCaptureGroupRight = sourceCaptureGroupRight;
        }

        public int getTargetCaptureGroupLeft() {
            return targetCaptureGroupLeft;
        }

        public void setTargetCaptureGroupLeft(int targetCaptureGroupLeft) {
            this.targetCaptureGroupLeft = targetCaptureGroupLeft;
        }

        public int getTargetCaptureGroupPlaceholder() {
            return targetCaptureGroupPlaceholder;
        }

        public void setTargetCaptureGroupPlaceholder(int targetCaptureGroupPlaceholder) {
            this.targetCaptureGroupPlaceholder = targetCaptureGroupPlaceholder;
        }

        public int getTargetCaptureGroupRight() {
            return targetCaptureGroupRight;
        }

        public void setTargetCaptureGroupRight(int targetCaptureGroupRight) {
            this.targetCaptureGroupRight = targetCaptureGroupRight;
        }

        // Compiled patterns and capture group numbers
        private Pattern targetPatternRight;
        private Pattern targetPatternLeft;
        private Pattern sourcePatternComplete;
        private Pattern targetPatternComplete;
        private int sourceCaptureGroupLeft;
        private int sourceCaptureGroupPlaceholder;
        private int sourceCaptureGroupRight;
        private int targetCaptureGroupLeft;
        private int targetCaptureGroupPlaceholder;
        private int targetCaptureGroupRight;


        /**
         * Combines the left and the right REGEX patterns into one giant expression. Even if the provided left and
         * right patterns don't match, this will still match the pattern.
         *
         * @param left  The left REGEX
         * @param right The right REGEX
         * @return A fully formed REGEX
         */
        private Pattern concatPatterns(String left, String right) {
            final String fullRegex = String.format("(%s)?%s(%s)?", left, PLACEHOLDER_REGEX, right);
            return Pattern.compile(fullRegex);

        }

        /**
         * Gets the capture group index containing the right pattern
         *
         * @param fullGroup The entire compiled regular expression
         * @param right     The right side REGEX pattern
         * @return The capture group that contains the entire expression
         */
        private int getRightCaptureGroupIndex(Pattern fullGroup, String right) {
            final int totalGroups = fullGroup.matcher("").groupCount();
            final int rightGroups = right != null ? Pattern.compile(right).matcher("").groupCount() : 0;
            return totalGroups - rightGroups;
        }

        public void construct() {
            if(sourceRight == null)
                sourceRight = "";
            if(sourceLeft == null)
                sourceLeft = "";
            if(targetRight == null)
                targetRight = "";
            if(targetLeft == null)
                targetLeft = "";

            targetPatternRight = Pattern.compile(targetRight);
            targetPatternLeft = Pattern.compile(targetLeft);
            sourcePatternComplete = concatPatterns(sourceLeft, sourceRight);
            targetPatternComplete = concatPatterns(targetLeft, targetRight);
            sourceCaptureGroupLeft = 1;
            targetCaptureGroupLeft = 1;
            sourceCaptureGroupRight = getRightCaptureGroupIndex(sourcePatternComplete, sourceRight);
            targetCaptureGroupRight = getRightCaptureGroupIndex(targetPatternComplete, targetRight);
            sourceCaptureGroupPlaceholder = sourceCaptureGroupRight - 2;
            targetCaptureGroupPlaceholder = targetCaptureGroupRight - 2;
        }
    }
}
