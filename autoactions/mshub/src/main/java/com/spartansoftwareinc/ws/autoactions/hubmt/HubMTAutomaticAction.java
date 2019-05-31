/**
 * Copyright (c) 2016 Spartan Software, Inc.  All Rights
 * Reserved.
 */
package com.spartansoftwareinc.ws.autoactions.hubmt;

import java.util.Map;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.component.WSParameter;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.mt.WSMTResult;
import com.spartansoftwareinc.ws.autoactions.SegmentMTAutomaticAction;
import com.spartansoftwareinc.ws.autoactions.hubmt.WSPlaceholderUtil.PHData;

/**
 * <p>
 * This automatic action is to translate an asset using a machine translation.
 * </p>
 * <p>
 * This is tuned for Microsoft Hub MT engine, which tend to get confused with WorldServer placeholder tags {0}, {1},
 * {2}, but performs reasonably well with regular HTML/XML tags; i.e. it leaves the text inside the tags intact but it
 * moves the tags along with the surrounding phrase, if translation requires re-ordering of words. This sends the
 * original text with HTML/XML tags restored to the MT engine, and restores the placeholder tags on the translated text.
 * </p>
 * <p>
 * This automatic action is optimized for use with Microsoft Hub MT engine, as it expects specific behavior of the MT
 * engine, such as normalization of spaces within a tag, removal or insertion of unbalanced tags within a segment.
 * </p>
 * <p>
 * This automatic action may change the order of the tags incorrectly. The translator or reviewer should pay close
 * attention to the tag order.
 * </p>
 * <p>
 * Usage note: Be sure to turn off machine translation in the filter. Instead, this automatic action must be in the
 * workflow.
 * </p>
 */
public class HubMTAutomaticAction
    extends SegmentMTAutomaticAction
{

    private static final String AUTO_ACTION_NAME = "Hub MT Automatic Action";
    private static final Logger log = Logger.getLogger( HubMTAutomaticAction.class );

    protected Logger getLogger() {
        return log;
    }

    /**
     * Gets the name of this automatic action.
     *
     * @return The name of this automatic action.
     */
    @Override
    public String getName()
    {
        return AUTO_ACTION_NAME;
    }

    /**
     * Gets the automatic action version.
     *
     * @return The automatic action version.
     */
    @Override
    public String getVersion()
    {
        return Version.BANNER;
    }

    /**
     * Gets the description of this automatic action.
     *
     * @return The description of this automatic action.
     */
    @Override
    public String getDescription()
    {
        return Version.PROJECT_NAME;
    }

    /**
     * Returns the list of supported parameters. 
     * 
     * @return Empty list.
     */
    @Override
    public WSParameter[] getParameters()
    {
        return super.getParameters();
    }

    /**
     * Returns an array of possible automatic action results. This automatic action always returns "Done", however.
     * 
     * @return An array of a single element, the string "Done"
     */
    @Override
    public String[] getReturns()
    {
        return super.getReturns();
    }

    private String leadingEndTags = null;
    private Map<Integer, PHData> phmap = null;

    @Override
    protected String getSegmentTextForMT(WSTextSegmentTranslation seg, WSLanguage sourceLang, WSLanguage targetLang) {
        // Mapping from integer id to the original tag-like string.
        // This should be common to all the segments in an asset.
        phmap = WSPlaceholderUtil.makePlaceholderMap( seg.getTargetPlaceholders() );
        String t = WSPlaceholderUtil.replacePlaceholders( seg.getSource(), phmap ); // Re-constructed original text.
        leadingEndTags = WSPlaceholderUtil.findEndTagsAtBeginning( t );
        t = t.substring( leadingEndTags.length() ); // Now the original text == leadingEndTags + t
        return t;
    }

    @Override
    protected String processMTResults(String textForMt, WSMTResult[] results,
            WSLanguage sourceLang, WSLanguage targetLang) {
        String x = leadingEndTags + results[0].getTranslation();
        return WSPlaceholderUtil.restorePlaceholders(x, phmap);
    }
}
