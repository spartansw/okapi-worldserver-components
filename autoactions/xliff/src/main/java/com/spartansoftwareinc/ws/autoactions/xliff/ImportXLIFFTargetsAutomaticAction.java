package com.spartansoftwareinc.ws.autoactions.xliff;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import net.sf.okapi.common.LocaleId;
import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSAssetTask;
import com.idiominc.wssdk.asset.WSAssetTranslation;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.component.WSParameter;
import com.idiominc.wssdk.component.WSParameterFactory;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.workflow.WSTask;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;
import com.spartansoftwareinc.ws.okapi.filters.xliff.XLIFFWSOkapiFilter;

public class ImportXLIFFTargetsAutomaticAction extends WSTaskAutomaticAction {
    private static final Logger LOG = Logger
            .getLogger(ImportXLIFFTargetsAutomaticAction.class);
    public static final String DONE = "Done";

    private final static String PARAM_ASSET_TYPE = "assetType";
    private final static String PARAM_ASSET_TYPE_TITLE = "Import From";

    private final static String PARAM_TRANSLATION_TYPE = "translationType";
    private final static String PARAM_TRANSLATION_TYPE_TITLE = "Import Translation As";

    private AssetType assetType;
    private WSTranslationType injectedTranslationType;

    @Override
    public String getDescription() {
        return "Copies trans-unit <target> content from an XLIFF asset into the current translation.";
    }

    @Override
    public String getName() {
        return "Import XLIFF Target Content";
    }

    @Override
    public String getVersion() {
        return Version.BANNER;
    }

    @Override
    public WSActionResult execute(WSContext context, @SuppressWarnings("rawtypes") Map parameters, WSTask task)
            throws WSException {
        loadParameters(parameters);

        if (!(task instanceof WSAssetTask)) {
            throw new IllegalStateException("Action run on non-asset task: " + task);
        }
        WSAssetTask assetTask = (WSAssetTask) task;
        WSNode node = getAsset(assetTask);
        // TODO: if it's not an XLIFF file, ignore it
        WSAssetTranslation translation = assetTask.getAssetTranslation();
        int count = injectTargetContent(node, translation);

        final String msg = String.format("Updated %d segment%s", count, count == 1 ? "" : "s");
        return new WSActionResult(DONE, msg);
    }

    protected WSNode getAsset(WSAssetTask assetTask) {
        if (AssetType.SOURCE.equals(assetType)) {
            return assetTask.getSourceAisNode();
        } else {
            return assetTask.getTargetAisNode();
        }
    }

    int injectTargetContent(WSNode node, WSAssetTranslation translation)
            throws WSException {
        LocaleId okapiSrcLocale = FilterUtil.getOkapiLocaleId(node);
        String encoding = node.getEncoding() != null ?
                node.getEncoding() : XLIFFWSOkapiFilter.DEFAULT_XLIFF_ENCODING;
        @SuppressWarnings("unchecked")
        Iterator<WSTextSegmentTranslation> textSegs = (Iterator<WSTextSegmentTranslation>)translation.textSegmentIterator();
        XLIFFTargetContentAligner aligner = new XLIFFTargetContentAligner(injectedTranslationType);
        try {
            int count = aligner.alignTargetContent(node.getInputStream(), encoding, okapiSrcLocale, textSegs);
            LOG.info("Imported " + count + " segment translations");
            return count;
        }
        catch (IOException e) {
            throw new WSRuntimeException(e);
        }
    }

    @Override
    public String[] getReturns() {
        return new String[] { DONE };
    }

    protected void loadParameters(@SuppressWarnings("rawtypes") Map parameters) {
        assetType = AssetType.fromLabel(((String) parameters.get(PARAM_ASSET_TYPE)));
        injectedTranslationType = TranslationType.valueOf(((String) parameters.get(PARAM_TRANSLATION_TYPE)).toUpperCase()).wsTranslationType;
    }

    @Override
    public WSParameter[] getParameters() {

        final WSParameter assetTypeParameter =
                WSParameterFactory.createSelectorParameter(PARAM_ASSET_TYPE, PARAM_ASSET_TYPE_TITLE,
                        enumValues(AssetType.class));

        final WSParameter translationTypeParameter =
                WSParameterFactory.createSelectorParameter(PARAM_TRANSLATION_TYPE, PARAM_TRANSLATION_TYPE_TITLE,
                        enumValues(TranslationType.class));

        return new WSParameter[] {
            assetTypeParameter, translationTypeParameter
        };
    }

    private static String[] enumValues(Class<?> enumClass) {
        String[] names = null;
        Object[] enums = enumClass.getEnumConstants();
        if (enums != null) {
            names = new String[enums.length];
            for (int i = 0; i < enums.length; i++) {
                names[i] = ((Enum<?>) enums[i]).toString();
            }
        }
        return names;
    }

    private static enum AssetType {
        SOURCE("Source Asset"),
        TARGET("Target Asset");

        private String label;
        private AssetType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        public static AssetType fromLabel(String label) {
            for (AssetType at : values()) {
                if (at.label.equals(label)) {
                    return at;
                }
            }
            return null;
        }
    }

    private static enum TranslationType {
        MACHINE_TRANSLATION(WSTranslationType.MACHINE_TRANSLATION),
        MANUAL_TRANSLATION(WSTranslationType.MANUAL_TRANSLATION);

        public WSTranslationType wsTranslationType;

        private TranslationType(WSTranslationType wsTranslationType) {
            this.wsTranslationType = wsTranslationType;
        }
    }
}
