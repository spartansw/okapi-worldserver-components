package com.spartansoftwareinc.ws.okapi.filters.json;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

import net.sf.okapi.filters.json.Parameters;

public class JSONFilterConfigurationDataTest {

    private final static String CONFIG_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1\n" +
                "extractIsolatedStrings.b=false\n" +
                "extractAllPairs.b=true\n" +
                "exceptions=^(^hello$)$\n" +
                "useKeyAsName.b=true\n" +
                "useFullKeyPath.b=false\n" +
                "useCodeFinder.b=true\n" +
                "subfilter=\n" +
                "codeFinderRules.count.i=9\n" +
                "codeFinderRules.rule0=:[a-zA-Z0-9_]+\n" +
                "codeFinderRules.rule1=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]\n" +
                "codeFinderRules.rule2=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v\n" +
                "codeFinderRules.rule3=\\{\\d[^\\\\]*?\\}\n" +
                "codeFinderRules.rule4=<.+?>\n" +
                "codeFinderRules.rule5=%%[a-zA-Z]+%%\n" +
                "codeFinderRules.rule6=%[0-9]+\\$[a-zA-Z]+\n" +
                "codeFinderRules.rule7=%[a-zA-Z]+\n" +
                "codeFinderRules.rule8=&[a-zA-Z]+;\n" +
                "codeFinderRules.sample=\n" +
                "codeFinderRules.useAllRulesWhenTesting.b=true]]></okapi>" +
                "<applySentenceBreaking>false</applySentenceBreaking>" +
                "</params>";

    @Test
    public void testToXML() throws Exception {
        JSONFilterConfigurationData data = new JSONFilterConfigurationData();
        data.setExcludedKeys(Collections.singletonList("hello"));
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        JSONFilterConfigurationData data = new JSONFilterConfigurationData();
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        Parameters params = data.getParameters();
        assertEquals("^(^hello$)$", params.getExceptions());
    }

}
