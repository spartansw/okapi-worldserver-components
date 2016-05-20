package com.spartansoftwareinc.ws.okapi.mt.mshub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class MTRequestConverter {
    private static final Pattern WS_PLACEHOLDER = Pattern.compile("\\{([0-9]+)\\}");

    /**
     * Convert a MT request string (which may contain WorldServer placeholder codes)
     * into an Okapi TextFragment with equivalent codes.  All codes are of type
     * PLACEHOLDER, with IDs set to the value within the WorldServer code (eg "{5}" -> 5).
     */
    public TextFragment toTextFragment(String source) {
        TextFragment tf = new TextFragment();
        int start = 0;
        Matcher m = WS_PLACEHOLDER.matcher(source);
        while (m.find()) {
            tf.append(source.substring(start, m.start()));
            int phId = Integer.valueOf(m.group(1));
            Code code = new Code();
            code.setTagType(TagType.PLACEHOLDER);
            code.setId(phId);
            tf.append(code);
            start = m.end();
        }
        tf.append(source.substring(start, source.length()));
        return tf;
    }

    public String fromTextFragment(TextFragment tf) {
        StringBuilder sb = new StringBuilder();
        char[] buf = tf.getCodedText().toCharArray();
        for (int i = 0; i < buf.length; i++) {
            char c = buf[i];
            if (TextFragment.isMarker(c)) {
                Code code = tf.getCode(tf.charAt(++i));
                sb.append("{").append(code.getId()).append("}");
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
