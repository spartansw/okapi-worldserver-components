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
    @Override
    public String getRedirectURI() {
        return null;
    }

    @Override
    public String render(WSContext context, HttpServletRequest request,
            WSComponentConfigurationData config) {
        T wsConfig = getConfigurationData(config);
        UITable table = buildConfigurationTable(context, request, config);
        table.add(new UICheckbox("Apply sentence-breaking", "sentenceBreaking", wsConfig.getApplySegmentation()));
        return table.render();
    }

    @Override
    public WSComponentConfigurationData save(WSContext context,
            HttpServletRequest request, WSComponentConfigurationData config) {
        T wsConfig = getConfigurationData(config);
        wsConfig.setApplySegmentation(UIUtil.getBoolean(request, "sentenceBreaking"));
        return updateConfiguration(context, request, config);
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

    protected abstract UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                               WSComponentConfigurationData config);

    protected abstract WSComponentConfigurationData updateConfiguration(WSContext context, HttpServletRequest request,
                                                WSComponentConfigurationData config);
}
