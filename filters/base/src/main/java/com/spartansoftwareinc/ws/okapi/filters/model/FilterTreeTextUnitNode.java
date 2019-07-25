package com.spartansoftwareinc.ws.okapi.filters.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spartansoftwareinc.ws.okapi.filters.OkapiMultiFilterBridge;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;

public class FilterTreeTextUnitNode extends FilterTreeNode {

    private static final Logger LOG = LoggerFactory.getLogger(FilterTreeTextUnitNode.class);


    private LocaleId sourceLocale;
    private LocaleId targetLocale;

    private Map<Integer, List<FilterTreeNode>> segmentMap;

    public FilterTreeTextUnitNode(ITextUnit textUnit, String encoding, LocaleId sourceLocale, LocaleId targetLocale) {
        this(textUnit, encoding, sourceLocale, targetLocale, null);
    }

    public FilterTreeTextUnitNode(ITextUnit textUnit, String encoding, LocaleId sourceLocale, LocaleId targetLocale, IFilter filterApplied) {
        setTextUnit(textUnit);
        this.setEncoding(encoding);
        this.sourceLocale = sourceLocale;
        this.targetLocale = targetLocale;
        this.setFilterApplied(filterApplied);

    }


    @Override
    public void applyFilterAndCreateChildren(OkapiMultiFilterBridge filterBridge, IFilter filter) {
        segmentMap = new HashMap<>();
        try {
            ISegments segments = getTextUnit().getSource().getSegments();
            int segmentIndex = 0;
            for (Segment segment : segments) {
                String textContent = segment.getContent().getCodedText();
                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(textContent.getBytes(getEncoding()));
                RawDocument mockRawDocument = new RawDocument(byteArrayInputStream, getEncoding(), sourceLocale, targetLocale);
                mockRawDocument.setEncoding(getEncoding());
                List<ITextUnit> newUnits = createTextUnits(filter, mockRawDocument, true);
                if (newUnits.size() > 0) {
                    List<FilterTreeNode> newChildren = addChildren(newUnits, getEncoding(), sourceLocale, targetLocale);
                    setFilterApplied(filter);
                    segmentMap.put(segmentIndex, newChildren);
                }
                segmentIndex += 1;
            }
        } catch (Exception ignored) {

        }

    }

    @Override
    public void updateWithChildrensTranslations(OkapiMultiFilterBridge filterBridge) {

        if (isLeaf()) {
            throw new RuntimeException("Cannot update a leaf node with children's translations because it has no children");
        }

        // If no target is set, add one
        TextContainer targetContainer = getTextUnit().getTarget(getTargetLocale());
        if (targetContainer == null) {
            getTextUnit().setTarget(getTargetLocale(), getTextUnit().getSource().clone());
            targetContainer = getTextUnit().getTarget(getTargetLocale());
        }

        // Loop through each segment and update with the target translation
        ISegments targetSegments = targetContainer.getSegments();
        int segmentIndex = 0;
        for (Segment segment : targetSegments) {
            List<FilterTreeNode> children = segmentMap.get(segmentIndex);

            // Create raw document from text Unit
            InputStream text = null;
            try {
                text = new ByteArrayInputStream(getTextUnit().getSource().toString().getBytes(getEncoding()));
            } catch (UnsupportedEncodingException e) {
                LOG.error("Encoding {} not supported in this node {}.\n{}", getEncoding(), toString(), e.getMessage());
            }
            RawDocument mockRawDocument = new RawDocument(text, getEncoding(), sourceLocale, targetLocale);
            mockRawDocument.setEncoding(getEncoding());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Update outputStream with the translation
            updateChildrenWithTranslations(mockRawDocument, outputStream, children);

            // Update the segment with outputStream
            try {
                segment.getContent().setCodedText(outputStream.toString(getEncoding()));
            } catch (UnsupportedEncodingException e) {
                LOG.error("Encoding {} not supported in this node {}.\n{}", getEncoding(), toString(), e.getMessage());
            }
        }
    }

    public LocaleId getSourceLocale() {
        return sourceLocale;
    }

    public void setSourceLocale(LocaleId sourceLocale) {
        this.sourceLocale = sourceLocale;
    }

    public LocaleId getTargetLocale() {
        return targetLocale;
    }

    public void setTargetLocale(LocaleId targetLocale) {
        this.targetLocale = targetLocale;
    }
}