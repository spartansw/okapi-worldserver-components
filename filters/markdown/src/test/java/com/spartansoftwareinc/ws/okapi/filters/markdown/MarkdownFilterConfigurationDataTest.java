package com.spartansoftwareinc.ws.okapi.filters.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

public class MarkdownFilterConfigurationDataTest {

    @Test
    public void testTranslateUrlsRoundTrip() throws Exception {
        MarkdownFilterConfigurationData data = new MarkdownFilterConfigurationData();
        String defaultXml = ConfigTestUtils.toXML(data);
        data.setTranslateUrls(true);
        assertNotEquals(defaultXml, ConfigTestUtils.toXML(data));
        String changedXml = defaultXml.replace("translateUrls.b=false", "translateUrls.b=true");
        assertEquals(changedXml, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testExtraWSParams() throws Exception {
        MarkdownFilterConfigurationData data = new MarkdownFilterConfigurationData();
        String testDirPath = "/var/ws/asi/okapi/configs";
        data.setFilterConfigDirPath(testDirPath);
        String xml = ConfigTestUtils.toXML(data);

        MarkdownFilterConfigurationData data2 = new MarkdownFilterConfigurationData();
        assertNull(data2.getFilterConfigDirPath());
        ConfigTestUtils.fromXML(data2, xml);
        assertEquals(testDirPath, data2.getFilterConfigDirPath());
    }

}
