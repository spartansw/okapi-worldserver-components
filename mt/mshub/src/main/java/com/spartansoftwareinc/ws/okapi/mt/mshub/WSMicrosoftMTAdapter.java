package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapter;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;

import net.sf.okapi.lib.translation.BaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSMicrosoftMTAdapter extends WSBaseMTAdapter {

    private static final String ADAPTER_NAME = "MS Translation Hub Adapter";
    private static final String ADAPTER_DESCRIPTION = "MT Adapter for Microsoft Translation Hub";

    private static final Logger logger = LoggerFactory.getLogger(WSMicrosoftMTAdapter.class);

    public String getName() {
        return ADAPTER_NAME;
    }

    public String getDescription() {
        return ADAPTER_DESCRIPTION;
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSComponentConfigurationUI getConfigurationUI() {
        return new WSMicrosoftMTAdapterConfigurationUI();
    }

    @Override
    protected WSMTAdapterConfigurationData getConfigurationData() {
        if (null != configurationData) {
            return (WSMTAdapterConfigurationData) configurationData;
        }

        WSComponentConfiguration configuration = getCurrentConfiguration();

        configurationData = null != configuration
                ? ((WSMTAdapterConfigurationData) configuration.getConfigurationData())
                : new WSMTAdapterConfigurationData();

        return (WSMTAdapterConfigurationData) configurationData;
    }

    @Override
    protected BaseConnector getMTConnector() {
        MicrosoftMTConnector connector = new MicrosoftMTConnector();
        WSMTAdapterConfigurationData configurationData = getConfigurationData();

        connector.getParameters().setAzureKey(configurationData.getAzureKey());
        connector.getParameters().setCategory(configurationData.getCategory());

        logger.info("Using configuration: category={}", configurationData.getCategory());

        return connector;
    }
}
