package com.spartansoftwareinc.ws.autoactions.xliff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSAssetTask;
import com.idiominc.wssdk.asset.WSAssetTranslation;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.component.WSParameter;
import com.idiominc.wssdk.component.WSParameterFactory;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.idiominc.wssdk.workflow.WSTask;
import com.spartansoftwareinc.ws.okapi.Version;
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
    private int nextPlaceholderId = 1;

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

    // TODO: it would be cool to do some refactoring so this code always
    // stayed in sync with OkapiFilterBridge, upon the behavior of
    // which this depends. The current behavior of that class is to
    // produce one WS text segment for each Okapi Segment object within
    // the source TextContainer.
    int injectTargetContent(WSNode node, WSAssetTranslation translation)
            throws WSException {
        List<ITextUnit> xliffTus = getEvents(node);
        @SuppressWarnings("unchecked")
        Iterator<WSTextSegmentTranslation> textSegs = (Iterator<WSTextSegmentTranslation>)translation.textSegmentIterator();
        int count = 0;
        for (ITextUnit xliffTu : xliffTus) {
            TextContainer sourceTc = xliffTu.getSource();
            TextContainer targetTc = findFirstTarget(xliffTu);
            if (targetTc == null) {
                for (@SuppressWarnings("unused")
                Segment seg : sourceTc.getSegments()) {
                    skipSegment(textSegs);
                }
                continue;
            }
            for (Segment seg : targetTc.getSegments()) {
                boolean injected = injectNextSegment(seg, textSegs);
                if (injected) {
                    count++;
                }
            }
        }
        LOG.info("Imported " + count + " segment translations");
        return count;
    }

    TextContainer findFirstTarget(ITextUnit tu) {
        Set<LocaleId> locales = tu.getTargetLocales();
        LocaleId first = locales.iterator().next();
        LOG.debug("First target locale is " + first);
        return tu.getTarget(first);
    }

    void skipSegment(Iterator<WSTextSegmentTranslation> textSegs) {
        checkForMoreWSSegments(textSegs);
        WSTextSegmentTranslation textSeg = textSegs.next();
        LOG.info("Skipping segment [" + textSeg.getSource() + "]");
    }

    /**
     * Update the next text segment translation with content from the XLIFF. Do nothing
     * if the translation was already the same as the XLIFF content.
     * @return true if the translation was updated, false if the translation was already
     * the same as the XLIFF content.
     */
    boolean injectNextSegment(Segment xliffSeg, Iterator<WSTextSegmentTranslation> textSegs) {
        checkForMoreWSSegments(textSegs);
        WSTextSegmentTranslation textSeg = textSegs.next();

        WSTextSegmentData wsMatch = WSTextSegmentData.fromOkapiSegment(xliffSeg);
        String text = assignPlaceholderIds(wsMatch.getText());
        if (textSeg.getTarget() == null || !textSeg.getTarget().equals(text)) {
            LOG.info("Overwriting existing target=[" + textSeg.getTarget() + "] with new target=[" + text + "]");
            textSeg.setTarget(text);
            textSeg.setTranslationType(injectedTranslationType);
            return true;
        } else {
            return false;
        }
    }

    Pattern PH_PATTERN = Pattern.compile("\\{(\\d+)\\}");

    private String assignPlaceholderIds(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                String test = text.substring(i);
                if (test.startsWith(WSFilter.PLACEHOLDER)) {
                    sb.append("{").append(Integer.toString(nextPlaceholderId++)).append("}");
                    i += WSFilter.PLACEHOLDER.length() - 1;
                }
                else {
                    // Escape "fake placeholders"
                    Matcher m = PH_PATTERN.matcher(test);
                    if (m.lookingAt()) {
                        sb.append("\\{").append(m.group(1)).append("\\}");
                        i += m.group().length() - 1;
                    }
                    else {
                        sb.append(c);
                    }
                }
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private void checkForMoreWSSegments(Iterator<WSTextSegmentTranslation> textSegs) {
        if (!textSegs.hasNext()) {
            throw new IllegalStateException("Source XLIFF contains more segments than asset");
        }
    }

    private List<ITextUnit> getEvents(WSNode node) throws WSException {
        LocaleId okapiSrcLocale = FilterUtil.getOkapiLocaleId(node);
        File tempFile = null;
        try (XLIFFFilter filter = new XLIFFFilter()) {
            // Filter may need multiple passes, so we need to buffer this to a
            // temp file
            tempFile = FilterUtil.convertAisContentIntoFile(node);
            String encoding = node.getEncoding() != null ?
                    node.getEncoding() : XLIFFWSOkapiFilter.DEFAULT_XLIFF_ENCODING;
            RawDocument rd = new RawDocument(tempFile.toURI(), encoding, okapiSrcLocale, okapiSrcLocale);
            filter.open(rd, false);
            List<ITextUnit> tus = new ArrayList<ITextUnit>();
            while (filter.hasNext()) {
                Event e = filter.next();
                if (e.isTextUnit()) {
                    tus.add(e.getTextUnit());
                }
            }
            return tus;
        } catch (IOException e) {
            throw new WSException(e);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
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
