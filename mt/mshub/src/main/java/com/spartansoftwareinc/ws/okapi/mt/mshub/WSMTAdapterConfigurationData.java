package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.component.mt.WSMTConfigurationData;

public class WSMTAdapterConfigurationData extends WSMTConfigurationData {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_MATCH_SCORE = 95;

    private String clientId;
    private String secret;
    private String category;
    private boolean useCustomScoring = false;
    private int matchScore = DEFAULT_MATCH_SCORE;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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
}
