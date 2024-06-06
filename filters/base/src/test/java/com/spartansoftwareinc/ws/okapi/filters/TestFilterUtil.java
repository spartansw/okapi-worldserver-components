package com.spartansoftwareinc.ws.okapi.filters;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.ais.WSSystemPropertyKey;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;

@RunWith(MockitoJUnitRunner.class)
public class TestFilterUtil {

    @Mock
    WSNode node;

    @Test
    public void testDetectEncoding() throws Exception {
        when(node.getProperty(WSSystemPropertyKey.ENCODING)).thenReturn("UTF8");
        assertEquals("UTF-8", FilterUtil.detectEncoding(node, "ASCII"));
        when(node.getProperty(WSSystemPropertyKey.ENCODING)).thenReturn(null);
        assertEquals("UTF-8", FilterUtil.detectEncoding(node, "UTF-8"));
    }
}
