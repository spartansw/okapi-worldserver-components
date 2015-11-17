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

    @Override
    public void translate(WSContext wsContext, WSMTRequest[] wsmtRequests, WSLanguage srcLanguage, WSLanguage tgtLanguage) {

        if (wsmtRequests.length > 0) {
            MicrosoftMTConnector mtConnector = initMicrosoftMTConnector(getMicrosoftMTConnector());
            final Locale srcLocale = srcLanguage.getLocale();
            final Locale tgtLocale = tgtLanguage.getLocale();
            LocaleId srcLocaleId = new LocaleId(srcLocale.getLanguage(), srcLocale.getCountry());
            LocaleId tgtLocaleId = new LocaleId(tgtLocale.getLanguage(), tgtLocale.getCountry());
            log.info("srcLocale = " + srcLanguage.getDisplayString() + "=>" + srcLocaleId.toBCP47() +
                     ", tgtLocale=" + tgtLanguage.getDisplayString() + "=>" + tgtLocaleId.toBCP47());
            mtConnector.setLanguages(srcLocaleId, tgtLocaleId);

            mtConnector.open();
            List<BatchData> batches = new ArrayList<BatchData>();
            for (WSMTRequest wsmtRequest : wsmtRequests) {
                if (batches.size() == 0) {
                    batches.add(new BatchData());
                }
                BatchData lastBatchData = batches.get(batches.size() - 1);

                if (!lastBatchData.add(wsmtRequest)) {
                    BatchData batchData = new BatchData();
                    batchData.add(wsmtRequest);
                    batches.add(batchData);
                }
            }

            for (BatchData batch : batches) {
                batch.process(mtConnector);
            }

            mtConnector.close();
        }
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

    private WSMTAdapterConfigurationData getConfiguration() {
        if (configurationData == null) {
            WSComponentConfiguration configuration = getCurrentConfiguration();
            configurationData = configuration != null
                    ? ((WSMTAdapterConfigurationData) configuration.getConfigurationData())
                    : new WSMTAdapterConfigurationData();
        }

        return configurationData;
    }

    public MicrosoftMTConnector getMicrosoftMTConnector() {
        return new MicrosoftMTConnector();
    }

    private MicrosoftMTConnector initMicrosoftMTConnector(MicrosoftMTConnector mtConnector) {
        ((Parameters) mtConnector.getParameters()).setClientId(getConfiguration().getClientId());
        ((Parameters) mtConnector.getParameters()).setSecret(getConfiguration().getSecret());
        ((Parameters) mtConnector.getParameters()).setCategory(getConfiguration().getCategory());

        return mtConnector;
    }

    private int getScore(QueryResult result) {
        WSMTAdapterConfigurationData config = getConfiguration();
        return config.useCustomScoring() ? config.getMatchScore() : result.getCombinedScore();
    }

    // Facilitate composition of the batch queries to MS Translation Hub
    // We need to deal with the following MS Translation Hub restrictions:
    //  - maximum number of segments that can be sent within one batch - 10
    //  - total amount of characters for all segments in the batch <= 10.000
    // details at https://msdn.microsoft.com/en-us/library/ff512418.aspx
    private class BatchData {
        // maximum number of segments in a batch query
        public static final int MAX_BATCH_ITEMS = 10;

        // when preparing batch queries MicrosoftMTConnector assumes that batch query
        // should not be longer than 10.000 characters including xml structure (although
        // it seems that API only restricts the total length of text segments themselves,
        // not including xml structure). We have no way of knowing the real size of xml created
        // in MicrosoftMTConnector structure so we assume that it should not be greater
        // than 10.000 - MAX_BATCH_CHARACTERS
        public static final int MAX_BATCH_CHARACTERS = 8500;

        // the length of the "<s:string></s:string>" wrapper for each text fragment inside a batch query
        private static final int TEXT_ITEM_WRAPPER_LENGTH = 21;

        private List<WSMTRequest> requests = new ArrayList<WSMTRequest>();
        private int batchLength = 0;
        private boolean isChunked = false;

        public boolean add(WSMTRequest request) {
            if (requests.size() >= MAX_BATCH_ITEMS) return false;

            int batchLengthIncrement = TEXT_ITEM_WRAPPER_LENGTH + request.getSource().length();
            if (batchLengthIncrement + batchLength > MAX_BATCH_CHARACTERS) {
                if (requests.size() > 0) {
                    return false;
                }
                isChunked = true;
            }
            requests.add(request);
            batchLength += batchLengthIncrement;

            return true;
        }

        public List<WSMTRequest> process(MicrosoftMTConnector mtConnector) {
            if (isChunked) {
                final WSMTRequest request = requests.get(0);
                List<TextFragment> fragments = split(new ArrayList<TextFragment>(), request.getSource(), 0, MAX_BATCH_CHARACTERS);
                List<List<QueryResult>> results = mtConnector.batchQuery(fragments);
                request.setResults(joinChunkedResults(request.getSource(), results));
            } else {
                List<List<QueryResult>> batchResults = mtConnector.batchQuery(extractTextFragments());
                if (batchResults.size() == requests.size()) {
                    for (int i = 0; i < requests.size(); i++) {
                        final List<QueryResult> requestResults = batchResults.get(i);
                        final WSMTRequest request = requests.get(i);
                        request.setResults(convert(request.getSource(), requestResults));
                    }
                }
            }

            return requests;
        }

        private List<TextFragment> extractTextFragments() {
            List<TextFragment> fragments = new ArrayList<TextFragment>();
            if (!isChunked) {
                for (WSMTRequest request : requests) {
                    fragments.add(new TextFragment(request.getSource()));
                }
            }

            return fragments;
        }

        private List<TextFragment> split(List<TextFragment> chunks, String src, int startIdx, int chunkSize) {
            if ((src.length() - startIdx) <= chunkSize) {
                chunks.add(new TextFragment(src.substring(startIdx)));
                return chunks;
            }

            String fullChunk = src.substring(startIdx, startIdx + chunkSize);
            int breakPos = fullChunk.lastIndexOf(" ");
            if (breakPos >= 0) {
                chunks.add(new TextFragment(fullChunk.substring(0, breakPos)));
                return split(chunks, src, startIdx + breakPos, chunkSize);
            } else {
                throw new IllegalArgumentException("Input string cannot be broken into words with a length <= " + chunkSize);
            }
        }

        private WSMTResult[] convert(String source, List<QueryResult> queryResults) {
            List<WSMTResult> results = new ArrayList<WSMTResult>();
            for (QueryResult queryResult : queryResults) {
                results.add(new WSMTResult(source, queryResult.target.getCodedText(), getScore(queryResult)));
            }
            return results.toArray(new WSMTResult[results.size()]);
        }

        private WSMTResult[] joinChunkedResults(String source, List<List<QueryResult>> batchResults) {
            // since we do not configure Okapi connector to request multiple results for a single
            // segment we can safely ignore QueryResults with index greater than 0
            int combinedScore = getConfiguration().useCustomScoring() ?
                                        getConfiguration().getMatchScore() : 100;
            StringBuilder sb = new StringBuilder();
            for (List<QueryResult> chunkResults : batchResults) {
                if (!chunkResults.isEmpty()) {
                    final QueryResult queryResult = chunkResults.get(0);
                    sb.append(queryResult.target.getCodedText());

                    if (!getConfiguration().useCustomScoring()) {
                        if (queryResult.getCombinedScore() < combinedScore) {
                            combinedScore = queryResult.getCombinedScore();
                        }
                    }
                }
            }

            return new WSMTResult[]{ new WSMTResult(source, sb.toString(), combinedScore) };
        }
    }
}
