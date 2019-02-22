package com.spartansoftware.ws.okapi.filters.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.idiominc.wssdk.asset.WSMarkupSegment;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.asset.WSTextSegment;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;

/**
 * Mock WSSegmentWriter for testing. The writer is provided a list of
 * expected segments on creation, and verifies that each segment written
 * to it matches its expectations.
 */
public class MockWSSegmentWriter implements WSSegmentWriter {
    @SuppressWarnings("unused")
    private List<WSSegment> expected; // For debugging
    private Iterator<WSSegment> expectedIt;

    public MockWSSegmentWriter(List<WSSegment> expected) {
        this.expected = expected;
        this.expectedIt = expected.iterator();
    }

    private WSSegment nextSegment() {
        assertTrue("Unexpected segment", expectedIt.hasNext());
        return expectedIt.next();
    }

    public void verifyComplete() {
        assertTrue("Not all expected segments were written", !expectedIt.hasNext());
    }

    @Override
    public WSTextSegment[] writeTextSegment(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTextSegment[] writeTextSegment(String string, boolean bln) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSTextSegment[] writeTextSegment(String encodedText, String[] placeholders) {
        WSSegment next = nextSegment();
        assertTrue(next instanceof WSTextSegment);
        WSTextSegment text = (WSTextSegment)next;
        assertEquals(text.getContent(), encodedText);
        List<WSTextSegmentPlaceholder> testPhs = new ArrayList<>();
        for (String s : placeholders) {
            testPhs.add(new MockWSTextSegmentPlaceholder(s));
        }
        assertEquals(Arrays.asList(text.getSourcePlaceholders()), testPhs);
        // This is not entirely correct, as we don't fix the placeholders, etc
        return new WSTextSegment[] { new MockWSTextSegment(encodedText, placeholders) };
    }

    @Override
    public WSTextSegment[] writeTextSegment(String encodedText, String[] placeholders, boolean splitIntoSentences) {
        return writeTextSegment(encodedText, placeholders);
    }

    @Override
    public WSTextSegment[] writeTextSegment(String string, String[] strings, String string1, boolean bln) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeMarkupSegment(String content) {
        WSSegment next = nextSegment();
        assertTrue(next instanceof WSMarkupSegment);
        assertEquals(((WSMarkupSegment)next).getContent(), content.replaceFirst("-[\\d\\w]+\\.save", "")); // Ignore the snapshot file suffix.
    }

    @Override
    public void writeMarkupSegment(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConsolidateSinglePlaceholders(boolean bln) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConsolidateSinglePlaceholders() {
        throw new UnsupportedOperationException();
    }
}