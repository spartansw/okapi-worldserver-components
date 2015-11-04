package com.spartansoftwareinc.ws.okapi.filters.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIMultiValueInput;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

import net.sf.okapi.filters.json.Parameters;

public class JSONFilterConfigurationUI extends WSOkapiFilterUI {

    public JSONFilterConfigurationUI() {
    }

    private JSONFilterConfigurationData getConfigData(WSComponentConfigurationData config) {
        return (config != null && config instanceof JSONFilterConfigurationData) ?
            (JSONFilterConfigurationData)config : new JSONFilterConfigurationData();
    }

    @Override
    public String render(WSContext context, HttpServletRequest request,
                         WSComponentConfigurationData config) {
        JSONFilterConfigurationData configData = getConfigData(config);
        Collection<String> excludedKeys = getExcludedKeys(configData);
        UITable table = new UITable();
        table.add(new UIMultiValueInput("Non-Translatable JSON Keys", "json",
                  excludedKeys, excludedKeys));
        return table.render();
    }

    @Override
    public WSComponentConfigurationData save(WSContext context, HttpServletRequest request,
                                             WSComponentConfigurationData config) {
        JSONFilterConfigurationData configData = getConfigData(config);
        Parameters params = configData.getParameters();
        JSONFilterConfigurationData.setExcludedKeys(params, UIUtil.getOptionValues(request, "json_keys_res"));
        configData.setParameters(params);
        return configData;
    }

    // ^(^key1$|^key2$|^key3$)$
    private Pattern KEYS_PATTERN = Pattern.compile("\\^\\((.*)\\)\\$");
    private Pattern KEY_PATTERN = Pattern.compile("\\^(.*)\\$");

    private List<String> getExcludedKeys(JSONFilterConfigurationData data) {
        Matcher m = KEYS_PATTERN.matcher(data.getParameters().getExceptions());
        if (m.matches()) {
            String[] rawKeys = m.group(1).split("\\|");
            List<String> keys = new ArrayList<String>();
            for (String raw : rawKeys) {
                Matcher rawMatcher = KEY_PATTERN.matcher(raw);
                if (rawMatcher.matches()) {
                    keys.add(rawMatcher.group(1));
                }
            }
            return keys;
        }
        return Collections.emptyList();
    }

}
