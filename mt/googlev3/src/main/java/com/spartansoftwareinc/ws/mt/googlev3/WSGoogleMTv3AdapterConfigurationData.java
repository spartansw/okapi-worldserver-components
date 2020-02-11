package com.spartansoftwareinc.ws.mt.googlev3;


import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;

public class WSGoogleMTv3AdapterConfigurationData extends WSBaseMTAdapterConfigurationData {
    private static final long serialVersionUID = 1L;

    private String credentialAisPath = "";
    private String credentialAbsolutePath = "";
    private String googleProjectNumOrId = "";
    private String googleLocation = "us-central1";
    private String modelGlossaryMap =
        "{"
         + "\"xx/yy\":{\"modelId\":\"xx_to_yy_model_id\","
         +            "\"glossaryId\":\"xx_to_yy_glossary_id\"}"
         + "}";

    public WSGoogleMTv3AdapterConfigurationData() {
        this.setUseCustomScoring(true);
    }

    /**
     * Method to obtain the AIS path to a google credential JSON file that works with the project.
     * @return
     */
    public String getCredentialAisPath() {
        return credentialAisPath;
    }

    public void setCredentialAisPath(String credentialAisPath) {
        this.credentialAisPath = credentialAisPath;
    }

    /**
     * Method to obtain the file path to the node getCredentialAisPath() refers to.
     * @return
     */
    public String getCredentialAbsolutePath() {
        return credentialAbsolutePath;
    }

    /**
     * This method is used internally when setCredentialAisPath is called.
     * @param credentialAbsolutePath
     */
    public void setCredentialAbsolutePath(String credentialAbsolutePath) {
        this.credentialAbsolutePath = credentialAbsolutePath;
    }

    /**
     * Method to obtain a Google project number or id set by the corresponding setter.
     * @return
     */
    public String getGoogleProjectNumOrId() {
        return googleProjectNumOrId;
    }

    public void setGoogleProjectNumOrId(String goggleProjectNumOrId) {
        this.googleProjectNumOrId = goggleProjectNumOrId;
    }

    /**
     * Method to obtain a Google cloud location, usually "us-central1".
     * @return
     */
    public String getGoogleLocation() {
        return googleLocation;
    }

    public void setGoogleLocation(String googleLocation) {
        this.googleLocation = googleLocation;
    }

    /**
     * Method to obtain a JSON string that maps a locale pair of the form "xx/yy"
     * to a map that has two entries "modelId" and "glossaryId".
     * <p>
     * xx is a source language tag and yy is a target language tag that Google Cloud understands.
     * They are usually a two letter ISO language code such as "en", with two exceptions
     * "zh-CN" for Mandarin Chinese written in the simplified form of the characters
     * and "zh-TW" for Mandarin Chinese written in the traditional form.
     * Note the Google Cloud Translation does not understand "en-US".</p>
     * <p>
     * The "modelId" or "glossaryId" entry does not need to exist if model or glossary is not used.
     * </p>
     *
     * @return the JSON string that represents the map
     */
    public String getModelGlossaryMap() {
        return modelGlossaryMap;
    }

    public void setModelGlossaryMap(String json) {
        this.modelGlossaryMap = json;
    }
}
