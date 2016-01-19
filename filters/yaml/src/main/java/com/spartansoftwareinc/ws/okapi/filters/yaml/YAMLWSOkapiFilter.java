package com.spartansoftwareinc.ws.okapi.filters.yaml;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.yaml.YamlFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class YAMLWSOkapiFilter extends WSOkapiFilter {

    private static final Logger LOG = LoggerFactory.getLogger(YAMLWSOkapiFilter.class);
    private static final String FILTER_NAME = "Okapi YAML Filter";
    private static final String FILTER_DESCRIPTION = "World Server YAML com";
    private static final String DEFAULT_ENCODING = "UTF-8";

    public String getName() {
        return FILTER_NAME;
    }

    public String getDescription() {
        return FILTER_DESCRIPTION;
    }

    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSFilterUIConfiguration getUIConfiguration() {
        return new YAMLFilterConfigurationUI();
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    public ConfiguredYAMLFilter getConfiguredFilter() {
        ConfiguredYAMLFilter filter = new ConfiguredYAMLFilter();
        YAMLFilterConfigurationData configurationData = getYAMLFilterConfiguration();
        filter.setParameters(configurationData.getParameters());
        filter.setExcludedKeys(configurationData.getExcludedKeys());
        return filter;
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    protected YAMLFilterConfigurationData getYAMLFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof YAMLFilterConfigurationData) ?
                (YAMLFilterConfigurationData)config : new YAMLFilterConfigurationData();
    }

    private class ConfiguredYAMLFilter extends YamlFilter {
        private Set<String> excludedKeys = new LinkedHashSet<String>();

        public void setExcludedKeys(Set<String> excludedKeys) {
            if (excludedKeys != null) {
                this.excludedKeys = excludedKeys;
            }
        }

        @Override
        public Event next() {
            Event next = super.next();
            if (next.isTextUnit()) {
                ITextUnit textUnit = next.getTextUnit();
                textUnit.setIsTranslatable(isTextUnitTranslatable(textUnit));
            }
            return next;
        }

        private boolean isTextUnitTranslatable(ITextUnit textUnit) {
            for (String key : excludedKeys) {
                if (!key.isEmpty() && textUnit.getName().endsWith(key)) {
                    return false;
                }
            }
            return true;
        }
    }
}

