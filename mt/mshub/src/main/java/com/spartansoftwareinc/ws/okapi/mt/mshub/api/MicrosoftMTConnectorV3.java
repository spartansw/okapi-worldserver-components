package com.spartansoftwareinc.ws.okapi.mt.mshub.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.connectors.microsoft.Parameters;
import net.sf.okapi.lib.translation.BaseConnector;

/**
 * Okapi Microsoft Custom Translator prototype connector.
 */
public class MicrosoftMTConnectorV3 extends BaseConnector {
    private static final Logger LOG = Logger.getLogger(MicrosoftMTConnectorV3.class);

    private Parameters params = new Parameters();
    private MicrosoftTextApiClient apiClient = new MicrosoftTextApiClient();

    @Override
    public String getName() {
        return "Microsoft Custom Translator";
    }

    /**
     * MT connector configuration settings as a string, for informational purposes.
     * @return MT connector config info
     */
    @Override
    public String getSettingsDisplay() {
        return apiClient.getBaseURL();
    }

    @Override
    public Parameters getParameters() {
        return params;
    }

    @Override
    public void setParameters(IParameters params) {
        this.params = (Parameters)params;
    }

    @Override
    public void open() {
        // Nothing to do
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public int query(String plainText) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int query(TextFragment text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<List<QueryResult>> batchQueryText(List<String> plainTexts) {
        try {
            List<TranslateResponse> response = apiClient.translate(plainTexts, srcCode, trgCode,
                    getParameters().getAzureKey(), getParameters().getCategory());
            return convertResults(plainTexts, response);
        } catch (IOException | URISyntaxException ex) {
            LOG.error(ex);
            throw new RuntimeException("Translation request failed", ex);
        }
    }

    private List<List<QueryResult>> convertResults(List<String> plainTexts, List<TranslateResponse> responses) {
        List<List<QueryResult>> results = new ArrayList();
        for (int i = 0; i < responses.size(); i++) {
            TranslateResponse response = responses.get(i);
            List<QueryResult> singleResult = new ArrayList<>();
            for (TranslateResponse.Translation translation : response.translations) {
                QueryResult qr = createQueryResult();
                qr.source = new TextFragment(plainTexts.get(i));
                qr.target = new TextFragment(translation.text);
                singleResult.add(qr);
            }
            results.add(singleResult);
        }
        return results;
    }

    private QueryResult createQueryResult() {
        QueryResult qr = new QueryResult();

        /**
         * MS custom translator v3 API does not have this information.
         */
        int rating = 0;
        int matchDegree = 75;
        int combinedScore = 75;
        qr.setQuality(Util.normalizeRange(-10, 10, rating));
        qr.setFuzzyScore(matchDegree); // Score from the system
        qr.setCombinedScore(combinedScore); // Adjusted score

        // Else: continue with that result
        qr.weight = getWeight();
        qr.origin = getName();
        if (!Util.isEmpty(params.getCategory())) {
            qr.engine = params.getCategory();
        }
        qr.matchType = MatchType.MT;
        return qr;
    }
}
