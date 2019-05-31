package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

/**
 * WorldServer MT adapter configuration data for the Microsoft Custom Translator V3 adapter.
 */
public class WSMicrosoftMTAdapterV3ConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 2L;

    private String azureKey = "";
    private String category = "";
    private int matchScore = 95;

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

    @Override
    public int getMatchScore() {
        return matchScore;
    }

    @Override
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
}
