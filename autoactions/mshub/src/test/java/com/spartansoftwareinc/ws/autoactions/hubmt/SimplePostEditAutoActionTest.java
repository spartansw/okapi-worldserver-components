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
