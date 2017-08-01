package com.spartansoftwareinc.ws.okapi.mt.google;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

public class WSGoogleMTAdapterConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 1L;

    private String apiKey;
    private int retryIntervalMs = 10000;
    private int retryCount = 10;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getRetryInterval() {
        return retryIntervalMs;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryInterval(int retryIntervalMs) {
        this.retryIntervalMs = retryIntervalMs;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}