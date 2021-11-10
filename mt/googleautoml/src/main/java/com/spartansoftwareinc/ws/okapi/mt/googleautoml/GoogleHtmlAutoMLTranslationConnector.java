package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import java.io.IOException;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiException;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.InvalidContentException;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.connectors.googleautoml.GoogleAutoMLTranslationConnector;
import net.sf.okapi.connectors.googleautoml.GoogleOAuth2Service;
import net.sf.okapi.connectors.googleautoml.util.ModelMapUtil;
import net.sf.okapi.lib.translation.QueryUtil;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GoogleAutoMLTranslationConnector but with MIME type support.
 */
public class GoogleHtmlAutoMLTranslationConnector extends GoogleAutoMLTranslationConnector {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleHtmlAutoMLTranslationConnector.class);

    private static final String BASE_URL = "https://automl.googleapis.com/v1";

    private final GoogleHtmlAutoMLTranslationAPIImpl api;
    private final GoogleOAuth2Service service;
    private final ModelMapUtil modelMapUtil;
    private final QueryUtil queryUtil;
    private GoogleHtmlAutoMLTranslationParameters params;

    public GoogleHtmlAutoMLTranslationConnector() {
        this.api = new GoogleHtmlAutoMLTranslationAPIImpl(BASE_URL);
        this.service = new GoogleOAuth2Service();
        this.modelMapUtil = new ModelMapUtil();
        this.queryUtil = new QueryUtil();
        this.params = new GoogleHtmlAutoMLTranslationParameters();
    }

    @Override
    public String getName() {
        return "Google Html AutoML Translation";
    }

    @Override
    public GoogleHtmlAutoMLTranslationParameters getParameters() {
        return this.params;
    }

    @Override
    public void setParameters(IParameters params) {
        this.params = (GoogleHtmlAutoMLTranslationParameters) params;
    }

    @Override
    public int query(String plainText) {
        checkConnector();
        if (plainText.isEmpty()) {
            return 0;
        }
        try {
            String modelResourceName = modelMapUtil.getModelResourceName(srcCode, trgCode);
            String translation = api.predict(plainText, params.getMimeType(), modelResourceName, service);
            current = 0;
            result = getQueryResult(plainText, translation, null);
            return 1;
        } catch (IOException | ParseException e) {
            throw new OkapiException(e);
        }
    }

    @Override
    public int query(TextFragment sourceFragment) {
        checkConnector();
        if (sourceFragment.isEmpty()) {
            return 0;
        }
        try {
            String modelResourceName = modelMapUtil.getModelResourceName(srcCode, trgCode);
            String htmlText = sourceFragment.getCodedText();
            String translation = api.predict(htmlText, params.getMimeType(), modelResourceName, service);
            current = 0;
            result = getQueryResult(htmlText, translation, sourceFragment);
            return 1;
        } catch (IOException | ParseException e) {
            throw new OkapiException(e);
        }
    }

    /**
     * Ensures that the connector is using valid parameters and an updated OAuth2 service.
     */
    private void checkConnector() {
        // Check the mapping
        if (Util.isEmpty(params.getModelMap())) {
            throw new OkapiException("This connector requires a mapping from language pairs to models.");
        }
        else {
            modelMapUtil.setMap(params.getModelMap());
        }
        // Make sure the service is authenticated
        if ( !service.hasCredential() ) {
            if ( !Util.isEmpty(params.getCredentialFilePath()) ) {
                service.setCredentialFilePath(params.getCredentialFilePath());
            }
            else if ( !Util.isEmpty(params.getCredentialString()) ) {
                service.setCredentialString(params.getCredentialString());
            }
            else {
                throw new OkapiException("This connector requires credentials for a Google service account.");
            }
        }
    }

    private QueryResult getQueryResult(String sourceText, String targetText, TextFragment sourceFragment) {
        QueryResult qr = new QueryResult();
        qr.setFuzzyScore(95);
        qr.setCombinedScore(95);
        qr.weight = getWeight();
        qr.origin = getName();
        qr.matchType = MatchType.MT;

        if (sourceFragment == null) {
            // Plain text
            qr.source = new TextFragment(sourceText);
            qr.target = new TextFragment(targetText);
        } else {
            // Text fragment
            try {
                qr.source = sourceFragment;
                qr.target = makeFragment(targetText, sourceFragment);
            } catch (InvalidContentException e) {
                // Something went wrong in the resulting MT candidate
                // We fall back on no candidate with a zero score
                LOG.error("This MT candidate will be ignored.\n{}\n{}", sourceText, e.getMessage());
                qr.setFuzzyScore(0);
                qr.setCombinedScore(0);
                qr.source = sourceFragment;
                qr.target = sourceFragment.clone();
                qr.setQuality(QueryResult.QUALITY_UNDEFINED);
            }
        }
        return qr;
    }

    private TextFragment makeFragment(String codedHtml, TextFragment sourceFragment) {
        return sourceFragment.hasCode()
            ? new TextFragment(queryUtil.fromCodedHTML(codedHtml, sourceFragment, false),
                sourceFragment.getClonedCodes())
            : new TextFragment(queryUtil.fromCodedHTML(codedHtml, sourceFragment, false));
    }
}
