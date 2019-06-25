package com.spartansoftwareinc.ws.okapi.filters;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSAssetSegmentationException;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;
import com.spartansoftwareinc.ws.okapi.filters.model.FilterTreeNode;
import com.spartansoftwareinc.ws.okapi.filters.model.FilterTreeRawDocumentNode;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;

public abstract class WSOkapiMultiFilter extends WSFilter {
    private static final Logger LOG = LoggerFactory.getLogger(WSOkapiMultiFilter.class);


    protected OkapiMultiFilterBridge filterBridge = new OkapiMultiFilterBridge();

    @Override
    public void parse(WSContext context, WSNode srcContent, WSSegmentWriter wsSegmentWriter) {
        try {

            InputStream content = srcContent.getInputStream();
            String encoding = srcContent.getEncoding();
            LocaleId sourceAndTargetLocale = FilterUtil.getOkapiLocaleId(srcContent);
            RawDocument srcRawDocument = new RawDocument(content, encoding, sourceAndTargetLocale, sourceAndTargetLocale);

            // Save information for later retrieval
            writeSourceAisPathSegment(srcContent, wsSegmentWriter);

            // Build filter Tree
            FilterTreeNode root = buildTree(srcRawDocument);

            // Take the text units and add them as segments
            List<FilterTreeNode> leaves = root.getLeaves();
            for (FilterTreeNode leaf : leaves) {
                // In some cases, the leaves of the tree may not be generated TextUnits, such as when the root node could not be translated.
                if (leaf.getType() == FilterTreeNode.NODE_TYPE.FINAL_TEXT_UNIT) {
                    ITextUnit textUnit = leaf.getTextUnit();
                    filterBridge.processTextUnit(wsSegmentWriter, textUnit);
                }
            }

        } catch (WSAisException ex) {
            getLoggerWithContext().error("Failure to access content repository", ex);
            throw new WSAssetSegmentationException(ex);
        } catch (IllegalStateException ex) {
            getLoggerWithContext().error(ex.getMessage(), ex);
            throw new WSAssetSegmentationException(ex);
        }
    }

    @Override
    public void save(WSContext context, WSNode targetContent, WSSegmentReader segmentReader) {

        try {
            // Where the translated content will be put
            OutputStream targetContentOutputStream = targetContent.getOutputStream();

            // Get source data by reading the AIS path from the first segment, and then using that to read the file
            String AISPath = readSourceAisPathSegment(segmentReader);
            WSNode sourceContent = context.getAisManager().getNode(AISPath);
            InputStream content = sourceContent.getInputStream();
            String encoding = sourceContent.getEncoding();
            LocaleId sourceLocale = FilterUtil.getOkapiLocaleId(sourceContent);
            LocaleId targetLocale = sourceLocale;
            RawDocument srcRawDocument = new RawDocument(content, encoding, sourceLocale, targetLocale);

            FilterTreeRawDocumentNode root = buildTree(srcRawDocument);

            // Update the leaves with the data from Worldserver
            List<FilterTreeNode> leaves = root.getLeaves();
            for (FilterTreeNode leaf : leaves) {
                if (leaf.getType() == FilterTreeNode.NODE_TYPE.FINAL_TEXT_UNIT) {
                    filterBridge.processTextUnit(segmentReader, targetLocale, leaf.getTextUnit());
                }
            }

            // Update the tree and output the data
            reconstructTreeFilters(root, targetContentOutputStream, filterBridge);

            // Close the raw document's input stream
            root.closeRawDocument();

        } catch (Exception ex) {
            ex.printStackTrace();
            getLoggerWithContext().error(ex.getMessage(), ex);
            throw new WSAssetSegmentationException(ex);
        }


    }

    protected abstract Logger getLoggerWithContext();

    /**
     * The tree is built both during {@link #parse(WSContext, WSNode, WSSegmentWriter)} and {@link #save(WSContext, WSNode, WSSegmentReader)}
     * to ensure that the output is equal.
     *
     * @param srcRawDocument The original source document.
     * @return The root of the tree.
     */
    protected abstract FilterTreeRawDocumentNode buildTree(RawDocument srcRawDocument);

    protected abstract void reconstructTreeFilters(FilterTreeRawDocumentNode root, OutputStream
            targetOutput, OkapiMultiFilterBridge filterBridge);

    /**
     * During import into WorldServer, we keep track of the source AIS path to use during export to preserve any
     * metadata in the PO file by writing a markup segment containing the AIS path. We will later reparse this during
     * export.
     *
     * @param srcContent    - AIS content
     * @param segmentWriter - Writer to communicate with WorldServer
     */
    private void writeSourceAisPathSegment(WSNode srcContent, WSSegmentWriter segmentWriter) {
        try {
            segmentWriter.writeMarkupSegment(srcContent.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new WSRuntimeException("Error serializing filter metadata", e);
        }
    }

    private String readSourceAisPathSegment(WSSegmentReader segmentReader) {
        try {
            WSSegment segment = segmentReader.read();
            return segment.getContent();

        } catch (Exception e) {
            e.printStackTrace();
            throw new WSRuntimeException("Error serializing filter metadata", e);
        }
    }

}
