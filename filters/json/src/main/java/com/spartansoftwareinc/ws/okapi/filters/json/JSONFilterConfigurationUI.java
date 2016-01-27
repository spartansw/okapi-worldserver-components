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

public class JSONFilterConfigurationUI extends WSOkapiFilterUI<JSONFilterConfigurationData> {

    public JSONFilterConfigurationUI() {
    }

    @Override
    protected JSONFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return (config != null && config instanceof JSONFilterConfigurationData) ?
            (JSONFilterConfigurationData)config : new JSONFilterConfigurationData();
    }

    @Override
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
                                WSComponentConfigurationData config) {
        JSONFilterConfigurationData configData = getConfigurationData(config);
        Collection<String> excludedKeys = getExcludedKeys(configData);
        UITable table = new UITable();
        table.add(new UIMultiValueInput("Non-Translatable JSON Keys", "json",
                  excludedKeys, excludedKeys));
        return table;
    }

    @Override
    protected JSONFilterConfigurationData updateConfiguration(WSContext context, HttpServletRequest request,
            WSComponentConfigurationData config) {
        JSONFilterConfigurationData configData = getConfigurationData(config);
        configData.setExcludedKeys(UIUtil.getOptionValues(request, "json_keys_res"));
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
