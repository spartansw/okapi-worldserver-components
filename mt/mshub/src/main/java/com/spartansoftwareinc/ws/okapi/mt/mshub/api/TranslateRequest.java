package com.spartansoftwareinc.ws.okapi.mt.mshub.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Microsoft custom translator translation request JSON object.
 */
public class TranslateRequest {
    @JsonProperty("text")
    public final String text;

    public TranslateRequest(String text) {
        this.text = text;
    }
}
