package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;


public class OpenXMLFilterConfigurationUI extends WSOkapiFilterUI<OpenXMLFilterConfigurationData> {
    //TODO

    @Override
    protected OpenXMLFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof OpenXMLFilterConfigurationData) ?
                (OpenXMLFilterConfigurationData)config : new OpenXMLFilterConfigurationData();
    }
}
