package com.spartansoftwareinc.ws.okapi.filters.model;

import java.io.OutputStream;
import java.util.List;

import com.spartansoftwareinc.ws.okapi.filters.OkapiMultiFilterBridge;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;

/**
 * This represents the actual document, and should always be the root node. It's children should always be {@link FilterTreeTextUnitNode}s.
 */
public class FilterTreeRawDocumentNode extends FilterTreeNode {

    private OutputStream translationOutput;


    public FilterTreeRawDocumentNode(RawDocument rawDocument) {
        this(rawDocument, null);
    }

    public FilterTreeRawDocumentNode(RawDocument rawDocument, IFilter filterApplied) {
        setRawDocument(rawDocument);
        setFilterApplied(filterApplied);
        translationOutput = null;
    }

    @Override
    public void applyFilterAndCreateChildren(OkapiMultiFilterBridge filterBridge, IFilter filter) {
        final RawDocument rawDocument = getRawDocument();
        List<ITextUnit> textUnits = createTextUnits(filter, getRawDocument(), false);
        if (textUnits.size() > 0) {
            LocaleId sourceLocale = rawDocument.getSourceLocale();
            LocaleId targetLocale = rawDocument.getTargetLocale();
            if (sourceLocale == null) {
                throw new RuntimeException("Source Locale cannot be null");
            }
            if (targetLocale == null) {
                throw new RuntimeException("Target Locale cannot be null");
            }

            addChildren(textUnits, getEncoding(), sourceLocale, targetLocale);
            setFilterApplied(filter);
        }

    }

    @Override
    public void updateWithChildrensTranslations(OkapiMultiFilterBridge filterBridge) {

        if (isLeaf()) {
            throw new RuntimeException("Cannot update a leaf node with children's translations because it has no children");
        }

        updateChildrenWithTranslations(getRawDocument(), translationOutput, getChildren());
    }

    /**
     * Closes the input stream, making it no longer accessible.
     */
    public void closeRawDocument() {
        getRawDocument().close();
    }

    public OutputStream getTranslationOutput() {
        return translationOutput;
    }

    public void setTranslationOutput(OutputStream translationOutput) {
        this.translationOutput = translationOutput;
    }

    @Override
    public String getEncoding() {
        return getRawDocument().getEncoding();
    }
}
