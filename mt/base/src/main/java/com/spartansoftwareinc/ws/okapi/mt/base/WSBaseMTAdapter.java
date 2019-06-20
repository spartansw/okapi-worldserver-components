package com.spartansoftwareinc.ws.okapi.mt.base;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSVersion;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.idiominc.wssdk.component.mt.WSMTAdapterComponent;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.linguistic.WSLanguagePair;
import com.idiominc.wssdk.linguistic.WSLinguisticManager;
import com.idiominc.wssdk.mt.WSMTResult;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.BaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class WSBaseMTAdapter extends WSMTAdapterComponent {

    private static final Logger log = LoggerFactory.getLogger(WSBaseMTAdapter.class);

    protected CodesMasker codesMasker;

    protected WSBaseMTAdapterConfigurationData configurationData;
    protected LocaleMap localeMap;

    public WSBaseMTAdapter() {
        codesMasker = new CodesMasker();
    }

    public CodesMasker getCodesMasker() {
        return codesMasker;
    }

    public void setCodesMasker(CodesMasker codesMasker) {
        this.codesMasker = codesMasker;
    }

    @Override
    public void translate(WSContext wsContext, WSMTRequest[] wsmtRequests, WSLanguage srcLanguage, WSLanguage tgtLanguage) {

        if (0 == wsmtRequests.length) {
            return;
        }

        localeMap = getLocaleMap(wsContext);
        BaseConnector connector = getMTConnector();

        final Locale srcLocale = srcLanguage.getLocale();
        final Locale tgtLocale = tgtLanguage.getLocale();

        LocaleId srcLocaleId = getLocaleId(srcLocale.getLanguage(), srcLocale.getCountry());
        LocaleId tgtLocaleId = getLocaleId(tgtLocale.getLanguage(), tgtLocale.getCountry());

        log.info("srcLocale = " + srcLanguage.getDisplayString() + "=>" + srcLocaleId.toBCP47() +
                ", tgtLocale = " + tgtLanguage.getDisplayString() + "=>" + tgtLocaleId.toBCP47());

        connector.setLanguages(srcLocaleId, tgtLocaleId);

        connector.open();
        process(connector, wsmtRequests, getConfigurationData().getIncludeCodes());

        connector.close();
    }

    public abstract WSComponentConfigurationUI getConfigurationUI();

    protected abstract WSBaseMTAdapterConfigurationData getConfigurationData();

    protected LocaleMap getLocaleMap(WSContext context) {
        WSBaseMTAdapterConfigurationData configData = getConfigurationData();
        if (configData.getLocaleMapAISPath() != null && !"".equals(configData.getLocaleMapAISPath())) {
            try {
                WSNode node = context.getAisManager().getNode(configData.getLocaleMapAISPath());
                if (node == null) {
                    log.warn("Unable to load locale map from AIS: {} does not exist", configData.getLocaleMapAISPath());
                    return new LocaleMap();
                }
                try (Reader r = new InputStreamReader(node.getInputStream(), StandardCharsets.UTF_8)) {
                    return LocaleMap.load(r);
                }
            } catch (Exception e) {
                log.error("Unable to load locale map from AIS ({}); {}", configData.getLocaleMapAISPath(), e.getMessage());
            }
        }
        return new LocaleMap();
    }

    protected abstract BaseConnector getMTConnector();

    /**
     * Override this to remap locale IDs as needed.  The common case for this is to map
     * some es variant to es-419, which Microsoft expects for "Latin American Spanish".
     * Since WorldServer doesn't support this, many implementations designate some other
     * Spanish variant (such as es-MX or es-AR) as a proxy.  The base implementation
     * creates a LocaleId with the specified language and country.
     *
     * @param language language tag
     * @param country  country tag
     * @return a LocaleId instance appropriate for the parameters
     */
    protected LocaleId getLocaleId(String language, String country) {
        return localeMap.getMappedLocale(new LocaleId(language, country));
    }

    @Override
    public boolean supportsPlaceholders() {
        log.info("Supports placeholders: " + getConfigurationData().getIncludeCodes());
        return getConfigurationData().getIncludeCodes();
    }

    @Override
    public WSLanguagePair[] getSupportedLanguagePairs(WSContext wsContext) {
        final WSLinguisticManager linguisticManager = wsContext.getLinguisticManager();
        return composeLanguagePairs(linguisticManager.getLanguages());
    }

    protected WSLanguagePair[] composeLanguagePairs(WSLanguage[] languages) {
        ArrayList<WSLanguagePair> pairs = new ArrayList<>();

        for (int i = 0; i < languages.length; i++) {
            for (int j = 0; j < languages.length; j++) {
                if (i == j) continue;
                pairs.add(new WSLanguagePair(languages[i], languages[j]));
            }
        }

        return pairs.toArray(new WSLanguagePair[pairs.size()]);
    }

    public String getVersion() {
        return Version.BANNER;
    }

    public WSVersion getMinimumWorldServerVersion() {
        return new WSVersion(9, 0, 0);
    }

    public void process(BaseConnector mtConnector, WSMTRequest[] requests, boolean includeCodes) {
        List<String> requestStrings = getRequestStrings(requests, includeCodes);

        List<List<QueryResult>> batchResults = mtConnector.batchQueryText(requestStrings);

        if (batchResults.size() != requests.length) {
            log.info("Got " + batchResults.size() + " results for " + requests.length + " requests");
            alignBatchResultsWithRequests(requests, requestStrings, includeCodes, batchResults);

            return;
        }

        for (int i = 0; i < requests.length; i++) {
            final List<QueryResult> queryResults = batchResults.get(i);
            final WSMTRequest request = requests[i];

            request.setResults(getMTResults(request.getSource(), queryResults, includeCodes));
        }
    }

    protected void alignBatchResultsWithRequests(WSMTRequest[] requests, List<String> requestStrings, boolean includeCodes,
                                                 List<List<QueryResult>> batchResults) {
        int nextRequestIndex = 0;

        for (List<QueryResult> queryResults : batchResults) {
            String resultSource = queryResults.get(0).source.getCodedText();

            for (; nextRequestIndex < requests.length; nextRequestIndex++) {
                if (requestStrings.get(nextRequestIndex).equals(resultSource)) {

                    String source = includeCodes
                            ? requests[nextRequestIndex].getSource()
                            : resultSource;

                    requests[nextRequestIndex].setResults(getMTResults(source, queryResults, includeCodes));
                    nextRequestIndex++;
                    break;
                } else {
                    requests[nextRequestIndex].setResults(new WSMTResult[0]);
                }
            }
        }
    }

    protected List<String> getRequestStrings(WSMTRequest[] requests, boolean includeCodes) {
        List<String> requestStrings = new ArrayList<>();

        for (WSMTRequest request : requests) {
            if (includeCodes) {
                String maskedString = codesMasker.mask(request.getSource());
                log.info("Request: Masked [" + request.getSource() + "] --> [" + maskedString + "]");
                requestStrings.add(maskedString);
                continue;
            }

            requestStrings.add(request.getSource());
        }

        return requestStrings;
    }

    protected WSMTResult[] getMTResults(String source, List<QueryResult> queryResults, boolean includeCodes) {
        List<WSMTResult> results = new ArrayList<>();

        for (QueryResult queryResult : queryResults) {
            if (includeCodes) {
                String unmaskedString = codesMasker.unmask(queryResult.target.getCodedText());
                log.info("Result: Unmasked [" + queryResult.target + "] --> [" + unmaskedString + "]");
                results.add(new WSMTResult(source, unmaskedString, getScore(queryResult)));
                continue;
            }

            results.add(new WSMTResult(source, queryResult.target.getCodedText(), getScore(queryResult)));
        }

        return results.toArray(new WSMTResult[results.size()]);
    }

    protected int getScore(QueryResult result) {
        WSBaseMTAdapterConfigurationData config = getConfigurationData();
        return config.useCustomScoring() ? config.getMatchScore() : result.getCombinedScore();
    }
}
