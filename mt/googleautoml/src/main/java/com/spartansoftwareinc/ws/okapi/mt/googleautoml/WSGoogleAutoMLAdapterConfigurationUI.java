package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import static com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData.MATCH_SCORE;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UIElement;
import com.spartansoftwareinc.ws.okapi.base.ui.UIRadioButton;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationUI;

public class WSGoogleAutoMLAdapterConfigurationUI extends WSBaseMTAdapterConfigurationUI {
    private static final Logger LOG = LoggerFactory.getLogger(WSGoogleAutoMLAdapterConfigurationUI.class);

    private static final String LABEL_CREDENTIAL_AIS_PATH = "Credential AIS Path";
    private static final String CREDENTIAL_AIS_PATH = "credentialAisPath";
    private static final String LABEL_MODEL_MAP = "Model JSON Map";
    private static final String MODEL_MAP = "modelMap";

    private static final int TEXT_FIELD_SIZE = 50;

    @Override
    protected WSBaseMTAdapterConfigurationData getConfigurationData(WSComponentConfigurationData configData) {
        return configData == null || !(configData instanceof WSGoogleAutoMLAdapterConfigurationData)
                ? new WSGoogleAutoMLAdapterConfigurationData()
                : ((WSGoogleAutoMLAdapterConfigurationData) configData);
    }

    @Override
    protected List<UIElement> getUIElements(WSBaseMTAdapterConfigurationData configData) {
        List<UIElement> elements = new ArrayList<>();
        WSGoogleAutoMLAdapterConfigurationData googleConfigData =
                (WSGoogleAutoMLAdapterConfigurationData) configData;
        int matchScore = googleConfigData.getMatchScore();

        elements.add(new UITextField(LABEL_CREDENTIAL_AIS_PATH, CREDENTIAL_AIS_PATH,
                googleConfigData.getCredentialAisPath()).setSize(TEXT_FIELD_SIZE));

        UIRadioButton.Option defaultOption = new UIRadioButton.Option("Use Translation Service Value", MATCH_SCORE_TRANSLATION_SERVICE,
                !googleConfigData.useCustomScoring());
        UIRadioButton.Option customOption = new UIRadioButton.Option("Use Custom Value", MATCH_SCORE_CUSTOM,
                googleConfigData.useCustomScoring(), getCustomValueHtml(MATCH_SCORE_CUSTOM_VALUE, 0, 100, matchScore));
        elements.add(new UIRadioButton(LABEL_MATCH_SCORE, MATCH_SCORE, defaultOption, customOption));

        elements.add(new UITextField(LABEL_MODEL_MAP, MODEL_MAP, googleConfigData.getModelMap())
                .setSize(TEXT_FIELD_SIZE));

        return elements;
    }

    @Override
    protected String validateAndSave(WSContext wsContext, HttpServletRequest request,
            WSBaseMTAdapterConfigurationData configData, String errors) {
        String credentialAisPath = request.getParameter(CREDENTIAL_AIS_PATH);
        if (credentialAisPath == null || credentialAisPath.isEmpty()) {
            errors = addError(LABEL_CREDENTIAL_AIS_PATH, errors);
        }

        WSNode credentialNode = null;
        try {
            credentialNode = wsContext.getAisManager().getNode(credentialAisPath);
        } catch (WSAisException e) {
            LOG.error("Error fetching the credential AIS node", e);
            errors = addError(LABEL_CREDENTIAL_AIS_PATH, errors);
        }
        if (credentialNode == null) {
            errors = addError(LABEL_CREDENTIAL_AIS_PATH, errors);
        }

        String modelMap = request.getParameter(MODEL_MAP);
        if (modelMap == null || modelMap.isEmpty()) {
            errors = addError(LABEL_MODEL_MAP, errors);
        }

        errors = parentValidateAndSave(request, configData, errors);

        if (errors == null) {
            WSGoogleAutoMLAdapterConfigurationData googleConfigData =
                    (WSGoogleAutoMLAdapterConfigurationData) configData;
            googleConfigData.setCredentialAisPath(credentialAisPath);
            googleConfigData.setCredentialAbsolutePath(credentialNode.getFile().getAbsolutePath());
            googleConfigData.setModelMap(modelMap);
        }
        return errors;
    }

    private String parentValidateAndSave(HttpServletRequest request, WSBaseMTAdapterConfigurationData configData, String errors) {
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

        if (errors == null) {
            configData.setUseCustomScoring(useCustomScoring);
            if (useCustomScoring) {
                configData.setMatchScore(matchScore);
            }
        }
        return errors;
    }

    private int getMatchScoreParameter(HttpServletRequest request) {
        try {
            return Integer.valueOf(request.getParameter(MATCH_SCORE_CUSTOM_VALUE));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
