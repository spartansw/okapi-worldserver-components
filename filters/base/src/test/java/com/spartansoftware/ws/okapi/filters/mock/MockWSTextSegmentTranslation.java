package com.spartansoftware.ws.okapi.filters.mock;

import com.idiominc.wssdk.asset.WSInvalidOrMismatchPlaceholderException;
import com.idiominc.wssdk.asset.WSLockStatus;
import com.idiominc.wssdk.asset.WSRepetitionStatus;
import com.idiominc.wssdk.asset.WSTextSegment;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTextSegmentTranslationHistory;
import com.idiominc.wssdk.asset.WSTranslationStatus;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.mt.WSMTResult;
import com.idiominc.wssdk.review.WSReviewError;
import com.idiominc.wssdk.tm.WSTmHit;
import com.idiominc.wssdk.user.WSUser;
import com.idiominc.wssdk.workflow.WSTaskStep;

public class MockWSTextSegmentTranslation implements WSTextSegmentTranslation {
    private String source, target;
    private String[] sourcePlaceholders, targetPlaceholders;
    private WSTranslationType translationType = WSTranslationType.NO_TRANSLATION;

    public MockWSTextSegmentTranslation(String source, String target, String[] sourcePh, String[] targetPh) {
        this.source = source;
        this.target = target;
        this.sourcePlaceholders = sourcePh;
        this.targetPlaceholders = targetPh;
    }

    private WSTextSegmentPlaceholder[] convertPh(String[] ph) {
        WSTextSegmentPlaceholder[] converted = new WSTextSegmentPlaceholder[ph.length];
        for (int i = 0; i < ph.length; i++) {
            converted[i] = new MockWSTextSegmentPlaceholder(ph[i]);
        }
        return converted;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setTarget(String target) throws WSInvalidOrMismatchPlaceholderException {
        this.target = target;
    }

    @Override
    public void clearReviewErrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearTarget() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getComments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTextSegmentTranslationHistory[] getHistory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSLockStatus getLockStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSMTResult[] getMTResults() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaximumTargetLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSRepetitionStatus getRepetitionStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSReviewError[] getReviewErrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTextSegment getSegment() {
        return getTargetSegment();
    }

    @Override
    public WSTextSegmentPlaceholder[] getSourcePlaceholders() {
        return convertPh(sourcePlaceholders);
    }

    @Override
    public long getSourceWordCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTMScore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTextSegmentPlaceholder[] getTargetPlaceholders() {
        return convertPh(targetPlaceholders);
    }

    @Override
    public WSTextSegment getTargetSegment() {
        return new MockWSTextSegment(target, targetPlaceholders);
    }

    @Override
    public long getTargetWordCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTranslationStatus getTranslationStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTranslationType getTranslationType() {
        return translationType;
    }

    @Override
    public boolean isExactTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFuzzyTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHumanTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isICETranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isManuallyTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNotTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isQuestioned() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRepairTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTmTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToHistory(WSUser arg0, WSTaskStep arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setComments(String arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHumanTranslated() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public void setLockStatus(WSLockStatus arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setManuallyTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setManuallyTranslated(WSTranslationStatus arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaximumTargetLength(int arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setQuestioned(boolean arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReviewErrors(WSReviewError[] arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTarget(WSTmHit arg0) throws WSInvalidOrMismatchPlaceholderException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTranslationStatus(WSTranslationStatus arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTranslationType(WSTranslationType type) {
        this.translationType = type;
    }

    @Override
    public void setTranslationType(WSTranslationType arg0, WSTranslationStatus arg1) {
        throw new UnsupportedOperationException();
    }

}
