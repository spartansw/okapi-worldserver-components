package com.spartansoftwareinc.ws.okapi.filters;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.asset.WSMarkupSegment;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.asset.WSTextSegment;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

public class OkapiMultiFilterBridge {
    private static final String MISSING_CODE_MSG = "Missing matching Okapi code for placeholder '%s' => '%s', id '%d'";
    private static final String MARKUP_SEG_VALIDATION_ERR_MSG = "Expected WSMarkupSegment but got '%s' instead! " +
            "Content: '%s'";
    private final Logger LOG = LoggerFactory.getLogger(OkapiMultiFilterBridge.class);

    private static final String SEGMENT_SEPARATOR = "[SEGMENT SEPARATOR]";


    /**
     * Takes a ITextUnit and builds segments in WorldServer.
     * @param wsSegmentWriter
     * @param textUnit
     */
    public void processTextUnit(WSSegmentWriter wsSegmentWriter, ITextUnit textUnit) {
        for (Segment segment : textUnit.getSourceSegments()) {
            SegmentInfoHolder filterSegment = convertToCustomSegment(segment);
            wsSegmentWriter.writeTextSegment(filterSegment.getEncodedText(),
                    filterSegment.getPlaceholders());
            LOG.info("Writing WS segment '{}'", filterSegment.getEncodedText());
            wsSegmentWriter.writeMarkupSegment(SEGMENT_SEPARATOR);
        }
    }

    private SegmentInfoHolder convertToCustomSegment(Segment segment) {
        StringBuilder stringContent = new StringBuilder();
        List<String> placeholders = new ArrayList<String>();
        TextFragment textFragment = segment.getContent();
        LOG.warn("Converting Segment to Worldserver SegmentInfoHolder: {}", textFragment.getCodedText());
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

    /**
     * Reads segments in Worldserver and updates the TextUnit with the translated text.
     * @param segmentReader
     * @param targetLocale
     * @param textUnit
     */
    public void processTextUnit(WSSegmentReader segmentReader, LocaleId targetLocale, ITextUnit textUnit) {
        TextContainer target = parseTarget(textUnit, segmentReader);
        textUnit.setTarget(targetLocale, target);
    }

    public void processTextUnits(List<ITextUnit> textUnits, WSSegmentReader segmentReader, LocaleId targetLocale) {
        for (ITextUnit textUnit : textUnits) {
            processTextUnit(segmentReader, targetLocale, textUnit);
        }
    }

    private TextContainer parseTarget(ITextUnit textUnit, WSSegmentReader segmentReader) {
        final TextContainer textContainer = textUnit.getSource().clone();

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
            final String content = wsTextSegment.getContent();
            wsTextContent.append(content);
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
            WSTextSegmentPlaceholder[] wsPlaceholders = wsTextSegment.getTargetPlaceholders();
            int placeholderCounter = 0;
            char[] wsContentChars = wsContent.toCharArray();
            for (int i = 0; i < wsContentChars.length; i++) {
                char contentChar = wsContentChars[i];
                if (contentChar == '{') {
                    String placeholder = getWSPlaceholder(i, wsContent);
                    if (!placeholder.isEmpty()) {
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



