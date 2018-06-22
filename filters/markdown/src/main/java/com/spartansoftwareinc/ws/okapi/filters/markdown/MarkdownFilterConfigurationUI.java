package com.spartansoftwareinc.ws.okapi.filters.markdown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIMultiValueInput;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

public class MarkdownFilterConfigurationUI extends WSOkapiFilterUI<MarkdownFilterConfigurationData> {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownFilterConfigurationUI.class);
	    
    private static final String TRANSLATE_URLS_LABEL = "Translate URLs";
    private static final String TRANSLATE_URLS_NAME = "translateUrls";
    private static final String TRANSLATE_CODE_BLOCKS_LABEL = "Translate Code Blocks";
    private static final String TRANSLATE_CODE_BLOCKS_NAME = "translateCodeBlocks";
    private static final String TRANSLATE_HEADER_METADATA_LABEL = "Translate Header Metadata (Yaml Values)";
    private static final String TRANSLATE_HEADER_METADATA_NAME = "translateHeaderMetadata";
    private static final String TRANSLATE_IMAGE_ALT_TEXT_LABEL = "Translate Image Alt Text";
    private static final String TRANSLATE_IMAGE_ALT_TEXT_NAME = "translateImageAltText";
    private static final String SUBFILTER_ID_LABEL = "HTML Subfilter ID (Leave blank to use the Markdown's default)";
    private static final String SUBFILTER_ID_NAME = "subfilterId";
    private static final String USE_CODE_FINDER_LABEL = "Use Inline Code Finder";
    private static final String USE_CODE_FINDER_NAME = "useCodeFinder";
    private static final String CODE_FINDER_RULES_LABEL = "Inline Code Finder Rules (Regular Expressions)";
    private static final String CODE_FINDER_RULES_NAME = "codeFinderRules";

   
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
                configData.getTranslateUrls()));
        table.add(new UICheckbox(TRANSLATE_CODE_BLOCKS_LABEL, TRANSLATE_CODE_BLOCKS_NAME,
                configData.getTranslateCodeBlocks()));
        table.add(new UICheckbox(TRANSLATE_HEADER_METADATA_LABEL, TRANSLATE_HEADER_METADATA_NAME,
                configData.getTranslateHeaderMetadata()));
        table.add(new UICheckbox(TRANSLATE_IMAGE_ALT_TEXT_LABEL, TRANSLATE_IMAGE_ALT_TEXT_NAME,
                configData.getTranslateImageAltText()));
        table.add(new UITextField(SUBFILTER_ID_LABEL, SUBFILTER_ID_NAME, 
        	configData.getHtmlSubfilter()));
        table.add(new UICheckbox(USE_CODE_FINDER_LABEL, USE_CODE_FINDER_NAME,
        	configData.getUseCodeFinder()));
        table.add(new UIMultiValueInput(CODE_FINDER_RULES_LABEL, CODE_FINDER_RULES_NAME,
        	configData.getCodeFinderRules(), configData.getCodeFinderRules()));        
        for (String rule: configData.getCodeFinderRules()) { //TODO: Remove me
            LOG.warn("rule: {}", rule);
        }
        return table;
    }

    @Override
    protected String validateAndSave(WSContext context, HttpServletRequest request, MarkdownFilterConfigurationData configData, String errors) {
        MarkdownFilterConfigurationData configurationData = getConfigurationData(configData);
        
        
        
        configurationData.setTranslateUrls(UIUtil.getBoolean(request, TRANSLATE_URLS_NAME));
        configurationData.setTranslateCodeBlockse(UIUtil.getBoolean(request, TRANSLATE_CODE_BLOCKS_NAME));
        configurationData.setTranslateHeaderMetadata(UIUtil.getBoolean(request, TRANSLATE_HEADER_METADATA_NAME));
        configurationData.setTranslateImageAltText(UIUtil.getBoolean(request, TRANSLATE_IMAGE_ALT_TEXT_NAME));
        String subfilterId = request.getParameter(SUBFILTER_ID_NAME);
        if (subfilterId != null && subfilterId.isEmpty()) {
            subfilterId = null;
        } // else { ... } TODO: Check if the specified id is valid and set errors if not.
        configurationData.setHtmlSubfilter(subfilterId);
        boolean useCodeFinder = UIUtil.getBoolean(request, USE_CODE_FINDER_NAME);
        List<String> rules = UIUtil.getOptionValues(request, CODE_FINDER_RULES_NAME + "_keys_res"); // See /base/src/main/resources/keyConfiguration.html.template 
        if (useCodeFinder && rules.isEmpty() ) { // No rules specified when turning on the code finder.
            errors = addError(USE_CODE_FINDER_NAME, errors);
            errors = addError(CODE_FINDER_RULES_NAME, errors);
            LOG.warn("Empty rules when using code finder. Returning \"{}\"", errors); //TODO: Remove me
            return errors;
        }
        for (String rule: rules) {
            LOG.warn("rule: {}", rule);
        }
        configurationData.setUseCodeFinder(useCodeFinder);
        configurationData.setCodeFinderRules(rules);
        LOG.warn("Returning errors=\"{}\"", errors); //TODO: Remove me
        return errors;
    }

}
