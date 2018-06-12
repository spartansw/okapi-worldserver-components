package com.spartansoftwareinc.ws.okapi.filters.markdown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIMultiValueInput;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

import net.sf.okapi.filters.markdown.Parameters;

public class MarkdownFilterConfigurationUI extends WSOkapiFilterUI<MarkdownFilterConfigurationData> {

    private static final String TRANSLATE_URLS_LABEL = "Translate URLs";
    private static final String TRANSLATE_URLS_NAME = "translateUrls";
    private static final String TRANSLATE_CODE_BLOCKS_LABEL = "Translate Code Blocks";
    private static final String TRANSLATE_CODE_BLOCKS_NAME = "translateCodeBlocks";
    private static final String TRANSLATE_HEADER_METADATA_LABEL = "Translate Header Metadata";
    private static final String TRANSLATE_HEADER_METADATA_NAME = "translateHeaderMetadata";
    private static final String TRANSLATE_IMAGE_ALT_TEXT_LABEL = "Translate Image Alt Text";
    private static final String TRANSLATE_IMAGE_ALT_TEXT_NAME = "translateImageAltText";

    @Override
    protected MarkdownFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof MarkdownFilterConfigurationData) ?
            (MarkdownFilterConfigurationData)config : new MarkdownFilterConfigurationData();
    }

    @Override
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                WSComponentConfigurationData config) {
        MarkdownFilterConfigurationData configData = getConfigurationData(config);

        UITable table = new UITable();     
        table.add(new UICheckbox(TRANSLATE_URLS_LABEL, TRANSLATE_URLS_NAME,
                configData.getParameters().getTranslateUrls()));
        table.add(new UICheckbox(TRANSLATE_CODE_BLOCKS_LABEL, TRANSLATE_CODE_BLOCKS_NAME,
                configData.getParameters().getTranslateCodeBlocks()));
        table.add(new UICheckbox(TRANSLATE_HEADER_METADATA_LABEL, TRANSLATE_HEADER_METADATA_NAME,
                configData.getParameters().getTranslateHeaderMetadata()));
        table.add(new UICheckbox(TRANSLATE_IMAGE_ALT_TEXT_LABEL, TRANSLATE_IMAGE_ALT_TEXT_NAME,
                configData.getParameters().getTranslateImageAltText()));
        return table;
    }

    @Override
    protected String validateAndSave(WSContext context, HttpServletRequest request, MarkdownFilterConfigurationData configData, String errors) {
        MarkdownFilterConfigurationData configurationData = getConfigurationData(configData);

        // No real error check performed at this point.
        configurationData.setTranslateUrls(UIUtil.getBoolean(request, TRANSLATE_URLS_NAME));
        configurationData.setTranslateCodeBlockse(UIUtil.getBoolean(request, TRANSLATE_CODE_BLOCKS_NAME));
        configurationData.setTranslateHeaderMetadata(UIUtil.getBoolean(request, TRANSLATE_HEADER_METADATA_NAME));
        configurationData.setTranslateImageAltText(UIUtil.getBoolean(request, TRANSLATE_IMAGE_ALT_TEXT_NAME));
        return errors;
    }

}
