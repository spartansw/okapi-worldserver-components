package com.spartansoftwareinc.ws.okapi.filters.po;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSAssetSegmentationException;
import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;
import com.idiominc.wssdk.component.filter.WSFilterUIConfiguration;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.spartansoftwareinc.ws.okapi.Version;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilter;

import net.sf.okapi.filters.po.POFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoWSOkapiFilter extends WSOkapiFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PoWSOkapiFilter.class);
    private static final String FILTER_NAME = "Okapi PO Filter";
    private static final String FILTER_DESCRIPTION = "Bilingual PO filter, using the Okapi PO filter";
    private static final String FILE_EXTENSION = ".po";
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Override
    public String getName() {
        return FILTER_NAME;
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public String getDescription() {
        return FILTER_DESCRIPTION;
    }

    @Override
    protected Logger getLoggerWithContext() {
        return LOG;
    }

    @Override
    public void save(WSContext context, WSNode targetContent, WSSegmentReader segmentReader) {
        super.save(context, targetContent, segmentReader);
        if (getPOFilterConfiguration().getCopyToPO()) {
            copyTargetToPOFile(context, targetContent);
        }
    }

    @Override
    public WSFilterUIConfiguration getUIConfiguration() {
        return new POFilterConfigurationUI();
    }

    @Override
    protected POFilter getConfiguredFilter() {
        POFilter filter = new POFilter();
        filter.setParameters(getPOFilterConfiguration().getParameters());
        return filter;
    }

    protected POFilterConfigurationData getPOFilterConfiguration() {
        WSFilterConfigurationData config = getConfiguration();
        return (config != null && config instanceof POFilterConfigurationData) ?
                (POFilterConfigurationData)config : new POFilterConfigurationData();
    }

    private void copyTargetToPOFile(WSContext context, WSNode targetContent) {
        String tempFilePath = getTempFilePath(targetContent.getPath());
        try {
            context.getAisManager().copy(targetContent, tempFilePath);
        } catch (WSAisException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new WSAssetSegmentationException(ex);
        }
    }

    @Override
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }

    private String getTempFilePath(String nodePath) {
        return nodePath.substring(0, nodePath.lastIndexOf('.')) + FILE_EXTENSION;
    }
}
