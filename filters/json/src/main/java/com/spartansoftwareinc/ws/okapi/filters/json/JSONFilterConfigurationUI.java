package com.spartansoftwareinc.ws.okapi.filters.json;

import com.idiominc.wssdk.component.WSComponentConfigurationData;

public class JSONFilterConfigurationUI extends AbstractJSONFilterConfigurationUI<JSONFilterConfigurationData> {

    public JSONFilterConfigurationUI() {
    }

    @Override
    protected JSONFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof JSONFilterConfigurationData) ?
            (JSONFilterConfigurationData)config : new JSONFilterConfigurationData();
    }
}
