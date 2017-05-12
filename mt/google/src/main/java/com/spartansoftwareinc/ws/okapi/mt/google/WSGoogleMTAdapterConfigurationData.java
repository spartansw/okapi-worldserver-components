package com.spartansoftwareinc.ws.okapi.mt.google;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

public class WSGoogleMTAdapterConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 1L;

    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}