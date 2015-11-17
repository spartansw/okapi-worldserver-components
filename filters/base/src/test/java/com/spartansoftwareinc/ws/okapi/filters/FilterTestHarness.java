package com.spartansoftwareinc.ws.okapi.filters;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftware.ws.okapi.filters.mock.FileMockWSNode;
import com.spartansoftware.ws.okapi.filters.mock.ResourceMockWSNode;
import com.spartansoftware.ws.okapi.filters.mock.MockWSLocale;
import com.spartansoftware.ws.okapi.filters.mock.MockWSMarkupSegment;
import com.spartansoftware.ws.okapi.filters.mock.MockWSSegmentReader;
import com.spartansoftware.ws.okapi.filters.mock.MockWSSegmentReaderBuilder;
import com.spartansoftware.ws.okapi.filters.mock.MockWSSegmentWriter;
import com.spartansoftware.ws.okapi.filters.mock.MockWSTextSegment;
import com.spartansoftwareinc.ws.okapi.filters.OkapiFilterBridge;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;

import net.sf.okapi.common.FileCompare;

/**
 * Extendable test harness for verifying that a WSOkapiFilter subclass produces
 * the expected segments and merged targets. This class manages interspersing expected markup
 * segments into the segment stream, so tests don't need to do so themselves.
 * <p>
 * To use, extend this class with your own filter-specific test class and call
 * {@link #parseAndExpectSegments} and {@link #mergeAndVerifyOutput} from your test methods
 * to test extract and merge functionality, respectively.
 */
public class FilterTestHarness {
    private WSFilter filter;

    public FilterTestHarness(WSFilter filter) {
        this.filter = filter;
    }

    public void extractAndExpectSegments(String resourceToTest, Charset charset, SegmentInfoHolder[] expected)
                                            throws IOException {
        WSNode srcNode = new ResourceMockWSNode(resourceToTest, charset, MockWSLocale.ENGLISH);
        List<WSSegment> list = new ArrayList<WSSegment>();
        list.add(new MockWSMarkupSegment(resourceToTest));
        for (SegmentInfoHolder holder : expected) {
            list.add(new MockWSTextSegment(holder.getEncodedText(), holder.getPlaceholders()));
            list.add(new MockWSMarkupSegment(OkapiFilterBridge.SEGMENT_SEPARATOR));
        }
        MockWSSegmentWriter expectWriter = new MockWSSegmentWriter(list);
        filter.parse(mock(WSContext.class), srcNode, expectWriter);
        expectWriter.verifyComplete();
    }

    public void mergeAndVerifyOutput(String sourceResource, String mergedGoldResource, Charset charset,
                                        List<SegmentInfoHolder> translatedSegmentContent) throws IOException {
        MockWSSegmentReader segReader = prepareSegmentReader(sourceResource, translatedSegmentContent);
        File temp = File.createTempFile("merge", ".tmp");
        WSNode tgtNode = new FileMockWSNode(temp, charset, MockWSLocale.FRENCH);
        Map<String, WSNode> sourceAisMapping = new HashMap<String, WSNode>();
        sourceAisMapping.put(sourceResource, new ResourceMockWSNode(sourceResource, charset, MockWSLocale.ENGLISH));
        filter.save(mockContext(mockAisManager(sourceAisMapping)), tgtNode, segReader);
        System.out.println("SourceFile: " + sourceResource + ", Temp file: " + temp);

        FileInputStream fis = new FileInputStream(temp);
        assertTrue(new FileCompare().filesExactlyTheSame(
                getClass().getResourceAsStream(mergedGoldResource), fis));
        fis.close();
        assertTrue(temp.delete());
    }

    private MockWSSegmentReader prepareSegmentReader(String sourceFileResource,
                                                     List<SegmentInfoHolder> translatedSegmentContent) {
        MockWSSegmentReaderBuilder factory = new MockWSSegmentReaderBuilder();
        factory.addMarkupSegment(sourceFileResource);
        for (SegmentInfoHolder s : translatedSegmentContent) {
            factory.addTextSegment(s.getEncodedText(), s.getPlaceholders())
                   .addMarkupSegment(OkapiFilterBridge.SEGMENT_SEPARATOR);
        }
        return factory.build();
    }

    private WSAisManager mockAisManager(Map<String, WSNode> aisPaths) {
        try {
            WSAisManager mgr = mock(WSAisManager.class);
            for (Map.Entry<String, WSNode> e : aisPaths.entrySet()) {
                when(mgr.getNode(e.getKey())).thenReturn(e.getValue());
            }
            return mgr;
        }
        catch (WSAisException e) {
            throw new RuntimeException(e);
        }
    }

    private WSContext mockContext(WSAisManager ais) {
        WSContext context = mock(WSContext.class);
        when(context.getAisManager()).thenReturn(ais);
        return context;
    }
}
