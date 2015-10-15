package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;

import javax.servlet.http.HttpServletRequest;

public class WSMTAdapterConfigurationUI extends WSComponentConfigurationUI {

    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    private static final String ERROR_MESSAGE = "Error: Please enter valid values for ";

    private static final String LABEL_CLIENT_ID = "Client Id";
    private static final String LABEL_CLIENT_SECRET = "Client Secret";
    private static final String LABEL_CATEGORY = "Category";

    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "secret";
    private static final String CATEGORY = "category";

    @Override
    public String render(WSContext wsContext, HttpServletRequest request, WSComponentConfigurationData config) {
        WSMTAdapterConfigurationData configData = config != null ?
                ((WSMTAdapterConfigurationData) config) : new WSMTAdapterConfigurationData();

        StringBuilder sb = new StringBuilder();
        String clientId = configData.getClientId() == null ? "" : configData.getClientId();
        String secret = configData.getSecret() == null ? "" : configData.getSecret();
        String category = configData.getCategory() == null ? "" : configData.getCategory();

        final String error = (String)request.getAttribute(ERROR_MESSAGE_ATTRIBUTE);
        if (error != null) {
            sb.append("<p style=\"color: red;\">");
            sb.append(UIUtil.escapeHtml(error));
            sb.append("</p>");
        }
        UITable table = new UITable()
                            .add(new UITextField(LABEL_CLIENT_ID, CLIENT_ID, clientId))
                            .add(new UITextField(LABEL_CLIENT_SECRET, CLIENT_SECRET, secret))
                            .add(new UITextField(LABEL_CATEGORY, CATEGORY, category));
        sb.append(table.render());
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
        if (clientId.length() < 1) {
            errors = addError(LABEL_CLIENT_ID, errors);
        }

        if (clientSecret.length() < 1) {
            errors = addError(LABEL_CLIENT_SECRET, errors);
        }

        if (errors != null) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE + errors);
            throw new IllegalArgumentException();
        }

        configData.setClientId(clientId);
        configData.setSecret(clientSecret);
        configData.setCategory(request.getParameter(CATEGORY));

        return configData;
    }

    private String addError(String field, String invalidFields) {
        return invalidFields == null ? field : invalidFields + ", " + field;
    }
}
