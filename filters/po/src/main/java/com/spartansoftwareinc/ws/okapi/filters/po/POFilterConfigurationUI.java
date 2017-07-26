package com.spartansoftwareinc.ws.okapi.filters.po;

import javax.servlet.http.HttpServletRequest;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

public class POFilterConfigurationUI extends WSOkapiFilterUI<POFilterConfigurationData> {
    @Override
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                WSComponentConfigurationData config) {
        POFilterConfigurationData poConfig = getConfigurationData(config);

        UITable table = new UITable();
        table.add(new UICheckbox("Copy Target Content to PO File", "copyTarget", poConfig.getCopyToPO()));
        return table;
    }

    @Override
    protected String validateAndSave(WSContext context, HttpServletRequest request, POFilterConfigurationData configData, String errors) {
        POFilterConfigurationData poConfig = getConfigurationData(configData);
        poConfig.setCopyToPO(UIUtil.getBoolean(request, "copyTarget"));

        return errors;
    }

    @Override
    protected POFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof POFilterConfigurationData) ?
            (POFilterConfigurationData)config : new POFilterConfigurationData();
    }
}
