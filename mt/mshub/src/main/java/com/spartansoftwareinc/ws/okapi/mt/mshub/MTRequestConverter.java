package com.spartansoftwareinc.ws.okapi.mt.mshub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTRequestConverter {
    private static final Pattern WS_PLACEHOLDER = Pattern.compile("\\{([0-9]+)\\}");
    private static final Pattern CODE_MARKUP = Pattern.compile("<span\\s+ws_id=\"(\\d+)\">\\s*</span>");

    /**
     * Replace WorldServer placeholder codes in &lt;span&gt;...&lt;/span&gt; markup
     * in order to protect them from translation.
     */
    public String addCodeMarkup(String source) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        Matcher m = WS_PLACEHOLDER.matcher(source);
        while (m.find()) {
            sb.append(source.substring(start, m.start()));
            sb.append("<span ws_id=\"");
            sb.append(m.group(1));
            sb.append("\"></span>");
            start = m.end();
        }
        sb.append(source.substring(start, source.length()));
        return sb.toString();
    }

    public String removeCodeMarkup(String s) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        Matcher m = CODE_MARKUP.matcher(s);
        while (m.find()) {
            sb.append(s.substring(start, m.start()));
            sb.append("{").append(m.group(1)).append("}");
            start = m.end();
        }
        sb.append(s.substring(start, s.length()));
        return sb.toString();
    }

}
