package com.spartansoftwareinc.ws.okapi.filters.yaml;

import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;
import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;

public class YAMLFilterConfigurationDataTest {

    private final static String CONFIG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1\n" +
            "extractIsolatedStrings.b=false\n" +
            "extractAllPairs.b=true\n" +
            "exceptions=\n" +
            "useKeyAsName.b=true\n" +
            "useFullKeyPath.b=true\n" +
            "useCodeFinder.b=true\n" +
            "escapeNonAscii.b=false\n" +
            "wrap.b=true\n" +
            "codeFinderRules.count.i=3\n" +
            "codeFinderRules.rule0=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpnYyBbHhSMmAZ]\n" +
            "codeFinderRules.rule1=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v\n" +
            "codeFinderRules.rule2=\\{\\{\\w.*?\\}\\}\n" +
            "codeFinderRules.sample=%s, %d, {1}, \\n, \\r, \\t, {{var}} etc.\n" +
            "codeFinderRules.useAllRulesWhenTesting.b=true]]></okapi><excludedKeys><key>hello</key></excludedKeys></params>";
    @Test
    public void testToXML() throws Exception {
        YAMLFilterConfigurationData data = new YAMLFilterConfigurationData();
        data.setExcludedKeys(Collections.singleton("hello"));
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        YAMLFilterConfigurationData data = new YAMLFilterConfigurationData();
        assertEquals(Collections.emptySet(), data.getExcludedKeys());
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        assertEquals(Collections.singleton("hello"), data.getExcludedKeys());
    }

}
