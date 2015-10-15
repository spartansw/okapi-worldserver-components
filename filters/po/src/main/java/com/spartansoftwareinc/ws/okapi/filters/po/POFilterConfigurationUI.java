package com.spartansoftwareinc.ws.okapi.filters.po;

import javax.servlet.http.HttpServletRequest;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.OkapiWsFilterUI;

public class POFilterConfigurationUI extends OkapiWsFilterUI {

    @Override
    public String render(WSContext context, HttpServletRequest request,
            WSComponentConfigurationData config) {
        POFilterConfigurationData poConfig = getConfigData(config);

        UITable table = new UITable();
        table.add(new UICheckbox("Copy Target Content to PO File", "copyTarget", poConfig.getCopyToPO()));
        return table.render();
    }

    @Override
    public WSComponentConfigurationData save(WSContext context,
            HttpServletRequest request, WSComponentConfigurationData config) {
        POFilterConfigurationData poConfig = getConfigData(config);
        poConfig.setCopyToPO(UIUtil.getBoolean(request, "copyTarget"));
        return poConfig;
    }

    private POFilterConfigurationData getConfigData(WSComponentConfigurationData config) {
        return (config != null && config instanceof POFilterConfigurationData) ?
            (POFilterConfigurationData)config : new POFilterConfigurationData();
    }
}
