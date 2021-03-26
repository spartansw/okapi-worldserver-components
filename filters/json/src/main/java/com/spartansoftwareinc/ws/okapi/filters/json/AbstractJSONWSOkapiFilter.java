package com.spartansoftwareinc.ws.okapi.filters.json;

import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;
import net.sf.okapi.filters.json.JSONFilter;

public abstract class AbstractJSONWSOkapiFilter<T extends AbstractJSONFilterConfigurationData> extends WSOkapiFilter<T> {
	private static final String FILTER_NAME = "Okapi JSON Filter";
	private static final String FILTER_DESCRIPTION = "World Server JSON com";
	private static final String DEFAULT_ENCODING = "UTF-8";

	@Override
	public String getName() {
		return FILTER_NAME;
	}

	@Override
	public String getDescription() {
		return FILTER_DESCRIPTION;
	}

	@Override
	public String getVersion() {
		return Version.BANNER;
	}

	@Override
	public JSONFilter getConfiguredFilter(T config) {
		JSONFilter filter = new JSONFilter();
		filter.setParameters(config.getParameters());
		return filter;
	}

	@Override
	protected String getDefaultEncoding() {
		return DEFAULT_ENCODING;
	}

}
