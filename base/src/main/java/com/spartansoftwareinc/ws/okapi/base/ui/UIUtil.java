package com.spartansoftwareinc.ws.okapi.base.ui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class UIUtil {
    public static String escapeHtml(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
            case '"':
                sb.append("&quot;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

    public static String loadResourceAsString(String htmlContentResourceName) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(
                UIUtil.class.getResourceAsStream(htmlContentResourceName), StandardCharsets.UTF_8)) {
            char[] buf = new char[4096];
            for (int i = reader.read(buf); i != -1; i = reader.read(buf)) {
                sb.append(new String(buf, 0, i));
            }
            return sb.toString();
        }
    }

    public static List<String> getOptionValues(HttpServletRequest request, String parameter) {
        String optionValuesString = request.getParameter(parameter);
        if (optionValuesString == null || optionValuesString.isEmpty()) return Collections.emptyList();
        return Arrays.asList(optionValuesString.split(","));
    }

    public static boolean getBoolean(HttpServletRequest request, String parameter) {
        return "on".equals(request.getParameter(parameter));
    }
}
