package com.spartansoftwareinc.ws.okapi.mt.mshub.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Microsoft custom translator translation request response JSON object.
 */
public class TranslateResponse {
    public final List<Translation> translations;

    @JsonCreator
    public TranslateResponse(@JsonProperty("translations") List<Translation> translations) {
        this.translations = translations;
    }

    public static class Translation {
        public final String to;
        public final String text;

        @JsonCreator
        public Translation(@JsonProperty("to") String to, @JsonProperty("text") String text) {
            this.to = to;
            this.text = text;
        }
    }
}
