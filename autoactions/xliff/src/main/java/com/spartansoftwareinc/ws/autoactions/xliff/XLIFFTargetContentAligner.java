package com.spartansoftwareinc.ws.autoactions.xliff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.xliff.XLIFFFilter;

class XLIFFTargetContentAligner {
    private static final Logger LOG = Logger
            .getLogger(XLIFFTargetContentAligner.class);

    private WSTranslationType injectedTranslationType;
    private int nextPlaceholderId = 1;

    public XLIFFTargetContentAligner(WSTranslationType injectedTranslationType) {
        this.injectedTranslationType = injectedTranslationType;
    }

    // TODO: it would be cool to do some refactoring so this code always
    // stayed in sync with OkapiFilterBridge, upon the behavior of
    // which this depends. The current behavior of that class is to
    // produce one WS text segment for each Okapi Segment object within
    // the source TextContainer.
    public int alignTargetContent(InputStream xliffStream, String encoding, LocaleId srcLocale,
                                  Iterator<WSTextSegmentTranslation> textSegs) throws IOException {
        List<ITextUnit> xliffTus = getEvents(xliffStream, encoding, srcLocale);
        int count = 0;
        for (ITextUnit xliffTu : xliffTus) {
            TextContainer sourceTc = xliffTu.getSource();
            TextContainer targetTc = findFirstTarget(xliffTu);
            if (targetTc == null) {
                for (@SuppressWarnings("unused")
                Segment seg : sourceTc.getSegments()) {
                    skipSegment(textSegs);
                }
                continue;
            }
            for (Segment seg : targetTc.getSegments()) {
                boolean injected = injectNextSegment(seg, textSegs);
                if (injected) {
                    count++;
                }
            }
        }
        return count;
    }

    private List<ITextUnit> getEvents(InputStream is, String encoding, LocaleId srcLocale) throws IOException {
        File tempFile = null;
        XLIFFFilter filter = null;
        try {
            filter = new XLIFFFilter();
            // Filter may need multiple passes, so we need to buffer this to a
            // temp file
            tempFile = FilterUtil.convertContentIntoFile(is, ".xlf");
            RawDocument rd = new RawDocument(tempFile.toURI(), encoding, srcLocale, srcLocale);
            filter.open(rd, false);
            List<ITextUnit> tus = new ArrayList<ITextUnit>();
            while (filter.hasNext()) {
                Event e = filter.next();
                if (e.isTextUnit()) {
                    tus.add(e.getTextUnit());
                }
            }
            return tus;
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            if (filter != null) {
                filter.close();
            }
        }
    }

    TextContainer findFirstTarget(ITextUnit tu) {
        Set<LocaleId> locales = tu.getTargetLocales();
        LocaleId first = locales.iterator().next();
        LOG.debug("First target locale is " + first);
        return tu.getTarget(first);
    }

    void skipSegment(Iterator<WSTextSegmentTranslation> textSegs) {
        checkForMoreWSSegments(textSegs);
        WSTextSegmentTranslation textSeg = textSegs.next();
        LOG.info("Skipping segment [" + textSeg.getSource() + "]");
    }

    /**
     * Update the next text segment translation with content from the XLIFF. Do nothing
     * if the translation was already the same as the XLIFF content.
     * @return true if the translation was updated, false if the translation was already
     * the same as the XLIFF content.
     */
    boolean injectNextSegment(Segment xliffSeg, Iterator<WSTextSegmentTranslation> textSegs) {
        checkForMoreWSSegments(textSegs);
        WSTextSegmentTranslation textSeg = textSegs.next();

        WSTextSegmentData wsMatch = WSTextSegmentData.fromOkapiSegment(xliffSeg);
        String text = assignPlaceholderIds(wsMatch.getText());
        if (textSeg.getTarget() == null || !textSeg.getTarget().equals(text)) {
            LOG.info("Overwriting existing target=[" + textSeg.getTarget() + "] with new target=[" + text + "]");
            textSeg.setTarget(text);
            textSeg.setTranslationType(injectedTranslationType);
            return true;
        } else {
            return false;
        }
    }

    Pattern PH_PATTERN = Pattern.compile("\\{(\\d+)\\}");

    private String assignPlaceholderIds(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                String test = text.substring(i);
                if (test.startsWith(WSFilter.PLACEHOLDER)) {
                    sb.append("{").append(Integer.toString(nextPlaceholderId++)).append("}");
                    i += WSFilter.PLACEHOLDER.length() - 1;
                }
                else {
                    // Escape "fake placeholders"
                    Matcher m = PH_PATTERN.matcher(test);
                    if (m.lookingAt()) {
                        sb.append("\\{").append(m.group(1)).append("\\}");
                        i += m.group().length() - 1;
                    }
                    else {
                        sb.append(c);
                    }
                }
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private void checkForMoreWSSegments(Iterator<WSTextSegmentTranslation> textSegs) {
        if (!textSegs.hasNext()) {
            throw new IllegalStateException("Source XLIFF contains more segments than asset");
        }
    }
}
