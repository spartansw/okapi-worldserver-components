package com.spartansoftware.ws.okapi.filters.mock;

import com.idiominc.wssdk.asset.WSInvalidOrMismatchPlaceholderException;
import com.idiominc.wssdk.asset.WSMarkupSegment;

/**
 * Mock WSMarkup segment.  Some methods unimplemented.
 */
public class MockWSMarkupSegment implements WSMarkupSegment {
    private String content;

    public MockWSMarkupSegment(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getSequence() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setContent(String string) throws WSInvalidOrMismatchPlaceholderException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "TestWSMarkupSegment(" + content + ")";
    }
}