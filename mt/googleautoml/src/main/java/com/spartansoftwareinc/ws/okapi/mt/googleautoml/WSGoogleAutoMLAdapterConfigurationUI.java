package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

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

        elements.add(new UITextField(LABEL_CREDENTIAL_AIS_PATH, CREDENTIAL_AIS_PATH,
                googleConfigData.getCredentialAisPath()).setSize(TEXT_FIELD_SIZE));
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

        if (errors == null) {
            WSGoogleAutoMLAdapterConfigurationData googleConfigData =
                    (WSGoogleAutoMLAdapterConfigurationData) configData;
            googleConfigData.setCredentialAisPath(credentialAisPath);
            googleConfigData.setCredentialAbsolutePath(credentialNode.getFile().getAbsolutePath());
            googleConfigData.setModelMap(modelMap);
        }
        return errors;
    }
}
