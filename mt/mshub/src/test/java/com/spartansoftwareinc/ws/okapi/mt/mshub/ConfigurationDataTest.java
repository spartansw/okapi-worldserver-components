package com.spartansoftwareinc.ws.okapi.mt.mshub;

import org.junit.Test;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigurationDataTest {

    @Test
    public void migrateConfigFromVersion1_4() throws Exception {
        try (InputStream ris = getClass().getResourceAsStream("/WSMTAdapterConfigurationData-v1_4.bin");
             ObjectInputStream is = new ObjectInputStream(ris)) {
            Object o = is.readObject();
            assertNotNull(o);
            assertEquals(WSMTAdapterConfigurationData.class, o.getClass());
            WSMTAdapterConfigurationData config = (WSMTAdapterConfigurationData) o;
            assertEquals("testCategory", config.getCategory());
            assertEquals(null, config.getAzureKey());
            assertTrue(config.getIncludeCodes());
            assertEquals(75, config.getMatchScore());
        }
    }

    @Test
    public void testSerialization() throws Exception {
        String currentConfigurationData = "current-configuration-data.bin";

        WSMTAdapterConfigurationData configurationData = getConfigurationData();

        Path outputPath = Paths.get(ClassLoader.getSystemResource(currentConfigurationData).toURI());

        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(outputPath))) {
            outputStream.writeObject(configurationData);
        }

        Path inputPath = Paths.get(ClassLoader.getSystemResource(currentConfigurationData).toURI());

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(inputPath))) {
            WSMTAdapterConfigurationData writtenConfigurationData = (WSMTAdapterConfigurationData) inputStream.readObject();

            assertConfigurationDataEqual(writtenConfigurationData, configurationData);
        }
    }

    private WSMTAdapterConfigurationData getConfigurationData() {
        WSMTAdapterConfigurationData configurationData = new WSMTAdapterConfigurationData();

        configurationData.setAzureKey("key");
        configurationData.setCategory("category");
        configurationData.setUseCustomScoring(true);
        configurationData.setMatchScore(98);
        configurationData.setIncludeCodes(true);
        configurationData.setLocaleMapAISPath("path");

        return configurationData;
    }

    private void assertConfigurationDataEqual(WSMTAdapterConfigurationData actual, WSMTAdapterConfigurationData expected) {
        assertEquals(expected.getAzureKey(), actual.getAzureKey());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.useCustomScoring(), actual.useCustomScoring());
        assertEquals(expected.getMatchScore(), actual.getMatchScore());
        assertEquals(expected.getIncludeCodes(), actual.getIncludeCodes());
        assertEquals(expected.getLocaleMapAISPath(), actual.getLocaleMapAISPath());
    }
}
