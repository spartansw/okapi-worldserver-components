package com.spartansoftwareinc.ws.mt.googlev3;

import static com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData.MATCH_SCORE;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UIElement;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextArea;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationData;
import com.spartansoftwareinc.ws.okapi.mt.base.WSBaseMTAdapterConfigurationUI;


public class WSGoogleMTv3AdapterConfigurationUI extends WSBaseMTAdapterConfigurationUI {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(WSGoogleMTv3AdapterConfigurationUI.class);
    static { LOG.setLevel(Level.DEBUG);} //TODO Comment me out

    private static final String LABEL_CREDENTIAL_AIS_PATH = "Credential AIS Path";
    private static final String CREDENTIAL_AIS_PATH = "credentialAisPath";
    private static final String LABEL_MODEL_GLOSSARY_MAP = "Language Pair To Model & Glossary Map";
    private static final String MODEL_GLOSSARY_MAP = "modelGlossaryMap";
    private static final String INCLUDE_CODES = "includeCodes";
    private static final String GOOGLE_PROJECT_NUM_OR_ID = "googleProj";
    private static final String GOOGLE_LOCATION = "googleLoc";

    private static final int LONG_TEXT_FIELD_SIZE = 80;
    private static final int TEXT_AREA_ROWS = 20;
    private static final int TEXT_AREA_COLS = 80;

    @Override
    protected WSBaseMTAdapterConfigurationData getConfigurationData(WSComponentConfigurationData configData) {
        return configData == null || !(configData instanceof WSGoogleMTv3AdapterConfigurationData)
            ? new WSGoogleMTv3AdapterConfigurationData()
            : ((WSGoogleMTv3AdapterConfigurationData) configData);
    }

    @Override
    protected List<UIElement> getUIElements(WSBaseMTAdapterConfigurationData configData) {
        List<UIElement> elements = new ArrayList<>();
        WSGoogleMTv3AdapterConfigurationData conf =
            (WSGoogleMTv3AdapterConfigurationData) configData;

        elements.add(new UITextField(LABEL_CREDENTIAL_AIS_PATH, CREDENTIAL_AIS_PATH,
            conf.getCredentialAisPath()).setSize(LONG_TEXT_FIELD_SIZE));

        elements.add(new UITextField("Google Project Number or ID", GOOGLE_PROJECT_NUM_OR_ID,
                                     conf.getGoogleProjectNumOrId()));
        elements.add(new UITextField("Google Location", GOOGLE_LOCATION,
                                     conf.getGoogleLocation()));
        elements.add(new UITextField(LABEL_MATCH_SCORE, MATCH_SCORE, String.valueOf(conf.getMatchScore())));
        elements.add(new UITextArea(LABEL_MODEL_GLOSSARY_MAP, MODEL_GLOSSARY_MAP,
            conf.getModelGlossaryMap())
            .setSize(TEXT_AREA_ROWS, TEXT_AREA_COLS));
        elements.add(new UICheckbox(LABEL_INCLUDE_CODES, INCLUDE_CODES, conf.getIncludeCodes()));

        return elements;
    }

    @Override
    protected String validateAndSave(WSContext wsContext, HttpServletRequest request,
        WSBaseMTAdapterConfigurationData configData, String errors) {

        String credentialAisPath = request.getParameter(CREDENTIAL_AIS_PATH);
        WSNode credentialNode = null;
        if (credentialAisPath == null || credentialAisPath.isEmpty()) {
            errors = addError(LABEL_CREDENTIAL_AIS_PATH, errors);
            LOG.error("credentialAisPath=" + credentialAisPath);
        } else {
            try {
                credentialNode = wsContext.getAisManager().getNode(credentialAisPath);
                if (credentialNode == null) {
                    errors = addError(LABEL_CREDENTIAL_AIS_PATH, errors);
                }
            } catch (WSAisException e) {
                LOG.error("Error fetching the credential AIS node {}" + credentialAisPath, e);
                errors = addError(LABEL_CREDENTIAL_AIS_PATH, errors);
            }
        }

        String projNumOrId = request.getParameter(GOOGLE_PROJECT_NUM_OR_ID);
        if (projNumOrId == null || projNumOrId.isEmpty()) {
            errors = addError(GOOGLE_PROJECT_NUM_OR_ID, errors);
        }

        String location = request.getParameter(GOOGLE_LOCATION);
        if (location == null || location.isEmpty()) {
            errors = addError(GOOGLE_LOCATION, errors);
        }

        String modelGlossaryMapJson = request.getParameter(MODEL_GLOSSARY_MAP);
        if (modelGlossaryMapJson == null || modelGlossaryMapJson.isEmpty()) {
            errors = addError(LABEL_MODEL_GLOSSARY_MAP, errors);
        }

        String matchScoreStr = request.getParameter(MATCH_SCORE);
        Integer matchScore = null;
        try {
            matchScore = Integer.valueOf(matchScoreStr);
        } catch (NumberFormatException e) {
            LOG.error("Parsing score \"" + matchScoreStr + "\" failed.", e);
        }
        if (matchScore == null || matchScore < 0 || matchScore > 100) {
            errors = addError(LABEL_MATCH_SCORE + " (must be integer between 0 .. 100)", errors);
        }

        boolean includeCodes = "on".equals(request.getParameter(INCLUDE_CODES));

        if (errors == null) {
            if ( configData != null) {
                WSGoogleMTv3AdapterConfigurationData conf =
                (WSGoogleMTv3AdapterConfigurationData) configData;
                LOG.debug("Setting the adapter specific configuration data...");
                conf.setCredentialAisPath(credentialAisPath);
                conf.setCredentialAbsolutePath(credentialNode.getFile().getAbsolutePath());
                conf.setGoogleProjectNumOrId(projNumOrId);
                conf.setGoogleLocation(location);
                conf.setModelGlossaryMap(modelGlossaryMapJson);
                conf.setMatchScore(matchScore);
                conf.setIncludeCodes(includeCodes);
            } else {
                LOG.error("Could not set the adapter specific configuration because configData was null");
            }
        }

        return errors;
    }

}
