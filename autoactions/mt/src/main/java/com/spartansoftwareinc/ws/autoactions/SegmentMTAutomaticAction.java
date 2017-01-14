package com.spartansoftwareinc.ws.autoactions;

import java.util.Iterator;
import java.util.Map;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.asset.WSAssetTask;
import com.idiominc.wssdk.asset.WSAssetTranslation;
import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;
import com.idiominc.wssdk.asset.WSTextSegmentTranslation;
import com.idiominc.wssdk.asset.WSTranslationType;
import com.idiominc.wssdk.component.WSParameter;
import com.idiominc.wssdk.component.autoaction.WSActionResult;
import com.idiominc.wssdk.component.autoaction.WSTaskAutomaticAction;
import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.mt.WSMTAdapterRuntimeException;
import com.idiominc.wssdk.mt.WSMTResult;
import com.idiominc.wssdk.mt.WSMTService;
import com.idiominc.wssdk.mt.WSUnsupportedLanguagePairException;
import com.idiominc.wssdk.workflow.WSProject;
import com.idiominc.wssdk.workflow.WSTask;

/**
 * Abstract base class for autoactions that perform MT on an asset with
 * implementation-dependent pre- and post-processing behavior. 
 *
 * Note that due to limitations in the WSSDK, this MT must be done
 * segment-by-segment, rather than in batches.
 *
 * Implementations should implement the getSegmentTextForMT() and
 * processMTResults() methods to implement any necessary pre- and
 * post-processing.
 */
public abstract class SegmentMTAutomaticAction extends WSTaskAutomaticAction
{

    protected abstract Logger getLogger();

    /**
     * Returns the list of supported parameters. 
     * 
     * @return Empty list.
     */
    @Override
    public WSParameter[] getParameters()
    {
        return ( new WSParameter[] {});
    }

    /**
     * Returns an array of possible automatic action results. This automatic action always returns "Done", however.
     * 
     * @return An array of a single element, the string "Done"
     */
    @Override
    public String[] getReturns()
    {
        return ( new String[] { "Done" } );
    }

    /**
     * Return the segment content to be sent for MT, with any pre-processing already applied.
     * @param seg segment for MT
     * @param sourceLang source language for MT
     * @param targetLang target language for MT
     * @return pre-processed source content
     */
    protected abstract String getSegmentTextForMT(WSTextSegmentTranslation seg, WSLanguage sourceLang, WSLanguage targetLang);

    /**
     * Process MT results and return the translated target, with any post-processing applied.
     * @param textForMt
     * @param results
     * @param sourceLang
     * @param targetLang
     * @return
     */
    protected abstract String processMTResults(String textForMt, WSMTResult[] results, WSLanguage sourceLang, WSLanguage targetLang);

    /**
     * A task-level automatic action component that performs machine translation on all translatable segments.
     * <p>
     *
     * @param context The WorldServer SDK Context.
     * @param parameters A map of auto action parameters and their values. This is currently ignored.
     * @param task The task for which to execute this automatic action.
     *            <p>
     * @return WSActionResult
     */
    @SuppressWarnings("rawtypes")
    @Override
    public WSActionResult execute( WSContext context, Map parameters, WSTask task )
    {
        // The standard prolog of the automatic action ...
        if ( !( task instanceof WSAssetTask ) )
        {
            return new WSActionResult( WSActionResult.ERROR,
                                       String.format( "%s can only be used with assets.", getName() ) );
        }
        WSAssetTask assetTask = (WSAssetTask) task;
        WSAssetTranslation assetTranslation = assetTask.getAssetTranslation();
        if ( assetTranslation == null )
        {
            return new WSActionResult( WSActionResult.ERROR,
                                       String.format( "The asset associated with %s needs to be segmented. Please put a segmentation step before this step.",
                                                      getName() ) );
        }

        // Determine if a MT adaptor has been set for the current task.
        WSMTService mt = assetTranslation.getMTService();
        if ( null == mt )
        {
            // No MT adaptor assigned to this task.
            WSProject project = task.getProject();
            getLogger().warn("No MT adapter has been assigned for task " + assetTask.getTargetPath() + " in project " +
                             project.getProjectGroup().getName() + " (ID " + project.getId() + ")");
            return new WSActionResult( "Done", "MT skipped; no MT adapter was configured");
        }

        if ( getLogger().isDebugEnabled() )
        {
            dumpSourceSegsToLog( assetTranslation );
        }

        try
        {
            @SuppressWarnings( "unchecked" )
            Iterator<WSTextSegmentTranslation> it = assetTranslation.textSegmentIterator();
            while ( it.hasNext() )
            { // "for" can't be used because it isn't Iterable.
                translateSeg( mt, it.next(), assetTranslation.getSourceLanguage(),
                              assetTranslation.getTargetLanguage() );
            }
            if ( getLogger().isDebugEnabled() )
            {
                dumpTargetSegsToLog( assetTranslation );
            }
        }
        catch ( WSUnsupportedLanguagePairException e )
        {
            getLogger().error( "The language pair not supporeted. \n" + e );
            return new WSActionResult( WSActionResult.ERROR, e.getMessage() );
        }
        catch ( WSMTAdapterRuntimeException e )
        {
            getLogger().error( "Error using MT to pre-translate the asset.\n" + e );
            return new WSActionResult( WSActionResult.ERROR, e.getMessage() );
        }

        return new WSActionResult( "Done",
                                   "Translatable text has been pre-filled with the result from the machine translation." );

    } // execute()

