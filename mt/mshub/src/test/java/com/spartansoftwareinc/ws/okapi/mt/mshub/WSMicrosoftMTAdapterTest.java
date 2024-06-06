package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.spartansoftwareinc.ws.okapi.mt.base.BatchQueryResults.getBatchQueryResults;
import static com.spartansoftwareinc.ws.okapi.mt.base.WSMTRequestStabs.getWSMTRequestStabs;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WSMicrosoftMTAdapterTest {

    @Mock
    private WSContext wsContext;

    @Mock
    private WSAisManager wsAisManager;

    @Mock
    private WSNode localeMapAisNode;

    @Mock
    private WSLanguage wsLanguage;

    @Mock
    private MicrosoftMTConnector mtConnector;

    @Test
    public void testSomeFilteredResponses() {
        WSMicrosoftMTAdapter mtAdapter = spy(new WSMicrosoftMTAdapter());
        doReturn(mtConnector).when(mtAdapter).getMTConnector();
        when(wsLanguage.getLocale()).thenReturn(Locale.ENGLISH);
        when(mtConnector.batchQueryText(anyList())).thenReturn(getBatchQueryResults(
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

    @Test
    public void testSomeFilteredResponsesWithCodes() {
        WSMicrosoftMTAdapter mtAdapter = spy(new WSMicrosoftMTAdapter());
        doReturn(mtConnector).when(mtAdapter).getMTConnector();
        WSMTAdapterConfigurationData config = new WSMTAdapterConfigurationData();
        config.setIncludeCodes(true);
        doReturn(config).when(mtAdapter).getConfigurationData();
        when(wsLanguage.getLocale()).thenReturn(Locale.ENGLISH);
        when(mtConnector.batchQueryText(anyList())).thenReturn(getBatchQueryResults(
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

    @Test
    public void testWSResultsAreSetOnWSRequest() {
        WSMicrosoftMTAdapter mtAdapter = spy(new WSMicrosoftMTAdapter());
        doReturn(mtConnector).when(mtAdapter).getMTConnector();
        when(wsLanguage.getLocale()).thenReturn(Locale.ENGLISH);
        when(mtConnector.batchQueryText(anyList())).thenReturn(getBatchQueryResults(
                "First segment", "Second segment", "Third segment"
        ));

        WSMTRequest[] requests = getWSMTRequestStabs("First segment", "Second segment", "Third segment");
        mtAdapter.translate(wsContext, requests, wsLanguage, wsLanguage);

        for (WSMTRequest request : requests) {
            assertEquals(request.getMTResults().length, 1);
        }

        assertEquals(requests[0].getMTResults()[0].getTranslation(), "First segment");
        assertEquals(requests[1].getMTResults()[0].getTranslation(), "Second segment");
        assertEquals(requests[2].getMTResults()[0].getTranslation(), "Third segment");
    }

    @Test
    public void testWSResultsAreSetOnWSRequestWithCodes() {
        WSMicrosoftMTAdapter mtAdapter = spy(new WSMicrosoftMTAdapter());
        mtAdapter.getConfigurationData().setIncludeCodes(true);
        doReturn(mtConnector).when(mtAdapter).getMTConnector();
        when(wsLanguage.getLocale()).thenReturn(Locale.ENGLISH);
        when(mtConnector.batchQueryText(anyList())).thenReturn(getBatchQueryResults(
                "First segment", "Second segment", "Third segment"
        ));

        WSMTRequest[] requests = getWSMTRequestStabs("First segment", "Second segment", "Third segment");
        mtAdapter.translate(wsContext, requests, wsLanguage, wsLanguage);

        for (WSMTRequest request : requests) {
            assertEquals(request.getMTResults().length, 1);
        }

        assertEquals(requests[0].getMTResults()[0].getTranslation(), "First segment");
        assertEquals(requests[1].getMTResults()[0].getTranslation(), "Second segment");
        assertEquals(requests[2].getMTResults()[0].getTranslation(), "Third segment");
    }

    @Test
    public void testLocaleMapping() throws Exception {
        WSMicrosoftMTAdapter mtAdapter = spy(new WSMicrosoftMTAdapter());
        mtAdapter.getConfigurationData().setLocaleMapAISPath("/Configuration/locales.txt");
        when(wsContext.getAisManager()).thenReturn(wsAisManager);
        when(wsAisManager.getNode("/Configuration/locales.txt")).thenReturn(localeMapAisNode);
        when(localeMapAisNode.getInputStream()).thenReturn(
                new ByteArrayInputStream("es-CO=es-419".getBytes(StandardCharsets.UTF_8)));
        doReturn(mtConnector).when(mtAdapter).getMTConnector();
        when(wsLanguage.getLocale()).thenReturn(new Locale("es", "CO"));
        LocaleId es419 = new LocaleId("es", "419");
        WSMTRequest[] requests = getWSMTRequestStabs("First segment", "Second segment", "Third segment");
        mtAdapter.translate(wsContext, requests, wsLanguage, wsLanguage);
        // Make sure the locale was set
    }
}
