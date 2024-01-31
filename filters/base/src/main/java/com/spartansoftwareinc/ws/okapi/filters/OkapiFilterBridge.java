package com.spartansoftwareinc.ws.okapi.filters;

import com.idiominc.wssdk.asset.WSMarkupSegment;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.asset.WSTextSegment;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OkapiFilterBridge {
    private static final String MISSING_CODE_MSG = "Missing matching Okapi code for placeholder '%s' => '%s', id '%d'";
    private static final String MARKUP_SEG_VALIDATION_ERR_MSG = "Expected WSMarkupSegment but got '%s' instead! " +
            "Content: '%s'";
    private final Logger LOG = LoggerFactory.getLogger(OkapiFilterBridge.class);

    public static final String SEGMENT_SEPARATOR = "[SEGMENT SEPARATOR]";

    public void writeWsSegments(IFilter filter, RawDocument srcRawDocument, WSSegmentWriter wsSegmentWriter,
                                boolean breakSentences) {
        filter.open(srcRawDocument);
        try {
            while (filter.hasNext()) {
                Event event = filter.next();
                if (event.isTextUnit()) {
                    ITextUnit textUnit = event.getTextUnit();
                    postProcessTextContainer(textUnit.getSource());
                    if (textUnit.isTranslatable()) {
                        processTextUnit(wsSegmentWriter, textUnit, breakSentences);
                    }
                }
            }
        } finally {
            filter.close();
        }
    }

    public void exportSegmentsToFile(RawDocument srcRawDocument, WSSegmentReader segmentReader,
                                     LocaleId targetLocale, IFilter filter, IFilterWriter filterWriter) {
        filter.open(srcRawDocument);
        try {
            while (filter.hasNext()) {
                Event event = filter.next();
                if (event.isTextUnit()) {
                    ITextUnit textUnit = event.getTextUnit();
                    postProcessTextContainer(textUnit.getSource());
                    postProcessTextContainer(textUnit.getTarget(targetLocale));
                    if (textUnit.isTranslatable()) {
                        processTextUnit(segmentReader, targetLocale, textUnit);
                    }
                }
                filterWriter.handleEvent(event);
            }
        } finally {
            filter.close();
        }
    }

    protected void postProcessTextContainer(TextContainer source) {

    }

    private void processTextUnit(WSSegmentWriter wsSegmentWriter, ITextUnit textUnit, boolean breakSentences) {
        for (Segment segment : textUnit.getSourceSegments()) {
            SegmentInfoHolder filterSegment = convertToCustomSegment(segment);
            wsSegmentWriter.writeTextSegment(filterSegment.getEncodedText(),
                    filterSegment.getPlaceholders(), breakSentences);
            LOG.info("Writing WS segment '{}'", filterSegment.getEncodedText());
            wsSegmentWriter.writeMarkupSegment(SEGMENT_SEPARATOR);
        }
    }

    private SegmentInfoHolder convertToCustomSegment(Segment segment) {
        StringBuilder stringContent = new StringBuilder();
        List<String> placeholders = new ArrayList<String>();
        TextFragment textFragment = segment.getContent();
        for (int i = 0; i < textFragment.length(); i++) {
            char textChar = textFragment.charAt(i);
            if (TextFragment.isMarker(textChar)) {
                Code code = textFragment.getCode(textFragment.charAt(++i));
                placeholders.add(code.getOuterData());
                stringContent.append(WSFilter.PLACEHOLDER);

            } else {
                stringContent.append(textChar);
            }
        }

        return new SegmentInfoHolder(stringContent.toString(), listToArray(placeholders));
    }

    private String[] listToArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    private void processTextUnit(WSSegmentReader segmentReader, LocaleId targetLocale, ITextUnit textUnit) {
        TextContainer target = parseTarget(textUnit, segmentReader);
        textUnit.setTarget(targetLocale, target);
    }

    private TextContainer parseTarget(ITextUnit textUnit, WSSegmentReader segmentReader) {
        TextContainer textContainer = textUnit.getSource().clone();

        for (Segment okapiSegment : textContainer.getSegments()) {
            WSSegment wsSegment = segmentReader.read();
            List<WSTextSegment> wsTextSegments = new ArrayList<>();
            for (; wsSegment instanceof WSTextSegment; wsSegment = segmentReader.read()) {
                wsTextSegments.add((WSTextSegment) wsSegment);
            }
            processWSSegment(okapiSegment, wsTextSegments);
            require(wsSegment instanceof WSMarkupSegment, String.format(MARKUP_SEG_VALIDATION_ERR_MSG,
                    wsSegment.getClass().toString(), wsSegment.getContent()));
        }

        return textContainer;
    }

    private void processWSSegment(Segment okapiSegment, List<WSTextSegment> wsTextSegments) {
        StringBuilder wsTextContent = new StringBuilder();
        for (WSTextSegment wsTextSegment : wsTextSegments) {
            wsTextContent.append(wsTextSegment.getContent());
        }
        LOG.info("Reading WS segment '{}', Okapi Segment '{}'",
                wsTextContent, okapiSegment.getContent().getCodedText());

        TextFragment okapiFragment = okapiSegment.getContent();
        TextFragment convertedTextFrag = writeToTextSegment(wsTextSegments, okapiFragment);
        okapiSegment.setContent(convertedTextFrag);
    }


    private TextFragment writeToTextSegment(List<WSTextSegment> wsTextSegments, TextFragment okapiTextFragment) {
        TextFragment targetOkapiFragment = new TextFragment();
        List<Code> codes = okapiTextFragment.getCodes();

        for (WSTextSegment wsTextSegment : wsTextSegments) {
            String wsContent = wsTextSegment.getContent();
            WSTextSegmentPlaceholder[] wsPlaceholders = wsTextSegment.getSourcePlaceholders();
            int placeholderCounter = 0;
            char[] wsContentChars = wsContent.toCharArray();
            for (int i = 0; i < wsContentChars.length; i++) {
                char contentChar = wsContentChars[i];
                if (contentChar == '{') {
                    String placeholder = getWSPlaceholder(i, wsContent);
                    if (!placeholder.isEmpty()) {
                        if (placeholderCounter >= wsPlaceholders.length) {
                            throw new RuntimeException(String.format("Tried to grab placeholder index %d (count "
                                            + "starts at 0) when there are only %d placeholders",
                                    placeholderCounter, wsPlaceholders.length));
                        }
                        WSTextSegmentPlaceholder ph = wsPlaceholders[placeholderCounter++];
                        Code code = getMatchingOkapiCode(codes, ph);
                        require(code != null, String.format(MISSING_CODE_MSG, ph.toString(), ph.getText(), ph.getId()));
                        targetOkapiFragment.append(code);
                        i += placeholder.length() - 1;
                    } else {
                        targetOkapiFragment.append(contentChar);
                    }
                } else {
                    targetOkapiFragment.append(contentChar);
                }
            }
        }
        return targetOkapiFragment;
    }


    private String getWSPlaceholder(int charIndex, String wsContent) {
        String code = "";
        int closingBraceIndex = wsContent.indexOf('}', charIndex);
        if (closingBraceIndex >= 0) {
            code = wsContent.substring(charIndex, 1 + closingBraceIndex);
        }
        return (code.matches("\\{[0-9]+\\}")) ? code : "";
    }

    private Code getMatchingOkapiCode(List<Code> codes, WSTextSegmentPlaceholder ph) {
        for (Code code : codes) {
            if (code.getOuterData().equals(ph.getText())) {
                return code;
            }
        }
        return null;
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}



