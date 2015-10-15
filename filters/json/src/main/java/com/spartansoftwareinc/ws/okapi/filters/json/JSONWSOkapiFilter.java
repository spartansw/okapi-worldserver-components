package com.spartansoftwareinc.ws.okapi.filters.json;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.filters.json.JSONFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONWSOkapiFilter extends WSOkapiFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JSONWSOkapiFilter.class);
    private static final String FILTER_NAME = "Okapi JSON Filter";
    private static final String FILTER_DESCRIPTION = "World Server JSON com";
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Override
    public String getName() {
        return FILTER_NAME;
    }

    @Override
    public String getDescription() {
        return FILTER_DESCRIPTION;
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSFilterUIConfiguration getUIConfiguration() {
        return new JSONFilterConfigurationUI();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    public JSONFilter getConfiguredFilter() {
        JSONFilter filter = new JSONFilter();
        filter.setParameters(getJSONFilterConfiguration().getParameters());
        return filter;
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    protected JSONFilterConfigurationData getJSONFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof JSONFilterConfigurationData) ?
                (JSONFilterConfigurationData)config : new JSONFilterConfigurationData();
    }
}