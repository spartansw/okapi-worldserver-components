package com.spartansoftwareinc.ws.okapi.mt.googleautoml;

import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.connectors.googleautoml.GoogleAutoMLTranslationParameters;

/**
 * GoogleAutoMLTranslationParameters with the ability to specify the MIME type.
 */
public class GoogleHtmlAutoMLTranslationParameters extends GoogleAutoMLTranslationParameters {
    private static final String MIME_TYPE = "mimeType";

    // Google AutoML supported mime types
    public enum MimeType {
        PLAIN("text/plain"),
        HTML("text/html");

        public final String value;

        MimeType(String mimeType) {
            this.value = mimeType;
        }

        public static MimeType getValue(String mimeType) {
            for (MimeType type : values()) {
                if (type.value.equals(mimeType)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unsupported MIME type: "+mimeType);
        }
    }

    public MimeType getMimeType() {
        String mimeType = getString(MIME_TYPE);
        if (mimeType != null) {
            return MimeType.getValue(mimeType);
        } else {
            return null;
        }
    }

    public void setMimeType(MimeType mimeType) {
        if (mimeType != null) {
            setString(MIME_TYPE, mimeType.value);
        } else {
            setString(MIME_TYPE, null);
        }
    }

    @Override
    public void reset() {
        super.reset();
        setMimeType(null);
    }

    @Override
    public ParametersDescription getParametersDescription() {
        // Note: credential string is not expose through the UI
        ParametersDescription desc = super.getParametersDescription();
        desc.add(MIME_TYPE, "MIME Type", "String indicating the format of the source text. See"
                +" https://cloud.google.com/translate/automl/docs/reference/rest/v1/projects.locations.models/predict");
        return desc;
    }

    @Override
    public EditorDescription createEditorDescription(ParametersDescription parametersDescription) {
        EditorDescription desc = super.createEditorDescription(parametersDescription);
        desc.addTextInputPart(parametersDescription.get(MIME_TYPE));
        return desc;
    }
}
