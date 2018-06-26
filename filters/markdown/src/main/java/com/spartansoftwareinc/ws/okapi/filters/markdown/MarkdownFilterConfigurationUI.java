package com.spartansoftwareinc.ws.okapi.filters.markdown;


import java.util.List;

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
    private static final String URL_TO_TRANSLATE_PATTERN_LABEL = "Pattern to Match Translatable URLs";
    private static final String URL_TO_TRANSLATE_PATTERN_NAME = "urlToTranslatePattern";    
    private static final String TRANSLATE_CODE_BLOCKS_LABEL = "Translate Code Blocks";
    private static final String TRANSLATE_CODE_BLOCKS_NAME = "translateCodeBlocks";
    private static final String TRANSLATE_HEADER_METADATA_LABEL = "Translate Header Metadata (YAML Values)";
    private static final String TRANSLATE_HEADER_METADATA_NAME = "translateHeaderMetadata";
    private static final String TRANSLATE_IMAGE_ALT_TEXT_LABEL = "Translate Image Alt Text";
    private static final String TRANSLATE_IMAGE_ALT_TEXT_NAME = "translateImageAltText";
    private static final String SUBFILTER_ID_LABEL = "HTML Subfilter ID (Leave blank for default)";
    private static final String SUBFILTER_ID_UNAVAILABLE_LABEL = "HTML Subfilter ID *NONE AVAILABLE*";
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
        configData.initializeFilterConfigDirPath(context);
        LOG.warn("After calling initializeConfigDir(), configData.getFilterConfigDirPath()={}", configData.getFilterConfigDirPath());
        boolean okToUseCustomSubfilter = configData.getFilterConfigDirPath()!=null;
        LOG.warn("okToUseCustomSubfilter={}", okToUseCustomSubfilter);

        
        UITable table = new UITable();     
        table.add(new UICheckbox(TRANSLATE_URLS_LABEL, TRANSLATE_URLS_NAME,
                configData.getTranslateUrls()));
        table.add(new UITextField(URL_TO_TRANSLATE_PATTERN_LABEL, URL_TO_TRANSLATE_PATTERN_NAME,
        	configData.getUrlToTranslatePattern()));
        table.add(new UICheckbox(TRANSLATE_CODE_BLOCKS_LABEL, TRANSLATE_CODE_BLOCKS_NAME,
                configData.getTranslateCodeBlocks()));
        table.add(new UICheckbox(TRANSLATE_HEADER_METADATA_LABEL, TRANSLATE_HEADER_METADATA_NAME,
                configData.getTranslateHeaderMetadata()));
        table.add(new UICheckbox(TRANSLATE_IMAGE_ALT_TEXT_LABEL, TRANSLATE_IMAGE_ALT_TEXT_NAME,
                configData.getTranslateImageAltText()));
        if (okToUseCustomSubfilter) {
            table.add(new UITextField(SUBFILTER_ID_LABEL, SUBFILTER_ID_NAME, 
        	configData.getHtmlSubfilter()));
        } else {
            table.add(new UITextField(SUBFILTER_ID_UNAVAILABLE_LABEL, SUBFILTER_ID_NAME, ""));
        }
        table.add(new UICheckbox(USE_CODE_FINDER_LABEL, USE_CODE_FINDER_NAME,
        	configData.getUseCodeFinder()));
        table.add(new UIMultiValueInput(CODE_FINDER_RULES_LABEL, CODE_FINDER_RULES_NAME,
        	configData.getCodeFinderRules(), configData.getCodeFinderRules()));        

        return table;
    }

    @Override
    protected String validateAndSave(WSContext context, HttpServletRequest request, MarkdownFilterConfigurationData configData, String errors) {
        MarkdownFilterConfigurationData configurationData = getConfigurationData(configData);
        if (configData.getFilterConfigDirPath()==null) { // Need to invent a way to pass a hidden variable from buildConfigurationTable to avoid this.
            LOG.warn("validateAndSave(...): filterConfigDirPath was null; calling initializeFilterConfigDirPath(context)");
            configData.initializeFilterConfigDirPath(context);            
        }
        
        boolean translateUrls = UIUtil.getBoolean(request, TRANSLATE_URLS_NAME);
        configurationData.setTranslateUrls(translateUrls);
        
        String urlToTranslatePattern = request.getParameter(URL_TO_TRANSLATE_PATTERN_NAME);
        if (urlToTranslatePattern != null && urlToTranslatePattern.isEmpty()) {
            urlToTranslatePattern = null;
        }
        if (translateUrls && urlToTranslatePattern == null) {
            configurationData.setUrlToTranslatePattern(".+");// Force the catch-all pattern. We do this because some users might not know regex well.
            errors = addError(urlToTranslatePattern, errors);
        } else {
            configurationData.setUrlToTranslatePattern(urlToTranslatePattern);
        }
        
        configurationData.setTranslateCodeBlockse(UIUtil.getBoolean(request, TRANSLATE_CODE_BLOCKS_NAME));
        configurationData.setTranslateHeaderMetadata(UIUtil.getBoolean(request, TRANSLATE_HEADER_METADATA_NAME));
        configurationData.setTranslateImageAltText(UIUtil.getBoolean(request, TRANSLATE_IMAGE_ALT_TEXT_NAME));
        
        String subfilterId = request.getParameter(SUBFILTER_ID_NAME);
        if (subfilterId != null && subfilterId.isEmpty()) {
            subfilterId = null;
        } else {
            ; //  TODO: Check if the specified id is valid and set errors if not.
        }
        configurationData.setHtmlSubfilter(subfilterId);
        
        boolean useCodeFinder = UIUtil.getBoolean(request, USE_CODE_FINDER_NAME);
        configurationData.setUseCodeFinder(useCodeFinder);

        List<String> rules = UIUtil.getOptionValues(request, CODE_FINDER_RULES_NAME + "_keys_res"); // See /base/src/main/resources/keyConfiguration.html.template 
        if (useCodeFinder && rules.isEmpty() ) { // No rules specified when turning on the code finder.
            errors = addError(USE_CODE_FINDER_NAME, errors);
            errors = addError(CODE_FINDER_RULES_NAME, errors);
            return errors;
        } else {
            configurationData.setCodeFinderRules(rules);
        }
                
        return errors;
    }

}
