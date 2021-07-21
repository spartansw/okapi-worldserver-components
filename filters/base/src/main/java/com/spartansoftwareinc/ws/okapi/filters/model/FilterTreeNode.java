package com.spartansoftwareinc.ws.okapi.filters.model;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spartansoftwareinc.ws.okapi.filters.OkapiMultiFilterBridge;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;


/**
 * Designed to handle multiple levels of Filtering. The root node is based around a {@link RawDocument}, which is
 * the source content. The children nodes are based around the {@link ITextUnit} which are a result of applying a filter
 * to the parent node.
 * <p>
 * When a node is first created, it has no children and no filter applied. When a filter is applied to the node, the
 * generated {@link ITextUnit}w from there are set as its children, and the filter used it set as
 * {@link FilterTreeNode#filterApplied}.
 * <p>
 * To generate a tree:
 * 1. Create the root node {@link FilterTreeRawDocumentNode#FilterTreeRawDocumentNode(RawDocument)}
 * 2. Generate children with a filter {@link FilterTreeNode#applyFilterOnLeavesAndCreateChildren(OkapiMultiFilterBridge, IFilter)}
 * 3. Repeat step 3 for each filter
 * <p>
 * To get the final units after generating a tree:
 * 1. Grab them {@link FilterTreeNode#getLeaves()}
 * 2. Grab the text units from each one {@link FilterTreeNode#getTextUnit()}
 * <p>
 * To create output document from translations:
 * 1. Set the output stream of the root node{@link FilterTreeRawDocumentNode#setTranslationOutput(OutputStream)}, then use {@link }
 * 2. Rebuild entire tree with the root node {@link FilterTreeNode#updateEntireTreesTranslations(OkapiMultiFilterBridge)}
 * 3. Output will be in your Outputstream you originally set {@link FilterTreeRawDocumentNode#getTranslationOutput()}
 */
public abstract class FilterTreeNode {

    private static final Logger LOG = LoggerFactory.getLogger(FilterTreeNode.class);


    private IFilter filterApplied = null;
    private List<FilterTreeNode> children = new ArrayList<>();
    private FilterTreeNode parent = null;

    private RawDocument rawDocument = null;
    private ITextUnit textUnit = null;

    private String encoding = null;


    public enum NODE_TYPE {
        ROOT_RAW_DOCUMENT, INTERMEDIATE_TEXT_UNIT, FINAL_TEXT_UNIT, INVALID
    }

    public FilterTreeTextUnitNode addChild(ITextUnit child, String encoding, LocaleId sourceLocale, LocaleId targetLocale) {
        final FilterTreeTextUnitNode node = new FilterTreeTextUnitNode(child, encoding, sourceLocale, targetLocale);
        node.setParent(this);
        this.children.add(node);
        return node;
    }

    public List<FilterTreeNode> addChildren(List<ITextUnit> children, String encoding, LocaleId sourceLocale, LocaleId targetLocale) {
        final List<FilterTreeNode> addedChildren = new ArrayList<>();
        for (ITextUnit child : children) {
            final FilterTreeTextUnitNode node = this.addChild(child, encoding, sourceLocale, targetLocale);
            addedChildren.add(node);
        }
        return addedChildren;
    }

    public List<FilterTreeNode> getChildren() {
        return children;
    }

    public List<FilterTreeNode> getChildren(int fromIndex, int toIndex) {
        return children.subList(fromIndex, toIndex);
    }

    public FilterTreeNode getParent() {
        return parent;
    }

