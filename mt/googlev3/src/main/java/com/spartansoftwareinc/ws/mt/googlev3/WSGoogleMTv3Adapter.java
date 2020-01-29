package com.spartansoftwareinc.ws.mt.googlev3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.GetSupportedLanguagesRequest;
import com.google.cloud.translate.v3.GlossaryName;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.SupportedLanguage;
import com.google.cloud.translate.v3.SupportedLanguages;
import com.google.cloud.translate.v3.TranslateTextGlossaryConfig;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;
import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.WSVersion;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.WSComponentConfiguration;
import com.idiominc.wssdk.component.WSComponentConfigurationUI;
import com.idiominc.wssdk.component.mt.WSMTRequest;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.linguistic.WSLanguagePair;
import com.idiominc.wssdk.linguistic.WSLinguisticManager;
import com.idiominc.wssdk.mt.WSMTResult;
import com.spartansoftwareinc.ws.okapi.mt.base.CodesMasker;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapter;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.lib.translation.BaseConnector;


/**
 * MT Adapter for WorldServer that uses Google Cloud Translation service over their version 3 API.
 * <p>
 * This Adapter can use the language model trained by Google AutoML and the glossaries.
 * </p>
 * <p>
 * NOTE: Despite this adpter's source code lives in Okapi WorldServer Components repository,
 * it DOES NOT use the corresponding Okapi Connector. In fact, an Okapi Connector that uses
 * the v3 API did not exist in Okapi as of this writing. This is a native implementation of
 * the Adapter except in the area of locale name conversion where we do use
 * Okapi's LocaleId as an intermediary. It does use the base and utility classes
 * found in the source repository.
 * </p>
 */
public class WSGoogleMTv3Adapter extends WSBaseMTAdapter {
    private static final Logger LOG = Logger.getLogger(WSGoogleMTv3Adapter.class);
    static { LOG.setLevel(Level.DEBUG);} //TODO Comment me out

    private static final String ADAPTER_NAME = "Google Cloud Translation (v3 API)";
    private static final String ADAPTER_DESCRIPTION = "MT adapter for Google Cloud v3 API";


    private static final String DEFAULT_CREDENTIAL_AIS_PATH
         = "/Spartan/Customization/google-translate-v3-credential.json";

    private CodesMasker masker = new CodesMasker();

    @Override
    public String getName() {
        return ADAPTER_NAME;
    }

