package com.spartansoftwareinc.ws.okapi.filters.xliff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;
import com.spartansoftwareinc.ws.okapi.filters.xliff.XLIFFFilterConfigurationData;

import net.sf.okapi.filters.xliff.Parameters;

public class XLIFFFilterConfigurationDataTest {

    @Test
    public void testChangeIncludeItsRoundTrip() throws Exception {
        XLIFFFilterConfigurationData data = new XLIFFFilterConfigurationData();
        assertTrue(data.getParameters().getIncludeIts());
        String defaultXml = ConfigTestUtils.toXML(data);
        Parameters params = data.getParameters();
        params.setIncludeIts(false);
        data.setParameters(params);
        assertNotEquals(defaultXml, ConfigTestUtils.toXML(data));
        String changedXml = defaultXml.replace("includeIts.b=true", "includeIts.b=false");
        assertEquals(changedXml, ConfigTestUtils.toXML(data));
    }
}
