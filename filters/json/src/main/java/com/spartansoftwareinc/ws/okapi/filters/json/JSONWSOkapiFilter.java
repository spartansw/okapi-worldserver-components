package com.spartansoftwareinc.ws.okapi.filters.json;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONWSOkapiFilter extends AbstractJSONWSOkapiFilter<JSONFilterConfigurationData> {
    private static final Logger LOG = LoggerFactory.getLogger(JSONWSOkapiFilter.class);

    @Override
    public WSFilterUIConfiguration getUIConfiguration() {
        return new JSONFilterConfigurationUI();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    protected JSONFilterConfigurationData getOkapiFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof JSONFilterConfigurationData) ?
                (JSONFilterConfigurationData)config : new JSONFilterConfigurationData();
    }
}