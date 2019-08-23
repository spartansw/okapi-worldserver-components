package com.spartansoftwareinc.ws.autoactions.hubmt;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.idiominc.wssdk.ais.WSAisManager;
import com.spartansoftwareinc.ws.autoactions.hubmt.config.SegmentWhitespaceFixYAMLConfigV2;

public class SegmentFixMTWhitespaceRemovalV2Test {

    private SegmentFixMTWhitespaceRemovalV2 mtWhitespaceRemoval;
    @Mock
    private WSAisManager aisManager;
    private SegmentWhitespaceFixYAMLConfigV2 config;

    @Before
    public void init() throws Exception {

        // Should fallback on default location
        final String configFileLocation = "";

        mtWhitespaceRemoval = new SegmentFixMTWhitespaceRemovalV2();
        config = mtWhitespaceRemoval.getConfig(aisManager, configFileLocation);
    }

    @Test
    public void fixWhitespaceTest() throws Exception {
        fixTargetThenAssertMatchFixed("Hello {1}my{2} darling", "Salut{1}disque{2}Darling", "Salut {1}disque{2} Darling");
        fixTargetThenAssertMatchFixed("{0} Hello {1}my{2} darling {999}", "{0}Salut{1}disque{2}Darling{999}",
            "{0} Salut {1}disque{2} Darling {999}");
        fixTargetThenAssertMatchFixed("{2} what {3} {4} is {5} that {6}", "{2}dangit{3}{4} bobby{5}hill {6}",
            "{2} dangit {3} {4} bobby {5} hill {6}");
        fixTargetThenAssertMatchFixed("\t\n  {1}   Whoooaaa theree buddy\t\t\n{2}\t{3}\n\nThat ain't{4}\t\tcool!{5}",
            "{1}Test test test{2}{3}ahahhaha{4}awesome!{5}",
            "{1}   Test test test\t\t\n{2}\t{3}\n\nahahhaha{4}\t\tawesome!{5}");

        fixTargetThenAssertMatchFixed("\t \n  {1}     Whoooaaa theree buddy\t\t\n{2}\t  {3}  \n\nThat ain't{4}\t\tcool!{5} ",
            "{1}Test test test{2}{3}ahahhaha{4}awesome!{5}",
            "{1}     Test test test\t\t\n{2}\t  {3}  \n\nahahhaha{4}\t\tawesome!{5}");

        // Every single space found on https://unicode-table.com/en/search/?q=space. There might be some duplicates mixed in.
        fixTargetThenAssertMatchFixed(
            "Hello" + " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    " + "{150002023023}"
                + " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    " + "there",
            "Bonjour{150002023023}Là",
            "Bonjour" + " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    "
                + "{150002023023}" + " \t\n\u2006\u00A0\u2002\u2003\u2003\u202F  \u200B \uDB40\uDC20         　    "
                + "Là");

        fixTargetThenAssertMatchFixed(
            "From the {1}Transfer Funds{2} {3}From{4} drop-down, choose the bank account the credit card was paid from.",

            "Dans la liste déroulante {1}Transférer des fonds{2} {3}depuis{4}, choisissez le compte bancaire d'où le paiement a été émis.",
            "Dans la liste déroulante {1}Transférer des fonds{2} {3}depuis{4}, choisissez le compte bancaire d'où le paiement a été émis.");


        fixTargetThenAssertMatchFixed("From the {1}Transfer Funds{2} {3}From{4} drop-down, choose the bank account the credit card "
            + "was paid from.","Dans la liste déroulante {1}Transférer des fonds{2} {3}depuis{4}, choisissez le compte bancaire d'où le paiement a été émis.","Dans la liste déroulante {1}Transférer des fonds{2} {3}depuis{4}, choisissez le compte bancaire d'où le paiement a été émis.");

        fixTargetThenAssertMatchFixed("{1}Note:{2}The word {3}Sales{4} and {5}Invoice{6}","{1}Nota:{2} Los términos “{3}ventas{4}” y “{5}factura de ingreso{6}” se usarán indistintamente en este artículo.","{1}Nota:{2}Los términos “{3}ventas{4}” y “{5}factura de ingreso{6}” se usarán indistintamente en este artículo.");

    }
    @Test
    public void testPunctuation() throws Exception {
        fixTargetThenAssertMatchFixed(
            "{1}This is one long sentence{2} in order to {3}test{4} punctuation {5}detection{6} which is {7}quite{8} rare.",

            "{1}This is now punctuated{2}, in order to {3}test!{4} Punctuation {5}detection,{6} while somewhat strange, is {7}important{8}!",
            "{1}This is now punctuated{2}, in order to {3}test!{4} Punctuation {5}detection,{6} while somewhat strange, is {7}important{8}!");

        fixTargetThenAssertMatchFixed(
            "{1}This is one long sentence{2} in order to {3}test{4} punctuation {5}detection{6} which is {7}quite{8} rare.",

            "{1}This is now punctuated{2}¿ In order to {3}test¡{4} Punctuation {5}detection¿{6} while somewhat strange, is {7}important{8}¡",
            "{1}This is now punctuated{2}¿ In order to {3}test¡{4} Punctuation {5}detection¿{6} while somewhat strange, is {7}important{8}¡");
    }


