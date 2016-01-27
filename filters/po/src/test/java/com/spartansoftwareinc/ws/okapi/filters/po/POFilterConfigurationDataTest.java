package com.spartansoftwareinc.ws.okapi.filters.po;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

public class POFilterConfigurationDataTest {
    private final static String CONFIG_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1\n" +
                    "bilingualMode.b=true\n" +
                    "makeID.b=true\n" +
                    "protectApproved.b=false\n" +
                    "useCodeFinder.b=true\n" +
                    "allowEmptyOutputTarget.b=true\n" +
                    "codeFinderRules.count.i=6\n" +
                    "codeFinderRules.rule0=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]\n" +
                    "codeFinderRules.rule1=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v\n" +
                    "codeFinderRules.rule2=\\{\\d[^\\\\]*?\\}\n" +
                    "codeFinderRules.rule3=<.+?>\n" +
                    "codeFinderRules.rule4=\\[\\[.+?\\]\\]\n" +
                    "codeFinderRules.rule5=\\[.+?\\]\n" +
                    "codeFinderRules.sample=%s, %d, {1}, \\n, \\r, \\t, etc.\n" +
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
