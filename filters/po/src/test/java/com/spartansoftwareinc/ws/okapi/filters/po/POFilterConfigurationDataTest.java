package com.spartansoftwareinc.ws.okapi.filters.po;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

public class POFilterConfigurationDataTest {
    private final static String TERMINATOR = System.getProperty("line.separator");
    private final static String CONFIG_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1" + TERMINATOR +
                    "bilingualMode.b=true" + TERMINATOR +
                    "makeID.b=true" + TERMINATOR +
                    "protectApproved.b=false" + TERMINATOR +
                    "useCodeFinder.b=true" + TERMINATOR +
                    "allowEmptyOutputTarget.b=true" + TERMINATOR +
                    "codeFinderRules.count.i=6" + TERMINATOR +
                    "codeFinderRules.rule0=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]" + TERMINATOR +
                    "codeFinderRules.rule1=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v" + TERMINATOR +
                    "codeFinderRules.rule2=\\{\\d[^\\\\]*?\\}" + TERMINATOR +
                    "codeFinderRules.rule3=<.+?>" + TERMINATOR +
                    "codeFinderRules.rule4=\\[\\[.+?\\]\\]" + TERMINATOR +
                    "codeFinderRules.rule5=\\[.+?\\]" + TERMINATOR +
                    "codeFinderRules.sample=%s, %d, {1}, \\n, \\r, \\t, etc." + TERMINATOR +
                    "codeFinderRules.useAllRulesWhenTesting.b=true]]></okapi>" +
                    "<applySentenceBreaking>false</applySentenceBreaking>" +
                    "<copyTargetToPO>true</copyTargetToPO>" +
                    "</params>";

    @Test
    public void testToXML() throws Exception {
        POFilterConfigurationData data = new POFilterConfigurationData();
        data.setCopyToPO(true);
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        POFilterConfigurationData data = new POFilterConfigurationData();
        assertEquals(false, data.getCopyToPO());
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        assertEquals(true, data.getCopyToPO());
    }
}
