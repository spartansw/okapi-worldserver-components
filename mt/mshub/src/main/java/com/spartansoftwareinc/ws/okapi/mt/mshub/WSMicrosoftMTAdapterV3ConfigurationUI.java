package com.spartansoftwareinc.ws.okapi.mt.mshub;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Joiner;
import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;

public class WSMicrosoftMTAdapterV3ConfigurationUI extends WSComponentConfigurationUI {
    private static final Logger LOG = Logger.getLogger(WSMicrosoftMTAdapterV3ConfigurationUI.class);

    /** Parameters that can be configured by WS users. */
    public enum Parameter {
        AZURE_KEY("Azure Key", "azureKey"),
        CATEGORY("Category", "category"),
        MATCH_SCORE("MT Match Score", "matchScore"),
        LOCALE_MAP_AIS_PATH("AIS Path for Locale Overrides", "localeMapAISPath");

        /** String that is displayed to WS users. */
        public final String label;
        /** String that is used as the 'name' attribute for HTML input elements. */
        public final String nameAttr;

        private Parameter(String label, String nameAttr) {
            this.label = label;
            this.nameAttr = nameAttr;
        }
    }

    public static class TextParameter {
        public final Parameter param;
        public final String value;
        public final int size;

        public TextParameter(Parameter param, String value, int size) {
            this.param = param;
            this.value = value;
            this.size = size;
        }
    }

    public static class NumberParameter {
        public final Parameter param;
        public final int value;
        public final int min;
        public final int max;

        public NumberParameter(Parameter param, int value, int min, int max) {
            this.param = param;
            this.value = value;
            this.min = min;
            this.max = max;
        }
    }

    static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    static final String ERROR_MESSAGE = "Error: Please enter valid values for ";

    private final Mustache mustache;

    public WSMicrosoftMTAdapterV3ConfigurationUI() {
        MustacheFactory mf = new DefaultMustacheFactory();
        try (InputStream is = getClass().getResourceAsStream("config.mustache");
                InputStreamReader reader = new InputStreamReader(is)) {
            mustache = mf.compile(reader, "config");
        } catch (IOException e) {
            throw new WSRuntimeException(e);
        }
    }

    @Override
    public String render(WSContext wsContext, HttpServletRequest request, WSComponentConfigurationData config) {
        WSMicrosoftMTAdapterV3ConfigurationData data = getMicrosoftMTAdapterConfiguration(config);

        Map<String, Object> scopes = new HashMap<>();
        scopes.put("errorMessage", request.getAttribute(ERROR_MESSAGE_ATTRIBUTE));
        scopes.put("azureKey", new TextParameter(Parameter.AZURE_KEY, data.getAzureKey(), 60));
        scopes.put("category", new TextParameter(Parameter.CATEGORY, data.getCategory(), 60));
        scopes.put("matchScore", new NumberParameter(Parameter.MATCH_SCORE, data.getMatchScore(), 0, 100));
        scopes.put("localeMapAISPath", new TextParameter(Parameter.LOCALE_MAP_AIS_PATH, data.getLocaleMapAISPath(), 60));

        StringWriter writer = new StringWriter();
        mustache.execute(writer, scopes);
        return writer.toString();
    }

    @Override
    public WSComponentConfigurationData save(WSContext wsContext, HttpServletRequest request,
            WSComponentConfigurationData config) {
        WSMicrosoftMTAdapterV3ConfigurationData data = getMicrosoftMTAdapterConfiguration(config);

        List<String> errors = new ArrayList<>();
        validateAndSave(wsContext, request, data, errors);
        if (!errors.isEmpty()) {
            String errorMessage = ERROR_MESSAGE + Joiner.on(", ").join(errors);
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        return data;
    }

    protected void validateAndSave(WSContext wsContext, HttpServletRequest request,
            WSMicrosoftMTAdapterV3ConfigurationData data, List<String> errors) {

        String azureKey = request.getParameter(Parameter.AZURE_KEY.nameAttr);
        if (azureKey == null || azureKey.isEmpty()) {
            errors.add(Parameter.AZURE_KEY.label);
        }

        int matchScore;
        try {
            matchScore = Integer.valueOf(request.getParameter(Parameter.MATCH_SCORE.nameAttr));
        } catch (NumberFormatException e) {
            matchScore = -1;
        }
        if (matchScore < 0 || matchScore > 100) {
            errors.add(Parameter.MATCH_SCORE.label);
        }

        String aisPath = request.getParameter(Parameter.LOCALE_MAP_AIS_PATH.nameAttr);
        if (aisPath == null) {
            errors.add(Parameter.LOCALE_MAP_AIS_PATH.label);

        } else {
            aisPath = aisPath.trim();

            try {
                if (wsContext.getAisManager().getMetaDataNode(aisPath) == null) {
                    errors.add(Parameter.LOCALE_MAP_AIS_PATH.label);
                }
            } catch (WSAisException e) {
                LOG.error("Error saving locale map ais path configuration", e);
                errors.add(Parameter.LOCALE_MAP_AIS_PATH.label);
            }
        }

        if (errors.isEmpty()) {
            data.setAzureKey(azureKey.trim());
            data.setCategory(request.getParameter(Parameter.CATEGORY.nameAttr).trim());
            data.setMatchScore(matchScore);
            data.setLocaleMapAISPath(aisPath);
        }
    }

    private WSMicrosoftMTAdapterV3ConfigurationData getMicrosoftMTAdapterConfiguration(WSComponentConfigurationData config) {
        if (config == null || !(config instanceof WSMicrosoftMTAdapterV3ConfigurationData)) {
            config = new WSMicrosoftMTAdapterV3ConfigurationData();
        }
        return (WSMicrosoftMTAdapterV3ConfigurationData) config;
    }
}
