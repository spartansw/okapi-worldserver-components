package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;
import net.sf.okapi.filters.openxml.ConditionalParameters;

public class OpenXMLFilterConfigurationData extends WSOkapiFilterConfigurationData<ConditionalParameters>{
    private static final long serialVersionUID = 1L; //TODO why do we need this line?
    //TODO

    @Override
    protected ConditionalParameters getDefaultParameters() {
        ConditionalParameters parameters = new ConditionalParameters();
        //TODO
        return parameters;
    }
}
