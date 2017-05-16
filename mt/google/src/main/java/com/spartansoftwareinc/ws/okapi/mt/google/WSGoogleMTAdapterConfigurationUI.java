package com.spartansoftwareinc.ws.okapi.mt.google;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UIElement;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class WSGoogleMTAdapterConfigurationUI extends WSBaseMTAdapterConfigurationUI {

    private static final Logger logger = LoggerFactory.getLogger(WSGoogleMTAdapterConfigurationUI.class);

    private static final String LABEL_API_KEY = "API Key";
    private static final String API_KEY = "apiKey";

    @Override
    protected WSBaseMTAdapterConfigurationData getConfigurationData(WSComponentConfigurationData configurationData) {
        return configurationData == null || !(configurationData instanceof WSGoogleMTAdapterConfigurationData)
                ? new WSGoogleMTAdapterConfigurationData()
                : ((WSGoogleMTAdapterConfigurationData) configurationData);
    }

    @Override
    protected List<UIElement> getUIElements(WSBaseMTAdapterConfigurationData configurationData) {
        List<UIElement> elements = new ArrayList<>();

        WSGoogleMTAdapterConfigurationData googleMTAdapterConfigurationData = (WSGoogleMTAdapterConfigurationData) configurationData;

        String apiKey = googleMTAdapterConfigurationData.getApiKey() == null ? "" : googleMTAdapterConfigurationData.getApiKey();

        elements.add(new UITextField(LABEL_API_KEY, API_KEY, apiKey).setSize(60));
        elements.addAll(super.getUIElements(configurationData));

        return elements;
    }

    @Override
    protected String validateAndSave(WSContext wsContext, HttpServletRequest request, WSBaseMTAdapterConfigurationData configurationData, String errors) {
        final String apiKey = request.getParameter(API_KEY);

        if (apiKey == null || apiKey.length() < 1) {
            errors = addError(LABEL_API_KEY, errors);
        }

        errors = super.validateAndSave(wsContext, request, configurationData, errors);

        if (null == errors) {
            WSGoogleMTAdapterConfigurationData googleMTAdapterConfigurationData = (WSGoogleMTAdapterConfigurationData) configurationData;

            googleMTAdapterConfigurationData.setApiKey(apiKey);
            googleMTAdapterConfigurationData.setIncludeCodes(true);
        }

        return errors;
    }
}
