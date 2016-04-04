package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

import javax.servlet.http.HttpServletRequest;

public class OpenXMLFilterConfigurationUI extends WSOkapiFilterUI<OpenXMLFilterConfigurationData> {

    @Override
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                              WSComponentConfigurationData config) {
        OpenXMLFilterConfigurationData openXMLConfig = getConfigurationData(config);

        UITable table = new UITable();
        table.add(new UICheckbox("Translate Document Properties", "translateDocProperties", openXMLConfig.isTranslateDocProperties())); //TODO re-check descr
        table.add(new UICheckbox("Translate Comments", "translateComments", openXMLConfig.isTranslateComments())); //TODO what's "input name" for? it's also very different in PO and JSON
        table.add(new UICheckbox("Cleanup Aggressively", "aggressiveCleanup", openXMLConfig.isAggressiveCleanup()));
        table.add(new UICheckbox("Automatically Accept Revisions", "automaticallyAcceptRevisions", openXMLConfig.isAutomaticallyAcceptRevisions()));
        table.add(new UICheckbox("Translate Notes in Powerpoint", "translatePowerpointNotes", openXMLConfig.isTranslatePowerpointNotes()));
        table.add(new UICheckbox("Translate Masters in Powerpoint", "translatePowerpointMasters", openXMLConfig.isTranslatePowerpointMasters()));
        table.add(new UICheckbox("Translate Headers and Footers in Word", "translateWordHeadersFooters", openXMLConfig.isTranslateWordHeadersFooters()));
        table.add(new UICheckbox("Translate Hidden Text in Word", "translateWordHidden", openXMLConfig.isTranslateWordHidden()));
        table.add(new UICheckbox("Exclude Graphic Meta Data in Word", "translateWordExcludeGraphicMetaData", openXMLConfig.isTranslateWordExcludeGraphicMetaData())); //TODO re-check descr
        table.add(new UICheckbox("Translate Hidden Text in Excel", "translateExcelHidden", openXMLConfig.isTranslateExcelHidden()));
        table.add(new UICheckbox("Add Tab As Character", "addTabAsCharacter", openXMLConfig.isAddTabAsCharacter())); //TODO re-check descr
        table.add(new UICheckbox("Add Line Separator As Character", "addLineSeparatorAsCharacter", openXMLConfig.isAddLineSeparatorAsCharacter())); //TODO re-check descr
        table.add(new UICheckbox("Replace No Break Hyphen with Regular Hyphen", "replaceNoBreakHyphenTag", openXMLConfig.isReplaceNoBreakHyphenTag()));
        table.add(new UICheckbox("Ignore Soft Hyphen", "ignoreSoftHyphenTag", openXMLConfig.isIgnoreSoftHyphenTag()));
        return table;
    }

    @Override
    protected OpenXMLFilterConfigurationData updateConfiguration(WSContext context, HttpServletRequest request,
                                                            WSComponentConfigurationData config) {
        OpenXMLFilterConfigurationData openXMLConfig = getConfigurationData(config);
        openXMLConfig.setTranslateDocProperties(UIUtil.getBoolean(request, "translateDocProperties"));
        openXMLConfig.setTranslateComments(UIUtil.getBoolean(request, "translateComments"));
        openXMLConfig.setAggressiveCleanup(UIUtil.getBoolean(request, "aggressiveCleanup"));
        openXMLConfig.setAutomaticallyAcceptRevisions(UIUtil.getBoolean(request, "automaticallyAcceptRevisions"));
        openXMLConfig.setTranslatePowerpointNotes(UIUtil.getBoolean(request, "translatePowerpointNotes"));
        openXMLConfig.setTranslatePowerpointMasters(UIUtil.getBoolean(request, "translatePowerpointMasters"));
        openXMLConfig.setTranslateWordHeadersFooters(UIUtil.getBoolean(request, "translateWordHeadersFooters"));
        openXMLConfig.setTranslateWordHidden(UIUtil.getBoolean(request, "translateWordHidden"));
        openXMLConfig.setTranslateWordExcludeGraphicMetaData(UIUtil.getBoolean(request, "translateWordExcludeGraphicMetaData"));
        openXMLConfig.setTranslateExcelHidden(UIUtil.getBoolean(request, "translateExcelHidden"));
        openXMLConfig.setAddTabAsCharacter(UIUtil.getBoolean(request, "addTabAsCharacter"));
        openXMLConfig.setAddLineSeparatorAsCharacter(UIUtil.getBoolean(request, "addLineSeparatorAsCharacter"));
        openXMLConfig.setReplaceNoBreakHyphenTag(UIUtil.getBoolean(request, "replaceNoBreakHyphenTag"));
        openXMLConfig.setIgnoreSoftHyphenTag(UIUtil.getBoolean(request, "ignoreSoftHyphenTag"));
        return openXMLConfig;
    }

    @Override
    protected OpenXMLFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof OpenXMLFilterConfigurationData) ?
                (OpenXMLFilterConfigurationData)config : new OpenXMLFilterConfigurationData();
    }
}
