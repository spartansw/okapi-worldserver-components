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

import static com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData.LOCALE_MAP_AIS_PATH;
import static com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData.MATCH_SCORE;

import java.util.ArrayList;
import java.util.List;

public class WSGoogleMTAdapterConfigurationUI extends WSBaseMTAdapterConfigurationUI {

    private static final Logger logger = LoggerFactory.getLogger(WSGoogleMTAdapterConfigurationUI.class);

    private static final String LABEL_API_KEY = "API Key";
    private static final String API_KEY = "apiKey";
    private static final String LABEL_RETRY_INTERVAL = "Retry Interval (ms)";
    private static final String RETRY_INTERVAL = "retryInterval";
    private static final String LABEL_RETRY_COUNT = "Retry Count";
    private static final String RETRY_COUNT = "retryCount";

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
        elements.add(new UITextField(LABEL_RETRY_COUNT, RETRY_COUNT,
                     Integer.toString(googleMTAdapterConfigurationData.getRetryCount())));
        elements.add(new UITextField(LABEL_RETRY_INTERVAL, RETRY_INTERVAL,
                Integer.toString(googleMTAdapterConfigurationData.getRetryInterval())));
        elements.addAll(super.getUIElements(configurationData));

        return elements;
    }

    @Override
    protected String validateAndSave(WSContext wsContext, HttpServletRequest request, WSBaseMTAdapterConfigurationData configurationData, String errors) {
        final String apiKey = request.getParameter(API_KEY);

        if (apiKey == null || apiKey.length() < 1) {
            errors = addError(LABEL_API_KEY, errors);
        }
        String retryCount = request.getParameter(RETRY_COUNT);
        if (retryCount == null || toInteger(retryCount) == null || toInteger(retryCount) < 0) {
            errors = addError(LABEL_RETRY_COUNT, errors);
        }
        String retryInterval = request.getParameter(RETRY_INTERVAL);
        if (retryInterval == null || toInteger(retryInterval) == null || toInteger(retryInterval) < 0) {
            errors = addError(LABEL_RETRY_INTERVAL, errors);
        }

        errors = super.validateAndSave(wsContext, request, configurationData, errors);

        if (null == errors) {
            WSGoogleMTAdapterConfigurationData googleMTAdapterConfigurationData = (WSGoogleMTAdapterConfigurationData) configurationData;

            googleMTAdapterConfigurationData.setApiKey(apiKey);
            googleMTAdapterConfigurationData.setIncludeCodes(true);
            googleMTAdapterConfigurationData.setRetryCount(toInteger(retryCount));
            googleMTAdapterConfigurationData.setRetryInterval(toInteger(retryInterval));
        }

        return errors;
    }

    private Integer toInteger(String val) {
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
