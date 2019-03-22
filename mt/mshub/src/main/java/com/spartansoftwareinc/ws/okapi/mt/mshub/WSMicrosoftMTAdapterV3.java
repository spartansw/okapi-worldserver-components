package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.spartansoftwareinc.ws.okapi.mt.mshub.api.RequestPartitioner;
import com.spartansoftwareinc.ws.okapi.mt.mshub.api.MicrosoftMTConnectorV3;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapter;

import net.sf.okapi.lib.translation.BaseConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.linguistic.WSLanguagePair;
import com.idiominc.wssdk.mt.WSUnsupportedLanguagePairException;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.QueryResult;

public class WSMicrosoftMTAdapterV3 extends WSBaseMTAdapter {

    private static final String ADAPTER_NAME = "MS Custom Translator V3 Adapter";
    private static final String ADAPTER_DESCRIPTION = "MT Adapter for Microsoft Custom Translator V3";

    private static final Logger LOG = LoggerFactory.getLogger(WSMicrosoftMTAdapterV3.class);

    @Override
    public String getName() {
        return ADAPTER_NAME;
    }

    @Override
    public String getDescription() {
        return ADAPTER_DESCRIPTION;
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSComponentConfigurationUI getConfigurationUI() {
        return new WSMicrosoftMTAdapterV3ConfigurationUI();
    }

    @Override
    protected WSBaseMTAdapterConfigurationData getConfigurationData() {
        if (null != configurationData) {
            return (WSMicrosoftMTAdapterV3ConfigurationData) configurationData;
        }

        WSComponentConfiguration configuration = getCurrentConfiguration();

        configurationData = null != configuration
                ? ((WSMicrosoftMTAdapterV3ConfigurationData) configuration.getConfigurationData())
                : new WSMicrosoftMTAdapterV3ConfigurationData();

        return (WSMicrosoftMTAdapterV3ConfigurationData) configurationData;
    }

    @Override
    protected WSLanguagePair[] composeLanguagePairs(WSLanguage[] languages) {
        ArrayList<WSLanguagePair> pairs = new ArrayList<>();

        // https://docs.microsoft.com/en-us/azure/cognitive-services/translator/language-support
        HashSet supportedLangs = new HashSet(Arrays.asList("af", "ar", "bg", "bn", "bs", "ca", "cs", "cy", "da", "de",
                "el", "en", "es", "et", "fa", "fi", "fil", "fj", "fr", "he", "hi", "hr", "ht", "hu", "id", "is", "it",
                "ja", "ko", "lt", "lv", "mg", "ms", "mt", "mww", "nb", "nl", "otq", "pl", "pt", "ro", "ru", "sk", "sl",
                "sm", "sr", "sv", "sw", "ta", "te", "th", "tlh", "to", "tr", "ty", "uk", "ur", "vi", "yua", "yue", "zh"));
        for (int i = 0; i < languages.length; i++) {
            for (int j = 0; j < languages.length; j++) {
                String sourceLang = languages[i].getLocale().getLanguage();
                String targetLang = languages[j].getLocale().getLanguage();
                boolean sourceLangValid = supportedLangs.contains(sourceLang);
                boolean targetLangValid = supportedLangs.contains(targetLang);
                if (i != j && sourceLangValid && targetLangValid) {
                    pairs.add(new WSLanguagePair(languages[i], languages[j]));
                }
            }
        }

        return pairs.toArray(new WSLanguagePair[pairs.size()]);
    }

    @Override
    protected BaseConnector getMTConnector() {
        MicrosoftMTConnectorV3 connector = new MicrosoftMTConnectorV3();
        WSMicrosoftMTAdapterV3ConfigurationData cfgData = (WSMicrosoftMTAdapterV3ConfigurationData) getConfigurationData();

        connector.getParameters().setAzureKey(cfgData.getAzureKey());
        connector.getParameters().setCategory(cfgData.getCategory());

        LOG.info("Using configuration: category={}", cfgData.getCategory());

        return connector;
    }

    @Override
    public void translate(WSContext context, WSMTRequest[] requests, WSLanguage source, WSLanguage target) {
        if (requests == null || source == null || target == null) {
            return;
        }
        if (!isSupportedLanguagePair(context, source, target)) {
            throw new WSUnsupportedLanguagePairException(
                    source.getName() + " to " + target.getName() + " translation not supported");
        }

        if (requests.length == 0) {
            LOG.warn("Translate called, but there is no source text to translate");
            return;
        }
        // Setup locale mapping before instantiating the connector
        localeMap = getLocaleMap(context);
        MicrosoftMTConnectorV3 connector = (MicrosoftMTConnectorV3)setupConnector(source, target);
        LOG.debug("Translate called for source texts: " + getSourceString(Arrays.asList(requests)));

        WSMicrosoftMTAdapterV3ConfigurationData config = (WSMicrosoftMTAdapterV3ConfigurationData) getConfigurationData();

        List<List<WSMTRequest>> requestPartitions;
        RequestPartitioner partitioner = new RequestPartitioner();
        try {
            requestPartitions = partitioner.partition(requests);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < requestPartitions.size(); i++) {
            LOG.debug("Generated partition " + (i + 1) + "/" + requestPartitions.size() + ": "
                    + getSourceString(requestPartitions.get(i)));
        }

        for (List<WSMTRequest> requestPartition : requestPartitions) {
            if (requestPartition.isEmpty()) {
                LOG.warn("Detected an unexpected empty partition");
                continue;
            }

            List<String> texts = getRequestStrings(requestPartition.toArray(new WSMTRequest[requestPartition.size()]),
                    config.getIncludeCodes());

            List<List<QueryResult>> responses;
            try {
                responses = connector.batchQueryText(texts);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < requestPartition.size(); i++) {
                WSMTRequest request = requestPartition.get(i);
                request.setResults(getMTResults(request.getSource(), responses.get(i), config.getIncludeCodes()));
            }
        }
    }

    private BaseConnector setupConnector(WSLanguage srcLanguage, WSLanguage tgtLanguage) {
        BaseConnector connector = getMTConnector();

        final Locale srcLocale = srcLanguage.getLocale();
        final Locale tgtLocale = tgtLanguage.getLocale();

        LocaleId srcLocaleId = getLocaleId(srcLocale.getLanguage(), srcLocale.getCountry());
        LocaleId tgtLocaleId = getLocaleId(tgtLocale.getLanguage(), tgtLocale.getCountry());

        LOG.info("srcLocale = " + srcLanguage.getDisplayString() + "=>" + srcLocaleId.toBCP47() +
                ", tgtLocale = " + tgtLanguage.getDisplayString() + "=>" + tgtLocaleId.toBCP47());

        connector.setLanguages(srcLocaleId, tgtLocaleId);
        return connector;
    }

    /** Returns a single string containing all of the source text in 'requests'. */
    public String getSourceString(List<WSMTRequest> requests) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < requests.size(); i++) {
            builder.append("[");
            builder.append(requests.get(i).getSource());
            builder.append("]");
            if (i < requests.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
