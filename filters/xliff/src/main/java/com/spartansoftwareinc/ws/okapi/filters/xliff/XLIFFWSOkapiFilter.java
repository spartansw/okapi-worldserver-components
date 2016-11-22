package com.spartansoftwareinc.ws.okapi.filters.xliff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.filters.xliff.XLIFFFilter;

public class XLIFFWSOkapiFilter extends WSOkapiFilter<XLIFFFilterConfigurationData> {
    private static final Logger LOG = LoggerFactory.getLogger(XLIFFWSOkapiFilter.class);

    @Override
    public String getDescription() {
        return "Bilingual XLIFF 1.2 filter";
    }

    @Override
    public String getName() {
        return "Okapi XLIFF Filter";
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    protected XLIFFFilterConfigurationData getOkapiFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof XLIFFFilterConfigurationData) ?
                (XLIFFFilterConfigurationData)config : new XLIFFFilterConfigurationData();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    protected XLIFFFilter getConfiguredFilter(XLIFFFilterConfigurationData configData) {
        XLIFFFilter filter = new XLIFFFilter();
        filter.setParameters(configData.getParameters());
        return filter;
    }

    @Override
    protected String getDefaultEncoding() {
        return "UTF-8";
    }
}
