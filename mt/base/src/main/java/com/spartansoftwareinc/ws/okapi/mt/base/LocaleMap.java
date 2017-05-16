package com.spartansoftwareinc.ws.okapi.mt.base;

import net.sf.okapi.common.LocaleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class LocaleMap {
    private static final Logger log = LoggerFactory.getLogger(LocaleMap.class);

    Map<LocaleId, LocaleId> map = new HashMap<>();

    public void add(LocaleId originalLocale, LocaleId mappedLocale) {
        map.put(originalLocale, mappedLocale);
    }

    public LocaleId getMappedLocale(LocaleId locale) {
        LocaleId l = map.get(locale);
        return (l != null) ? l : locale;
    }

    public static LocaleMap load(Reader reader) throws IOException {
        BufferedReader r = new BufferedReader(reader);
        LocaleMap lm = new LocaleMap();

        for (String line = r.readLine(); line != null; line = r.readLine()) {
            String[] parts = line.replace('_', '-').split("=");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            if (parts.length != 2 || "".equals(parts[0]) || "".equals(parts[1])) {
                log.warn("Skipping malformed locale mapping: '{}'", line);
                continue;
            }
            try {
                lm.add(LocaleId.fromBCP47(parts[0], true), LocaleId.fromBCP47(parts[1], true));
            } catch (IllegalArgumentException e) {
                log.warn("Skipping locale mapping '{}'. Reason: {}", line, e.getMessage());
            }
        }

        return lm;
    }
}
