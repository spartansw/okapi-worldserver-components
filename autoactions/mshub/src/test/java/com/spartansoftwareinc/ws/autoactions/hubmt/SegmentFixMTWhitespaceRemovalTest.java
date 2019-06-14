package com.spartansoftwareinc.ws.autoactions.hubmt;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class SegmentFixMTWhitespaceRemovalTest {

    private SegmentFixMTWhitespaceRemoval mtWhitespaceRemoval;

    @Before
    public void init() throws Exception {
        mtWhitespaceRemoval = new SegmentFixMTWhitespaceRemoval();
        mtWhitespaceRemoval.setConfig(null);
    }

    @Test
    public void fixWhitespaceTest() throws Exception {
        testWhitespace("Hello {1}my{2} darling", "Salut{1}disque{2}Darling", "Salut {1}disque{2} Darling");
        testWhitespace("{0} Hello {1}my{2} darling {999}", "{0}Salut{1}disque{2}Darling{999}", "{0} Salut {1}disque{2} Darling {999}");
        testWhitespace("{2} what {3} {23 is {} that 3}", "{2}dangit{3}{23 bobby{}hill 3}", "{2} dangit {3} {23 bobby{}hill 3}");
        testWhitespace("\t\n  {1}   Whoooaaa theree buddy\t\t\n{2}\t{3}\n\nThat ain't{4}\t\tcool!{5}", "{1}Test test test{2}{3}ahahhaha{4}awesome!{5}", "\t\n  {1}   Test test test\t\t\n{2}\t{3}\n\nahahhaha{4}\t\tawesome!{5}");

        testWhitespace("\t \n  {1}     Whoooaaa theree buddy\t\t\n{2}\t  {3}  \n\nThat ain't{4}\t\tcool!{5} ", "{1}Test test test{2}{3}ahahhaha{4}awesome!{5}", "\t \n  {1}     Test test test\t\t\n{2}\t  {3}  \n\nahahhaha{4}\t\tawesome!{5} ");

        // Every single space found on https://unicode-table.com/en/search/?q=space. There might be some duplicates mixed in.
        testWhitespace("Hello"+
                " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    " +
                        "{150002023023}" +
                        " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    "+
                        "there",
                "Bonjour{150002023023}Là",
                "Bonjour"+
                " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    " +
                        "{150002023023}" +
                        " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    "+
        "Là");


    }

    @Test
    public void testConfigLoading() throws Exception {

        InputStream config = mtWhitespaceRemoval.loadConfigFromResources();
        java.util.Scanner s = new java.util.Scanner(config).useDelimiter("\\A");
        StringBuilder configString = new StringBuilder();
        while (s.hasNext()) {
            configString.append(s.next());
        }
        config.close();

        assertEquals("regex: \"(?:[\\\\s\\\\h\\\\v\\\\u2009\\\\u200B]|\\\\uDB40\\\\uDC20)*\\\\{(\\\\d+)\\\\}(?:[\\\\s\\\\h\\\\v\\\\u2009\\\\u200B]|\\\\uDB40\\\\uDC20)*\"\n" +
                "captureGroup: 1", configString.toString());

    }

    /**
     * @param source Original string
     * @param target Translated string
     * @param fixed  The proper translated string
     */
    private void testWhitespace(final String source, final String target, final String fixed) {

        String test_fixed = mtWhitespaceRemoval.fixWhitespace(source, target);
        assertEquals(fixed, test_fixed);
    }


}
