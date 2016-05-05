package com.spartansoftwareinc.ws.okapi.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.filters.properties.PropertiesFilter;

// Dummy filter (an unconfigured properties filter) so that we can
// exercise the base code.
public class DummyWSOkapiFilter extends WSOkapiFilter<DummyConfigData> {
    private static final Logger LOG = LoggerFactory.getLogger(DummyWSOkapiFilter.class);

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    protected IFilter getConfiguredFilter(DummyConfigData configData) {
        return new PropertiesFilter();
    }

    @Override
    protected String getDefaultEncoding() {
        return "UTF-8";
    }

    @Override
    protected DummyConfigData getOkapiFilterConfiguration() {
        return new DummyConfigData();
    }
}
