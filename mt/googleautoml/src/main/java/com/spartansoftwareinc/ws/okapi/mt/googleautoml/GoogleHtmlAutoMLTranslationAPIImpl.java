package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.StreamUtil;
import net.sf.okapi.common.exceptions.OkapiException;
import net.sf.okapi.connectors.googleautoml.GoogleAutoMLTranslationAPIImpl;
import net.sf.okapi.connectors.googleautoml.GoogleOAuth2Service;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GoogleAutoMLTranslationAPIImpl, but with MIME type support.
 */
public class GoogleHtmlAutoMLTranslationAPIImpl {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleAutoMLTranslationAPIImpl.class);

    private final String baseUrl;
    private final PredictHtmlAPIUtil predictApiUtil;

    public GoogleHtmlAutoMLTranslationAPIImpl(String baseUrl) {
        this.baseUrl = baseUrl;
        this.predictApiUtil = new PredictHtmlAPIUtil();
    }

    public String predict(String sourceText, GoogleHtmlAutoMLTranslationParameters.MimeType mimeType,
            String modelResourceName, GoogleOAuth2Service service) throws IOException, ParseException {
        List<String> translations = new ArrayList<>();
        List<JSONObject> requests = predictApiUtil.getPredictRequests(sourceText, mimeType);

        for (JSONObject request : requests) {
            URL url = new URL(String.format("%s/%s:predict", baseUrl, modelResourceName));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + service.getAccessToken());
            conn.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(request.toJSONString());
            }

            if (conn.getResponseCode() == 200) {
                translations.add(predictApiUtil.extractTranslation(conn.getInputStream()));
            } else {
                String msg = StreamUtil.streamUtf8AsString(conn.getErrorStream());
                LOG.info("Error during AutoML predict call: {}", msg);
                throw new OkapiException(msg);
            }
        }

        StringBuilder translationBuilder = new StringBuilder();
        for (String translation : translations) {
            translationBuilder.append(translation);
        }
        return translationBuilder.toString();
    }
}
