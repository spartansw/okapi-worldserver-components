package com.spartansoftwareinc.ws.autoactions.hubmt;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.spartansoftwareinc.ws.autoactions.hubmt.SegmentFixMTWhitespaceRemoval;

public class SegmentFixMTWhitespaceRemovalTest {

    private SegmentFixMTWhitespaceRemoval mtWhitespaceRemoval;

    @Before
    public void init() throws Exception {
        mtWhitespaceRemoval = new SegmentFixMTWhitespaceRemoval();
    }

    @Test
    public void fixWhitespaceTest() throws Exception {
        testWhitespace("Hello {1}my{2} darling", "Salut{1}disque{2}Darling", "Salut {1}disque{2} Darling");
        testWhitespace("{0} Hello {1}my{2} darling {999}", "{0}Salut{1}disque{2}Darling{999}", "{0} Salut {1}disque{2} Darling {999}");
        testWhitespace("{2} what {3} {23 is {} that 3}", "{2}dangit{3}{23 bobby{}hill 3}", "{2} dangit {3} {23 bobby{}hill 3}");
        testWhitespace("\t\n{1}   Whoooaaa theree buddy\t\t\n{2}\t{3}\n\nThat ain't{4}\t\tcool!{5}", "{1}Test test test{2}{3}ahahhaha{4}awesome!{5}", "\t\n{1}   Test test test\t\t\n{2}\t{3}\n\nahahhaha{4}\t\tawesome!{5}");

    }

    /**
     *
     * @param source Original string
     * @param target Translated string
     * @param fixed The proper translated string
     */
    private void testWhitespace(final String source, final String target, final String fixed) {

        String test_fixed = mtWhitespaceRemoval.fixWhitespace(source, target);
        assertEquals(fixed, test_fixed);
    }


}
