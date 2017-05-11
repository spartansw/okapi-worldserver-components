package com.spartansoftwareinc.ws.okapi.mt.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodesMaskerTest {
    private CodesMasker codesMasker = new CodesMasker();

    @Test
    public void testNoCodes() {
        assertEquals("Hello world.", codesMasker.mask("Hello world."));
        assertEquals("Hello world.", codesMasker.unmask(("Hello world.")));
    }

    @Test
    public void testCodes() {
        String s = "Hello <span ws_id=\"1\"></span>world<span ws_id=\"2\"></span>.";
        assertEquals(s, codesMasker.mask("Hello {1}world{2}."));
        assertEquals("Hello {1}world{2}.", codesMasker.unmask(s));
    }

    @Test
    public void testCodesWithLargerIds() {
        String s = "Hello <span ws_id=\"31\"></span>world<span ws_id=\"32\"></span>.";
        assertEquals(s, codesMasker.mask("Hello {31}world{32}."));
        assertEquals("Hello {31}world{32}.", codesMasker.unmask(s));
    }

    @Test
    public void testCodesWithoutText() {
        String s = "<span ws_id=\"1\"></span><span ws_id=\"2\"></span>";
        assertEquals(s, codesMasker.mask("{1}{2}"));
        assertEquals("{1}{2}", codesMasker.unmask(s));
    }

    @Test
    public void testIntroducedWhitespace() {
        assertEquals("{1}{2}", codesMasker.unmask("<span ws_id=\"1\"> </span><span ws_id=\"2\">    </span>"));
    }

    @Test
    public void testMessedUpTags() {
        String in = "<span ws_id=\"20\"></span>-path1 <tag1>-path2 " +
                    "<tag2><span ws_id=\"21\"></span><span ws_id=\"22\"></span>";
        String out = "<span ws_id=\"20\"></span>-path1 <tag1>-path2 <tag2> " +
                   "<span ws_id=\"21\"><span ws_id=\"22\"></span></span> </tag2> </tag1>";
        String wsIn = "{20}-path1 <tag1>-path2 <tag2>{21}{22}";
        String wsOut = "{20}-path1 <tag1>-path2 <tag2> {21}{22} </tag2> </tag1>";
        assertEquals(in, codesMasker.mask(wsIn));
        assertEquals(wsOut, codesMasker.unmask(out));
    }
}
