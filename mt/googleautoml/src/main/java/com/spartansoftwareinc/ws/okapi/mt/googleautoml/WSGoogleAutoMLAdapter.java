package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.spartansoftwareinc.ws.okapi.mt.base.CustomCodesMasker;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapter;

import net.sf.okapi.connectors.googleautoml.GoogleAutoMLTranslationConnector;
import net.sf.okapi.lib.translation.BaseConnector;

public class WSGoogleAutoMLAdapter extends WSBaseMTAdapter {
    private GoogleAutoMLTranslationConnector connector;

    public WSGoogleAutoMLAdapter() {
        setCodesMasker(new CustomCodesMasker("<\\s*div\\s+ws_id\\s*=\\s*\"(\\d+)\"\\s*>(\\s*<\\s*/\\s*div>)?"));
    }

    @Override
    public String getName() {
        return "Google AutoML Translation Service Adapter";
    }

    @Override
    public String getDescription() {
        return "MT Adapter for Google AutoML Translation Service";
    }

    @Override
    public WSComponentConfigurationUI getConfigurationUI() {
        return new WSGoogleAutoMLAdapterConfigurationUI();
    }

    @Override
    protected WSGoogleAutoMLAdapterConfigurationData getConfigurationData() {
        if (configurationData != null) {
            return (WSGoogleAutoMLAdapterConfigurationData) configurationData;
        }

        WSComponentConfiguration configuration = getCurrentConfiguration();
        configurationData = configuration == null
                ? new WSGoogleAutoMLAdapterConfigurationData()
                : ((WSGoogleAutoMLAdapterConfigurationData) configuration.getConfigurationData());

        return (WSGoogleAutoMLAdapterConfigurationData) configurationData;
    }

    @Override
    protected BaseConnector getMTConnector() {
        if (connector == null) {
            connector = new GoogleAutoMLTranslationConnector();
            WSGoogleAutoMLAdapterConfigurationData configData = getConfigurationData();
            connector.getParameters().setCredentialFilePath(configData.getCredentialAbsolutePath());
            connector.getParameters().setModelMap(configData.getModelMap());
        }
        return connector;
    }
}
