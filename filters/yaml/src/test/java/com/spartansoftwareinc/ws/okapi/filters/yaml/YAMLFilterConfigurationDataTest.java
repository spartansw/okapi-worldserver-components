package com.spartansoftwareinc.ws.okapi.filters.yaml;

import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;
import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

public class YAMLFilterConfigurationDataTest {
    private final static String TERMINATOR = System.getProperty("line.separator");
    private final static String CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1" + TERMINATOR +
            "extractIsolatedStrings.b=false" + TERMINATOR +
            "extractAllPairs.b=true" + TERMINATOR +
            "exceptions=" + TERMINATOR +
            "useKeyAsName.b=true" + TERMINATOR +
            "useFullKeyPath.b=true" + TERMINATOR +
            "useCodeFinder.b=true" + TERMINATOR +
            "subFilterProcessLiteralAsBlock.b=false" + TERMINATOR +
            "escapeNonAscii.b=false" + TERMINATOR +
            "wrap.b=true" + TERMINATOR +
            "codeFinderRules.count.i=3" + TERMINATOR +
            "codeFinderRules.rule0=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpnYyBbHhSMmAZ]" + TERMINATOR +
            "codeFinderRules.rule1=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v" + TERMINATOR +
            "codeFinderRules.rule2=\\{\\{\\w.*?\\}\\}" + TERMINATOR +
            "codeFinderRules.sample=%s, %d, {1}, \\n, \\r, \\t, {{var}} etc." + TERMINATOR +
            "codeFinderRules.useAllRulesWhenTesting.b=true]]></okapi>" +
            "<applySentenceBreaking>true</applySentenceBreaking>" +
            "<excludedKeys><key>hello</key></excludedKeys></params>";
    @Test
    public void testToXML() throws Exception {
        YAMLFilterConfigurationData data = new YAMLFilterConfigurationData();
        data.setApplySegmentation(true);
        data.setExcludedKeys(Collections.singleton("hello"));
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        YAMLFilterConfigurationData data = new YAMLFilterConfigurationData();
        assertFalse(data.getApplySegmentation());
        assertEquals(Collections.emptySet(), data.getExcludedKeys());
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        assertEquals(Collections.singleton("hello"), data.getExcludedKeys());
        assertTrue(data.getApplySegmentation());
    }

}
