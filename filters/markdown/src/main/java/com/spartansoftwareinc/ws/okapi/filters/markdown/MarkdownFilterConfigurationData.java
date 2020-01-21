package com.spartansoftwareinc.ws.okapi.filters.markdown;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

import net.sf.okapi.filters.markdown.Parameters;

public class MarkdownFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Parameters getDefaultParameters() {
        Parameters parameters = new Parameters();
        parameters.reset();
        parameters.setUseCodeFinder(false); // Turning off since we aren't providing a way to configure the rules.
        return parameters;
    }
    
    // translateUrl
    public boolean getTranslateUrls() {
        return getParameters().getTranslateUrls();
    }

    public void setTranslateUrls(boolean translateUrls) {
        Parameters params = getParameters();
        params.setTranslateUrls(translateUrls);
        setParameters(params);
    }
    
    // translateCodeBlocks
    public boolean getTranslateCodeBlocks() {
        return getParameters().getTranslateCodeBlocks();
    }

    public void setTranslateCodeBlockse(boolean translateCodeBlocks) {
        Parameters params = getParameters();
        params.setTranslateCodeBlocks(translateCodeBlocks);
        setParameters(params);
    }

    // translateHeaderMetadata
    public boolean getTranslateHeaderMetadata() {
        return getParameters().getTranslateHeaderMetadata();
    }

    public void setTranslateHeaderMetadata(boolean translateHeaderMetadata) {
        Parameters params = getParameters();
        params.setTranslateHeaderMetadata(translateHeaderMetadata);
        setParameters(params);
    }
    
    // translateImageAltText
    public boolean getTranslateImageAltText() {
        return getParameters().getTranslateImageAltText();
    }

    public void setTranslateImageAltText(boolean translateImageAltText) {
        Parameters params = getParameters();
        params.setTranslateImageAltText(translateImageAltText);
        setParameters(params);
    }
    
}
