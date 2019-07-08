package com.spartansoftwareinc.ws.autoactions.hubmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        testWhitespace("{2} what {3} {4} is {5} that {6}", "{2}dangit{3}{4} bobby{5}hill {6}", "{2} dangit {3} {4} bobby {5} hill {6}");
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

        testWhitespace("From the {1}Transfer Funds{2} {3}From{4} drop-down, choose the bank account the credit card was paid from.",

                "Dans la liste déroulante {1}Transférer des fonds{2} {3}depuis{4}, choisissez le compte bancaire d'où le paiement a été émis.",
                "Dans la liste déroulante {1}Transférer des fonds{2} {3}depuis{4}, choisissez le compte bancaire d'où le paiement a été émis.");

        testWhitespace("{1}This is one long sentence{2} in order to {3}test{4} punctuation {5}detection{6} which is {7}quite{8} rare.",

                "{1}This is now punctuated{2}, in order to {3}test!{4} Punctuation {5}detection,{6} while somewhat strange, is {7}important{8}!",
                "{1}This is now punctuated{2}, in order to {3}test!{4} Punctuation {5}detection,{6} while somewhat strange, is {7}important{8}!");

        testWhitespace("{1}This is one long sentence{2} in order to {3}test{4} punctuation {5}detection{6} which is {7}quite{8} rare.",

                "{1}This is now punctuated{2}¿ In order to {3}test¡{4} Punctuation {5}detection¿{6} while somewhat strange, is {7}important{8}¡",
                "{1}This is now punctuated{2}¿ In order to {3}test¡{4} Punctuation {5}detection¿{6} while somewhat strange, is {7}important{8}¡");

        testWhitespace("{1}reorder{2} {3}test{4}.", "{3}test{4} {1}reorder{2}.", "{3}test{4}{1}reorder{2}.");

        // Theoretical sentence beginning/end case
//        testWhitespace("{1}Beginning and end{2}.",
//
//                "Not {1}beginning{2} or end.",
//                "Not {1}beginning{2} or end.");
//        testWhitespace("Not {1}beginning{2} or end.",
//                "{1}Beginning and end{2}.",
//                "{1}Beginning and end{2}.");


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
        // Check regex valid
        String regex = mtWhitespaceRemoval.config.getRegex();
        assertNotNull(regex);
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(" {123} ");
        assertEquals(6, matcher.groupCount());

        // Check other params
        assertEquals(mtWhitespaceRemoval.config.getLeftCaptureGroup(), new Integer(1));
        assertEquals(mtWhitespaceRemoval.config.getLeftIgnoreCaptureGroup(), new Integer(2));
        assertEquals(mtWhitespaceRemoval.config.getCenterCaptureGroup(), new Integer(3));
        assertEquals(mtWhitespaceRemoval.config.getCompareCaptureGroup(), new Integer(4));
        assertEquals(mtWhitespaceRemoval.config.getRightIgnoreCaptureGroup(), new Integer(5));
        assertEquals(mtWhitespaceRemoval.config.getRightCaptureGroup(), new Integer(6));
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
