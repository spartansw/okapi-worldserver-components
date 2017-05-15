package com.spartansoftwareinc.ws.okapi.mt.mshub;

import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

public class WSMTAdapterConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 1L;

    static final String AZURE_KEY = "azureKey";
    static final String CATEGORY = "category";

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField(AZURE_KEY, String.class),
            new ObjectStreamField(CATEGORY, String.class),
            new ObjectStreamField(USE_CUSTOM_SCORING, boolean.class),
            new ObjectStreamField(MATCH_SCORE, int.class),
            new ObjectStreamField(INCLUDE_CODES, boolean.class),
            new ObjectStreamField(LOCALE_MAP_AIS_PATH, String.class),
    };

    private String azureKey;
    private String category;

    public String getAzureKey() {
        return azureKey;
    }

    public void setAzureKey(String azureKey) {
        this.azureKey = azureKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = inputStream.readFields();

        setAzureKey((String) fields.get(AZURE_KEY, getAzureKey()));
        setCategory((String) fields.get(CATEGORY, getCategory()));
        setUseCustomScoring(fields.get(USE_CUSTOM_SCORING, useCustomScoring()));
        setMatchScore(fields.get(MATCH_SCORE, getMatchScore()));
        setIncludeCodes(fields.get(INCLUDE_CODES, getIncludeCodes()));
        setLocaleMapAISPath((String) fields.get(LOCALE_MAP_AIS_PATH, getLocaleMapAISPath()));
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        ObjectOutputStream.PutField fields = outputStream.putFields();

        fields.put(AZURE_KEY, getAzureKey());
        fields.put(CATEGORY, getCategory());
        fields.put(USE_CUSTOM_SCORING, useCustomScoring());
        fields.put(MATCH_SCORE, getMatchScore());
        fields.put(INCLUDE_CODES, getIncludeCodes());
        fields.put(LOCALE_MAP_AIS_PATH, getLocaleMapAISPath());

        outputStream.writeFields();
    }
}