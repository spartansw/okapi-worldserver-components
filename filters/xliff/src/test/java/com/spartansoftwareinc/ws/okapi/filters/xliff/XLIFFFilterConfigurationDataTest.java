package com.spartansoftwareinc.ws.okapi.filters.xliff;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;
import com.spartansoftwareinc.ws.okapi.filters.xliff.XLIFFFilterConfigurationData;

public class XLIFFFilterConfigurationDataTest {
    private final static String TERMINATOR = System.getProperty("line.separator");
    private final static String CONFIG_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1" + TERMINATOR +
            "useCustomParser.b=true" + TERMINATOR +
            "factoryClass=com.ctc.wstx.stax.WstxInputFactory" + TERMINATOR +
            "fallbackToID.b=false" + TERMINATOR +
            "escapeGT.b=false" + TERMINATOR +
            "addTargetLanguage.b=true" + TERMINATOR +
            "overrideTargetLanguage.b=false" + TERMINATOR +
            "outputSegmentationType.i=0" + TERMINATOR +
            "ignoreInputSegmentation.b=false" + TERMINATOR +
            "addAltTrans.b=false" + TERMINATOR +
            "addAltTransGMode.b=true" + TERMINATOR +
            "editAltTrans.b=false" + TERMINATOR +
            "includeExtensions.b=true" + TERMINATOR +
            "includeIts.b=true" + TERMINATOR +
            "balanceCodes.b=true" + TERMINATOR +
            "allowEmptyTargets.b=false" + TERMINATOR +
            "targetStateMode.i=0" + TERMINATOR +
            "targetStateValue=needs-translation" + TERMINATOR +
            "alwaysUseSegSource.b=false" + TERMINATOR +
            "quoteModeDefined.b=true" + TERMINATOR +
            "quoteMode.i=0" + TERMINATOR +
            "useSdlXliffWriter.b=false" + TERMINATOR +
            "preserveSpaceByDefault.b=false" + TERMINATOR +
            "useIwsXliffWriter.b=false" + TERMINATOR +
            "iwsBlockFinished.b=true" + TERMINATOR +
            "iwsTransStatusValue=finished" + TERMINATOR +
            "iwsTransTypeValue=manual_translation" + TERMINATOR +
            "iwsBlockLockStatus.b=false" + TERMINATOR +
            "iwsBlockTmScore.b=false" + TERMINATOR +
            "iwsBlockTmScoreValue=100.00" + TERMINATOR +
            "iwsBlockMultipleExact.b=false" + TERMINATOR +
            "inlineCdata.b=false" + TERMINATOR +
            "skipNoMrkSegSource.b=false" + TERMINATOR +
            "useCodeFinder.b=false" + TERMINATOR +
            "codeFinderRules.count.i=1" + TERMINATOR +
            "codeFinderRules.rule0=</?([A-Z0-9a-z]*)\\b[^>]*>" + TERMINATOR +
            "codeFinderRules.sample=&name; <tag></at><tag/> <tag attr='val'> </tag=\"val\">" + TERMINATOR +
            "codeFinderRules.useAllRulesWhenTesting.b=true"+
            "]]></okapi><applySentenceBreaking>false</applySentenceBreaking></params>";

    @Test
    public void testToXML() throws Exception {
        XLIFFFilterConfigurationData data = new XLIFFFilterConfigurationData();
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        XLIFFFilterConfigurationData data = new XLIFFFilterConfigurationData();
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        // TODO asserts
    }
}