    @Override
    public String getDescription() {
        return ADAPTER_DESCRIPTION;
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSVersion getMinimumWorldServerVersion() {
        return new WSVersion(10, 0, 0);
    }

    @Override
    public WSComponentConfigurationUI getConfigurationUI() {
        return new WSGoogleMTv3AdapterConfigurationUI();
    }

    /**
     * This MT Adapter does not use an Okapi connector, so it always returns null.
     * @return null
     */
    @Override
    protected BaseConnector getMTConnector() {
        LOG.error(new Exception("getMTConnector() is not expected to be called."));
        return null;
    }

    @Override
    protected WSGoogleMTv3AdapterConfigurationData getConfigurationData() {
        if (configurationData != null) {
            return (WSGoogleMTv3AdapterConfigurationData) configurationData;
        }

        WSComponentConfiguration configuration = getCurrentConfiguration();
        LOG.debug("configuration=" + configuration);
        if (configuration == null) {
            LOG.debug("configuration == null. Creating a new instance and returning it.");
            return new WSGoogleMTv3AdapterConfigurationData();
        } else if (configuration.getConfigurationData() != null) {
            return (WSGoogleMTv3AdapterConfigurationData) configuration.getConfigurationData();
        } else {
            LOG.error("configuration != null but configuration.getConfigurationData() = null. "
                    + "Returning null but the further processing will probably fail.");
            return null;
        }
    }

    @Override
    public boolean supportsPlaceholders() {
        return getConfigurationData().getIncludeCodes();
    }

    @Override
    public void translate(WSContext context, WSMTRequest[] mtReqs,
                          WSLanguage sourceLang, WSLanguage targetLang) {

//        LocationName parent = LocationName.of("277598865374"/*getConfigurationData().getGoogleProjectNumOrId()*/,
//            "us-central1" /*getConfigurationData().getGoogleLocation()*/);
        LocationName parent = LocationName.of(getConfigurationData().getGoogleProjectNumOrId(),
                                              getConfigurationData().getGoogleLocation());

        localeMap = getLocaleMap(context);

        String srcLangTag = convertWSLangToGoogleLangTag(sourceLang);
        String tgtLangTag = convertWSLangToGoogleLangTag(targetLang);
        LOG.debug("sourceLang = " + sourceLang.getDisplayString() + "=>" + srcLangTag +
            ", targetLang = " + targetLang.getDisplayString() + "=>" + tgtLangTag);

        ModelGlossaryMap modelGlossaryMapObj = new ModelGlossaryMap();
        modelGlossaryMapObj.loadMap(getConfigurationData().getModelGlossaryMap());

        String modelId = null;
        String glossaryId = null;

        String langPair = srcLangTag + "/" + tgtLangTag;
        ModelGlossaryMap.ModelGlossary entry = modelGlossaryMapObj.get(langPair);
        if (entry!=null) {
            modelId = entry.modelId;
            glossaryId = entry.glossaryId;
        }
        LOG.debug("Using modelId=\"" + modelId + "\", glossaryId=\"" + glossaryId + "\"");

        TranslateTextRequest.Builder googleMTReqTemplate = TranslateTextRequest.newBuilder();
        googleMTReqTemplate.setParent(parent.toString())
                           .setSourceLanguageCode(srcLangTag)
                           .setTargetLanguageCode(tgtLangTag);
        if (getConfigurationData().getIncludeCodes()) {
            googleMTReqTemplate.setMimeType("text/html");
        } else {
            googleMTReqTemplate.setMimeType("text/plain");
        }
        if (glossaryId!=null && !glossaryId.isEmpty()) {
            GlossaryName glossaryName = GlossaryName.of(getConfigurationData().getGoogleProjectNumOrId(),
                                                        getConfigurationData().getGoogleLocation(), glossaryId);
            TranslateTextGlossaryConfig glossaryConfig =
                TranslateTextGlossaryConfig.newBuilder().setGlossary(glossaryName.toString()).build();
            googleMTReqTemplate.setGlossaryConfig(glossaryConfig);
        }

        // Not sure why model includes project and locations but that's what the google code sample at
        // https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/translate/cloud-client/src/main/java/com/example/translate/TranslateTextWithGlossaryAndModel.java
        // shows.
        if (modelId!=null && !modelId.isEmpty()) {
            StringBuilder modelPathBuilder = new StringBuilder();
            modelPathBuilder.append("projects/").append(getConfigurationData().getGoogleProjectNumOrId())
                            .append("/locations/").append(getConfigurationData().getGoogleLocation());
            modelPathBuilder.append("/models/").append(modelId);
            googleMTReqTemplate.setModel(modelPathBuilder.toString());
        }

        TranslationServiceSettings settings = createSettingsWithCredential(context);
        LOG.debug("client setting: " + settings.toString());

        boolean handlePlaceholders = getConfigurationData().getIncludeCodes();
        try (TranslationServiceClient client = TranslationServiceClient.create(settings)) {
            for (WSMTRequest wsMTReq: mtReqs) {
                String srcText = wsMTReq.getSource();
                if (handlePlaceholders) {
                                    srcText = masker.mask(srcText);
                };
                TranslateTextRequest googleMTReq = googleMTReqTemplate.addContents(srcText).build();
                LOG.debug("Source lang: " + googleMTReq.getSourceLanguageCode()
                      + ", Target lang: " + googleMTReq.getTargetLanguageCode());
                LOG.debug("Translating: " + srcText);
                TranslateTextResponse googleMTRes = client.translateText(googleMTReq);
                int nTrans = googleMTRes.getTranslationsCount();
                WSMTResult[] wsMTResults = new WSMTResult[nTrans];
                int i = 0;

                List<Translation> trs = (glossaryId == null) ? googleMTRes.getTranslationsList()
                                                             : googleMTRes.getGlossaryTranslationsList();
                for (Translation translation : trs) {
                    String translatedText = translation.getTranslatedText();
                    if (handlePlaceholders) {
                        translatedText = masker.unmask(translatedText);
                    };
                    WSMTResult r = new WSMTResult(srcText, translatedText, getConfigurationData().getMatchScore());
                    LOG.debug("Translation#" + i + ": " + r.getTranslation());
                    wsMTResults[i++] = r;
                }
                wsMTReq.setResults(wsMTResults);
            }
        } catch (IOException e) {
                throw new WSRuntimeException(e);
        }
    }

    /**
     * Returns language pairs supported by Google Cloud Translation.
     * @param context
     * @return
     */
    @Override
    public WSLanguagePair[] getSupportedLanguagePairs(WSContext context) {
        LocationName parent = LocationName.of(getConfigurationData().getGoogleProjectNumOrId(),
                                              getConfigurationData().getGoogleLocation());
//        LocationName parent = LocationName.of("277598865374"/*getConfigurationData().getGoogleProjectNumOrId()*/,
//            "us-central1" /*getConfigurationData().getGoogleLocation()*/);
        WSLinguisticManager lingmgr = context.getLinguisticManager();
        TranslationServiceSettings settings = createSettingsWithCredential(context);
        try (TranslationServiceClient client = TranslationServiceClient.create(settings)) {
            GetSupportedLanguagesRequest request =
                GetSupportedLanguagesRequest.newBuilder().setParent(parent.toString()).build();
            SupportedLanguages response = client.getSupportedLanguages(request);
            List<SupportedLanguage> googleLangs = response.getLanguagesList();
            LOG.debug("Google supports " + googleLangs.size() + " languages.");
            List<WSLanguagePair> pairs = new ArrayList<>();
            for (SupportedLanguage srcLang: googleLangs) {
//                LOG.debug("google lang displayName: " + srcLang.getDisplayName()
//                        + ", languageCode: " + srcLang.getLanguageCode()
//                        + ", toString: " + srcLang.toString());
                if (!srcLang.getSupportSource()) continue;

                WSLanguage srcWSLang = convertGoogleLangToWSLang(lingmgr, srcLang);
                if (srcWSLang == null) continue; // WS doesn't support this language.
//                LOG.debug("For WSLang " + srcWSLang.getName()
//                        + "'s Locale: toLanguageTag(): " + srcWSLang.getLocale().toLanguageTag()
//                        + ", toString(): " + srcWSLang.getLocale().toString()
//                        + ", getLanguage(): " + srcWSLang.getLocale().getLanguage()
//                        + ", getCountry(): " + srcWSLang.getLocale().getCountry() );
                for (SupportedLanguage targetLang: googleLangs) {
                    if (srcLang.equals(targetLang)) continue;
                    if (!targetLang.getSupportTarget()) continue;
                    WSLanguage targetWSLang = convertGoogleLangToWSLang(lingmgr, targetLang);
                    if (targetWSLang == null) continue;
                    pairs.add(new WSLanguagePair(srcWSLang, targetWSLang));
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(pairs.size() + " supported language pairs found:");
//                for (WSLanguagePair pair: pairs) {
//                    LOG.debug("  " + pair.getSourceLanguage().getName()
//                             + " => " + pair.getTargetLanguage().getName());
//                }
            }
            return pairs.toArray(new WSLanguagePair[0]);
        } catch (IOException e) {
            throw new WSRuntimeException(e);
        }
    }

    // Get the credential and put it into the settings object.
    // If the absolute filepath hasn't been set yet, set it according to the AIS path.
    private TranslationServiceSettings createSettingsWithCredential(WSContext context)
    {
        WSGoogleMTv3AdapterConfigurationData conf = getConfigurationData();
        String aisPath, absPath;
        if ( conf != null ) { // This should always be the case but...
            aisPath = getConfigurationData().getCredentialAisPath();
            absPath = getConfigurationData().getCredentialAbsolutePath();
        } else {
            aisPath = DEFAULT_CREDENTIAL_AIS_PATH;
            LOG.error("getConfigurationData() returned null. Trying the default path " + aisPath);
            absPath = null;
        }

        if (absPath == null || absPath.isEmpty()) {
            if (aisPath == null || aisPath.isEmpty()) {
                throw new WSRuntimeException("AIS path to Google credential file must be specified.");
            }
            // This situation should not happen because absolute path should have been set by config time
            // in WSGoogleMTv3AdapterConfigurationUI.  We try to set it anyway after logging this warning.
            LOG.warn("The credential aboslute file path was not set despite that the AIS path was set as " + aisPath);
            WSNode credentialNode = null;
            try {
                credentialNode = context.getAisManager().getNode(aisPath);
                absPath = credentialNode.getFile().getAbsolutePath();
                if ( conf != null ) {
                    conf.setCredentialAbsolutePath(absPath);
                }
            } catch (WSAisException e) {
                throw new WSRuntimeException("Error fetching credential node " + aisPath, e);
            }
        }
        try {
            GoogleCredentials creds = GoogleCredentials
                .fromStream(new FileInputStream(absPath))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");
            return TranslationServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(creds))
                .build();
        } catch (IOException e) {
            throw new WSRuntimeException(String.format("Google Translate credential file must be placed at %s in AIS",
                                                        getConfigurationData().getCredentialAisPath()),
                                         e);
        }
    }

    private WSLanguage convertGoogleLangToWSLang(WSLinguisticManager lingmgr, SupportedLanguage googleLang) {
        return lingmgr.getLanguage(Locale.forLanguageTag(googleLang.getLanguageCode()));
    }

    private String convertWSLangToGoogleLangTag(WSLanguage wsLang) {
        final Locale wsLocale = wsLang.getLocale();
        LocaleId localeId = getLocaleId(wsLocale.getLanguage(), wsLocale.getCountry());
        String tag = localeId.toBCP47();

        // Google only recognizes 2-letter ISO code without country code except zh-TW and zh-CN.
        if (!tag.startsWith("zh") && tag.length()!=2) {
            return tag.substring(0,2);
        } else {
            return tag;
        }
    }

}
