package com.spartansoftwareinc.ws.okapi.filters.model;

import java.util.Arrays;

/**
 * An intermediate class that holds information extracted from an Okapi segment.
 */
public class SegmentInfoHolder {
    private String encodedText;
    private String[] placeholders;

    public SegmentInfoHolder(String encodedText, String... placeholders) {
        this.encodedText = encodedText;
        this.placeholders = placeholders;
    }

    public String getEncodedText() {
        return encodedText;
    }

    public String[] getPlaceholders() {
        return placeholders;
    }

    @Override
    public String toString() {
        return "SegmentInfoHolder(text='" + getEncodedText() + "', placeholders=" + 
                Arrays.asList(placeholders).toString() + ")";
    }
}
