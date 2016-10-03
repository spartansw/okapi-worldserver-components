package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSVersion;
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
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;
import net.sf.okapi.connectors.microsoft.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void translate(WSContext wsContext, WSMTRequest[] wsmtRequests, WSLanguage srcLanguage, WSLanguage tgtLanguage) {

        if (wsmtRequests.length > 0) {
            MicrosoftMTConnector mtConnector = initMicrosoftMTConnector(getMicrosoftMTConnector());
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
        return new LocaleId(language, country);
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
        ((Parameters) mtConnector.getParameters()).setClientId(getConfiguration().getClientId());
        ((Parameters) mtConnector.getParameters()).setSecret(getConfiguration().getSecret());
        ((Parameters) mtConnector.getParameters()).setCategory(getConfiguration().getCategory());

        return mtConnector;
    }

    protected int getScore(QueryResult result) {
        WSMTAdapterConfigurationData config = getConfiguration();
        return config.useCustomScoring() ? config.getMatchScore() : result.getCombinedScore();
    }

    protected void processWithCodes(MicrosoftMTConnector mtConnector, WSMTRequest[] requests) {
        List<TextFragment> extractedTextFragments = extractTextFragments(requests);
        List<List<QueryResult>> batchResults = mtConnector.batchQuery(extractedTextFragments);
        if (batchResults.size() == requests.length) {
            for (int i = 0; i < requests.length; i++) {
                final List<QueryResult> requestResults = batchResults.get(i);
                final WSMTRequest request = requests[i];
                request.setResults(convertTextFragment(request.getSource(), requestResults));
            }
        }
        else {
            log.warn("Got " + batchResults.size() + " results for " + requests.length + " requests");
            alignResponseTextFragments(requests, extractedTextFragments, batchResults);
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
            log.warn("Got " + batchResults.size() + " results for " + requests.length + " requests");
            alignResponseStrings(requests, extractedStrings, batchResults);
        }
    }

    // TODO: refactor with the alignResponseTextFragments version
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

    protected void alignResponseTextFragments(WSMTRequest[] requests, List<TextFragment> requestFragments,
                                              List<List<QueryResult>> batchResults) {
        int nextRequestIndex = 0;
        for (int i = 0; i < batchResults.size(); i++) {
            List<QueryResult> results = batchResults.get(i);
            TextFragment resultSource = results.get(0).source;

            for ( ; nextRequestIndex < requests.length; nextRequestIndex++) {
                if (requestFragments.get(nextRequestIndex).equals(resultSource)) {
                    requests[nextRequestIndex].setResults(convertTextFragment(requests[nextRequestIndex].getSource(), results));
                    nextRequestIndex++;
                    break;
                }
                else {
                    requests[nextRequestIndex].setResults(new WSMTResult[0]);
                }
            }
        }
    }

    protected List<TextFragment> extractTextFragments(WSMTRequest[] requests) {
        List<TextFragment> fragments = new ArrayList<TextFragment>();
        for (WSMTRequest request : requests) {
            TextFragment tf = converter.toTextFragment(request.getSource());
            log.debug("Request: Converted [" + request.getSource() + "] --> [" + tf.getCodedText() + "]");
            fragments.add(tf);
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

    protected WSMTResult[] convertTextFragment(String source, List<QueryResult> queryResults) {
        List<WSMTResult> results = new ArrayList<WSMTResult>();
        for (QueryResult queryResult : queryResults) {
            String s = converter.fromTextFragment(queryResult.target);
            log.debug("Result: Converted [" + queryResult.target + "] --> [" + s + "]");
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
