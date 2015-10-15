package com.spartansoftware.ws.okapi.filters.mock;

import java.util.Iterator;

import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.component.filter.WSSegmentReader;

/**
 * Mock WSSegmentReader for testing.
 */
public class MockWSSegmentReader implements WSSegmentReader {
    private final Iterator<WSSegment> wsSegments;

    public MockWSSegmentReader(Iterator<WSSegment> segmentIter) {
        this.wsSegments = segmentIter;
    }

    @Override
    public WSSegment read() {
        return wsSegments.next();
    }

    @Override
    public String readContent() {
        return read().getContent();
    }
}
