package com.spartansoftwareinc.ws.okapi.filters.idml;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.component.WSComponentConfigurationData;
import com.spartansoftwareinc.ws.okapi.base.ui.UICheckbox;
import com.spartansoftwareinc.ws.okapi.base.ui.UITable;
import com.spartansoftwareinc.ws.okapi.base.ui.UITextField;
import com.spartansoftwareinc.ws.okapi.base.ui.UIUtil;
import com.spartansoftwareinc.ws.okapi.filters.ui.WSOkapiFilterUI;

import javax.servlet.http.HttpServletRequest;

public class IDMLFilterConfigurationUI extends WSOkapiFilterUI<IDMLFilterConfigurationData> {

    private static final String MAX_ATTRIBUTE_SIZE_LABEL = "Max Attribute Size (bytes)";
    private static final String MAX_ATTRIBUTE_SIZE_NAME = "maxAttributeSize";
    private static final int MAX_ATTRIBUTE_SIZE_FIELD_SIZE = 10;

    private static final String UNTAG_XML_STRUCTURES_LABEL = "Untag XML Structures";
    private static final String UNTAG_XML_STRUCTURES_NAME = "untagXmlStructures";

    private static final String EXTRACT_NOTES_LABEL = "Extract Notes";
    private static final String EXTRACT_NOTES_NAME = "extractNotes";

    private static final String EXTRACT_MASTER_SPREADS_LABEL = "Extract Master Spreads";
    private static final String EXTRACT_MASTER_SPREADS_NAME = "extractMasterSpreads";

    private static final String EXTRACT_HIDDEN_LAYERS_LABEL = "Extract Hidden Layers";
    private static final String EXTRACT_HIDDEN_LAYERS_NAME = "extractHiddenLayers";

    @Override
    protected UITable buildConfigurationTable(WSContext context, HttpServletRequest request, WSComponentConfigurationData config) {
        IDMLFilterConfigurationData configurationData = getConfigurationData(config);

        UITable table = new UITable();

        String maxAttributeSizeValue = Integer.toString(configurationData.getMaxAttributeSize());

        table.add(new UITextField(MAX_ATTRIBUTE_SIZE_LABEL, MAX_ATTRIBUTE_SIZE_NAME, maxAttributeSizeValue).setSize(MAX_ATTRIBUTE_SIZE_FIELD_SIZE));

        table.add(new UICheckbox(UNTAG_XML_STRUCTURES_LABEL, UNTAG_XML_STRUCTURES_NAME, configurationData.getUntagXmlStructures()));
        table.add(new UICheckbox(EXTRACT_NOTES_LABEL, EXTRACT_NOTES_NAME, configurationData.getExtractNotes()));
        table.add(new UICheckbox(EXTRACT_MASTER_SPREADS_LABEL, EXTRACT_MASTER_SPREADS_NAME, configurationData.getExtractMasterSpreads()));
        table.add(new UICheckbox(EXTRACT_HIDDEN_LAYERS_LABEL, EXTRACT_HIDDEN_LAYERS_NAME, configurationData.getExtractHiddenLayers()));

        return table;
    }

    @Override
    protected String validateAndSave(WSContext context, HttpServletRequest request, IDMLFilterConfigurationData configData, String errors) {
        IDMLFilterConfigurationData configurationData = getConfigurationData(configData);

        if (null == request.getParameter(MAX_ATTRIBUTE_SIZE_NAME)) {
            errors = addError(MAX_ATTRIBUTE_SIZE_NAME, errors);
            return errors;
        }

        final String maxAttributeSizeString = request.getParameter(MAX_ATTRIBUTE_SIZE_NAME).trim();

        if (maxAttributeSizeString.isEmpty()) {
            errors = addError(MAX_ATTRIBUTE_SIZE_NAME, errors);
            return errors;
        }

        final int maxAttributeSize;

        try {
            maxAttributeSize = Integer.parseInt(maxAttributeSizeString);
        } catch (NumberFormatException e) {
            errors = addError(MAX_ATTRIBUTE_SIZE_NAME, errors);
            return errors;
        }

        if (maxAttributeSize <= 0) {
            errors = addError(MAX_ATTRIBUTE_SIZE_NAME, errors);
            return errors;
        }

        configurationData.setMaxAttributeSize(maxAttributeSize);
        configurationData.setUntagXmlStructures(UIUtil.getBoolean(request, UNTAG_XML_STRUCTURES_NAME));
        configurationData.setExtractNotes(UIUtil.getBoolean(request, EXTRACT_NOTES_NAME));
        configurationData.setExtractNotes(UIUtil.getBoolean(request, EXTRACT_MASTER_SPREADS_NAME));
        configurationData.setExtractNotes(UIUtil.getBoolean(request, EXTRACT_HIDDEN_LAYERS_NAME));

        return errors;
    }

    @Override
    protected IDMLFilterConfigurationData getConfigurationData(WSComponentConfigurationData config) {
        return config instanceof IDMLFilterConfigurationData
                ? (IDMLFilterConfigurationData) config
                : new IDMLFilterConfigurationData();
    }
}
