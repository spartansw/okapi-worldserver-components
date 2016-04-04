package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class OpenXMLFilterConfigurationData extends WSOkapiFilterConfigurationData<ConditionalParameters>{
    private static final long serialVersionUID = 1L;

    private boolean translateDocProperties = true;
    private boolean translateComments = true;
    private boolean aggressiveCleanup = false;
    private boolean automaticallyAcceptRevisions = true;
    private boolean translatePowerpointNotes = true;
    private boolean translatePowerpointMasters = true;
    private boolean translateWordHeadersFooters = true;
    private boolean translateWordHidden = false;
    private boolean translateWordExcludeGraphicMetaData = false;
    private boolean translateExcelHidden = false;
    private boolean addTabAsCharacter = false;
    private boolean addLineSeparatorAsCharacter = false;
    private boolean replaceNoBreakHyphenTag = false;
    private boolean ignoreSoftHyphenTag = false;
//    public TreeSet<String> tsExcelExcludedColors; TODO
//    public TreeSet<String> tsExcelExcludedColumns; TODO
//    public TreeSet<String> tsExcludeWordStyles; TODO

    public boolean isTranslateDocProperties() {
        return translateDocProperties;
    }

    public void setTranslateDocProperties(boolean translateDocProperties) {
        this.translateDocProperties = translateDocProperties;
    }

    public boolean isTranslateComments() {
        return translateComments;
    }

    public void setTranslateComments(boolean translateComments) {
        this.translateComments = translateComments;
    }

    public boolean isAggressiveCleanup() {
        return aggressiveCleanup;
    }

    public void setAggressiveCleanup(boolean aggressiveCleanup) {
        this.aggressiveCleanup = aggressiveCleanup;
    }

    public boolean isAutomaticallyAcceptRevisions() {
        return automaticallyAcceptRevisions;
    }

    public void setAutomaticallyAcceptRevisions(boolean automaticallyAcceptRevisions) {
        this.automaticallyAcceptRevisions = automaticallyAcceptRevisions;
    }

    public boolean isTranslatePowerpointNotes() {
        return translatePowerpointNotes;
    }

    public void setTranslatePowerpointNotes(boolean translatePowerpointNotes) {
        this.translatePowerpointNotes = translatePowerpointNotes;
    }

    public boolean isTranslatePowerpointMasters() {
        return translatePowerpointMasters;
    }

    public void setTranslatePowerpointMasters(boolean translatePowerpointMasters) {
        this.translatePowerpointMasters = translatePowerpointMasters;
    }

    public boolean isTranslateWordHeadersFooters() {
        return translateWordHeadersFooters;
    }

    public void setTranslateWordHeadersFooters(boolean translateWordHeadersFooters) {
        this.translateWordHeadersFooters = translateWordHeadersFooters;
    }

    public boolean isTranslateWordHidden() {
        return translateWordHidden;
    }

    public void setTranslateWordHidden(boolean translateWordHidden) {
        this.translateWordHidden = translateWordHidden;
    }

    public boolean isTranslateWordExcludeGraphicMetaData() {
        return translateWordExcludeGraphicMetaData;
    }

    public void setTranslateWordExcludeGraphicMetaData(boolean translateWordExcludeGraphicMetaData) {
        this.translateWordExcludeGraphicMetaData = translateWordExcludeGraphicMetaData;
    }

    public boolean isTranslateExcelHidden() {
        return translateExcelHidden;
    }

    public void setTranslateExcelHidden(boolean translateExcelHidden) {
        this.translateExcelHidden = translateExcelHidden;
    }

    public boolean isAddTabAsCharacter() {
        return addTabAsCharacter;
    }

    public void setAddTabAsCharacter(boolean addTabAsCharacter) {
        this.addTabAsCharacter = addTabAsCharacter;
    }

    public boolean isAddLineSeparatorAsCharacter() {
        return addLineSeparatorAsCharacter;
    }

    public void setAddLineSeparatorAsCharacter(boolean addLineSeparatorAsCharacter) {
        this.addLineSeparatorAsCharacter = addLineSeparatorAsCharacter;
    }

    public boolean isReplaceNoBreakHyphenTag() {
        return replaceNoBreakHyphenTag;
    }

    public void setReplaceNoBreakHyphenTag(boolean replaceNoBreakHyphenTag) {
        this.replaceNoBreakHyphenTag = replaceNoBreakHyphenTag;
    }

    public boolean isIgnoreSoftHyphenTag() {
        return ignoreSoftHyphenTag;
    }

    public void setIgnoreSoftHyphenTag(boolean ignoreSoftHyphenTag) {
        this.ignoreSoftHyphenTag = ignoreSoftHyphenTag;
    }

    @Override
    protected ConditionalParameters getDefaultParameters() {
        ConditionalParameters parameters = new ConditionalParameters();
        return parameters;
    }

    @Override
    protected void saveAdditionalConfiguration(Document doc, Node parent) {
        saveParameter(doc, parent, translateDocProperties, "translateDocProperties");
        saveParameter(doc, parent, translateComments, "translateComments");
        saveParameter(doc, parent, aggressiveCleanup, "aggressiveCleanup");
        saveParameter(doc, parent, automaticallyAcceptRevisions, "automaticallyAcceptRevisions");
        saveParameter(doc, parent, translatePowerpointNotes, "translatePowerpointNotes");
        saveParameter(doc, parent, translatePowerpointMasters, "translatePowerpointMasters");
        saveParameter(doc, parent, translateWordHeadersFooters, "translateWordHeadersFooters");
        saveParameter(doc, parent, translateWordHidden, "translateWordHidden");
        saveParameter(doc, parent, translateWordExcludeGraphicMetaData, "translateWordExcludeGraphicMetaData");
        saveParameter(doc, parent, translateExcelHidden, "translateExcelHidden");
        saveParameter(doc, parent, addTabAsCharacter, "addTabAsCharacter");
        saveParameter(doc, parent, addLineSeparatorAsCharacter, "addLineSeparatorAsCharacter");
        saveParameter(doc, parent, replaceNoBreakHyphenTag, "replaceNoBreakHyphenTag");
        saveParameter(doc, parent, ignoreSoftHyphenTag, "ignoreSoftHyphenTag");
    }

    private void saveParameter(Document doc, Node parent, boolean value, String name) {
        Node child = parent.appendChild(doc.createElement(name));
        child.appendChild(doc.createTextNode(Boolean.toString(value)));
    }

    @Override
    protected void loadAdditionalConfiguration(Node configNode) {
        if (configNode.getNodeName().equals("translateDocProperties")) {
            translateDocProperties = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translateCommentsNode")) {
            translateComments = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("aggressiveCleanupNode")) {
            aggressiveCleanup = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("automaticallyAcceptRevisionsNode")) {
            automaticallyAcceptRevisions = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translatePowerpointNotes")) {
            translatePowerpointNotes = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translatePowerpointMasters")) {
            translatePowerpointMasters = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translateWordHeadersFooters")) {
            translateWordHeadersFooters = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translateWordHidden")) {
            translateWordHidden = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translateWordExcludeGraphicMetaData")) {
            translateWordExcludeGraphicMetaData = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("translateExcelHidden")) {
            translateExcelHidden = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("addTabAsCharacter")) {
            addTabAsCharacter = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("addLineSeparatorAsCharacter")) {
            addLineSeparatorAsCharacter = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("replaceNoBreakHyphenTag")) {
            replaceNoBreakHyphenTag = Boolean.valueOf(configNode.getTextContent());
        } else if (configNode.getNodeName().equals("ignoreSoftHyphenTag")) {
            ignoreSoftHyphenTag = Boolean.valueOf(configNode.getTextContent());
        }
    }

    //TODO remove this comments after everything is sorted out. make sure I didn't miss anything
//    private static final String TRANSLATEDOCPROPERTIES = "bPreferenceTranslateDocProperties";
//    private static final String TRANSLATECOMMENTS = "bPreferenceTranslateComments";
//    private static final String AGGRESSIVECLEANUP = "bPreferenceAggressiveCleanup";
//    private static final String AUTOMATICALLY_ACCEPT_REVISIONS = "bPreferenceAutomaticallyAcceptRevisions";
//    private static final String TRANSLATEPOWERPOINTNOTES = "bPreferenceTranslatePowerpointNotes";
//    private static final String TRANSLATEPOWERPOINTMASTERS = "bPreferenceTranslatePowerpointMasters";
//    private static final String TRANSLATEWORDHEADERSFOOTERS = "bPreferenceTranslateWordHeadersFooters";
//    private static final String TRANSLATEWORDALLSTYLES = "bPreferenceTranslateWordAllStyles"; // tsExcludeWordStyles hack?
//    private static final String TRANSLATEWORDHIDDEN = "bPreferenceTranslateWordHidden";
//    private static final String TRANSLATEEXCELEXCLUDECOLORS = "bPreferenceTranslateExcelExcludeColors"; // tsExcelExcludedColors hack?
//    private static final String TRANSLATEEXELEXCLUDECOLUMNS = "bPreferenceTranslateExcelExcludeColumns"; // tsExcelExcludedColumns hack?
//    private static final String TRANSLATEWORDEXCLUDEGRAPHICMETADATA ="bPreferenceTranslateWordExcludeGraphicMetaData";
//    private static final String TRANSLATEEXCELHIDDEN = "bPreferenceTranslateExcelHidden";
//    private static final String ADDTABASCHARACTER = "bPreferenceAddTabAsCharacter";
//    private static final String ADDLINESEPARATORASCHARACTER ="bPreferenceAddLineSeparatorAsCharacter";
//    private static final String REPLACE_NO_BREAK_HYPHEN_TAG ="bPreferenceReplaceNoBreakHyphenTag";
//    private static final String IGNORE_SOFT_HYPHEN_TAG ="bPreferenceIgnoreSoftHyphenTag";
//    public final static int MSWORD=1; // Looks like that's just a random constant
//    public TreeSet<String> tsExcelExcludedColors;
//    public TreeSet<String> tsExcelExcludedColumns;
//    public TreeSet<String> tsExcludeWordStyles;
//    public ParseType nFileType=ParseType.MSWORD; // "Not serialized, this is state that is stashed in the parameters as a hack."
}
