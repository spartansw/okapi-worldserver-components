package com.spartansoftwareinc.ws.okapi.filters.markdown;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.filters.markdown.MarkdownFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownWSOkapiFilter extends WSOkapiFilter<MarkdownFilterConfigurationData> {

    private static final Logger LOG = LoggerFactory.getLogger(MarkdownWSOkapiFilter.class);
    private static final String FILTER_NAME = "Okapi Markdown Filter";
    private static final String FILTER_DESCRIPTION = "World Server Markdown Filter using Okapi Markdown Filter";
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
        return new MarkdownFilterConfigurationUI();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    public MarkdownFilter getConfiguredFilter(MarkdwownFilterConfigurationData config) {
        MarkdownFilter filter = new MarkdownFilter();
        filter.setParameters(config.getParameters());
        return filter;
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    @Override
    protected MarkdownFilterConfigurationData getOkapiFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof MarkdownFilterConfigurationData) ?
	       (MarkdownFilterConfigurationData)config : new MarkdownFilterConfigurationData();
    }
}