    @Test
    public void testBeggingEndSentences() throws Exception {
        fixTargetThenAssertMatchFixed("This sentence has a {1}word{2}.", "This {1}word{2}lost its spaces.",
            "This {1}word{2} lost its spaces.");
        fixTargetThenAssertMatchFixed("This sentence has a {1}word{2}.", "This {1}word{2} has its spaces.",
            "This {1}word{2} has its spaces.");
        fixTargetThenAssertMatchFixed("This sentence has a {1}word{2}.", "This {1}word{2}, now has punctuation.",
            "This {1}word{2}, now has punctuation.");


        // Theoretical sentence beginning/end case
        fixTargetThenAssertMatchFixed("{1}Beginning and end{2}.\t",
            "Not {1}beginning{2} or end.", "Not {1}beginning{2} or end.", "French (France)");
        fixTargetThenAssertMatchFixed("Not {1}beginning{2} or end.", "{1}Beginning and end{2}.", "{1}Beginning and end{2}.");
    }

    @Test
    public void tempTest() throws Exception{
        fixTargetThenAssertMatchFixed("{13}Note:{14}The word {15}Sales{16} and {17}Invoice{18}", "{13} Note: {14} Mot {15} Ventes {16} et {17} Facture {18}", "{13}Note:{14}Mot {15}Ventes{16} et {17}Facture{18}");

        fixTargetThenAssertMatchFixed("This is a simple {1}test{2}.", "C'est un outil simple {1} test {2} .", "C'est un outil simple {1}test{2}.");

        fixTargetThenAssertMatchFixed("This sentence has a {1}word{2}.", "This {1}word{2} has its spaces.",
            "This {1}word{2} has its spaces.");
    }


    /**
     * @param source Original string
     * @param brokenTarget Translated string
     * @param expectedTarget  The expected fixed string
     */
    private void fixTargetThenAssertMatchFixed(final String source, final String brokenTarget, final String expectedTarget) {

        fixTargetThenAssertMatchFixed(source, brokenTarget, expectedTarget, "");
    }

    /**
     * @param source         Original string
     * @param brokenTarget         Translated string
     * @param expectedTarget       The expected fixed string
     * @param targetLanguage The target translation language
     */
    private void fixTargetThenAssertMatchFixed(final String source, final String brokenTarget, final String expectedTarget,
        final String targetLanguage) {

        String test_fixed = mtWhitespaceRemoval.fixSegment(source, brokenTarget, config, targetLanguage);
        assertEquals(expectedTarget, test_fixed);
    }


}
