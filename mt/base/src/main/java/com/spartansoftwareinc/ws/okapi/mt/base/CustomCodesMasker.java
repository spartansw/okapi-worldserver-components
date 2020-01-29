package com.spartansoftwareinc.ws.okapi.mt.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Code Masker with customizable regex for code detection.
 * This uses &lt;div ws_id="nn"&gt; instead of &lt;span ws_id="nn"&gt;
 */
public class CustomCodesMasker extends CodesMasker {
    private static final Pattern WS_PLACEHOLDER = Pattern.compile("\\{([0-9]+)\\}");
    private Pattern MS_MASKED_CODE_MARKUP;

    /**
     * Constructor that uses the specified regex to find the &lt;div&gt; elements
     * inserted by us. The regex must match with &lt;div ws_id="nn"&gt;.
     * @param regex
     */
    public CustomCodesMasker(String regex) {
        this.MS_MASKED_CODE_MARKUP = Pattern.compile(regex);
    }

    /**
     * Replace WorldServer placeholder code curly braces with parenthesis
     * in order to protect them from translation.
     */
    public String mask(String source) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        Matcher m = WS_PLACEHOLDER.matcher(source);
        while (m.find()) {
            sb.append(source.substring(start, m.start()));
            sb.append("<div ws_id=\"");
            sb.append(m.group(1));
            sb.append("\"></div>");
            start = m.end();
        }
        sb.append(source.substring(start, source.length()));
        return sb.toString();
    }

    public String unmask(String s) {
        return removeWellformedCodes(s);
    }

    private String removeWellformedCodes(String s) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        Matcher m = MS_MASKED_CODE_MARKUP.matcher(s);
        while (m.find()) {
            sb.append(s.substring(start, m.start()));
            sb.append("{").append(m.group(1)).append("}");
            start = m.end();
        }
        sb.append(s.substring(start, s.length()));
        return sb.toString();
    }
}
