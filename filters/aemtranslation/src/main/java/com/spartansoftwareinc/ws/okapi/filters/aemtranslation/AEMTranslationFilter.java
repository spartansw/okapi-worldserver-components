package com.spartansoftwareinc.ws.okapi.filters.aemtranslation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.OkapiMultiFilterBridge;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiMultiFilter;
import com.spartansoftwareinc.ws.okapi.filters.model.FilterTreeNode;
import com.spartansoftwareinc.ws.okapi.filters.model.FilterTreeRawDocumentNode;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.json.JSONFilter;
import net.sf.okapi.filters.xml.XMLFilter;


public class AEMTranslationFilter extends WSOkapiMultiFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AEMTranslationFilter.class);
    private static final String FILTER_NAME = "Okapi AEM Translation File Filter";
    private static final String FILTER_DESCRIPTION = "Filters an AEM Translation File. The file is an XML file which can contain JSON and HTML.";

    private static final String XML_ITS_CONFIG_FILE = "aem_xml_translation_config.xml";
    private static final String HTML_CONFIG_FILE = "html_filter_params.yml";

    private enum SEGMENT_STATE {SENTENCE_SEGMENT, SUPERSCRIPT_SEGMENT}


    public String getName() {
        return FILTER_NAME;
    }

    public String getDescription() {
        return FILTER_DESCRIPTION;
    }

    public String getVersion() {
        return Version.BANNER;
    }

    protected FilterTreeRawDocumentNode buildTree(RawDocument srcRawDocument) {

        final IFilter xmlFilter = getXMLFilter();
        final IFilter jsonFilter = getJSONFilter();
        final IFilter htmlFilter = getHTMLFilter();

        // Apply first layer filter (XML Filter)
        final FilterTreeRawDocumentNode root = new FilterTreeRawDocumentNode(srcRawDocument);
        root.applyFilterAndCreateChildren(filterBridge, xmlFilter);

        // If filter could not be applied, then document can't be parsed and need to skip
        if (root.getChildren().size() == 0) {
            return root;
        }

        // Apply second layer filter (JSON Filter)
        root.applyFilterOnLeavesAndCreateChildren(filterBridge, jsonFilter);

        // Apply third layer filter (HTML Filter)
        root.applyFilterOnLeavesAndCreateChildren(filterBridge, htmlFilter);

        // Fix <sup> elements at the end of sentences to be segmented alone
        for (FilterTreeNode leaf : root.getLeaves()) {
            ITextUnit textUnit = leaf.getTextUnit();
            TextContainer segments = resegmentSupPlaceholders(textUnit.getSourceSegments());
            textUnit.setSource(segments);
        }

        return root;

    }

    /**
     * Resentments segments from a {@link ITextUnit}'s {@link ISegments} so that the superscripts at the end of a sentence
     * are in their own segments.
     * <p>
     * Example input segmentation:
     * 1. I see a black dog running down the street.<sup>1</sup> It appears to be lost.
     * <p>
     * Example output Segmentation:
     * 1. I see a black dog running down the street.
     * 2. <sup>1</sup>
     * 3. It appears to be lost.
     *
     * @param segments The segments to resegnmented. Usually the result of {@link ITextUnit#getSourceSegments()}
     * @return The newly segmented content.
     */
    private TextContainer resegmentSupPlaceholders(ISegments segments) {

        final String LEFT_SUP = "<sup>";
        final String RIGHT_SUP = "</sup>";
        final char PERIOD = '.';

        final TextContainer textContainer = new TextContainer();
        final Stack<String> supStack = new Stack<>();
        boolean rightAfterPeriod = false;
        int segmentIdCount = 1;

        for (Segment segment : segments) {
            SEGMENT_STATE state = SEGMENT_STATE.SENTENCE_SEGMENT;
            TextFragment newContent = new TextFragment();

            final TextFragment currentContent = segment.getContent();
            for (int i = 0; i < currentContent.length(); i++) {
                char textChar = currentContent.charAt(i);

                // If a placeholder, check if it's right after a period and is the <sup> placeholder.
                // If it is, then start a new segment. Once a character is encountered that is outside of the <sup></sup>
                // Element, then we start another segment.
                if (TextFragment.isMarker(textChar)) {
                    final Code code = currentContent.getCode(currentContent.charAt(++i));
                    String outerData = code.getOuterData();
                    if (rightAfterPeriod && LEFT_SUP.equals(outerData)) {
                        state = SEGMENT_STATE.SUPERSCRIPT_SEGMENT;
                        supStack.push(outerData);
                        textContainer.append(new Segment(String.valueOf(segmentIdCount), newContent));
                        segmentIdCount += 1;
                        newContent = new TextFragment();
                    } else if (LEFT_SUP.equals(outerData)) {
                        supStack.push(outerData);
                    } else if (RIGHT_SUP.equals(outerData) && state == SEGMENT_STATE.SUPERSCRIPT_SEGMENT) {
                        supStack.pop();
                    }
                    newContent.append(code.clone());
                } else {

                    // The first character encountered outside of a <sup></sup> element, after in its own segment,
                    // means we have reached the end of that segment and need to start another.
                    if (state == SEGMENT_STATE.SUPERSCRIPT_SEGMENT && supStack.empty()) {
                        textContainer.append(new Segment(String.valueOf(segmentIdCount), newContent));
                        segmentIdCount += 1;

                        newContent = new TextFragment();
                        state = SEGMENT_STATE.SENTENCE_SEGMENT;
                    }
                    newContent.append(textChar);
                    rightAfterPeriod = PERIOD == textChar;
                }
            }

            textContainer.append(new Segment(String.valueOf(segmentIdCount), newContent));
            segmentIdCount += 1;

        }
        textContainer.setHasBeenSegmentedFlag(true);
        return textContainer;
    }

    @Override
    protected void reconstructTreeFilters(FilterTreeRawDocumentNode root, OutputStream
            targetOutput, OkapiMultiFilterBridge filterBridge) {
        root.setTranslationOutput(targetOutput);
        root.updateEntireTreesTranslations(filterBridge);
    }


    /**
     * XML Filter is configured specifically for AEM Translation file using ITS rules from the config file {@value XML_ITS_CONFIG_FILE}.
     *
     * @return
     */
    private IFilter getXMLFilter() {
        XMLFilter xmlFilter = new XMLFilter();
        net.sf.okapi.filters.its.Parameters parameters = xmlFilter.getParameters();
        try {
            parameters.fromString(loadConfigFromResources(XML_ITS_CONFIG_FILE));
        } catch (IOException e) {
            LOG.error("Could not load config file {} for XMLFilter", XML_ITS_CONFIG_FILE);
        }
        return xmlFilter;
    }

    /**
     * JSON Filter configured to extract stand alone strings, like those in arrays.
     *
     * @return
     */
    private IFilter getJSONFilter() {
        JSONFilter jsonFilter = new JSONFilter();
        net.sf.okapi.filters.json.Parameters parameters = jsonFilter.getParameters();
        parameters.setExtractStandalone(true);
        return jsonFilter;
    }

    /**
     * HTML Filter is configured specifically for AEM Translation file using config file {@value HTML_CONFIG_FILE}.
     *
     * @return
     */
    private IFilter getHTMLFilter() {
        HtmlFilter htmlFilter = new HtmlFilter();
        IParameters parameters = htmlFilter.getParameters();
        try {
            parameters.fromString(loadConfigFromResources(HTML_CONFIG_FILE));
        } catch (IOException e) {
            LOG.error("Could not load config file {} for HtmlFilter", HTML_CONFIG_FILE);
        }
        return htmlFilter;
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    private String loadConfigFromResources(String configFile) throws IOException {
        InputStream resource = getClass().getResourceAsStream(configFile);
        if (resource == null) {
            throw new FileNotFoundException("Unable to load Resource " + configFile
                    + " stored in package resources.");
        }

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }


}

