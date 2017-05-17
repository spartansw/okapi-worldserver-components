package com.spartansoftwareinc.ws.okapi.mt.google;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.linguistic.WSLanguagePair;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapter;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.connectors.google.GoogleMTv2Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSGoogleMTAdapter extends WSBaseMTAdapter {

    private static final String ADAPTER_NAME = "Google Translation Service Adapter";
    private static final String ADAPTER_DESCRIPTION = "MT Adapter for Google Translation Service";

    private static final Logger logger = LoggerFactory.getLogger(WSGoogleMTAdapter.class);

    private GoogleMTv2Connector connector;

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
    protected WSGoogleMTAdapterConfigurationData getConfigurationData() {
        if (null != configurationData) {
            return (WSGoogleMTAdapterConfigurationData) configurationData;
        }

        WSComponentConfiguration configuration = getCurrentConfiguration();

        configurationData = null != configuration
                ? ((WSGoogleMTAdapterConfigurationData) configuration.getConfigurationData())
                : new WSGoogleMTAdapterConfigurationData();

        return (WSGoogleMTAdapterConfigurationData) configurationData;
    }

    @Override
    protected GoogleMTv2Connector getMTConnector() {
        if (connector == null) {
            connector = new GoogleMTv2Connector();
            WSGoogleMTAdapterConfigurationData configurationData = getConfigurationData();

            connector.getParameters().setApiKey(configurationData.getApiKey());
        }
        return connector;
    }

    @Override
    public WSLanguagePair[] getSupportedLanguagePairs(WSContext wsContext) {
        List<LocaleId> locales = getMTConnector().getSupportedLanguages();
        WSLanguage[] availableLangs = wsContext.getLinguisticManager().getLanguages();
        List<WSLanguage> langs = new ArrayList<>();
        for (LocaleId locale : locales) {
            WSLanguage lang = findLanguage(availableLangs, locale);
            if (lang != null) {
                langs.add(lang);
            }
        }
        return composeLanguagePairs(langs.toArray(new WSLanguage[langs.size()]));
    }
    private WSLanguage findLanguage(WSLanguage[] available, LocaleId locale) {
        String lang = locale.getLanguage();
        String region = locale.getRegion();
        for (WSLanguage wslang : available) {
            Locale l = wslang.getLocale();
            if (l.getLanguage().equals(lang)) {
                if (region != null) {
                    if (region.equals(l.getCountry())) {
                        return wslang;
                    }
                }
                else if ("".equals(l.getCountry())) {
                    return wslang;
                }
            }
        }
        logger.debug("Could not find a WorldServer locale to map to Google locale {}", locale);
        return null;
    }
}
