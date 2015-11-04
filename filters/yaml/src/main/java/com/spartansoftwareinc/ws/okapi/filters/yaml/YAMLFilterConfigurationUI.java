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

public class YAMLFilterConfigurationUI extends WSOkapiFilterUI {

    public YAMLFilterConfigurationUI() {
    }

    private YAMLFilterConfigurationData getConfigData(WSComponentConfigurationData config) {
        return (config != null && config instanceof YAMLFilterConfigurationData) ?
            (YAMLFilterConfigurationData)config : new YAMLFilterConfigurationData();
    }

    @Override
    public String render(WSContext context, HttpServletRequest request,
                         WSComponentConfigurationData config) {

        Collection<String> excludedKeys = getConfigData(config).getExcludedKeys();
        UITable table = new UITable();
        table.add(new UIMultiValueInput("Non-Translatable YAML Keys", "yaml",
                  excludedKeys, excludedKeys));
        return table.render();
    }

    @Override
    public WSComponentConfigurationData save(WSContext context, HttpServletRequest request,
                                             WSComponentConfigurationData config) {
        YAMLFilterConfigurationData configData = getConfigData(config);
        configData.setExcludedKeys(new LinkedHashSet<>(UIUtil.getOptionValues(request, "yaml_keys_res")));
        return configData;
    }
}
