package com.spartansoftwareinc.ws.okapi.filters.json;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;
import net.sf.okapi.filters.json.Parameters;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJSONFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Parameters getDefaultParameters() {
		Parameters parameters = new Parameters();
		setCodeFinder(parameters);
		parameters.setExtractAllPairs(true);
		return parameters;
	}

	public void setExcludedKeys(List<String> excludedKeys) {
		Parameters params = getParameters();
		StringBuilder sb = new StringBuilder("^(");
		boolean first = true;
		for (String key : excludedKeys) {
			if (first) {
				first = false;
			}
			else {
				sb.append("|");
			}
			appendExcludedKey(sb, key);
		}
		sb.append(")$");
		params.setExceptions(sb.toString());
		setParameters(params);
	}

	private static StringBuilder appendExcludedKey(StringBuilder builder, String key) {
		return builder.append("^").append(key).append("$");
	}

	private void setCodeFinder(Parameters parameters) {
		parameters.setUseCodeFinder(true);

		int numRules = 0;
		String colonLabelRule[] = getColonLabelCodeFinderRule(numRules);
		numRules += colonLabelRule.length;

		String htmlRules[] = getHtmlCodeFinderRules(numRules);
		numRules += htmlRules.length;

		List<String> codeFinderData = new ArrayList<>();
		codeFinderData.add("useAllRulesWhenTesting.b=true");
		codeFinderData.add("count.i="+numRules);
		codeFinderData.add(FilterUtil.join(colonLabelRule, "\n"));
		codeFinderData.add(FilterUtil.join(htmlRules, "\n"));

		parameters.setCodeFinderData(
				FilterUtil.join(codeFinderData.toArray(new String[codeFinderData.size()]),
						"\n")
		);
	}

	private String[] getColonLabelCodeFinderRule(int numRules) {
		String colonLabelRule[] = {"rule"+numRules+++"=:[a-zA-Z0-9_]+"};
		return colonLabelRule;
	}

	private String[] getHtmlCodeFinderRules(int numRules) {
		String htmlRules[] = {
				"rule"+numRules+++"=%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]",
				"rule"+numRules+++"=(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v",
				"rule"+numRules+++"=\\{\\d[^\\\\]*?\\}",
				"rule"+numRules+++"=<.+?>",
				"rule"+numRules+++"=%%[a-zA-Z]+%%",
				"rule"+numRules+++"=%[0-9]+\\$[a-zA-Z]+",
				"rule"+numRules+++"=%[a-zA-Z]+",
				"rule"+numRules+++"=&[a-zA-Z]+;"};
		return htmlRules;
	}

}
