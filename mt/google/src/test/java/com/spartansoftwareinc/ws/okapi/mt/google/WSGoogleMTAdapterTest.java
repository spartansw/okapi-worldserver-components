package com.spartansoftwareinc.ws.okapi.mt.google;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import net.sf.okapi.connectors.google.GoogleMTv2Connector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

import static com.spartansoftwareinc.ws.okapi.mt.base.BatchQueryResults.getBatchQueryResults;
import static com.spartansoftwareinc.ws.okapi.mt.base.WSMTRequestStabs.getWSMTRequestStabs;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WSGoogleMTAdapterTest {

    @Mock
    private WSContext wsContext;

    @Mock
    private WSAisManager wsAisManager;

    @Mock
    private WSNode localeMapAisNode;

    @Mock
    private WSLanguage wsLanguage;

    @Mock
    private GoogleMTv2Connector connector;

    @Test
    public void testSomeFilteredResponses() {
        WSGoogleMTAdapter mtAdapter = spy(new WSGoogleMTAdapter());
        doReturn(connector).when(mtAdapter).getMTConnector();
        when(wsLanguage.getLocale()).thenReturn(Locale.ENGLISH);
        when(connector.batchQueryText(anyList())).thenReturn(getBatchQueryResults(
                "First segment", "Third segment"
        ));
        WSMTRequest[] requests = getWSMTRequestStabs("First segment", "Second segment", "Third segment");
        mtAdapter.translate(wsContext, requests, wsLanguage, wsLanguage);
        assertEquals(1, requests[0].getMTResults().length);
        assertEquals(0, requests[1].getMTResults().length);
        assertEquals(1, requests[2].getMTResults().length);
        assertEquals(requests[0].getMTResults()[0].getTranslation(), "First segment");
        assertEquals(requests[2].getMTResults()[0].getTranslation(), "Third segment");
    }
}