    public void setParent(FilterTreeNode parent) {
        this.parent = parent;
    }


    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }

    public IFilter getFilterApplied() {
        return filterApplied;
    }

    public void setFilterApplied(IFilter filterApplied) {
        this.filterApplied = filterApplied;
    }


    public RawDocument getRawDocument() {
        return rawDocument;
    }

    public void setRawDocument(RawDocument rawDocument) {
        this.rawDocument = rawDocument;
    }

    public ITextUnit getTextUnit() {
        return textUnit;
    }

    public void setTextUnit(ITextUnit textUnit) {
        this.textUnit = textUnit;
    }

    public NODE_TYPE getType() {
        if (textUnit != null) {
            if (isLeaf()) {
                return NODE_TYPE.FINAL_TEXT_UNIT;
            } else {
                return NODE_TYPE.INTERMEDIATE_TEXT_UNIT;
            }
        }
        if (rawDocument != null) {
            return NODE_TYPE.ROOT_RAW_DOCUMENT;
        }

        return NODE_TYPE.INVALID;

    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public List<FilterTreeNode> postOrderTraversal() {

        List<FilterTreeNode> postOrderList = new ArrayList<>();
        traversePost(this, postOrderList);
        return postOrderList;
    }


    private void traversePost(FilterTreeNode node, List<FilterTreeNode> postOrderTraversalList) {

        for (FilterTreeNode child : node.getChildren()) {
            traversePost(child, postOrderTraversalList);
        }
        postOrderTraversalList.add(node);
    }

    public List<FilterTreeNode> getLeaves() {
        List<FilterTreeNode> leaves = new ArrayList<>();
        for (FilterTreeNode child : preOrderTraversal()) {
            if (child.isLeaf()) {
                leaves.add(child);
            }

        }
        return leaves;
    }

    public List<FilterTreeNode> preOrderTraversal() {
        List<FilterTreeNode> preOrderList = new ArrayList<>();
        traversePre(this, preOrderList);
        return preOrderList;
    }

    private void traversePre(FilterTreeNode node, List<FilterTreeNode> preOrderTraversalList) {
        preOrderTraversalList.add(node);
        for (FilterTreeNode child : node.getChildren()) {
            traversePre(child, preOrderTraversalList);
        }
    }

    /**
     * Will take the input filter, apply it to the current node, and then create a child {@link FilterTreeTextUnitNode} for each {@link ITextUnit}.
     *
     * @param filterBridge
     * @param filter
     */
    public abstract void applyFilterAndCreateChildren(OkapiMultiFilterBridge filterBridge, IFilter filter);

    /**
     * Traverses the entire tree and applies the {@link #applyFilterAndCreateChildren(OkapiMultiFilterBridge, IFilter)} method on every leaf.
     *
     * @param filterBridge
     * @param filter
     */
    public void applyFilterOnLeavesAndCreateChildren(OkapiMultiFilterBridge filterBridge, IFilter filter) {
        List<FilterTreeNode> leaves = this.getLeaves();
        for (FilterTreeNode node : leaves) {
            node.applyFilterAndCreateChildren(filterBridge, filter);
        }
    }

    /**
     * Reads the children's target translations, and updates the target translation of the current node.
     *
     * @param filterBridge
     */
    public abstract void updateWithChildrensTranslations(OkapiMultiFilterBridge filterBridge);

    /**
     * Reads the children's target translations, and updates the target translation of the current node.
     *
     * @param sourceRawDocument The original, untranslated, content of the current node.
     * @param outputTranslation The output where you want the translated content to go
     * @param children          The children to use. This is a parameter because not all children might not be used.
     */
    protected void updateChildrenWithTranslations(RawDocument sourceRawDocument, OutputStream outputTranslation, List<FilterTreeNode> children) {

        IFilter filter = getFilterApplied();

        // Prepare the Okapi writer
        filter.open(sourceRawDocument);
        IFilterWriter filterWriter = filter.createFilterWriter();
        filterWriter.setOptions(sourceRawDocument.getTargetLocale(), getEncoding());
        filterWriter.setOutput(outputTranslation);


        // Grab the ITextUnits
        List<ITextUnit> childrenTextUnits = new ArrayList<>();
        for (FilterTreeNode child : children) {
            childrenTextUnits.add(child.getTextUnit());
        }

        // Execute the writing
        int textUnitIndex = 0;
        try {
            while (filter.hasNext()) {
                Event event = filter.next();
                if (event.isTextUnit()) {

                    // Update the text unit with the translated text unit
                    ITextUnit untranslatedTextUnit = event.getTextUnit();
                    ITextUnit translatedTextUnit = childrenTextUnits.get(textUnitIndex);
                    TextContainer translatedTarget = translatedTextUnit.getTarget(sourceRawDocument.getTargetLocale());
                    if (translatedTarget != null) {
                        untranslatedTextUnit.setTarget(sourceRawDocument.getTargetLocale(), translatedTarget.clone());
                    }
                    textUnitIndex++;
                }
                filterWriter.handleEvent(event);
            }
        } finally {
            filter.close();

        }
    }

    public OutputStream getTranslationOutput() {
        throw new UnsupportedOperationException();
    }

    /**
     * From the leaves all the way to the root, update all the translated text.
     *
     * @param filterBridge
     */
    public void updateEntireTreesTranslations(OkapiMultiFilterBridge filterBridge) {
        for (FilterTreeNode node : this.postOrderTraversal()) {
            if (!node.isLeaf()) {
                node.updateWithChildrensTranslations(filterBridge);
            }
        }
    }

    @Override
    public String toString() {
        NODE_TYPE type = getType();
        String filter = "";
        if (filterApplied != null) {
            filter = filterApplied.getName();
        }
        if (type == NODE_TYPE.ROOT_RAW_DOCUMENT) {
            return String.format("%s %s %s %s", type.name(), getEncoding(), filter, rawDocument);
        } else {
            TextContainer target = textUnit.getTarget(((FilterTreeTextUnitNode) this).getTargetLocale());
            return String.format("%s %s %s %s ===> %s", type.name(), getEncoding(), filter, textUnit, target);
        }
    }

    /**
     * @param filter           The filter to use and apply to this node
     * @param srcRawDocument   The document to read
     * @param closeRawDocument Close the document, which may have a stream open, after creating the text units.
     * @return
     */
    public List<ITextUnit> createTextUnits(IFilter filter, RawDocument srcRawDocument, boolean closeRawDocument) {
        List<ITextUnit> textUnits = new ArrayList<>();
        filter.open(srcRawDocument);
        try {
            while (filter.hasNext()) {
                Event event = filter.next();
                if (event.isTextUnit()) {
                    ITextUnit textUnit = event.getTextUnit();
                    if (textUnit.isTranslatable()) {
                        textUnits.add(textUnit);
                    }
                }
            }
            return textUnits;
        } finally {
            if (closeRawDocument) {
                filter.close();
            }
        }
    }
}
