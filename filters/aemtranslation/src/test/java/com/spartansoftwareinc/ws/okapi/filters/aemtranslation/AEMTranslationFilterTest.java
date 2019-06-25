package com.spartansoftwareinc.ws.okapi.filters.aemtranslation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftware.ws.okapi.filters.mock.MockWSSegmentReader;
import com.spartansoftware.ws.okapi.filters.mock.MockWSSegmentReaderBuilder;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.OkapiFilterBridge;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;

public class AEMTranslationFilterTest {

    @Test
    public void testFilterSimple() throws Exception {


        // Test 1
        String testFile1 = "/test_aem_translation_file.xml";
        List<SegmentInfoHolder> segments = new ArrayList<>();
        addSegment(segments, "Test");
        addSegment(segments, "This is a great test. Or is it?" + WSFilter.PLACEHOLDER + " Haha that's fine.",
                "<br/>");
        addSegment(segments, "Good:");
        addSegment(segments, "None");
        addSegment(segments, "Bad:");
        addSegment(segments, WSFilter.PLACEHOLDER + "big" + WSFilter.PLACEHOLDER,
                "<strong>", "</strong>");
        testExtraction(testFile1, segments);

    }

    @Test
    public void testFilterComplex() throws Exception {


        // Test 1
        String testFile1 = "/test_aem_translation_file2.xml";
        List<SegmentInfoHolder> segments = new ArrayList<>();
        addSegment(segments, "TEST Title");
        addSegment(segments, "Some conent");
        addSegment(segments, "More content");
        addSegment(segments, "Application Note");
        addSegment(segments, "Abstract");
        addSegment(segments, "Sentence one.");
        addSegment(segments, WSFilter.PLACEHOLDER + "1" + WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER + "2" + WSFilter.PLACEHOLDER, "<sup>", "</sup>", "<sup>", "</sup>");
        addSegment(segments, " Sentence two.");
        addSegment(segments, WSFilter.PLACEHOLDER + "3" + WSFilter.PLACEHOLDER, "<sup>", "</sup>");
        addSegment(segments, "That was a great sentence. Now here's another! " + WSFilter.PLACEHOLDER + "4,5,6" + WSFilter.PLACEHOLDER, "<sup>", "</sup>");
        addSegment(segments, "These things are evil:");
        addSegment(segments, "See");
        addSegment(segments, "Hear");
        addSegment(segments, "Speak");
        addSegment(segments, "x");
        addSegment(segments, "y");
        addSegment(segments, "a");
        addSegment(segments, "b");
        addSegment(segments, "This isn't you're basic test!  ");


        testExtraction(testFile1, segments);

    }


    @Test
    public void testFilterManifest() throws Exception {


        // Test 1
        String testFile1 = "/test_aem_translation_file3.xml";
        List<SegmentInfoHolder> segments = new ArrayList<>();
        testExtraction(testFile1, segments);

    }

    @Test
    public void superScriptTest() throws Exception {


        // Test 1
        final String inputFile = "/super_script_test.xml";
        final String outputFile = "/super_script_test_output.xml";

        List<SegmentInfoHolder> segments = new ArrayList<>();
        addSegment(segments, "Test words.");
        addSegment(segments, WSFilter.PLACEHOLDER + "1-5" + WSFilter.PLACEHOLDER, "<sup>", "</sup>");
        addSegment(segments, " well whadda you know.");
        addSegment(segments, WSFilter.PLACEHOLDER + "6-10" + WSFilter.PLACEHOLDER, "<sup>", "</sup>");
        testExtraction(inputFile, segments);


        List<SegmentInfoHolder> segmentsOutput = new ArrayList<>();
        addSegment(segmentsOutput, "Test words.");
        addSegment(segmentsOutput, "{1}1-5{2}", "<sup>", "</sup>");
        addSegment(segmentsOutput, " well whadda you know.");
        addSegment(segmentsOutput, "{1}6-10{2}", "<sup>", "</sup>");
        testMerging(inputFile, outputFile, segmentsOutput);


    }


    private void testExtraction(String inputFile, List<SegmentInfoHolder> segments) throws Exception {

        final FilterTestHarness harness = new FilterTestHarness(new AEMTranslationFilter());
        SegmentInfoHolder[] segmentsArray = new SegmentInfoHolder[segments.size()];
        int i = 0;
        for (SegmentInfoHolder s : segments) {
            segmentsArray[i] = s;
            i++;
        }

        // Create segments
        harness.extractAndExpectSegments(inputFile, StandardCharsets.UTF_8, segmentsArray);

    }

    private void testMerging(String inputFile, String outputFile, List<SegmentInfoHolder> segments) throws IOException {
        final FilterTestHarness harness = new FilterTestHarness(new AEMTranslationFilter());
        SegmentInfoHolder[] segmentsArray = new SegmentInfoHolder[segments.size()];
        int i = 0;
        for (SegmentInfoHolder s : segments) {
            segmentsArray[i] = s;
            i++;
        }

        // Merge Segments
        harness.mergeAndVerifyOutput(inputFile, outputFile, StandardCharsets.UTF_8, segments);
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

    private URL loadConfigFromResources(String configFile) throws WSException {
        URL resource = getClass().getResource(configFile);
        if (resource == null) {
            throw new WSException(new FileNotFoundException("Unable to load Resource " + configFile
                    + " stored in package resources."));
        }
        return resource;
    }

    private void addSegment(List<SegmentInfoHolder> segments, String text, String... placeholders) {
        segments.add(new SegmentInfoHolder(text, placeholders));
    }


}

