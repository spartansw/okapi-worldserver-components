package com.spartansoftwareinc.ws.okapi.filters.idml;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;
import net.sf.okapi.filters.idml.Parameters;

public class IDMLFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {

    private static final long serialVersionUID = 1L;

    @Override
    protected Parameters getDefaultParameters() {
        Parameters parameters = new Parameters();
        parameters.reset();

        return parameters;
    }

    public int getMaxAttributeSize() {
        return getParameters().getMaxAttributeSize();
    }

    public void setMaxAttributeSize(int maxAttributeSize) {
        Parameters params = getParameters();
        params.setMaxAttributeSize(maxAttributeSize);
        setParameters(params);
    }

    public boolean getUntagXmlStructures() {
        return getParameters().getUntagXmlStructures();
    }

    public void setUntagXmlStructures(boolean untagXmlStructures) {
        Parameters params = getParameters();
        params.setUntagXmlStructures(untagXmlStructures);
        setParameters(params);
    }

    public boolean getExtractNotes() {
        return getParameters().getExtractNotes();
    }

    public void setExtractNotes(boolean extractNotes) {
        Parameters params = getParameters();
        params.setExtractNotes(extractNotes);
        setParameters(params);
    }

    public boolean getExtractMasterSpreads() {
        return getParameters().getExtractMasterSpreads();
    }

    public void setExtractMasterSpreads(boolean extractMasterSpreads) {
        Parameters params = getParameters();
        params.setExtractMasterSpreads(extractMasterSpreads);
        setParameters(params);
    }

    public boolean getExtractHiddenLayers() {
        return getParameters().getExtractHiddenLayers();
    }

    public void setExtractHiddenLayers(boolean extractHiddenLayers) {
        Parameters params = getParameters();
        params.setExtractHiddenLayers(extractHiddenLayers);
        setParameters(params);
    }
}
