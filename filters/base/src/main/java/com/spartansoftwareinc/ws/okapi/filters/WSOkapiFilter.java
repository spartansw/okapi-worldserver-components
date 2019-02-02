package com.spartansoftwareinc.ws.okapi.filters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSAssetManager;
import com.idiominc.wssdk.asset.WSAssetSegmentationException;
import com.idiominc.wssdk.asset.WSAssetTranslation;
import com.idiominc.wssdk.asset.WSMarkupSegment;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;

public abstract class WSOkapiFilter<T extends WSOkapiFilterConfigurationData<?>> extends WSFilter{
    protected OkapiFilterBridge filterBridge = new OkapiFilterBridge();

    /**
     * Parses the source asset using Okapi filter and writes translatable Okapi TextUnits as WS segments.
     * Also copies the current content in the same folder with a suffix "-{fingerPrint}.save" attached to the name,
     * if one doesn't already exist.
     * The save node's ais path is recorded in the meta segment.
     * <p>
     * Note: Unlike the native WorldServer filters, the Okapi based WorldServer filters do not put
     * the complete data to the segments from which the original content can be reconstructed.
     * They only save the translatable data. They need access to the original content when
     * they merge the translations in save(). The node content changes over time. That is why
     * we make a copy of the source node.
     */
    @Override
    public void parse(WSContext context, WSNode srcContent, WSSegmentWriter wsSegmentWriter) {
        File tempSourceFile = null; //TODO: Remove this and all usage of tempSourceFile if the experiment not using it succeeds.
        String fingerPrint = null;
        try {
            WSAisManager am = context.getAisManager();
            srcContent.lock(); // Lock the node so that it won't be written.
            fingerPrint = srcContent.getFingerprint();
            getLoggerWithContext().warn("parse() called with srcContent whose finger print is {}.", fingerPrint); // TODO: Downgrade to debug or trace.
            String saveFilePath = srcContent.getPath() + "-" + fingerPrint + ".save";

            WSNode savedNode = am.getNode(saveFilePath);
            if (savedNode == null) {
                am.copy(srcContent, saveFilePath);
                savedNode = am.getNode(saveFilePath);
                if( savedNode == null) {
                    getLoggerWithContext().error("Node {} was copied to {}, but getNode on the saved node returned null. Giving up.",
                            srcContent.getPath(), saveFilePath);
                    return;
                }
            }
            getLoggerWithContext().warn("savedNode({}) has been created.", savedNode.getPath()); // TODO: Downgrade to debug or trace.

            //tempSourceFile = FilterUtil.convertAisContentIntoFile(srcContent);
            LocaleId okapiLocale = FilterUtil.getOkapiLocaleId(srcContent);

            RawDocument srcRawDocument = new RawDocument(srcContent.getInputStream()/*tempSourceFile.toURI()*/,
                    FilterUtil.detectEncoding(srcContent, getDefaultEncoding()), okapiLocale);
            srcRawDocument.setTargetLocale(okapiLocale);

            IFilter filter = getConfiguredFilter(getOkapiFilterConfiguration());
            writeSourceAisPathSegment(savedNode, getOkapiFilterConfiguration(), wsSegmentWriter);
            filterBridge.writeWsSegments(filter, srcRawDocument, wsSegmentWriter, isApplyingSegmentation());

//        } catch (IOException ex) {
//            getLoggerWithContext().error("File IO failure when parsing WSNode content", ex);
//            throw new WSAssetSegmentationException(ex);
        } catch (WSAisException ex) {
            getLoggerWithContext().error("Failure to access content repository", ex);
            throw new WSAssetSegmentationException(ex);
        } catch (IllegalStateException ex) {
            getLoggerWithContext().error(ex.getMessage(), ex);
            throw new WSAssetSegmentationException(ex);
        } finally {
            if (tempSourceFile != null) {
                //noinspection ResultOfMethodCallIgnored
                if (!tempSourceFile.delete()) {
                    getLoggerWithContext().warn("Couldn't delete temp file {} for filter source node {}",
                             tempSourceFile, srcContent.getPath());
                }
            }
            try {
                srcContent.unlock();
            } catch (WSAisException ex) {
                getLoggerWithContext().error("Unlocking the source node failed: {}", ex);
            }
        }
    }

