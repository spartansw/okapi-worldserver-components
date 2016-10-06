/**
 * Copyright (c) 2016 Spartan Software, Inc.  All Rights
 * Reserved.
 */
package com.spartansoftwareinc.ws.autoactions.hubmt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.LoggerProvider;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;

import org.apache.log4j.Logger;

/**
 * Collection of static methods that handles WorldServer's placeholders.
 */
public class WSPlaceholderUtil
{
    static {
        // Jericho will try to autodetect the logging framework, but it expects a
        // newer version of log4j than what WS is actually running.  So we will force
        // it to use something else.
        Config.LoggerProvider = LoggerProvider.JAVA;
    }

    private static final Logger log = Logger.getLogger( WSPlaceholderUtil.class );

    private static final Pattern CURLY_N_RE = Pattern.compile( "\\{\\d+\\}" ); // Matches {1}, {2}, {123}, etc.

    private static final Pattern endTagsAtBeginningRE = Pattern.compile( "^(</\\w+>\\s*)+" ); // Consecutive end-tags at
                                                                                              // the beginning of a
                                                                                              // segment

    // An end-tag, including the (ridiculous) case where Hub converts "<?foo?>" to
    // "<?foo> </?foo>".
    private static final Pattern endTagRE = Pattern.compile( "</\\??[\\w-]+>\\s*" ); // An end-tag.

    /**
     * Replaces the WorldServer style place holders of the form '{' number '}' (e.g. {2}, {34}, etc.) with the string
     * from the map.
     * 
     * @param s Input string with placeholders. e.g. "Click {1}here{2} or {3}cancel{4} the operation.{5}")
     * @param placeholderMap An int-to-String map.
     * @return a String where all placeholders are replaced by the corresponding strings.
     */
    static String replacePlaceholders( String s, Map<Integer, PHData> phmap )
    {
        StringBuilder sb = new StringBuilder();
        Matcher m = CURLY_N_RE.matcher( s );
        int b = 0;
        while ( m.find() )
        {
            sb.append( s.substring( b, m.start() ) );
            int d = Integer.valueOf( s.substring( m.start() + 1, m.end() - 1 ) );
            sb.append( phmap.get( d ).mtForm );
            b = m.end();
        }
        sb.append( s.substring( b ) );
        return sb.toString();
    }

    static class PHData {
        final String rawForm;
        // This is what we actually send to MT, possibly different from rawForm
        final String mtForm;

        PHData(String rawForm, String mtForm) {
            this.rawForm = rawForm;
            this.mtForm = mtForm;
        }

        @Override
        public String toString() {
            return "{raw='" + rawForm + "'; mt='" + mtForm + "'}";
        }
    }

    private static final String WS_ID_ATTR = "ws_id";

    private static String getDummyPairedTagContent(int phId) {
        return String.format("QFX%04d", phId);
    }

    static PHData parsePlaceholderContent(String content, int phId) {
        Source source = new Source(content);
        String firstTag = null;
        List<Tag> mtTags = source.getAllTags();
        for (Tag tag : mtTags) {
            if (tag instanceof StartTag && firstTag == null) {
                // Special case: XML Processing instructions
                if (tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION) {
                    // Jericho exposes the name for <?foo?> as "?foo".
                    String piForm = "<" + tag.getName() + " " + WS_ID_ATTR + "='" + phId + "'?>";
                    return new PHData(content, piForm);
                }
                firstTag = tag.getName();
            }
            else if (tag instanceof EndTag) {
                if (firstTag != null && firstTag.equals(tag.getName())) {
                    // This is a paired tag!
                    String pairedMtForm = "<" + firstTag + " " + WS_ID_ATTR + "='" + phId + "'>"
                                        + getDummyPairedTagContent(phId) + "</" + firstTag + ">";
                    return new PHData(content, pairedMtForm);
                }
            }
        }
        if (mtTags.isEmpty()) {
            // This is triggered by translatable attributes, but
            // worth tracking for other reasons
            log.info("Warning: placeholder with no tags '" + content + "'");
            return new PHData(content, content);
        }
        return makeTagForMT(content, mtTags.get(0), phId);
    }

    private static PHData makeTagForMT(String content, Tag tag, int phId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        if (tag instanceof EndTag) {
            sb.append("/");
            sb.append(tag.getName());
        }
        else {
            sb.append(tag.getName());
            sb.append(" ").append(WS_ID_ATTR).append("='").append(phId).append("'");
            if (((StartTag)tag).isSyntacticalEmptyElementTag()) {
                sb.append("/");
            }
        }
        sb.append(">");
        String mt = sb.toString();
        return new PHData(content, mt);
    }

    /**
     * Make an int-to-String map from an array of makePlaceholderMap. Consecutive spaces are normalized to one space
     * except at the end of the tag where spaces before /&gt; or &gt; are removed. This is to match the observed
     * behavior of Hub MT.
     * 
     * @param holders An array of holders from WS SDK
     * @return a simple int-to-String map; each string is normalized to the form Hub MT unlikely changes.
     */
    public static Map<Integer, PHData> makePlaceholderMap( WSTextSegmentPlaceholder[] holders )
    {
        HashMap<Integer, PHData> phmap = new HashMap<Integer, PHData>();
        for ( WSTextSegmentPlaceholder h : holders )
        {
            phmap.put(h.getId(), parsePlaceholderContent(h.getText(), h.getId()));
        }
        return phmap;
    }

