package com.spartansoftwareinc.ws.okapi.mt.mshub;

import java.io.InputStream;
import java.io.ObjectInputStream;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigDataMigrationTest {

    @Test
    @Ignore
    public void migrateConfigFromVersion1_4() throws Exception {
        try (InputStream ris = getClass().getResourceAsStream("/WSMTAdapterConfigurationData-v1_4.bin");
             ObjectInputStream is = new ObjectInputStream(ris)) {
            Object o = is.readObject();
            assertNotNull(o);
            assertEquals(WSMicrosoftMTAdapterConfigurationData.class, o.getClass());
            WSMicrosoftMTAdapterConfigurationData config = (WSMicrosoftMTAdapterConfigurationData)o;
            assertEquals("testCategory", config.getCategory());
            assertEquals(null, config.getAzureKey());
            assertTrue(config.getIncludeCodes());
            assertEquals(75, config.getMatchScore());
        }
    }
}