    /**
     * Cast the result of getConfiguration() to the appropriate subclass for this filter.
     * @return
     */
    protected abstract T getOkapiFilterConfiguration();

    /**
     * Indicates if WorldServer segmentation should be applied.  This returns false
     * by default, but may be overridden by subclasses.
     * @return true if segmentation should be applied by WorldServer
     */
    protected boolean isApplyingSegmentation() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof WSOkapiFilterConfigurationData<?>) ?
                ((WSOkapiFilterConfigurationData<?>)config).getApplySegmentation() :
                WSOkapiFilterConfigurationData.DEFAULT_APPLY_SEGMENTATION;
    }

    @Override
    public void save(WSContext context, WSNode targetContent, WSSegmentReader segmentReader) {
        File tempSourceFile = null; //TODO: Remove me

        try (OutputStream targetFile = targetContent.getOutputStream()) {
            getLoggerWithContext().warn("save() called with targetContent whose finger print is {}.", targetContent.getFingerprint()); // TODO: Downgrade to debug or trace.

            LocaleId targetOkapiLocale = FilterUtil.getOkapiLocaleId(targetContent);

            // TODO split out into a sub function that gets the segment from the segmentReader
            T configData = getOkapiFilterConfiguration();
            WSMarkupSegment segment = FilterUtil.expectMarkupSegment(segmentReader);
            MarkupSegmentMetadata<T> meta = MarkupSegmentMetadata.fromSegment(segment, configData);

            WSNode srcContent = context.getAisManager().getNode(meta.getAsset());
            getLoggerWithContext().warn("save() is using the saved source node({}) for merge.", srcContent.getPath()); // TODO: Downgrade to debug or trace.


            IFilter filter = getConfiguredFilter(meta.getConfig());
            IFilterWriter filterWriter = filter.createFilterWriter();
            filterWriter.setOutput(targetFile);
            filterWriter.setOptions(targetOkapiLocale, FilterUtil.detectEncoding(targetContent, getDefaultEncoding()));

            //tempSourceFile = FilterUtil.convertAisContentIntoFile(srcContent);
            LocaleId srcOkapiLocale = FilterUtil.getOkapiLocaleId(srcContent);

            RawDocument srcRawDocument = new RawDocument(srcContent.getInputStream() /*tempSourceFile.toURI()*/,
                    FilterUtil.detectEncoding(srcContent, getDefaultEncoding()), srcOkapiLocale);
            srcRawDocument.setTargetLocale(targetOkapiLocale);

            filterBridge.exportSegmentsToFile(srcRawDocument, segmentReader, targetOkapiLocale, filter, filterWriter);

        } catch (IOException ex) {
            getLoggerWithContext().error("File IO failure when parsing WSNode content", ex);
        } catch (IllegalStateException ex) {
            getLoggerWithContext().error("Failure to access content repository", ex);
        } catch (WSAisException ex) {
            getLoggerWithContext().error(ex.getMessage(), ex);
        } finally {
            if (tempSourceFile != null) {
                //noinspection ResultOfMethodCallIgnored
                tempSourceFile.delete();
            }
        }
    }

    protected abstract Logger getLoggerWithContext();

    protected abstract IFilter getConfiguredFilter(T configData);

    protected abstract String getDefaultEncoding();

    /**
     * During import into WorldServer, we keep track of the source AIS path to use during export to preserve any
     * metadata in the PO file by writing a markup segment containing the AIS path. We will later reparse this during
     * export.
     *
     * @param srcContent    - AIS content
     * @param configData    - filter configuration used to filter the asset
     * @param segmentWriter - Writer to communicate with WorldServer
     */
    protected void writeSourceAisPathSegment(WSNode srcContent, T configData, WSSegmentWriter segmentWriter) {
        try {
            MarkupSegmentMetadata<T> meta = MarkupSegmentMetadata.fromAsset(srcContent, configData);
            segmentWriter.writeMarkupSegment(meta.toXML());
        }
        catch (Exception e) {
            throw new WSRuntimeException("Error serializing filter metadata", e);
        }
    }
}
