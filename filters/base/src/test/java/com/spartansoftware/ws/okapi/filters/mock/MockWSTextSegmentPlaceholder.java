package com.spartansoftware.ws.okapi.filters.mock;

import com.idiominc.wssdk.asset.WSMarkupInfo;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;

/**
 * Mock WSTextSegmentPlaceholder for testing.
 */
public class MockWSTextSegmentPlaceholder implements WSTextSegmentPlaceholder {
    private final String placeholderText;

    public MockWSTextSegmentPlaceholder(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    @Override
    public String getText() {
        return this.placeholderText;
    }

    @Override
    public int getId() {
        return 0;
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
        return placeholderText.equals(((MockWSTextSegmentPlaceholder)obj).placeholderText);
    }

    @Override
    public int hashCode() {
        return placeholderText.hashCode();
    }
}