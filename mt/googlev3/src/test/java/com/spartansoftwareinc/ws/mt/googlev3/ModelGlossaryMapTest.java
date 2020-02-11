package com.spartansoftwareinc.ws.mt.googlev3;

import static org.junit.Assert.*;

import org.junit.Test;

public class ModelGlossaryMapTest {
    @Test
    public void testBasic() {
        ModelGlossaryMap map = new ModelGlossaryMap();
        map.add("en/fr", "fr-model", "fr-glossary");
        map.add("en/ja", "ja-model", "ja-glossary");
        map.add("en/zh-TW", "zhTW-model", "zhTW-glossary");
        map.add("en/pt", null, "pt-glossary");
        map.add("en/it", "it-model", null);

        ModelGlossaryMap.ModelGlossary entry;
        assertNull(map.get("fr/en"));
        entry = map.get("en/ja");
        assertNotNull(entry);
        assertEquals("ja-model", entry.modelId);
        assertEquals("ja-glossary", entry.glossaryId);
        entry = map.get("en/pt");
        assertNotNull(entry);
        assertNull(entry.modelId);
        assertEquals("pt-glossary", entry.glossaryId);
    }

    @Test
    public void testLoad() {
        ModelGlossaryMap map = new ModelGlossaryMap();
        map.loadMap("{\"en/fr\":{\"modelId\":\"fr-model\",\"glossaryId\":\"fr-glossary\"}}");
        ModelGlossaryMap.ModelGlossary entry;
        entry = map.get("en/ja");
        assertNull(entry);
        entry = map.get("en/fr");
        assertNotNull(entry);
        assertEquals("fr-glossary", entry.glossaryId);
    }
}