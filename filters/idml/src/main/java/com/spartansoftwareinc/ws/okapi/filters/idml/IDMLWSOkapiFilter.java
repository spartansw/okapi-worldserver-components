package com.spartansoftwareinc.ws.okapi.filters.idml;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;
import net.sf.okapi.filters.idml.IDMLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDMLWSOkapiFilter extends WSOkapiFilter<IDMLFilterConfigurationData> {

    private static final Logger LOG = LoggerFactory.getLogger(IDMLWSOkapiFilter.class);

    private static final String FILTER_NAME = "Okapi IDML Filter";
    private static final String FILTER_DESCRIPTION = "World Server IDML com";
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
        return new IDMLFilterConfigurationUI();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    @Override
    public IDMLFilter getConfiguredFilter(IDMLFilterConfigurationData config) {
        IDMLFilter filter = new IDMLFilter();
        filter.setParameters(config.getParameters());
        return filter;
    }

    protected IDMLFilterConfigurationData getOkapiFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();

        return config instanceof IDMLFilterConfigurationData
                ? (IDMLFilterConfigurationData) config
                : new IDMLFilterConfigurationData();
    }
}
