package com.spartansoftwareinc.ws.okapi.filters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSAssetSegmentationException;
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

    @Override
    public void parse(WSContext context, WSNode srcContent, WSSegmentWriter wsSegmentWriter) {
        File tempSourceFile = null;
        try {
            tempSourceFile = FilterUtil.convertAisContentIntoFile(srcContent);
            LocaleId okapiLocale = FilterUtil.getOkapiLocaleId(srcContent);

            RawDocument srcRawDocument = new RawDocument(tempSourceFile.toURI(),
                    FilterUtil.detectEncoding(srcContent, getDefaultEncoding()), okapiLocale);
            srcRawDocument.setTargetLocale(okapiLocale);

            IFilter filter = getConfiguredFilter(getOkapiFilterConfiguration());
            writeSourceAisPathSegment(srcContent, getOkapiFilterConfiguration(), wsSegmentWriter);
            filterBridge.writeWsSegments(filter, srcRawDocument, wsSegmentWriter, isApplyingSegmentation());

        } catch (IOException ex) {
            getLoggerWithContext().error("File IO failure when parsing WSNode content", ex);
            throw new WSAssetSegmentationException(ex);
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
                    getLoggerWithContext().warn("Couldn't delete temp file " + tempSourceFile + " for filter source node"
                             + srcContent.getPath());
                }
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
        File tempSourceFile = null;
        OutputStream targetFile = null;
        try {
            targetFile = targetContent.getOutputStream();
            LocaleId targetOkapiLocale = FilterUtil.getOkapiLocaleId(targetContent);

            // TODO split out into a sub function that gets the segment from the segmentReader
            T configData = getOkapiFilterConfiguration();
            WSMarkupSegment segment = FilterUtil.expectMarkupSegment(segmentReader);
            MarkupSegmentMetadata<T> meta = MarkupSegmentMetadata.fromSegment(segment, configData);
            WSNode srcContent = context.getAisManager().getNode(meta.getAsset());

            IFilter filter = getConfiguredFilter(meta.getConfig());
            IFilterWriter filterWriter = filter.createFilterWriter();
            filterWriter.setOutput(targetFile);
            filterWriter.setOptions(targetOkapiLocale, FilterUtil.detectEncoding(targetContent, getDefaultEncoding()));

            tempSourceFile = FilterUtil.convertAisContentIntoFile(srcContent);
            LocaleId srcOkapiLocale = FilterUtil.getOkapiLocaleId(srcContent);

            RawDocument srcRawDocument = new RawDocument(tempSourceFile.toURI(),
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
            if (targetFile != null) {
                try {
                    targetFile.close();
                }
                catch (IOException e) {
                    throw new WSAssetSegmentationException(e);
                }
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
            e.printStackTrace();
            throw new WSRuntimeException("Error serializing filter metadata", e);
        }
    }
}
