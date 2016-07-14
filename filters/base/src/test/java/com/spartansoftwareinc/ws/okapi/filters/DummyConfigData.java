package com.spartansoftwareinc.ws.okapi.filters;

import net.sf.okapi.common.StringParameters;

public class DummyConfigData extends WSOkapiFilterConfigurationData<StringParameters> {
    private static final long serialVersionUID = 1L;

    @Override
    protected StringParameters getDefaultParameters() {
        StringParameters params = new StringParameters();
        params.setString("testKey", "");
        return params;
    }
}