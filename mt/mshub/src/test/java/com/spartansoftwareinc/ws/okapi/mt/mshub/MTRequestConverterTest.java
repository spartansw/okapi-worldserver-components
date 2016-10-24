package com.spartansoftwareinc.ws.okapi.mt.mshub;

import org.junit.Test;

import static org.junit.Assert.*;

public class MTRequestConverterTest {
    private MTRequestConverter converter = new MTRequestConverter();

    @Test
    public void testNoCodes() {
        assertEquals("Hello world.", converter.addCodeMarkup("Hello world."));
        assertEquals("Hello world.", converter.removeCodeMarkup(("Hello world.")));
    }

    @Test
    public void testCodes() {
        String s = "Hello <span ws_id=\"1\"></span>world<span ws_id=\"2\"></span>.";
        assertEquals(s, converter.addCodeMarkup("Hello {1}world{2}."));
        assertEquals("Hello {1}world{2}.", converter.removeCodeMarkup(s));
    }

    @Test
    public void testCodesWithLargerIds() {
        String s = "Hello <span ws_id=\"31\"></span>world<span ws_id=\"32\"></span>.";
        assertEquals(s, converter.addCodeMarkup("Hello {31}world{32}."));
        assertEquals("Hello {31}world{32}.", converter.removeCodeMarkup(s));
    }

    @Test
    public void testCodesWithoutText() {
        String s = "<span ws_id=\"1\"></span><span ws_id=\"2\"></span>";
        assertEquals(s, converter.addCodeMarkup("{1}{2}"));
        assertEquals("{1}{2}", converter.removeCodeMarkup(s));
    }

    @Test
    public void testIntroducedWhitespace() {
        assertEquals("{1}{2}", converter.removeCodeMarkup("<span ws_id=\"1\"> </span><span ws_id=\"2\">    </span>"));
    }
}
