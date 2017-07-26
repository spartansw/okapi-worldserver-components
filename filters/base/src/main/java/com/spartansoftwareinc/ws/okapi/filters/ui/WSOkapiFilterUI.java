package com.spartansoftwareinc.ws.okapi.filters.ui;

import javax.servlet.http.HttpServletRequest;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

public abstract class WSOkapiFilterUI<T extends WSOkapiFilterConfigurationData<?>> extends WSFilterUIConfiguration {

    protected static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    protected static final String ERROR_MESSAGE = "Error: Please enter valid values for ";

    @Override
    public String getRedirectURI() {
        return null;
    }

    @Override
    public String render(WSContext context, HttpServletRequest request, WSComponentConfigurationData config) {
        StringBuilder sb = new StringBuilder();

        final String error = (String) request.getAttribute(ERROR_MESSAGE_ATTRIBUTE);
        if (error != null) {
            sb.append("<p style=\"color: red;\">");
            sb.append(UIUtil.escapeHtml(error));
            sb.append("</p>");
        }

        T wsConfig = getConfigurationData(config);
        UITable table = buildConfigurationTable(context, request, config);
        table.add(new UICheckbox("Apply sentence-breaking", "sentenceBreaking", wsConfig.getApplySegmentation()));

        sb.append(table.render());

        return sb.toString();
    }

    /**
     * Convert the config object to an instance of the appropriate
     * {@link WSOkapiFilterConfigurationData} implementation for this filter. If
     * the config object was null, this method should return a new instance of
     * the WSOkapiFilterConfigurationData implementation initialized with the
     * filter's default configuration.
     *
     * @param config existing config data stored in WorldServer, or null
     * @return new or existing instance of the configuration implementation for
     *         this filter
     */
    protected abstract T getConfigurationData(WSComponentConfigurationData config);

    /**
     * Provide the table of options that is used to build the filter UI.  By default,
     * this returns an empty table, to which common options (such as sentence-breaking)
     * are added.  Override this method to supply your own {@link UITable} containing
     * options for the specific filter.
     */
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                              WSComponentConfigurationData config) {
        return new UITable();
    }

    @Override
    public WSComponentConfigurationData save(WSContext context, HttpServletRequest request, WSComponentConfigurationData config) {

        T configData = getConfigurationData(config);

        String errors = validateAndSave(context, request, configData, null);

        if (null != errors) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE + errors);
            throw new IllegalArgumentException();
        }

        configData.setApplySegmentation(UIUtil.getBoolean(request, "sentenceBreaking"));

        return configData;
    }

    /**
     * Validates and saves the request parameters into a configuration object.
     *
     * @param context    The context
     * @param request    The request
     * @param configData The configuration data
     * @param errors     The errors
     *
     * @return {@code null} - if no errors found
     *         comma-delimited parameters - otherwise
     */
    protected String validateAndSave(WSContext context, HttpServletRequest request, T configData, String errors) {
        return errors;
    }

    protected String addError(String field, String invalidFields) {
        return invalidFields == null ? field : invalidFields + ", " + field;
    }
}
