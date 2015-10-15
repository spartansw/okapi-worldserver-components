package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.component.mt.WSMTConfigurationData;

public class WSMTAdapterConfigurationData extends WSMTConfigurationData {
    private static final long serialVersionUID = 1L;

    private String clientId;
    private String secret;
    private String category;

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
}
