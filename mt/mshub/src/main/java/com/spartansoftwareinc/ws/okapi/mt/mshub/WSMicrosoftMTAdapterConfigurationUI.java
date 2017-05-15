package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIElement;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData.INCLUDE_CODES;
import static com.spartansoftwareinc.ws.okapi.mt.mshub.WSMTAdapterConfigurationData.AZURE_KEY;
import static com.spartansoftwareinc.ws.okapi.mt.mshub.WSMTAdapterConfigurationData.CATEGORY;

public class WSMicrosoftMTAdapterConfigurationUI extends WSBaseMTAdapterConfigurationUI {

    private static final Logger logger = LoggerFactory.getLogger(WSMicrosoftMTAdapterConfigurationUI.class);

    private static final String LABEL_AZURE_KEY = "Azure Key";
    private static final String LABEL_CATEGORY = "Category";

    @Override
    protected WSBaseMTAdapterConfigurationData getConfigurationData(WSComponentConfigurationData configurationData) {
        return configurationData == null || !(configurationData instanceof WSMTAdapterConfigurationData)
                ? new WSMTAdapterConfigurationData()
                : ((WSMTAdapterConfigurationData) configurationData);
    }

    @Override
    protected List<UIElement> getUIElements(WSBaseMTAdapterConfigurationData configurationData) {
        List<UIElement> elements = new ArrayList<>();

        WSMTAdapterConfigurationData microsoftMTAdapterConfigurationData = (WSMTAdapterConfigurationData) configurationData;

        String azureKey = microsoftMTAdapterConfigurationData.getAzureKey() == null ? "" : microsoftMTAdapterConfigurationData.getAzureKey();
        String category = microsoftMTAdapterConfigurationData.getCategory() == null ? "" : microsoftMTAdapterConfigurationData.getCategory();

        elements.add(new UITextField(LABEL_AZURE_KEY, AZURE_KEY, azureKey).setSize(60));
        elements.add(new UITextField(LABEL_CATEGORY, CATEGORY, category).setSize(60));

        elements.addAll(super.getUIElements(configurationData));

        elements.add(new UICheckbox(LABEL_INCLUDE_CODES, INCLUDE_CODES, configurationData.getIncludeCodes()));

        return elements;
    }

    @Override
    protected String validateAndSave(WSContext wsContext, HttpServletRequest request, WSBaseMTAdapterConfigurationData configurationData, String errors) {
        final String azureKey = request.getParameter(AZURE_KEY);

        if (azureKey == null || azureKey.length() < 1) {
            errors = addError(LABEL_AZURE_KEY, errors);
        }

        errors = super.validateAndSave(wsContext, request, configurationData, errors);

        if (null == errors) {
            WSMTAdapterConfigurationData microsoftMTAdapterConfigurationData = (WSMTAdapterConfigurationData) configurationData;

            microsoftMTAdapterConfigurationData.setAzureKey(azureKey);
            microsoftMTAdapterConfigurationData.setCategory(request.getParameter(CATEGORY).trim());

            microsoftMTAdapterConfigurationData.setIncludeCodes("on".equals(request.getParameter(INCLUDE_CODES)));
        }

        return errors;
    }
}
