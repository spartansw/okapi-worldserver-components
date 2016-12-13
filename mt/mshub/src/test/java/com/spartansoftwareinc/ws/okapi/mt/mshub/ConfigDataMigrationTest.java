package com.spartansoftwareinc.ws.okapi.mt.mshub;

import java.io.InputStream;
import java.io.ObjectInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigDataMigrationTest {

    @Test
    public void migrateConfigFromVersion1_4() throws Exception {
        InputStream ris = getClass().getResourceAsStream("/WSMTAdapterConfigurationData-v1_4.bin");
        ObjectInputStream is = new ObjectInputStream(ris);
        Object o = is.readObject();
        assertNotNull(o);
        assertEquals(WSMTAdapterConfigurationData.class, o.getClass());
        WSMTAdapterConfigurationData config = (WSMTAdapterConfigurationData)o;
        assertEquals("testCategory", config.getCategory());
        assertEquals(null, config.getAzureKey());
        assertTrue(config.getIncludeCodes());
        assertEquals(75, config.getMatchScore());
    }
}
