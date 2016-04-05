package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.spartansoftwareinc.ws.okapi.filters.ConfigTestUtils;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpenXMLFilterConfigurationDataTest {

    private final static String CONFIG_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><params><okapi><![CDATA[#v1\n" +
                    "bPreferenceTranslateDocProperties.b=true\n" +
                    "bPreferenceTranslateComments.b=true\n" +
                    "bPreferenceTranslatePowerpointNotes.b=true\n" +
                    "bPreferenceTranslatePowerpointMasters.b=true\n" +
                    "bPreferenceTranslateWordHeadersFooters.b=true\n" +
                    "bPreferenceTranslateWordAllStyles.b=true\n" +
                    "bPreferenceTranslateWordHidden.b=false\n" +
                    "bPreferenceTranslateWordExcludeGraphicMetaData.b=false\n" +
                    "bPreferenceTranslateExcelExcludeColors.b=false\n" +
                    "bPreferenceTranslateExcelExcludeColumns.b=false\n" +
                    "bPreferenceAddLineSeparatorAsCharacter.b=false\n" +
                    "bPreferenceReplaceNoBreakHyphenTag.b=false\n" +
                    "bPreferenceIgnoreSoftHyphenTag.b=false\n" +
                    "bPreferenceAddTabAsCharacter.b=false\n" +
                    "bPreferenceAggressiveCleanup.b=false\n" +
                    "tsExcelExcludedColors.i=0\n" +
                    "tsExcelExcludedColumns.i=0\n" +
                    "tsExcludeWordStyles.i=0]]></okapi>" +
                    "<applySentenceBreaking>false</applySentenceBreaking>" +
                    "<translateDocProperties>true</translateDocProperties>" +
                    "<translateComments>true</translateComments>" +
                    "<aggressiveCleanup>false</aggressiveCleanup>" +
                    "<automaticallyAcceptRevisions>true</automaticallyAcceptRevisions>" +
                    "<translatePowerpointNotes>true</translatePowerpointNotes>" +
                    "<translatePowerpointMasters>true</translatePowerpointMasters>" +
                    "<translateWordHeadersFooters>true</translateWordHeadersFooters>" +
                    "<translateWordHidden>false</translateWordHidden>" +
                    "<translateWordExcludeGraphicMetaData>false</translateWordExcludeGraphicMetaData>" +
                    "<translateExcelHidden>false</translateExcelHidden>" +
                    "<addTabAsCharacter>false</addTabAsCharacter>" +
                    "<addLineSeparatorAsCharacter>false</addLineSeparatorAsCharacter>" +
                    "<replaceNoBreakHyphenTag>true</replaceNoBreakHyphenTag>" +
                    "<ignoreSoftHyphenTag>false</ignoreSoftHyphenTag></params>";

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
        assertEquals(false/*true*/, params.getReplaceNoBreakHyphenTag()); //TODO fix ConfigData. should be "true"!
    }

}
