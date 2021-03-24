package com.spartansoftwareinc.ws.mt.googlev3;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.mt.WSMTResult;

@RunWith(MockitoJUnitRunner.class)
public class WSGoogleMTv3AdapterTest {
    @Mock private TranslateTextRequest.Builder googleMTReqTemplate;
    @Mock private WSComponentConfiguration componentConfiguration;
    @Mock private TranslationServiceClient client;
    @Mock private TranslateTextRequest googleMTReq;
    @Mock private TranslateTextResponse mtResponse;
    @Mock private Translation translation;

    @Captor private ArgumentCaptor<String> stringCaptor;

    private WSGoogleMTv3Adapter mtAdapter;

    @BeforeClass
    public static void init() {
        Logger logger = Logger.getLogger(WSGoogleMTv3Adapter.class);
        logger.setLevel(Level.WARN);
    }

    @Test
    public void getTranslation_correct_placeholders() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String tgtText = "lk{1}hks{2}kkk{3}";
        when(translation.getTranslatedText()).thenReturn(tgtText);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(tgtText, results[0].getTranslation());

        verify(client).translateText(googleMTReq);
    }

    @Test
    public void getTranslation_placeholders_in_different_order() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String tgtText = "lk{2}hks{3}kkk{1}";
        when(translation.getTranslatedText()).thenReturn(tgtText);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(tgtText, results[0].getTranslation());

        verify(client).translateText(googleMTReq);
    }

    @Test
    public void getTranslation_corrupted_placeholders() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String firstTranslation = "lk{1}hks{2 }kkk{3}";
        String secondTranslation = "lk hks kkk";
        when(translation.getTranslatedText())
            .thenReturn(firstTranslation)
            .thenReturn(secondTranslation);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(secondTranslation + "{1}{2}{3}", results[0].getTranslation());

        verify(client, times(2)).translateText(googleMTReq);

        verify(googleMTReqTemplate, times(2)).addContents(stringCaptor.capture());
        List<String> params = stringCaptor.getAllValues();
        assertEquals(2, params.size());
        assertEquals(srcText, params.get(0));
        assertEquals("a bc def ghi", params.get(1));
    }

    @Test
    public void getTranslation_missing_placeholders() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String firstTranslation = "lk{1}hks{2}kkk";
        String secondTranslation = "lk hks kkk";
        when(translation.getTranslatedText())
            .thenReturn(firstTranslation)
            .thenReturn(secondTranslation);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(secondTranslation + "{1}{2}{3}", results[0].getTranslation());

        verify(client, times(2)).translateText(googleMTReq);

        verify(googleMTReqTemplate, times(2)).addContents(stringCaptor.capture());
        List<String> params = stringCaptor.getAllValues();
        assertEquals(2, params.size());
        assertEquals(srcText, params.get(0));
        assertEquals("a bc def ghi", params.get(1));
    }

    @Test
    public void getTranslation_extra_placeholders() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String firstTranslation = "lk{1}hks{2}kk{3}kk{2}";
        String secondTranslation = "lk hks kk kk";
        when(translation.getTranslatedText())
            .thenReturn(firstTranslation)
            .thenReturn(secondTranslation);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(secondTranslation + "{1}{2}{3}", results[0].getTranslation());

        verify(client, times(2)).translateText(googleMTReq);

        verify(googleMTReqTemplate, times(2)).addContents(stringCaptor.capture());
        List<String> params = stringCaptor.getAllValues();
        assertEquals(2, params.size());
        assertEquals(srcText, params.get(0));
        assertEquals("a bc def ghi", params.get(1));
    }

    @Test
    public void getTranslation_wrong_placeholders() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String firstTranslation = "lk{1}hks{2}kk{4}kk";
        String secondTranslation = "lk hks kk kk";
        when(translation.getTranslatedText())
            .thenReturn(firstTranslation)
            .thenReturn(secondTranslation);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(secondTranslation + "{1}{2}{3}", results[0].getTranslation());

        verify(client, times(2)).translateText(googleMTReq);

        verify(googleMTReqTemplate, times(2)).addContents(stringCaptor.capture());
        List<String> params = stringCaptor.getAllValues();
        assertEquals(2, params.size());
        assertEquals(srcText, params.get(0));
        assertEquals("a bc def ghi", params.get(1));
    }

    @Test
    public void getTranslation_duplicated_placeholders() throws Exception {
        String srcText = "a{1}bc{2}def{3}ghi";
        String firstTranslation = "lk{1}hks{2}kk{2}kk";
        String secondTranslation = "lk hks kk kk";
        when(translation.getTranslatedText())
            .thenReturn(firstTranslation)
            .thenReturn(secondTranslation);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(secondTranslation + "{1}{2}{3}", results[0].getTranslation());

        verify(client, times(2)).translateText(googleMTReq);

        verify(googleMTReqTemplate, times(2)).addContents(stringCaptor.capture());
        List<String> params = stringCaptor.getAllValues();
        assertEquals(2, params.size());
        assertEquals(srcText, params.get(0));
        assertEquals("a bc def ghi", params.get(1));
    }

    @Test
    public void getTranslation_no_placeholders_in_src() throws Exception {
        String srcText = "a bc def ghi";
        String tgtText = "lk hks kkk";
        when(translation.getTranslatedText()).thenReturn(tgtText);

        Method method = setupGetTranslationCall(true);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(tgtText, results[0].getTranslation());

        verify(client).translateText(googleMTReq);
        verify(mtResponse, times(2)).getTranslationsList();
    }

    @Test
    public void getTranslation_no_placeholders_support() throws Exception {
        String srcText = "a bc def ghi";
        String tgtText = "lk hks kkk";
        when(translation.getTranslatedText()).thenReturn(tgtText);

        Method method = setupGetTranslationCall(false);
        WSMTResult[] results = (WSMTResult[]) method.invoke(mtAdapter, client, googleMTReqTemplate, srcText, false);

        assertEquals(1, results.length);
        assertEquals(tgtText, results[0].getTranslation());

        verify(client).translateText(googleMTReq);
        verify(mtResponse).getTranslationsList();
    }


    private Method setupGetTranslationCall(boolean includeCodes) throws Exception {
        when(googleMTReqTemplate.clone()).thenReturn(googleMTReqTemplate);
        when(googleMTReqTemplate.addContents(anyString())).thenReturn(googleMTReqTemplate);
        when(googleMTReqTemplate.build()).thenReturn(googleMTReq);

        when(client.translateText(googleMTReq)).thenReturn(mtResponse);

        when(mtResponse.getTranslationsList()).thenReturn(Arrays.asList(translation));

        WSGoogleMTv3AdapterConfigurationData configData = new WSGoogleMTv3AdapterConfigurationData();
        configData.setIncludeCodes(includeCodes);

        when(componentConfiguration.getConfigurationData()).thenReturn(configData);

        mtAdapter = new WSGoogleMTv3Adapter();
        mtAdapter.setCurrentConfiguration(componentConfiguration);

        Method method = WSGoogleMTv3Adapter.class.getDeclaredMethod("getTranslation", TranslationServiceClient.class,
            TranslateTextRequest.Builder.class, String.class, boolean.class);
        method.setAccessible(true);
        return method;
    }
}
