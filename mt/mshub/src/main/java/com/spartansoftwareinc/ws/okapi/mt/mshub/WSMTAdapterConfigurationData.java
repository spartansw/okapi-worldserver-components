package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.component.mt.WSMTConfigurationData;

public class WSMTAdapterConfigurationData extends WSMTConfigurationData {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_MATCH_SCORE = 95;

    @Deprecated private String clientId;
    @Deprecated private String secret;
    private String azureKey;
    private String category;
    private boolean useCustomScoring = false;
    private int matchScore = DEFAULT_MATCH_SCORE;
    private boolean includeCodes = false;
    private String localeMapAISPath = null;

    public String getAzureKey() {
        return azureKey;
    }

    public void setAzureKey(String azureKey) {
        this.azureKey = azureKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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