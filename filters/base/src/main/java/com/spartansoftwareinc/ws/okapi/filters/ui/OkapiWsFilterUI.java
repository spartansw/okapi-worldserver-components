package com.spartansoftwareinc.ws.okapi.filters.ui;

import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;


public abstract class OkapiWsFilterUI extends WSFilterUIConfiguration {

    @Override
    public String getRedirectURI() {
        return null;
    }
}
