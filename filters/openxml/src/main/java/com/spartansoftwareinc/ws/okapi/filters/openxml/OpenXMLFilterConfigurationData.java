package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;
import net.sf.okapi.filters.openxml.ConditionalParameters;

public class OpenXMLFilterConfigurationData extends WSOkapiFilterConfigurationData<ConditionalParameters>{
    private static final long serialVersionUID = 1L;

    @Override
    protected ConditionalParameters getDefaultParameters() {
        ConditionalParameters parameters = new ConditionalParameters();
        parameters.setAutomaticallyAcceptRevisions(true);
        return parameters;
    }

//    public TreeSet<String> tsExcelExcludedColors; TODO
//    public TreeSet<String> tsExcelExcludedColumns; TODO
//    public TreeSet<String> tsExcludeWordStyles; TODO

    public boolean isTranslateDocProperties() {
        return getParameters().getTranslateDocProperties();
    }

    public void setTranslateDocProperties(boolean translateDocProperties) {
        ConditionalParameters params = getParameters();
        params.setTranslateDocProperties(translateDocProperties);
        setParameters(params);
    }

    public boolean isTranslateComments() {
        return getParameters().getTranslateComments();
    }

    public void setTranslateComments(boolean translateComments) {
        ConditionalParameters params = getParameters();
        params.setTranslateComments(translateComments);
        setParameters(params);
    }

    public boolean isAggressiveCleanup() {
        return getParameters().getCleanupAggressively();
    }

    public void setAggressiveCleanup(boolean aggressiveCleanup) {
        ConditionalParameters params = getParameters();
        params.setCleanupAggressively(aggressiveCleanup);
        setParameters(params);
    }

    public boolean isAutomaticallyAcceptRevisions() {
        return getParameters().getAutomaticallyAcceptRevisions();
    }

    public void setAutomaticallyAcceptRevisions(boolean automaticallyAcceptRevisions) {
        ConditionalParameters params = getParameters();
        params.setAutomaticallyAcceptRevisions(automaticallyAcceptRevisions);
        setParameters(params);
    }

    public boolean isTranslatePowerpointNotes() {
        return getParameters().getTranslatePowerpointNotes();
    }

    public void setTranslatePowerpointNotes(boolean translatePowerpointNotes) {
        ConditionalParameters params = getParameters();
        params.setTranslatePowerpointNotes(translatePowerpointNotes);
        setParameters(params);
    }

    public boolean isTranslatePowerpointMasters() {
        return getParameters().getTranslatePowerpointMasters();
    }

    public void setTranslatePowerpointMasters(boolean translatePowerpointMasters) {
        ConditionalParameters params = getParameters();
        params.setTranslatePowerpointMasters(translatePowerpointMasters);
        setParameters(params);
    }

    public boolean isTranslateWordHeadersFooters() {
        return getParameters().getTranslateWordHeadersFooters();
    }

    public void setTranslateWordHeadersFooters(boolean translateWordHeadersFooters) {
        ConditionalParameters params = getParameters();
        params.setTranslateWordHeadersFooters(translateWordHeadersFooters);
        setParameters(params);
    }

    public boolean isTranslateWordHidden() {
        return getParameters().getTranslateWordHidden();
    }

    public void setTranslateWordHidden(boolean translateWordHidden) {
        ConditionalParameters params = getParameters();
        params.setTranslateWordHidden(translateWordHidden);
        setParameters(params);
    }

    public boolean isTranslateWordExcludeGraphicMetaData() {
        return getParameters().getTranslateWordExcludeGraphicMetaData();
    }

    public void setTranslateWordExcludeGraphicMetaData(boolean translateWordExcludeGraphicMetaData) {
        ConditionalParameters params = getParameters();
        params.setTranslateWordExcludeGraphicMetaData(translateWordExcludeGraphicMetaData);
        setParameters(params);
    }

    public boolean isTranslateExcelHidden() {
        return getParameters().getTranslateExcelHidden();
    }

    public void setTranslateExcelHidden(boolean translateExcelHidden) {
        ConditionalParameters params = getParameters();
        params.setTranslateExcelHidden(translateExcelHidden);
        setParameters(params);
    }

    public boolean isAddTabAsCharacter() {
        return getParameters().getAddTabAsCharacter();
    }

    public void setAddTabAsCharacter(boolean addTabAsCharacter) {
        ConditionalParameters params = getParameters();
        params.setAddTabAsCharacter(addTabAsCharacter);
        setParameters(params);
    }

    public boolean isAddLineSeparatorAsCharacter() {
        return getParameters().getAddTabAsCharacter();
    }

    public void setAddLineSeparatorAsCharacter(boolean addLineSeparatorAsCharacter) {
        ConditionalParameters params = getParameters();
        params.setAddLineSeparatorCharacter(addLineSeparatorAsCharacter);
        setParameters(params);
    }

    public boolean isReplaceNoBreakHyphenTag() {
        return getParameters().getReplaceNoBreakHyphenTag();
    }

    public void setReplaceNoBreakHyphenTag(boolean replaceNoBreakHyphenTag) {
        ConditionalParameters params = getParameters();
        params.setReplaceNoBreakHyphenTag(replaceNoBreakHyphenTag);
        setParameters(params);
    }

    public boolean isIgnoreSoftHyphenTag() {
        return getParameters().getIgnoreSoftHyphenTag();
    }

    public void setIgnoreSoftHyphenTag(boolean ignoreSoftHyphenTag) {
        ConditionalParameters params = getParameters();
        params.setIgnoreSoftHyphenTag(ignoreSoftHyphenTag);
        setParameters(params);
    }

}
