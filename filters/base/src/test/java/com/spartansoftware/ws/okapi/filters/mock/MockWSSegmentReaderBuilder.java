package com.spartansoftware.ws.okapi.filters.mock;

import java.util.ArrayList;
import java.util.List;

import com.idiominc.wssdk.asset.WSSegment;

/**
 * Builder to create MockWSSegmentReader instances for testing.
 */
public class MockWSSegmentReaderBuilder {
    private final List<WSSegment> wsSegments = new ArrayList<WSSegment>();

    public MockWSSegmentReaderBuilder addWSSegment(WSSegment seg) {
        wsSegments.add(seg);
        return this;
    }

    public MockWSSegmentReaderBuilder addMarkupSegment(String content) {
        addWSSegment(new MockWSMarkupSegment(content));
        return this;
    }

    public MockWSSegmentReaderBuilder addTextSegment(String content, String[] placeholders) {
        addWSSegment(new MockWSTextSegment(content, placeholders));
        return this;
    }

    public MockWSSegmentReader build() {
        return new MockWSSegmentReader(this.wsSegments.iterator());
    }
}