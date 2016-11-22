package com.spartansoftwareinc.ws.autoactions.xliff;

import java.util.ArrayList;
import java.util.List;

import com.idiominc.wssdk.component.filter.WSFilter;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Holds the content required to create a WorldServer WSTextSegment.
 */
class WSTextSegmentData {
    private String text;
    private String[] placeholders;

    private WSTextSegmentData(String text, String[] placeholders) {
        this.text = text;
        this.placeholders = placeholders;
    }

    public String getText() {
        return text;
    }

    public String[] getPlaceholders() {
        return placeholders;
    }

    public static WSTextSegmentData fromOkapiSegment(Segment okapiSeg) {
        // The tricky part here is the code conversion.  Okapi code data needs to
        // be pulled out into a placeholder array for WS, and the Okapi code markers
        // in the source text need to be replaced with WSFilter.PLACEHOLDER markers.
        List<String> wsPlaceholders = new ArrayList<String>();
        StringBuilder wsContent = new StringBuilder();
        TextFragment okapiContent = okapiSeg.getContent();
        List<Code> okapiCodes = okapiContent.getCodes();
        String okapiCodedText = okapiContent.getCodedText();
        int codedTextLen = okapiCodedText.length();
        for (int i = 0; i < codedTextLen; i++) {
            char c = okapiCodedText.charAt(i);
            if (TextFragment.isMarker(c)) {
                int codeIndex = TextFragment.toIndex(okapiCodedText.charAt(++i));
                wsContent.append(WSFilter.PLACEHOLDER);
                Code code = okapiCodes.get(codeIndex);
                // Use the best display data available
                wsPlaceholders.add(code.hasOuterData() ? code.getOuterData() : code.getData());
            }
            else {
                wsContent.append(c);
            }
        }
        return new WSTextSegmentData(wsContent.toString(), wsPlaceholders.toArray(new String[wsPlaceholders.size()]));
    }

}
