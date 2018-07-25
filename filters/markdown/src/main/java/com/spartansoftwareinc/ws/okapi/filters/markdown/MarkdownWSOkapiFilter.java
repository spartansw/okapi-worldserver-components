package com.spartansoftwareinc.ws.okapi.filters.markdown;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.FilterInfo;
import net.sf.okapi.filters.html.HtmlFilter;
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
    public MarkdownFilter getConfiguredFilter(MarkdownFilterConfigurationData config) {
        MarkdownFilter filter = new MarkdownFilter();
        filter.setParameters(config.getParameters());

        LOG.debug("config({}).filterConfigDirPath={}", ((Object)config).toString(), config.getFilterConfigDirPath());
        if (config.getHtmlSubfilter()==null) {
            return filter;
        }
        
        // Make sure custom config dir has filters.
        if (config.getFilterConfigDirPath()==null) {
            LOG.error("No filter config directory specified.");
            return filter;
        }
        
        FilterConfigurationMapper fcm = new FilterConfigurationMapper();
        fcm.addConfigurations(HtmlFilter.class.getCanonicalName());
		
        fcm.setCustomConfigurationsDirectory(config.getFilterConfigDirPath());
        fcm.updateCustomConfigurations();
        
	if (fcm.getFiltersInfo().size() == 0) {
	    LOG.warn("No filter found in the directory {}", config.getFilterConfigDirPath());
	    return filter;
	}
	    
	if (LOG.isDebugEnabled()) {
	    for (FilterInfo fi : fcm.getFiltersInfo()) {
		LOG.warn("  filter named {} of class {} found", fi.name, fi.className);
	    }
        }
        
        filter.setFilterConfigurationMapper(fcm);
        return filter;
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    @Override
    public void parse(WSContext context, WSNode srcContent, WSSegmentWriter wsSegmentWriter) {
	MarkdownFilterConfigurationData config = getOkapiFilterConfiguration();
	if (config.getFilterConfigDirPath()==null) {
	    LOG.error("filterConfigDirPath should have been set by UI code but it was null. Calling initializeFilterConfigDirPath(WSContext)...");
	    config.initializeFilterConfigDirPath(context);
	}
	super.parse(context, srcContent, wsSegmentWriter);
    }

    
    @Override
    protected MarkdownFilterConfigurationData getOkapiFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof MarkdownFilterConfigurationData) ?
	       (MarkdownFilterConfigurationData)config : new MarkdownFilterConfigurationData();
    }
}