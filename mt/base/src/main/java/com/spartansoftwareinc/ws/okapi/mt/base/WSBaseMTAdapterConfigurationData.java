package com.spartansoftwareinc.ws.okapi.mt.base;

import com.idiominc.wssdk.component.mt.WSMTConfigurationData;

public class WSBaseMTAdapterConfigurationData extends WSMTConfigurationData {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_MATCH_SCORE = 95;

    private boolean useCustomScoring = false;
    private int matchScore = DEFAULT_MATCH_SCORE;
    private boolean includeCodes = false;
    private String localeMapAISPath = null;

    public boolean useCustomScoring() {
        return useCustomScoring;
    }

    public void setUseCustomScoring(boolean useCustomScoring) {
        this.useCustomScoring = useCustomScoring;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public boolean getIncludeCodes() {
        return includeCodes;
    }

    public void setIncludeCodes(boolean includeCodes) {
        this.includeCodes = includeCodes;
    }

    public String getLocaleMapAISPath() {
        return this.localeMapAISPath;
    }

    public void setLocaleMapAISPath(String aisPath) {
        this.localeMapAISPath = aisPath;
    }
}