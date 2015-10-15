package com.spartansoftware.ws.okapi.filters.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.idiominc.wssdk.asset.WSInvalidOrMismatchPlaceholderException;
import com.idiominc.wssdk.asset.WSTextSegment;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;

/**
 * Mock WSTextSegment for testing.  Several methods are unsupported.
 */
public class MockWSTextSegment implements WSTextSegment {
    private final String content;
    private final List<MockWSTextSegmentPlaceholder> placeholders = new ArrayList<>();

    public MockWSTextSegment(String content, String[] placeholders) {
        this.content = content;
        for (String ph : placeholders) {
            this.placeholders.add(new MockWSTextSegmentPlaceholder(ph));
        }
    }

    @Override
    public String toString() {
        return "TestWSTextSegment(text='" + getContent() + "', placeholders=" + 
                Arrays.asList(placeholders).toString() + ")";
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public WSTextSegmentPlaceholder[] getTargetPlaceholders() {
        return this.placeholders.toArray(new WSTextSegmentPlaceholder[this.placeholders.size()]);
    }

    @Override
    public WSTextSegmentPlaceholder[] getPlaceholders() {
        return this.placeholders.toArray(new WSTextSegmentPlaceholder[this.placeholders.size()]);
    }

    @Override
    public WSTextSegmentPlaceholder[] getSourcePlaceholders() {
        return this.placeholders.toArray(new WSTextSegmentPlaceholder[this.placeholders.size()]);
    }

    @Override
    public void setPlaceholders(WSTextSegmentPlaceholder[] wstsps) throws WSInvalidOrMismatchPlaceholderException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setContent(String string,
                           WSTextSegmentPlaceholder[] wstsps) throws WSInvalidOrMismatchPlaceholderException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSID(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long wordCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getComments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setComments(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setContent(String string) throws WSInvalidOrMismatchPlaceholderException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSequence() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
