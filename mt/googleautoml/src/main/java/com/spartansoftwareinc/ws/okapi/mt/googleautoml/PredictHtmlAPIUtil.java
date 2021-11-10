package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.connectors.googleautoml.util.PredictAPIUtil;
import org.json.simple.JSONObject;

/**
 * PredictAPIUtil with the ability to specify the Google AutoML MIME type.
 */
public class PredictHtmlAPIUtil extends PredictAPIUtil {

    public List<JSONObject> getPredictRequests(String sourceText,
            GoogleHtmlAutoMLTranslationParameters.MimeType mimeType) {
        List<JSONObject> requests = new ArrayList<>();
        List<String> splitTexts = getSplitTexts(sourceText);

        for (String splitText : splitTexts) {
            JSONObject root = new JSONObject();
            JSONObject payload = new JSONObject();
            JSONObject textSnippet = new JSONObject();
            textSnippet.put("content", splitText);
            if (mimeType != null) {
                textSnippet.put("mime_type", mimeType.value);
            }
            payload.put("textSnippet", textSnippet);
            root.put("payload", payload);
            requests.add(root);
        }

        return requests;
    }

    /**
     * Splits the source text in smaller segments if the source text length exceeds
     * {@link PredictAPIUtil#CONTENT_CHAR_LIMIT}.
     */
    private List<String> getSplitTexts(String sourceText) {
        List<String> splitTexts = new ArrayList<>();
        for (int i = 0; i < sourceText.length(); i += CONTENT_CHAR_LIMIT) {
            splitTexts.add(sourceText.substring(i, Math.min(sourceText.length(), i + CONTENT_CHAR_LIMIT)));
        }
        return splitTexts;
    }
}
