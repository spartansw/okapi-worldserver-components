package com.spartansoftwareinc.ws.okapi.filters.json;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

import net.sf.okapi.filters.json.Parameters;

public class JSONFilterConfigurationDataTest {

    private final static String TERMINATOR = System.getProperty("line.separator");
    private final static String CONFIG_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1" + TERMINATOR +
                "extractIsolatedStrings.b=false" + TERMINATOR +
                "extractAllPairs.b=true" + TERMINATOR +
                "exceptions=^(^hello$)$" + TERMINATOR +
                "useKeyAsName.b=true" + TERMINATOR +
                "useFullKeyPath.b=false" + TERMINATOR +
                "useLeadingSlashOnKeyPath.b=true" + TERMINATOR +
                "escapeForwardSlashes.b=true" + TERMINATOR +
                "useCodeFinder.b=true" + TERMINATOR +
                "noteProducingKeys=" + TERMINATOR +
                "subfilter=" + TERMINATOR +
                "codeFinderRules.count.i=9" + TERMINATOR +
                "codeFinderRules.rule0=:[a-zA-Z0-9_]+" + TERMINATOR +
                "codeFinderRules.rule1=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]" + TERMINATOR +
                "codeFinderRules.rule2=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v" + TERMINATOR +
                "codeFinderRules.rule3=\\{\\d[^\\\\]*?\\}" + TERMINATOR +
                "codeFinderRules.rule4=<.+?>" + TERMINATOR +
                "codeFinderRules.rule5=%%[a-zA-Z]+%%" + TERMINATOR +
                "codeFinderRules.rule6=%[0-9]+\\$[a-zA-Z]+" + TERMINATOR +
                "codeFinderRules.rule7=%[a-zA-Z]+" + TERMINATOR +
                "codeFinderRules.rule8=&[a-zA-Z]+;" + TERMINATOR +
                "codeFinderRules.sample=" + TERMINATOR +
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
