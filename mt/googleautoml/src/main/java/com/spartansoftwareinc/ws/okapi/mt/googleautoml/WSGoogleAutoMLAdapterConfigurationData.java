package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

public class WSGoogleAutoMLAdapterConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 1L;

    private String credentialAisPath = "";
    private String credentialAbsolutePath = "";
    private String modelMap = "";

    public String getCredentialAisPath() {
        return credentialAisPath;
    }

    public void setCredentialAisPath(String credentialAisPath) {
        this.credentialAisPath = credentialAisPath;
    }

    public String getCredentialAbsolutePath() {
        return credentialAbsolutePath;
    }

    public void setCredentialAbsolutePath(String credentialAbsolutePath) {
        this.credentialAbsolutePath = credentialAbsolutePath;
    }

    public String getModelMap() {
        return modelMap;
    }

    public void setModelMap(String modelMap) {
        this.modelMap = modelMap;
    }
}
