package com.spartansoftwareinc.ws.okapi.mt.google;

import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapter;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;
import net.sf.okapi.connectors.google.GoogleMTv2Connector;
import net.sf.okapi.lib.translation.BaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSGoogleMTAdapter extends WSBaseMTAdapter {

    private static final String ADAPTER_NAME = "Google Translation Service Adapter";
    private static final String ADAPTER_DESCRIPTION = "MT Adapter for Google Translation Service";

    private static final Logger logger = LoggerFactory.getLogger(WSGoogleMTAdapter.class);

    public String getName() {
        return ADAPTER_NAME;
    }

    public String getDescription() {
        return ADAPTER_DESCRIPTION;
    }

    @Override
    public WSComponentConfigurationUI getConfigurationUI() {
        return new WSGoogleMTAdapterConfigurationUI();
    }

    @Override
    protected WSBaseMTAdapterConfigurationData getConfigurationData() {
        if (null != configurationData) {
            return configurationData;
        }

        WSComponentConfiguration configuration = getCurrentConfiguration();

        configurationData = null != configuration
                ? ((WSGoogleMTAdapterConfigurationData) configuration.getConfigurationData())
                : new WSGoogleMTAdapterConfigurationData();

        return configurationData;
    }

    @Override
    protected BaseConnector getMTConnector() {
        GoogleMTv2Connector connector = new GoogleMTv2Connector();
        WSGoogleMTAdapterConfigurationData configurationData = (WSGoogleMTAdapterConfigurationData) getConfigurationData();

        connector.getParameters().setApiKey(configurationData.getApiKey());

        return connector;
    }
}
