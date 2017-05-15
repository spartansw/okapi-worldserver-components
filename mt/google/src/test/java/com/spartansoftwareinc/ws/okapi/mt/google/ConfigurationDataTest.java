package com.spartansoftwareinc.ws.okapi.mt.google;

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
    public void testSerialization() throws Exception {
        String currentConfigurationData = "current-configuration-data.bin";

        WSGoogleMTAdapterConfigurationData configurationData = getConfigurationData();

        Path outputPath = Paths.get(ClassLoader.getSystemResource(currentConfigurationData).toURI());

        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(outputPath))) {
            outputStream.writeObject(configurationData);
        }

        Path inputPath = Paths.get(ClassLoader.getSystemResource(currentConfigurationData).toURI());

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(inputPath))) {
            WSGoogleMTAdapterConfigurationData actualConfigurationData = (WSGoogleMTAdapterConfigurationData) inputStream.readObject();

            assertConfigurationDataEqual(actualConfigurationData, configurationData);
        }
    }

    private WSGoogleMTAdapterConfigurationData getConfigurationData() {
        WSGoogleMTAdapterConfigurationData configurationData = new WSGoogleMTAdapterConfigurationData();

        configurationData.setApiKey("key");
        configurationData.setUseCustomScoring(true);
        configurationData.setMatchScore(98);
        configurationData.setIncludeCodes(true);
        configurationData.setLocaleMapAISPath("path");

        return configurationData;
    }

    private void assertConfigurationDataEqual(WSGoogleMTAdapterConfigurationData actual, WSGoogleMTAdapterConfigurationData expected) {
        assertEquals(expected.getApiKey(), actual.getApiKey());
        assertEquals(expected.useCustomScoring(), actual.useCustomScoring());
        assertEquals(expected.getMatchScore(), actual.getMatchScore());
        assertEquals(expected.getIncludeCodes(), actual.getIncludeCodes());
        assertEquals(expected.getLocaleMapAISPath(), actual.getLocaleMapAISPath());
    }
}
