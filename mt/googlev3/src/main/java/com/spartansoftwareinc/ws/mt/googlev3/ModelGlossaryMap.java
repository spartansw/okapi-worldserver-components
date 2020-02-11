package com.spartansoftwareinc.ws.mt.googlev3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idiominc.wssdk.WSRuntimeException;

/**
 * This maps a language pair to a language model and a glossary (term database).
 * The key is a string of the form "source_language_tag/target_language_tag",
 * for example "en/ja", "en/zh-TW", etc. The language tag has to be one that
 * Google Cloud Translation service can recognize, i.e. just a 2-letter
 * ISO language code with two exceptions, zh-TW and zh-CN.
 */
class ModelGlossaryMap {
    private static final Logger LOG = Logger.getLogger(ModelGlossaryMap.class);
    //static { LOG.setLevel(Level.DEBUG);}

    ObjectMapper om = new ObjectMapper();

    public static class ModelGlossary {
        public String modelId;
        public String glossaryId;

        public ModelGlossary() {} // Jackson Databind requires the default constructor.

        public ModelGlossary(String modelId, String glossaryId) {
            this.modelId = modelId;
            this.glossaryId = glossaryId;
        }
    }

    private Map<String, ModelGlossary> theMap = new HashMap<>();

    @JsonAnyGetter
    public Map<String, ModelGlossary> getMap() {
        return theMap;
    }

    public void add(String localeTagPair, String modelId, String glossaryId) {
        ModelGlossary entry;
        entry = theMap.get(localeTagPair);
        if (entry != null) {
            if (modelId.equals(entry.modelId) && glossaryId.equals(entry.glossaryId)) {
                return;
            }
            LOG.warn("Existing entry for locale pair " + localeTagPair
                    + ": modelId=" + entry.modelId + ", glossaryId=" + entry.glossaryId
                    + "is replaced by modelId=" + modelId + ", glossaryId=" + glossaryId);
            entry.modelId = modelId;
            entry.glossaryId = glossaryId;
        } else {
            theMap.put(localeTagPair, new ModelGlossary(modelId, glossaryId));
        }
    }

    public ModelGlossary get(String localeTagPair) {
        return theMap.get(localeTagPair);
    }

    public void loadMap(String json) {
        try {
            theMap = om.readValue(json, new TypeReference<Map<String, ModelGlossary>>(){});
        } catch (IOException e) {
            throw new WSRuntimeException("Error in the model and glossary map.", e);
        }
    }
}
