package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.mt.WSMTResult;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;
import net.sf.okapi.connectors.microsoft.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WSMicrosoftMTAdapterTest {

    @Mock
    private WSContext wsContext;

    @Mock
    private WSLanguage wsLanguage;

    @Mock
    private MicrosoftMTConnector mtConnector;

    @Mock(extraInterfaces = {IParameters.class})
    private Parameters parameters;

    @Test
    public void testWSResultsAreSetOnWSRequest() {
        WSMicrosoftMTAdapter mtAdapter = spy(new WSMicrosoftMTAdapter());
        doReturn(mtConnector).when(mtAdapter).getMicrosoftMTConnector();
        when(mtConnector.getParameters()).thenReturn(parameters);
        when(wsLanguage.getLocale()).thenReturn(Locale.ENGLISH);
        when(mtConnector.batchQuery(anyList())).thenReturn(mockBatchQueryReturns(
                "First segment", "Second segment", "Third segment"
        ));

        WSMTRequest[] requests = composeWSMTRequests("First segment", "Second segment", "Third segment");
        mtAdapter.translate(wsContext, requests, wsLanguage, wsLanguage);

        for (WSMTRequest request : requests) {
            assertEquals(request.getMTResults().length, 1);
        }

        assertEquals(requests[0].getMTResults()[0].getTranslation(), "First segment");
        assertEquals(requests[1].getMTResults()[0].getTranslation(), "Second segment");
        assertEquals(requests[2].getMTResults()[0].getTranslation(), "Third segment");
    }

    private WSMTRequest[] composeWSMTRequests(String ... segments) {
        if (segments == null || segments.length == 0) {
            return new WSMTRequest[]{};
        }

        WSMTRequest[] requests = new WSMTRequest[segments.length];
        for (int i = 0; i < segments.length; i++) {
            requests[i] = new WSMTRequestImpl(segments[i]);
        }

        return requests;
    }

    private List<List<QueryResult>> mockBatchQueryReturns(String ... source) {
        List<List<QueryResult>> queryResultsList = new ArrayList<List<QueryResult>>();
        for (String s : source) {
            List<QueryResult> queryResults = new ArrayList<QueryResult>();
            final QueryResult qr = new QueryResult();
            qr.target = new TextFragment(s);
            queryResults.add(qr);
            queryResultsList.add(queryResults);
        }

        return queryResultsList;
    }

    private static class WSMTRequestImpl implements WSMTRequest {
        private final String source;
        private WSMTResult[] wsmtResults;

        public WSMTRequestImpl(String source) {
            this.source = source;
        }

        @Override
        public String getSource() {
            return source;
        }

        @Override
        public void setResults(WSMTResult[] wsmtResults) {
            this.wsmtResults = wsmtResults;
        }

        @Override
        public WSMTResult[] getMTResults() {
            return wsmtResults;
        }

        @Override
        public int getScore() {
            return 0;
        }

        @Override
        public boolean isRepetition() {
            return false;
        }
    }
}
