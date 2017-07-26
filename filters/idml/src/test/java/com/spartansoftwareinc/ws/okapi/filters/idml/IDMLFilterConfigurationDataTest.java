package com.spartansoftwareinc.ws.okapi.filters.idml;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;
import net.sf.okapi.filters.idml.Parameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IDMLFilterConfigurationDataTest {

    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final static String CONFIG_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1" + LINE_SEPARATOR +
                    "maxAttributeSize.i=4194304" + LINE_SEPARATOR +
                    "untagXmlStructures.b=true" + LINE_SEPARATOR +
                    "extractNotes.b=false" + LINE_SEPARATOR +
                    "extractMasterSpreads.b=true" + LINE_SEPARATOR +
                    "extractHiddenLayers.b=false]]></okapi>" +
                    "<applySentenceBreaking>false</applySentenceBreaking>" +
                    "</params>";

    @Test
    public void testToXML() throws Exception {
        IDMLFilterConfigurationData data = new IDMLFilterConfigurationData();
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        IDMLFilterConfigurationData data = new IDMLFilterConfigurationData();
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        Parameters params = data.getParameters();
        assertEquals(true, params.getExtractMasterSpreads());
    }

}
