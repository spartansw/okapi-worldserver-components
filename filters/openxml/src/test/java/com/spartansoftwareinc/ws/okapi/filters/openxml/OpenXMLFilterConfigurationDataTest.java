package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpenXMLFilterConfigurationDataTest {

    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final static String CONFIG_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1" + LINE_SEPARATOR +
                    "bPreferenceTranslateDocProperties.b=true" + LINE_SEPARATOR +
                    "bPreferenceTranslateComments.b=true" + LINE_SEPARATOR +
                    "bPreferenceTranslatePowerpointNotes.b=true" + LINE_SEPARATOR +
                    "bPreferenceTranslatePowerpointMasters.b=true" + LINE_SEPARATOR +
                    "bPreferenceIgnorePlaceholdersInPowerpointMasters.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateWordHeadersFooters.b=true" + LINE_SEPARATOR +
                    "bPreferenceTranslateWordHidden.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateWordExcludeGraphicMetaData.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateExcelExcludeColors.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateExcelExcludeColumns.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateExcelSheetNames.b=false" + LINE_SEPARATOR +
                    "bPreferenceAddLineSeparatorAsCharacter.b=false" + LINE_SEPARATOR +
                    "sPreferenceLineSeparatorReplacement=$0a$" + LINE_SEPARATOR +
                    "bPreferenceReplaceNoBreakHyphenTag.b=true" + LINE_SEPARATOR +
                    "bPreferenceIgnoreSoftHyphenTag.b=false" + LINE_SEPARATOR +
                    "bPreferenceAddTabAsCharacter.b=false" + LINE_SEPARATOR +
                    "bPreferenceAggressiveCleanup.b=false" + LINE_SEPARATOR +
                    "bPreferenceAutomaticallyAcceptRevisions.b=true" + LINE_SEPARATOR +
                    "bPreferencePowerpointIncludedSlideNumbersOnly.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateExcelDiagramData.b=false" + LINE_SEPARATOR +
                    "bPreferenceTranslateExcelDrawings.b=false" + LINE_SEPARATOR +
                    "tsComplexFieldDefinitionsToExtract.i=1" + LINE_SEPARATOR +
                    "cfd0=HYPERLINK" + LINE_SEPARATOR +
                    "tsExcelExcludedColors.i=0" + LINE_SEPARATOR +
                    "tsExcelExcludedColumns.i=0" + LINE_SEPARATOR +
                    "tsExcludeWordStyles.i=0" + LINE_SEPARATOR +
                    "tsPowerpointIncludedSlideNumbers.i=0]]></okapi>" +
                    "<applySentenceBreaking>false</applySentenceBreaking>" +
                    "</params>";

    @Test
    public void testToXML() throws Exception {
        OpenXMLFilterConfigurationData data = new OpenXMLFilterConfigurationData();
        data.setReplaceNoBreakHyphenTag(true);
        assertEquals(CONFIG_XML, ConfigTestUtils.toXML(data));
    }

    @Test
    public void testFromXML() throws Exception {
        OpenXMLFilterConfigurationData data = new OpenXMLFilterConfigurationData();
        ConfigTestUtils.fromXML(data, CONFIG_XML);
        ConditionalParameters params = data.getParameters();
        assertEquals(true, params.getReplaceNoBreakHyphenTag());
    }

}
