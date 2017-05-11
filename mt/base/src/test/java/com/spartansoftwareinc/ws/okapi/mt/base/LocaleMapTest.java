package com.spartansoftwareinc.ws.okapi.mt.base;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Before;

import net.sf.okapi.common.LocaleId;

public class LocaleMapTest {
    private LocaleMap lm = new LocaleMap();

    @Before
    public void setup() {
        lm.add(new LocaleId("in"), new LocaleId("id"));
        lm.add(new LocaleId("pat", "AN"), new LocaleId("fr", "FR"));
    }

    @Test
    public void testMapping() {
        assertEquals(new LocaleId("id"), lm.getMappedLocale(new LocaleId("in")));
        assertEquals(new LocaleId("fr", "FR"), lm.getMappedLocale(new LocaleId("pat", "AN")));
    }

    @Test
    public void testNoMatchMeansIdentifyTranformation() {
        assertEquals(new LocaleId("de", "DE"), lm.getMappedLocale(new LocaleId("de", "DE")));
    }

    @Test
    public void testCaseSensitivity() {
        assertEquals(new LocaleId("id"), lm.getMappedLocale(new LocaleId("IN")));
        assertEquals(new LocaleId("de", "DE"), lm.getMappedLocale(new LocaleId("DE", "DE")));
    }

    @Test
    public void testPartialMatchesDontCount() {
        assertEquals(new LocaleId("pat"), lm.getMappedLocale(new LocaleId("pat")));
    }

    @Test
    public void fromFile() throws Exception {
        lm = LocaleMap.load(new StringReader("fr=de\nes-AR=es-419"));
        assertEquals(2, lm.map.size());
        assertEquals(new LocaleId("de"), lm.getMappedLocale(new LocaleId("fr")));
        assertEquals(new LocaleId("es", "419"), lm.getMappedLocale(new LocaleId("es", "AR")));
        // no entry
        assertEquals(new LocaleId("zh"), lm.getMappedLocale(new LocaleId("zh")));
    }

    @Test
    public void testBeForgivingAboutUnderscoreInsteadOfDash() throws Exception {
        lm = LocaleMap.load(new StringReader("es_AR=es-419"));
        assertEquals(new LocaleId("es", "419"), lm.getMappedLocale(new LocaleId("es", "AR")));
    }

    @Test
    public void testSkipMalformedEntries() throws Exception {
        lm = LocaleMap.load(new StringReader("abc\nfoo=\n=bar"));
        assertEquals(0, lm.map.size());
    }

    @Test
    public void testSkipUnparseableLocales() throws Exception {
        lm = LocaleMap.load(new StringReader("zh&cn=fr"));
        assertEquals(0, lm.map.size());
    }
}
