package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.filters.openxml.OpenXMLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenXMLWSOkapiFilter extends WSOkapiFilter {

    private static final Logger LOG = LoggerFactory.getLogger(OpenXMLWSOkapiFilter.class);

    private static final String FILTER_NAME = "Okapi OpenXML Filter";
    private static final String FILTER_DESCRIPTION = "World Server OpenXML com";
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
        return new OpenXMLFilterConfigurationUI();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    public OpenXMLFilter getConfiguredFilter() {
        OpenXMLFilter filter = new OpenXMLFilter();
        filter.setParameters(getOpenXMLFilterConfiguration().getParameters());
        return filter;
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    protected OpenXMLFilterConfigurationData getOpenXMLFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof OpenXMLFilterConfigurationData) ?
                (OpenXMLFilterConfigurationData)config : new OpenXMLFilterConfigurationData();
    }
}