    private void dumpSourceSegsToLog( WSAssetTranslation wsat )
    {
        @SuppressWarnings( "unchecked" )
        Iterator<WSTextSegmentTranslation> it = wsat.textSegmentIterator();

        while ( it.hasNext() )
        { // "for" can't be used because it isn't Iterable.
            StringBuilder sb = new StringBuilder();
            WSTextSegmentTranslation seg = it.next();
            sb.append( "Segment source:" ).append( seg.getSource() ).append( "\n" );
            WSTextSegmentPlaceholder[] holders = seg.getSourcePlaceholders();
            for ( WSTextSegmentPlaceholder h : holders )
            {
                sb.append( "source placeholder {" ).append( h.getId() ).append( "}:" ).append( h.getText() ).append( "\n" );
            }
            getLogger().debug( sb.toString() );
        }
    }

    private void dumpTargetSegsToLog( WSAssetTranslation wsat )
    {
        // The dump goes to the log as WARN level messages because that is the
        // level our server is set to log.
        @SuppressWarnings( "unchecked" )
        Iterator<WSTextSegmentTranslation> it = wsat.textSegmentIterator();

        while ( it.hasNext() )
        { // "for" can't be used because it isn't Iterable.
            StringBuilder sb = new StringBuilder();
            WSTextSegmentTranslation seg = it.next();
            sb.append( "Segment target:" ).append( seg.getTarget() ).append( "\n" );
            WSTextSegmentPlaceholder[] holders = seg.getTargetPlaceholders();
            for ( WSTextSegmentPlaceholder h : holders )
            {
                sb.append( "target placeholder {" ).append( h.getId() ).append( "}:" ).append( h.getText() ).append( "\n" );
            }
            getLogger().debug( sb.toString() );
        }
    }

    /*
     * Translate a segment, by restoring the original text with HTML/XML tags in, rather than {n}, send it to MT, and
     * re-replacing them to a form a new segment.
     */
    private void translateSeg( WSMTService mt, WSTextSegmentTranslation seg, WSLanguage sourceLang,
                                 WSLanguage targetLang )
    {
        StringBuilder debugLogMsg = null;
        WSTranslationType tt = seg.getTranslationType();
        String x = null;
        if ( !( ( tt == WSTranslationType.NO_TRANSLATION )
                || ( tt == WSTranslationType.FUZZY_MATCH_TM_TRANSLATION ) ) )
       {
            getLogger().info( String.format( "Skipping MT for \"%s\" because it is marked %s, score=%d.",
                                         seg.getSource(), seg.getTranslationType().toString(), seg.getTMScore() ) );
           return;
       }

        String t = getSegmentTextForMT(seg, sourceLang, targetLang);
        if (t.trim().isEmpty()) 
        {
            getLogger().info("Skipping MT because it is an empty string after trimming.");
            return;
        }

        if ( getLogger().isDebugEnabled() )
        {
            debugLogMsg = new StringBuilder( "Text to be sent to MT:" ).append( t ).append( "\n" );
        }
        try 
        { 
            long startMs = System.currentTimeMillis();
            WSMTResult[] mtrs = mt.translate(t, sourceLang, targetLang);
            long durationMs = System.currentTimeMillis() - startMs;
            getLogger().info(String.format("translate(\"%.50s%s\", %s, %s) took %d ms", t, (t.length() > 50 ? "..." : ""),
                    toLanguageTag(sourceLang), toLanguageTag(targetLang),
                    durationMs));
            if (mtrs.length == 0) 
            {
                getLogger().warn(String.format("translate(\"%s\", %s, %s) returned no translation.", t,
                    toLanguageTag(sourceLang), toLanguageTag(targetLang)));
                return;
            }
            else if (mtrs.length > 1) 
            {
                getLogger().warn(String.format(
                        "translate(\"%s\", %s, %s) returned %d (>1) translations. Only the first one is used.", t,
                    toLanguageTag(sourceLang), toLanguageTag(targetLang),
                    mtrs.length));
            }
            x = processMTResults(t, mtrs, sourceLang, targetLang);
            seg.getTargetSegment().setContent(x);
            seg.setTranslationType(WSTranslationType.MACHINE_TRANSLATION);
        } catch (Exception e) {
            getLogger().error(String.format("Exception caught.\n Source:%s\n Translated(x):%s\n%s", t, x, e.toString()));
            return;
        }
        if ( getLogger().isDebugEnabled() )
        {
            debugLogMsg.append( "After reverse-replacement:" ).append( x ).append( '\n' );
            getLogger().debug( debugLogMsg.toString() );
        }
    }

    private String toLanguageTag(WSLanguage lang) {
        Locale l = lang.getLocale();
        return l.getLanguage() + "-" + l.getCountry();
    }
}
