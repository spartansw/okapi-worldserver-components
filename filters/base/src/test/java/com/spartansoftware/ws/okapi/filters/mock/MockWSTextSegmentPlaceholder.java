package com.spartansoftware.ws.okapi.filters.mock;

import java.util.Objects;

import com.idiominc.wssdk.asset.WSMarkupInfo;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;

/**
 * Mock WSTextSegmentPlaceholder for testing.
 */
public class MockWSTextSegmentPlaceholder implements WSTextSegmentPlaceholder {
    private final String placeholderText;
    private final int id;

    public MockWSTextSegmentPlaceholder(String placeholderText, int id) {
        this.placeholderText = placeholderText;
        this.id = id;
    }
    
    public MockWSTextSegmentPlaceholder(String placeholderText) {
        this(placeholderText, 0);
    }

    @Override
    public String getText() {
        return this.placeholderText;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public WSMarkupInfo getMarkupInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "{" + placeholderText + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || !(obj instanceof MockWSTextSegmentPlaceholder)) return false;
        return Objects.equals(placeholderText, ((MockWSTextSegmentPlaceholder)obj).placeholderText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeholderText);
    }
}