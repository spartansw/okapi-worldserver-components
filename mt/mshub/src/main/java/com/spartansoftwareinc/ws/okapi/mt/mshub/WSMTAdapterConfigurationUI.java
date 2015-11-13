package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
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

    private static final String LABEL_CLIENT_ID = "Client Id";
    private static final String LABEL_CLIENT_SECRET = "Client Secret";
    private static final String LABEL_CATEGORY = "Category";
    private static final String LABEL_MATCH_SCORE = "MT Match Score";

    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "secret";
    private static final String CATEGORY = "category";
    private static final String MATCH_SCORE = "matchScore";
    private static final String MATCH_SCORE_MSHUB = "mshub";
    private static final String MATCH_SCORE_CUSTOM = "custom";
    private static final String MATCH_SCORE_CUSTOM_VALUE = "customValue";

    @Override
    public String render(WSContext wsContext, HttpServletRequest request, WSComponentConfigurationData config) {
        WSMTAdapterConfigurationData configData = config != null ?
                ((WSMTAdapterConfigurationData) config) : new WSMTAdapterConfigurationData();

        StringBuilder sb = new StringBuilder();
        String clientId = configData.getClientId() == null ? "" : configData.getClientId();
        String secret = configData.getSecret() == null ? "" : configData.getSecret();
        String category = configData.getCategory() == null ? "" : configData.getCategory();
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
                            .add(new UITextField(LABEL_CLIENT_ID, CLIENT_ID, clientId))
                            .add(new UITextField(LABEL_CLIENT_SECRET, CLIENT_SECRET, secret))
                            .add(new UITextField(LABEL_CATEGORY, CATEGORY, category))
                            .add(new UIRadioButton(LABEL_MATCH_SCORE, MATCH_SCORE, defaultOption, customOption));
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

        final String clientId = request.getParameter(CLIENT_ID);
        final String clientSecret = request.getParameter(CLIENT_SECRET);

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

        if (clientId.length() < 1) {
            errors = addError(LABEL_CLIENT_ID, errors);
        }

        if (clientSecret.length() < 1) {
            errors = addError(LABEL_CLIENT_SECRET, errors);
        }

        if (matchScore < 0 || matchScore > 100) {
            errors = addError(LABEL_MATCH_SCORE, errors);
        }

        if (errors != null) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE + errors);
            throw new IllegalArgumentException();
        }

        configData.setClientId(clientId);
        configData.setSecret(clientSecret);
        configData.setCategory(request.getParameter(CATEGORY));
        configData.setUseCustomScoring(useCustomScoring);
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
        catch (Exception e) {
            return -1;
        }
    }
}
