package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIRadioButton;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSMTAdapterConfigurationUI extends WSComponentConfigurationUI {
    private static final Logger LOG = LoggerFactory.getLogger(WSMTAdapterConfigurationUI.class);

    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    private static final String ERROR_MESSAGE = "Error: Please enter valid values for ";

    private static final String LABEL_AZURE_KEY = "Azure Key";
    private static final String LABEL_CATEGORY = "Category";
    private static final String LABEL_MATCH_SCORE = "MT Match Score";
    private static final String LABEL_INCLUDE_CODES = "Include Codes for MT";
    private static final String LABEL_LOCALE_MAP_AIS_PATH = "AIS Path for Locale Overrides";

    private static final String AZURE_KEY = "azureKey";
    private static final String CATEGORY = "category";
    private static final String MATCH_SCORE = "matchScore";
    private static final String MATCH_SCORE_MSHUB = "mshub";
    private static final String MATCH_SCORE_CUSTOM = "custom";
    private static final String MATCH_SCORE_CUSTOM_VALUE = "customValue";
    private static final String INCLUDE_CODES = "includeCodes";
    private static final String LOCALE_MAP_AIS_PATH = "localeMapAISPath";

    @Override
    public String render(WSContext wsContext, HttpServletRequest request, WSComponentConfigurationData config) {
        WSMTAdapterConfigurationData configData = config != null ?
                ((WSMTAdapterConfigurationData) config) : new WSMTAdapterConfigurationData();

        StringBuilder sb = new StringBuilder();
        String azureKey = configData.getAzureKey() == null ? "" : configData.getAzureKey();
        String category = configData.getCategory() == null ? "" : configData.getCategory();
        String aisPath = configData.getLocaleMapAISPath() == null ? "" : configData.getLocaleMapAISPath();
        int matchScore = configData.getMatchScore();

        final String error = (String)request.getAttribute(ERROR_MESSAGE_ATTRIBUTE);
        if (error != null) {
            sb.append("<p style=\"color: red;\">");
            sb.append(UIUtil.escapeHtml(error));
            sb.append("</p>");
        }
        UIRadioButton.Option defaultOption = new UIRadioButton.Option("Use MS Hub Value", MATCH_SCORE_MSHUB,
                                                !configData.useCustomScoring());
        UIRadioButton.Option customOption = new UIRadioButton.Option("Use Custom Value", MATCH_SCORE_CUSTOM,
                        configData.useCustomScoring(), getCustomValueHtml(MATCH_SCORE_CUSTOM_VALUE, 0, 100, matchScore));
        UITable table = new UITable()
                            .add(new UITextField(LABEL_AZURE_KEY, AZURE_KEY, azureKey).setSize(60))
                            .add(new UITextField(LABEL_CATEGORY, CATEGORY, category).setSize(60))
                            .add(new UICheckbox(LABEL_INCLUDE_CODES, INCLUDE_CODES, configData.getIncludeCodes()))
                            .add(new UIRadioButton(LABEL_MATCH_SCORE, MATCH_SCORE, defaultOption, customOption))
                            .add(new UITextField(LABEL_LOCALE_MAP_AIS_PATH, LOCALE_MAP_AIS_PATH, aisPath).setSize(60));
        sb.append(table.render());
        return sb.toString();
    }

    private String getCustomValueHtml(String inputName, int min, int max, int value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<input type=\"number\" min=\"");
        sb.append(min);
        sb.append("\" max=\"");
        sb.append(max);
        sb.append("\" name=\"");
        sb.append(inputName);
        sb.append("\" value=\"");
        sb.append(value);
        sb.append("\"/>");
        return sb.toString();
    }

    @Override
    public WSComponentConfigurationData save(WSContext wsContext, HttpServletRequest request,
                                             WSComponentConfigurationData config) {

        WSMTAdapterConfigurationData configData = config == null || !(config instanceof WSMTAdapterConfigurationData)
                ? new WSMTAdapterConfigurationData()
                : ((WSMTAdapterConfigurationData) config);

        final String azureKey = request.getParameter(AZURE_KEY);
        final String aisPath = request.getParameter(LOCALE_MAP_AIS_PATH).trim();

        String errors = null;
        boolean useCustomScoring = false;
        int matchScore = configData.getMatchScore();
        String scoreOptionValue = request.getParameter(MATCH_SCORE);
        if (MATCH_SCORE_CUSTOM.equals(scoreOptionValue)) {
            useCustomScoring = true;
            matchScore = getMatchScoreParameter(request);
        }
        else if (MATCH_SCORE_MSHUB.equals(scoreOptionValue)) {
            useCustomScoring = false;
        }
        else {
            errors = addError(LABEL_MATCH_SCORE, errors);
        }

        if (azureKey == null || azureKey.length() < 1) {
            errors = addError(LABEL_AZURE_KEY, errors);
        }

        if (matchScore < 0 || matchScore > 100) {
            errors = addError(LABEL_MATCH_SCORE, errors);
        }

        try {
            if (wsContext.getAisManager().getMetaDataNode(aisPath) == null) {
                errors = addError(LABEL_LOCALE_MAP_AIS_PATH, errors);
            }
        }
        catch (WSAisException e) {
            LOG.error("Error saving locale map ais path configuration", e);
            errors = addError(LABEL_LOCALE_MAP_AIS_PATH, errors);
        }

        if (errors != null) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE + errors);
            throw new IllegalArgumentException();
        }

        configData.setAzureKey(azureKey);
        configData.setCategory(request.getParameter(CATEGORY).trim());
        configData.setUseCustomScoring(useCustomScoring);
        configData.setIncludeCodes("on".equals(request.getParameter(INCLUDE_CODES)));
        configData.setLocaleMapAISPath(aisPath);
        if (useCustomScoring) {
            configData.setMatchScore(matchScore);
        }

        return configData;
    }

    private String addError(String field, String invalidFields) {
        return invalidFields == null ? field : invalidFields + ", " + field;
    }

    private int getMatchScoreParameter(HttpServletRequest request) {
        try {
            return Integer.valueOf(request.getParameter(MATCH_SCORE_CUSTOM_VALUE));
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
}
