package com.spartansoftwareinc.ws.autoactions.xliff;

import org.junit.*;

import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.spartansoftware.ws.okapi.filters.mock.MockWSTextSegmentTranslation;

import net.sf.okapi.common.LocaleId;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XLIFFTargetContentAlignerTest {

    @Test
    public void testAlignment() throws Exception {
        XLIFFTargetContentAligner aligner = new XLIFFTargetContentAligner(WSTranslationType.MANUAL_TRANSLATION);
        List<WSTextSegmentTranslation> translation = new ArrayList<>();
        translation.add(t("Hello", "", new String[0], new String[0]));
        translation.add(t("Goodbye {1}my friends{2}.", "", new String[] {"<g id=\"1\">", "</g>"}, new String[0]));
        try (InputStream is = getClass().getResourceAsStream("/test.xlf")) {
            int count = aligner.alignTargetContent(is, "UTF-8", LocaleId.ENGLISH, translation.iterator());
            assertEquals(2, count);
            assertEquals("Bonjour", translation.get(0).getTarget());
            assertEquals("Au revoir {1}mes amis{2}.", translation.get(1).getTarget());
            assertEquals(WSTranslationType.MANUAL_TRANSLATION, translation.get(0).getTranslationType());
            assertEquals(WSTranslationType.MANUAL_TRANSLATION, translation.get(1).getTranslationType());
        }
    }

    private WSTextSegmentTranslation t(String source, String target, String[] sourcePh, String[] targetPh) {
        return new MockWSTextSegmentTranslation(source, target, sourcePh, targetPh);
    }
}
