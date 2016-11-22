package com.spartansoftwareinc.ws.okapi.filters.xliff;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

import net.sf.okapi.filters.xliff.Parameters;

public class XLIFFFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Parameters getDefaultParameters() {
        return new Parameters();
    }
}
