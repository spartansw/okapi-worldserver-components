package com.spartansoftwareinc.ws.okapi.filters.yaml;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.servlet.http.HttpServletRequest;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIMultiValueInput;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

public class YAMLFilterConfigurationUI extends WSOkapiFilterUI<YAMLFilterConfigurationData> {

    public YAMLFilterConfigurationUI() {
    }

    @Override
    protected YAMLFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof YAMLFilterConfigurationData) ?
            (YAMLFilterConfigurationData)config : new YAMLFilterConfigurationData();
    }

    @Override
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                WSComponentConfigurationData config) {
        Collection<String> excludedKeys = getConfigurationData(config).getExcludedKeys();
        UITable table = new UITable();
        table.add(new UIMultiValueInput("Non-Translatable YAML Keys", "yaml",
                  excludedKeys, excludedKeys));
        return table;
    }

    @Override
    protected String validateAndSave(WSContext context, HttpServletRequest request, YAMLFilterConfigurationData configData, String errors) {
        YAMLFilterConfigurationData configurationData = getConfigurationData(configData);
        configurationData.setExcludedKeys(new LinkedHashSet<>(UIUtil.getOptionValues(request, "yaml_keys_res")));

        return errors;
    }
}
