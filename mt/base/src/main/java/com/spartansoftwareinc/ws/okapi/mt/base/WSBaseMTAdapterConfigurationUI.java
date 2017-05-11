package com.spartansoftwareinc.ws.okapi.mt.base;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIElement;
import com.spartansoftwareinc.ws.okapi.base.ui.UIRadioButton;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public abstract class WSBaseMTAdapterConfigurationUI extends WSComponentConfigurationUI {

    private static final Logger LOG = LoggerFactory.getLogger(WSBaseMTAdapterConfigurationUI.class);

    protected static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    protected static final String ERROR_MESSAGE = "Error: Please enter valid values for ";

    protected static final String LABEL_MATCH_SCORE = "MT Match Score";
    protected static final String LABEL_INCLUDE_CODES = "Include Codes for MT";
    protected static final String LABEL_LOCALE_MAP_AIS_PATH = "AIS Path for Locale Overrides";

    protected static final String MATCH_SCORE = "matchScore";
    protected static final String MATCH_SCORE_TRANSLATION_SERVICE = "translationService";
    protected static final String MATCH_SCORE_CUSTOM = "custom";
    protected static final String MATCH_SCORE_CUSTOM_VALUE = "customValue";
    protected static final String INCLUDE_CODES = "includeCodes";
    protected static final String LOCALE_MAP_AIS_PATH = "localeMapAISPath";

    @Override
    public String render(WSContext wsContext, HttpServletRequest request, WSComponentConfigurationData configurationData) {
        StringBuilder sb = new StringBuilder();

        final String error = (String) request.getAttribute(ERROR_MESSAGE_ATTRIBUTE);
        if (error != null) {
            sb.append("<p style=\"color: red;\">");
            sb.append(UIUtil.escapeHtml(error));
            sb.append("</p>");
        }

        WSBaseMTAdapterConfigurationData configData = getConfigurationData(configurationData);

        UITable table = new UITable().addAll(getUIElements(configData));
        sb.append(table.render());

        return sb.toString();
    }

    protected abstract WSBaseMTAdapterConfigurationData getConfigurationData(WSComponentConfigurationData configurationData);

    protected List<UIElement> getUIElements(WSBaseMTAdapterConfigurationData configurationData) {
        List<UIElement> uiElements = new ArrayList<>();

        String aisPath = configurationData.getLocaleMapAISPath() == null ? "" : configurationData.getLocaleMapAISPath();
        int matchScore = configurationData.getMatchScore();

        UIRadioButton.Option defaultOption = new UIRadioButton.Option("Use Translation Service Value", MATCH_SCORE_TRANSLATION_SERVICE,
                !configurationData.useCustomScoring());
        UIRadioButton.Option customOption = new UIRadioButton.Option("Use Custom Value", MATCH_SCORE_CUSTOM,
                configurationData.useCustomScoring(), getCustomValueHtml(MATCH_SCORE_CUSTOM_VALUE, 0, 100, matchScore));

        uiElements.add(new UICheckbox(LABEL_INCLUDE_CODES, INCLUDE_CODES, configurationData.getIncludeCodes()));
        uiElements.add(new UIRadioButton(LABEL_MATCH_SCORE, MATCH_SCORE, defaultOption, customOption));
        uiElements.add(new UITextField(LABEL_LOCALE_MAP_AIS_PATH, LOCALE_MAP_AIS_PATH, aisPath).setSize(60));

        return uiElements;
    }

    protected String getCustomValueHtml(String inputName, int min, int max, int value) {
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
    public WSComponentConfigurationData save(WSContext wsContext, HttpServletRequest request, WSComponentConfigurationData configurationData) {

        WSBaseMTAdapterConfigurationData configData = getConfigurationData(configurationData);

        String errors = null;

        errors = validateAndSave(wsContext, request, configData, errors);

        if (null != errors) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE + errors);
            throw new IllegalArgumentException();
        }

        return configData;
    }

    protected String validateAndSave(WSContext wsContext, HttpServletRequest request, WSBaseMTAdapterConfigurationData configData, String errors) {
        boolean useCustomScoring = false;
        int matchScore = configData.getMatchScore();
        String scoreOptionValue = request.getParameter(MATCH_SCORE);

        if (MATCH_SCORE_CUSTOM.equals(scoreOptionValue)) {
            useCustomScoring = true;
            matchScore = getMatchScoreParameter(request);
        } else if (MATCH_SCORE_TRANSLATION_SERVICE.equals(scoreOptionValue)) {
            useCustomScoring = false;
        } else {
            errors = addError(LABEL_MATCH_SCORE, errors);
        }

        if (matchScore < 0 || matchScore > 100) {
            errors = addError(LABEL_MATCH_SCORE, errors);
        }

        final String aisPath = request.getParameter(LOCALE_MAP_AIS_PATH).trim();

        try {
            if (wsContext.getAisManager().getMetaDataNode(aisPath) == null) {
                errors = addError(LABEL_LOCALE_MAP_AIS_PATH, errors);
            }
        } catch (WSAisException e) {
            LOG.error("Error saving locale map ais path configuration", e);
            errors = addError(LABEL_LOCALE_MAP_AIS_PATH, errors);
        }

        if (errors == null) {
            configData.setUseCustomScoring(useCustomScoring);
            if (useCustomScoring) {
                configData.setMatchScore(matchScore);
            }

            configData.setIncludeCodes("on".equals(request.getParameter(INCLUDE_CODES)));
            configData.setLocaleMapAISPath(aisPath);
        }

        return errors;
    }

    protected String addError(String field, String invalidFields) {
        return invalidFields == null ? field : invalidFields + ", " + field;
    }

    private int getMatchScoreParameter(HttpServletRequest request) {
        try {
            return Integer.valueOf(request.getParameter(MATCH_SCORE_CUSTOM_VALUE));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
