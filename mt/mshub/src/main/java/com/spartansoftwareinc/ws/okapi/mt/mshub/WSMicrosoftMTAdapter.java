package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSVersion;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.idiominc.wssdk.component.mt.WSMTAdapterComponent;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.linguistic.WSLanguagePair;
import com.idiominc.wssdk.linguistic.WSLinguisticManager;
import com.idiominc.wssdk.mt.WSMTResult;
import com.spartansoftwareinc.ws.okapi.Version;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;
import net.sf.okapi.connectors.microsoft.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WSMicrosoftMTAdapter extends WSMTAdapterComponent {

    private static final String ADAPTER_NAME = "MS Translation Hub Adapter";
    private static final String ADAPTER_DESCRIPTION = "MT Adapter for Microsoft Translation Hub";

    private static final Logger log = LoggerFactory.getLogger(WSMicrosoftMTAdapter.class);

    private WSMTAdapterConfigurationData configurationData;
    private MTRequestConverter converter = new MTRequestConverter();
    private MicrosoftMTConnector connector = new MicrosoftMTConnector();
    private LocaleMap localeMap = new LocaleMap();

    @Override
    public void translate(WSContext wsContext, WSMTRequest[] wsmtRequests, WSLanguage srcLanguage, WSLanguage tgtLanguage) {

        if (wsmtRequests.length > 0) {
            MicrosoftMTConnector mtConnector = initMicrosoftMTConnector(getMicrosoftMTConnector());
            localeMap = initializeLocaleMap(wsContext);
            final Locale srcLocale = srcLanguage.getLocale();
            final Locale tgtLocale = tgtLanguage.getLocale();
            LocaleId srcLocaleId = getLocaleId(srcLocale.getLanguage(), srcLocale.getCountry());
            LocaleId tgtLocaleId = getLocaleId(tgtLocale.getLanguage(), tgtLocale.getCountry());
            log.info("srcLocale = " + srcLanguage.getDisplayString() + "=>" + srcLocaleId.toBCP47() +
                     ", tgtLocale=" + tgtLanguage.getDisplayString() + "=>" + tgtLocaleId.toBCP47());
            mtConnector.setLanguages(srcLocaleId, tgtLocaleId);

            mtConnector.open();
            if (getConfiguration().getIncludeCodes()) {
                processWithCodes(mtConnector, wsmtRequests);
            }
            else {
                processWithoutCodes(mtConnector, wsmtRequests);
            }
            mtConnector.close();
        }
    }

    protected LocaleMap initializeLocaleMap(WSContext context) {
        WSMTAdapterConfigurationData configData = getConfiguration();
        if (configData.getLocaleMapAISPath() != null && !"".equals(configData.getLocaleMapAISPath())) {
            Reader r = null;
            try {
                WSNode node = context.getAisManager().getNode(configData.getLocaleMapAISPath());
                if (node == null) {
                    log.warn("Unable to load locale map from AIS: {} does not exist", configData.getLocaleMapAISPath());
                    return new LocaleMap();
                }
                r = new InputStreamReader(node.getInputStream(), StandardCharsets.UTF_8);
                return LocaleMap.load(r);
            }
            catch (Exception e) {
                log.error("Unable to load locale map from AIS ({}); {}", configData.getLocaleMapAISPath(), e.getMessage());
            }
            finally {
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
        return new LocaleMap();
    }

    /**
     * Override this to remap locale IDs as needed.  The common case for this is to map
     * some es variant to es-419, which Microsoft expects for "Latin American Spanish".
     * Since WorldServer doesn't support this, many implementations designate some other
     * Spanish variant (such as es-MX or es-AR) as a proxy.  The base implementation
     * creates a LocaleId with the specified language and country.
     *
     * @param language language tag
     * @param country country tag
     * @return a LocaleId instance appropriate for the parameters
     */
    protected LocaleId getLocaleId(String language, String country) {
        return localeMap.getMappedLocale(new LocaleId(language, country));
    }

    @Override
    public boolean supportsPlaceholders() {
        log.info("Supports placeholders: " + getConfiguration().getIncludeCodes());
        return getConfiguration().getIncludeCodes();
    }

    @Override
    public WSLanguagePair[] getSupportedLanguagePairs(WSContext wsContext) {
        final WSLinguisticManager linguisticManager = wsContext.getLinguisticManager();
        return composeLanguagePairs(linguisticManager.getLanguages());
    }

    @Override
    public WSComponentConfigurationUI getConfigurationUI() {
        return new WSMTAdapterConfigurationUI();
    }

    public String getName() {
        return ADAPTER_NAME;
    }

    public String getDescription() {
        return ADAPTER_DESCRIPTION;
    }

    public String getVersion() {
        return Version.BANNER;
    }

    public WSVersion getMinimumWorldServerVersion() {
        return new WSVersion(9, 0, 0);
    }

    private WSLanguagePair[] composeLanguagePairs(WSLanguage[] languages) {
        ArrayList<WSLanguagePair> pairs = new ArrayList<WSLanguagePair>();

        for (int i = 0; i < languages.length; i++) {
            for (int j = 0; j < languages.length; j++) {
                if (i == j) continue;
                pairs.add(new WSLanguagePair(languages[i], languages[j]));
            }
        }

        return (WSLanguagePair[]) pairs.toArray(new WSLanguagePair[pairs.size()]);
    }

    WSMTAdapterConfigurationData getConfiguration() {
        if (configurationData == null) {
            WSComponentConfiguration configuration = getCurrentConfiguration();
            configurationData = configuration != null
                    ? ((WSMTAdapterConfigurationData) configuration.getConfigurationData())
                    : new WSMTAdapterConfigurationData();
        }

        return configurationData;
    }

    public MicrosoftMTConnector getMicrosoftMTConnector() {
        return connector;
    }

    protected MicrosoftMTConnector initMicrosoftMTConnector(MicrosoftMTConnector mtConnector) {
        ((Parameters) mtConnector.getParameters()).setAzureKey(getConfiguration().getAzureKey());
        ((Parameters) mtConnector.getParameters()).setCategory(getConfiguration().getCategory());
        log.info("Using configuration: category={}", getConfiguration().getCategory());
        return mtConnector;
    }

    protected int getScore(QueryResult result) {
        WSMTAdapterConfigurationData config = getConfiguration();
        return config.useCustomScoring() ? config.getMatchScore() : result.getCombinedScore();
    }

    protected void processWithCodes(MicrosoftMTConnector mtConnector, WSMTRequest[] requests) {
        List<String> codedStrings = extractStringsWithCodeMarkup(requests);
        List<List<QueryResult>> batchResults = mtConnector.batchQueryText(codedStrings);
        if (batchResults.size() == requests.length) {
            for (int i = 0; i < requests.length; i++) {
                final List<QueryResult> requestResults = batchResults.get(i);
                final WSMTRequest request = requests[i];
                request.setResults(removeCodeMarkup(request.getSource(), requestResults));
            }
        }
        else {
            log.info("Got " + batchResults.size() + " results for " + requests.length + " requests");
            alignResponseCodedText(requests, codedStrings, batchResults);
        }
    }

    protected void processWithoutCodes(MicrosoftMTConnector mtConnector, WSMTRequest[] requests) {
        List<String> extractedStrings = extractStrings(requests);
        List<List<QueryResult>> batchResults = mtConnector.batchQueryText(extractedStrings);
        if (batchResults.size() == requests.length) {
            for (int i = 0; i < requests.length; i++) {
                final List<QueryResult> requestResults = batchResults.get(i);
                final WSMTRequest request = requests[i];
                request.setResults(convertText(request.getSource(), requestResults));
            }
        }
        else {
            log.info("Got " + batchResults.size() + " results for " + requests.length + " requests");
            alignResponseStrings(requests, extractedStrings, batchResults);
        }
    }

    // TODO: refactor with the alignResponseCodedText version
    protected void alignResponseStrings(WSMTRequest[] requests, List<String> requestStrings,
                                        List<List<QueryResult>> batchResults) {
        int nextRequestIndex = 0;
        for (int i = 0; i < batchResults.size(); i++) {
            List<QueryResult> results = batchResults.get(i);
            String resultSource = results.get(0).source.toString();

            for ( ; nextRequestIndex < requests.length; nextRequestIndex++) {
                if (requestStrings.get(nextRequestIndex).equals(resultSource)) {
                    requests[nextRequestIndex].setResults(convertText(resultSource, results));
                    nextRequestIndex++;
                    break;
                }
                else {
                    requests[nextRequestIndex].setResults(new WSMTResult[0]);
                }
            }
        }
    }

    protected void alignResponseCodedText(WSMTRequest[] requests, List<String> codedStrings,
                                          List<List<QueryResult>> batchResults) {
        int nextRequestIndex = 0;
        for (int i = 0; i < batchResults.size(); i++) {
            List<QueryResult> results = batchResults.get(i);
            String resultSource = results.get(0).source.getCodedText();

            for ( ; nextRequestIndex < requests.length; nextRequestIndex++) {
                if (codedStrings.get(nextRequestIndex).equals(resultSource)) {
                    requests[nextRequestIndex].setResults(removeCodeMarkup(requests[nextRequestIndex].getSource(), results));
                    nextRequestIndex++;
                    break;
                }
                else {
                    requests[nextRequestIndex].setResults(new WSMTResult[0]);
                }
            }
        }
    }

    protected List<String> extractStringsWithCodeMarkup(WSMTRequest[] requests) {
        List<String> fragments = new ArrayList<String>();
        for (WSMTRequest request : requests) {
            String s = converter.addCodeMarkup(request.getSource());
            log.info("Request: Converted [" + request.getSource() + "] --> [" + s + "]");
            fragments.add(s);
        }
        return fragments;
    }

    protected List<String> extractStrings(WSMTRequest[] requests) {
        List<String> strings = new ArrayList<String>();
        for (WSMTRequest request : requests) {
            strings.add(request.getSource());
        }
        return strings;
    }

    protected WSMTResult[] removeCodeMarkup(String source, List<QueryResult> queryResults) {
        List<WSMTResult> results = new ArrayList<WSMTResult>();
        for (QueryResult queryResult : queryResults) {
            String s = converter.removeCodeMarkup(queryResult.target.getCodedText());
            log.info("Result: Converted [" + queryResult.target + "] --> [" + s + "]");
            results.add(new WSMTResult(source, s, getScore(queryResult)));
        }
        return results.toArray(new WSMTResult[results.size()]);
    }

    protected WSMTResult[] convertText(String source, List<QueryResult> queryResults) {
        List<WSMTResult> results = new ArrayList<WSMTResult>();
        for (QueryResult queryResult : queryResults) {
            results.add(new WSMTResult(source, queryResult.target.getCodedText(), getScore(queryResult)));
        }
        return results.toArray(new WSMTResult[results.size()]);
    }
}
