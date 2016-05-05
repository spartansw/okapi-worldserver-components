package com.spartansoftwareinc.ws.okapi.filters;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import com.idiominc.wssdk.ais.WSNode;
import com.spartansoftware.ws.okapi.filters.mock.MockWSLocale;
import com.spartansoftware.ws.okapi.filters.mock.MockWSMarkupSegment;
import com.spartansoftware.ws.okapi.filters.mock.ResourceMockWSNode;

import net.sf.okapi.common.StringParameters;

public class TestMarkupSegmentMetadata extends XMLTestCase {
    static final String XML =
            "<filter metadataVersion=\"1\"><asset>/foo/bar.docx</asset><config><params>" +
            "<okapi><![CDATA[#v1\n" +
                "testKey=testValue]]></okapi>" +
            "<applySentenceBreaking>true</applySentenceBreaking>" +
            "</params></config></filter>";
    @Test
    public void testLoadXMLFromMarkup() throws Exception {
        DummyConfigData config = new DummyConfigData();
        assertEquals("", config.getParameters().getString("testKey")); // default value
        assertEquals(false, config.getApplySegmentation()); // default value
        MarkupSegmentMetadata<DummyConfigData> m = MarkupSegmentMetadata.fromSegment(new MockWSMarkupSegment(XML), config);
        assertNotNull(m);
        assertEquals("/foo/bar.docx", m.getAsset());
        assertNotNull(m.getConfig().getParameters());
        assertEquals("testValue", m.getConfig().getParameters().getString("testKey"));
        assertEquals(true, m.getConfig().getApplySegmentation());
    }

    static final String LEGACY_MARKUP = "/path/to/asset.html";

    @Test
    public void testLoadLegacyMarkup() throws Exception {
        DummyConfigData config = new DummyConfigData();
        assertEquals(false, config.getApplySegmentation()); // default value
        assertEquals("", config.getParameters().getString("testKey"));
        MarkupSegmentMetadata<DummyConfigData> m =
                MarkupSegmentMetadata.fromSegment(new MockWSMarkupSegment(LEGACY_MARKUP), config);
        assertNotNull(m);
        assertEquals("/path/to/asset.html", m.getAsset());
        // config should be unchanged from defaults
        assertEquals(false, config.getApplySegmentation());
        assertEquals("", config.getParameters().getString("testKey"));
    }


    static final String XML2 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<filter metadataVersion=\"1\"><asset>/test.properties</asset><config><params><okapi><![CDATA[#v1\n" +
                "testKey=testValue]]></okapi>" +
            "<applySentenceBreaking>true</applySentenceBreaking></params></config></filter>";

    @Test
    public void testWriteAndRoundtripXML() throws Exception {
        WSNode srcNode = new ResourceMockWSNode("/test.properties", StandardCharsets.UTF_8, MockWSLocale.ENGLISH);
        DummyConfigData config = new DummyConfigData();
        config.setApplySegmentation(true);
        StringParameters p = config.getParameters();
        p.setString("testKey", "testValue");
        config.setParameters(p);
        MarkupSegmentMetadata<DummyConfigData> meta = MarkupSegmentMetadata.fromAsset(srcNode, config);
        String xml = meta.toXML();
        assertXMLEqual(XML2, xml);

        DummyConfigData loaded = new DummyConfigData();
        MarkupSegmentMetadata<DummyConfigData> m =
                MarkupSegmentMetadata.fromSegment(new MockWSMarkupSegment(xml), loaded);
        assertNotNull(m);
        assertEquals("/test.properties", m.getAsset());
        assertEquals(true, loaded.getApplySegmentation());
        assertEquals("testValue", loaded.getParameters().getString("testKey"));
    }
}
