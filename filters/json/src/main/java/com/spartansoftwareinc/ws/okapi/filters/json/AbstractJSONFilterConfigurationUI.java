package com.spartansoftwareinc.ws.okapi.filters.json;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIMultiValueInput;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;
import net.sf.okapi.filters.json.Parameters;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractJSONFilterConfigurationUI<T extends AbstractJSONFilterConfigurationData> extends WSOkapiFilterUI<T> {

	@Override
	protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request,
											  WSComponentConfigurationData config) {
		T configData = getConfigurationData(config);
		Collection<String> excludedKeys = getExcludedKeys(configData);
		UITable table = new UITable();
		table.add(new UIMultiValueInput("Non-Translatable JSON Keys", "json",
				excludedKeys, excludedKeys));
		table.add(new UICheckbox("Extract Standalone Keys", "extractIsolated",
				configData.getParameters().getExtractStandalone()));
		return table;
	}

	@Override
	protected String validateAndSave(WSContext context, HttpServletRequest request, T configData, String errors) {
		T configurationData = getConfigurationData(configData);

		configurationData.setExcludedKeys(UIUtil.getOptionValues(request, "json_keys_res"));
		Parameters params = configurationData.getParameters();
		params.setExtractStandalone(UIUtil.getBoolean(request, "extractIsolated"));
		configurationData.setParameters(params);

		return errors;
	}

	// ^(^key1$|^key2$|^key3$)$
	private Pattern KEYS_PATTERN = Pattern.compile("\\^\\((.*)\\)\\$");
	private Pattern KEY_PATTERN = Pattern.compile("\\^(.*)\\$");

	private List<String> getExcludedKeys(T data) {
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
