package com.spartansoftwareinc.ws.autoactions.hubmt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.spartansoftwareinc.ws.autoactions.hubmt.config.SimplePostEditAutoActionYAMLConfig;

public class SimplePostEditAutoActionTest {

    private SimplePostEditAutoAction simplePostEditAutoAction;
    private SimplePostEditAutoActionYAMLConfig config;

    @Before
    public void init() throws Exception {
        simplePostEditAutoAction = new SimplePostEditAutoAction();
        String configFileLocation = "";
        config = simplePostEditAutoAction.getConfig(null, configFileLocation);
    }

    @Test
    public void fixWhitespaceTest() {
        final List<SimplePostEditAutoActionYAMLConfig.Action> actions = config.getActionsForLanguage("French (France)");

        testUpdate(actions,
            "Retrouvez ci-dessous l'ensemble des articles au sujet de la clôture des comptes dans {1}Google:{2}",
            "Retrouvez ci-dessous l'ensemble des articles au sujet de la clôture des comptes dans {1}Google\u00A0:{2}");
        testUpdate(actions, " Note: Si l'acompte", " Note\u00A0: Si l'acompte");
        testUpdate(actions, "{1}Comment créer une facture d'acompte?{2}",
            "{1}Comment créer une facture d'acompte\u00A0?{2}");
        testUpdate(actions, "Cet article a-t-il été utile\u00A0?", "Cet article a-t-il été utile\u00A0?");
    }


    @Test
    public void testURLs() throws Exception {
        final List<SimplePostEditAutoActionYAMLConfig.Action> frenchFranceActions = config.getActionsForLanguage("French (France)");
        testUpdate(frenchFranceActions, "https://www.youtube.com/watch?v=abc212_AVC",
            "https://www.youtube.com/watch?v=abc212_AVC");
//        testUpdate(frenchFranceActions, "The URL is {1}https\u00A0://www.youtube.com/watch\u00A0?v=abc212_AVC{2}.",
//            "The URL is {1}https://www.youtube.com/watch?v=abc212_AVC{2}.");
        testUpdate(frenchFranceActions, "The URL is {1}https://www.youtube.com/watch?v=abc212_AVC{2}.",
            "The URL is {1}https://www.youtube.com/watch?v=abc212_AVC{2}.");

        final List<SimplePostEditAutoActionYAMLConfig.Action> frenchCanadaActions = config.getActionsForLanguage(
            "French (Canada)");
        testUpdate(frenchCanadaActions, "https://www.youtube.com/watch?v=abc212_AVC",
            "https://www.youtube.com/watch?v=abc212_AVC");
//        testUpdate(frenchFranceActions, "The URL is {1}https\u00A0://www.youtube.com/watch?v=abc212_AVC{2}.",
//            "The URL is {1}https://www.youtube.com/watch?v=abc212_AVC{2}.");
        testUpdate(frenchCanadaActions, "The URL is {1}https://www.youtube.com/watch?v=abc212_AVC{2}. This: is a "
                + "sentence?",
            "The URL is {1}https://www.youtube.com/watch?v=abc212_AVC{2}. This\u00A0: is a "
            + "sentence?");
    }

    /**
     * @param actions The actions to perform
     * @param input   Original string
     * @param fixed   The proper translated string
     */
    private void testUpdate(final List<SimplePostEditAutoActionYAMLConfig.Action> actions, final String input,
        final String fixed) {
        String test_fixed = simplePostEditAutoAction.updateString(input, actions);
        assertEquals(fixed, test_fixed);
    }

}
