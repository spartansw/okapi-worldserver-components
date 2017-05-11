package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

public class WSMicrosoftMTAdapterConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 1L;

    private String azureKey;
    private String category;

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
}