    /**
     * Reverse replacement of tags (e.g. "&lt;i&gt;&lt;a href=...&gt;") to WS placeholders ("{3}").
     * <p>
     * Hub MT sometimes (always?) replaces "/&gt;" with "&gt;" for some stand-alone tags such as &lt;br/&gt; or
     * &lt;img/&gt;. This is probably their way of normalization. In order to handle this case, this method tries to
     * match with the string without the back slash if the placeholder's value ends with "/&gt;".
     * </p>
     * <p>
     * Hub MT also change spacing. For example spaces before "/&gt;" is removed. Consecutive spaces are made a single
     * space.
     * </p>
     * <p>
     * Hub MT inserts or remove the end tags if they don't balance. For example: <br>
     * &lt;span&gt;Hello! <br>
     * becomes: <br>
     * &lt;span&gt;¡Hola!&lt;/span&gt; <br>
     * and: <br>
     * Hello!&lt;/span&gt; <br>
     * becomes: <br>
     * ¡Hola! <br>
     * We will remove extra inserted tags and we add all tags that were in the original text but wasn't found in
     * translation to the end.
     * </p>
     * <p>
     * NOTE ON RE-ORDERING OF IDENTICAL TAGS: <br>
     * If the same tags appear in a segment, the placeholder ids may be re-arranged. This results in a pair of
     * placeholders look separated. For example, if a segment before translation like this: <br>
     * {1}The dog{2} chases {3}the cat{4}. <br>
     * may result in this: <br>
     * {3}the cat{2} is chased by {1}the dog{4}. <br>
     * if the translation results in re-ordering of phrases around tag pairs, and some tags are identical, such as this
     * case:
     * <table border="1">
     * <caption>Sample placeholders</caption>
     * <tr>
     * <th>id</th>
     * <th>value</th>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>&lt;a href="dog.html"&gt;</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>&lt;/a&gt;</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>&lt;a href="cat.html"&gt;</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>&lt;/a&gt;</td>
     * </tr>
     * </table>
     * This may look wrong but since both {2} and {4} represent the same &lt;/a&gt; tag, the result is not incorrect.
     * 
     * @param s The string returned from Hub MT that includes HTML/XML tags
     * @param phmap The int-to-String map made by makePlaceholderMap
     * @return The string where tags are replaced by placeholders
     */
    public static String restorePlaceholders( String s, Map<Integer, PHData> phmap )
    {
        if ( phmap.isEmpty() )
            return removeEndTags( s ); // Optimization
        StringBuilder sb = new StringBuilder( s );
        StringBuilder missingPhSb = new StringBuilder(); // Accumulates the tags missing after translation
        for ( Map.Entry<Integer, PHData> e : phmap.entrySet() )
        {
            // We'd like to use s.replace method but it replaces all occurrences
            // of the given substring. That is a problem because we don't want to see
            // "Click {1}here{2} or {3}here{2}" where {2}, representing "</a>", appears twice.
            String ph = String.format( "{%d}", e.getKey() );
            PHData phData = e.getValue();
            String tag = phData.mtForm; // Usually a complete tag. Sometimes a fragment of a tag.
            int i = sb.indexOf( tag );
            if ( i >= 0 )
            {
                sb.replace( i, i + tag.length(), ph );
            }
            else if ( tag.endsWith( "/>" ) )
            { // Try without "/"
                tag = tag.substring( 0, tag.length() - 2 ) + ">";
                i = sb.indexOf( tag );
                if ( i >= 0 )
                {
                    sb.replace( i, i + tag.length(), ph );
                }
            }
            else if (tag.startsWith("<?") && tag.endsWith("?>")) {
                // Special case handling of processing instructions seen in some languages, in
                // which <?foo?> --> <?foo> </?foo> (which is ridiculous)
                // Try without "?"
                tag = tag.substring( 0, tag.length() - 2 ) + ">";
                i = sb.indexOf( tag );
                if ( i >= 0 )
                {
                    sb.replace( i, i + tag.length(), ph );
                }
            }
            else
            {
                log.warn( String.format( "Tag %s (for %s) was not found in translation \"%s\". It will be appended at the end.",
                                         tag, ph, s ) );
                missingPhSb.append( ph );
            }
        }
        if ( missingPhSb.length() > 0 )
        {
            sb.append( missingPhSb );
        }
        return removeEndTags( sb.toString() );
    }

    /**
     * Return the sequence of the end tags at the beginning. <br>
     * Sometimes, a segment starts with the end tags like &lt;/span&gt;&lt;/span&gt;&lt;/span&gt;. This Hub MT removes
     * these tags but we ends up moving the unfound tags to the end. To remedy this situation, we call Hub MT with the
     * substring after the end tags. This method find the beginning the beginning end tag sequence.
     * 
     * @param s A string which might start with a sequence of the end tags.
     * @return The end tags at the beginning of s, potentially including the trailing spaces. An empty string ("") if no
     *         end tags.
     */
    public static String findEndTagsAtBeginning( String s )
    {
        Matcher m = endTagsAtBeginningRE.matcher( s );
        if ( m.find() )
        {
            return m.group();
        }
        else
        {
            return ""; // No end tag at the beginning.
        }
    }

    /**
     * Remove substrings that look like end tags that Hub MT may have inserted. We call this after restoring the
     * placeholders. This is necessary because Hub TM inserts end tags when the tags are not balanced.
     * 
     * @param s A string that may have end tags remained.
     * @return The string after all end tags are removed.
     */
    public static String removeEndTags( String s )
    {
        return endTagRE.matcher( s ).replaceAll( "" );
    }

}
