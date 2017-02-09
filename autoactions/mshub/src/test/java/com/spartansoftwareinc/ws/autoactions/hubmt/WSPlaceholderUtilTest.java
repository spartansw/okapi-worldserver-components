/**
 * Copyright (c) 2016 Spartan Software, Inc.  All Rights
 * Reserved.
 */
package com.spartansoftwareinc.ws.autoactions.hubmt;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import com.idiominc.wssdk.asset.WSTextSegmentPlaceholder;
import com.spartansoftware.ws.okapi.filters.mock.MockWSTextSegmentPlaceholder;
import com.spartansoftwareinc.ws.autoactions.hubmt.WSPlaceholderUtil.PHData;
import com.spartansoftwareinc.ws.autoactions.hubmt.WSPlaceholderUtil.PHData.Type;

public class WSPlaceholderUtilTest
{
    /*
     * Test makePlaceholderMap
     */
    @Test
    public void testMakePlaceholderMap()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[3];
        wsphs[0] = new MockWSTextSegmentPlaceholder( "<img  src=\"xyz.jpg\"   />", 1 );
        wsphs[1] = new MockWSTextSegmentPlaceholder( "<a    href=\"xyz.html\"   > ", 2 );
        wsphs[2] = new MockWSTextSegmentPlaceholder( "</a>", 3 );
        Map<Integer, PHData> map = WSPlaceholderUtil.makePlaceholderMap( wsphs );
        assertEquals( 3, map.size() );
        assertEquals( "<img ws_id='1'/>", map.get( 1 ).mtForm );
        assertEquals( "<a ws_id='2'>", map.get( 2 ).mtForm );
        assertEquals( "</a>", map.get( 3 ).mtForm );
    }

    /*
     * Test the replacement logic using the most basic, simple replacement, corresponding to the supposed original HTML
     * file: <br/> <pre> &lt;html;gt;&lt;body&gt;&lt;p&gt;Please click &lt;a href="help.html&gt;here<&lt;/a&gt; for
     * help. &lt;/body&gt;&lt;/html;&gt; </pre> This would generate three segments, and the second segment should appear
     * like: <pre> Please click {1}here{2} for help. </pre>
     */
    @Test
    public void testSimpleReplacement()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[2];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<a href=\"help.html\">", 1);
        wsphs[1] = new MockWSTextSegmentPlaceholder("</a>", 2);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "Please click {1}here{2} for help.";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "Please click <a ws_id='1'>here</a> for help.", x );
        String y = WSPlaceholderUtil.restorePlaceholders( x, phmap );
        assertEquals( parameterizedText, y );
    }

    /*
     * Test the replacement logic in a more tricky case where an IMG tag is divided into two pieces at the ALT attribute
     * string.
     */
    @Test
    public void testFragmentedImgTag()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[2];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<img alt=\"", 3);
        wsphs[1] = new MockWSTextSegmentPlaceholder("\" src=\"diagram.jpg\">", 4);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "Do you see this {3}alt test here{4} clearly?";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "Do you see this <img alt=\"alt test here\" src=\"diagram.jpg\"> clearly?", x );
        String y = WSPlaceholderUtil.restorePlaceholders( x, phmap );
        assertEquals( parameterizedText, y );
    }

    /*
     * Test the case where the order of the placeholders changes after translation.
     */
    @Test
    public void testReordering()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[4];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<a href=\"dog.html\">", 5);
        wsphs[1] = new MockWSTextSegmentPlaceholder("</a>", 6);
        wsphs[2] = new MockWSTextSegmentPlaceholder("<a href=\"cat.html\">", 7);
        wsphs[3] = new MockWSTextSegmentPlaceholder("</a>", 8);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "A {5}cat{6} is chased by a {7}dog{8}";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "A <a ws_id='5'>cat</a> is chased by a <a ws_id='7'>dog</a>", x );
        String y = WSPlaceholderUtil.restorePlaceholders( x, phmap );
        assertEquals( "A {5}cat{6} is chased by a {7}dog{8}", y );
        // Let's pretend the placeholders were re-ordered in translation...
        String z = WSPlaceholderUtil.restorePlaceholders(
                "A <a ws_id='7'>cat</a> is chased by a <a ws_id='5'>dog</a>", phmap );
        assertEquals( "A {7}cat{8} is chased by a {5}dog{6}", z );
    }

    @Test
    public void testReordering2()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[6];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<tt>", 1);
        wsphs[1] = new MockWSTextSegmentPlaceholder("</tt>", 2);
        wsphs[2] = new MockWSTextSegmentPlaceholder("<tt>", 3);
        wsphs[3] = new MockWSTextSegmentPlaceholder("</tt>", 4);
        wsphs[4] = new MockWSTextSegmentPlaceholder("<tt>", 5);
        wsphs[5] = new MockWSTextSegmentPlaceholder("</tt>", 6);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "A {1}cat{2} is {3}chased{4} by a {5}dog{6}";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "A <tt ws_id='1'>cat</tt> is <tt ws_id='3'>chased</tt> by a <tt ws_id='5'>dog</tt>", x );
        String y = WSPlaceholderUtil.restorePlaceholders( x, phmap );
        assertEquals( "A {1}cat{2} is {3}chased{4} by a {5}dog{6}", y );
        // Let's pretend the placeholders were re-ordered in translation...
        String z = WSPlaceholderUtil.restorePlaceholders(
                "A <tt ws_id='3'>chat</tt> is <tt ws_id='5'>chased</tt> by a <tt ws_id='1'>chien</tt>", phmap );
        assertEquals( "A {3}chat{4} is {5}chased{6} by a {1}chien{2}", z );
    }

    @Test
    public void testReordering3() {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[6];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<tt>", 41);
        wsphs[1] = new MockWSTextSegmentPlaceholder("</tt>", 42);
        wsphs[2] = new MockWSTextSegmentPlaceholder("<tt>", 43);
        wsphs[3] = new MockWSTextSegmentPlaceholder("</tt>", 44);
        wsphs[4] = new MockWSTextSegmentPlaceholder("<tt>", 45);
        wsphs[5] = new MockWSTextSegmentPlaceholder("</tt>", 46);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "{41}--d{42} (the name), {43}--b{44} (the ID), and {45}--c{46} (the name).";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "<tt ws_id='41'>--d</tt> (the name), <tt ws_id='43'>--b</tt> (the ID), and <tt ws_id='45'>--c</tt> (the name).", x );
        String z = WSPlaceholderUtil.restorePlaceholders(
                "<tt ws_id='41'>--d</tt> (le nom), <tt ws_id='43'>--b</tt> (l’ID) et <tt ws_id='45'>--c</tt> (le nom).",
                phmap );
        System.out.println(z);
        assertEquals("{41}--d{42} (le nom), {43}--b{44} (l’ID) et {45}--c{46} (le nom).", z);
    }

    /*
     * Test if restorePlaceholders handles the case where Hub MT normalized the end of the stand-alone tag to ">" from
     * "/>", and removed leading spaces before it. That is, "<img src="abc.jpg"  />" might become "<img src="abc.jpg">.
     * This is a variation of the testFragmentedImgTag test case.
     */
    //@Test
    public void testNormalizedEndOfStandaloneTagReplacement()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[2];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<img alt=\"", 9);
        wsphs[1] = new MockWSTextSegmentPlaceholder("\" src=\"diagram.jpg\"/>", 10);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "Do you see this {9}alt test here{10} clearly?";
        String y =
            WSPlaceholderUtil.restorePlaceholders( "Do you see this <img alt=\"alt test here\" src=\"diagram.jpg\"> clearly?",
                                                   phmap );
        assertEquals( parameterizedText, y );
    }

    /*
     * Test if restorePlaceholders() handles the case where Hub MT removes unclosed tags as expected.
     */
    @Test
    public void testExtraEndTagsRemoved()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[3];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<li>", 11);
        wsphs[1] = new MockWSTextSegmentPlaceholder("</li>", 12);
        wsphs[2] = new MockWSTextSegmentPlaceholder("</ol>", 13);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "{11}¡Hola!{12}{13}";
        String y = WSPlaceholderUtil.restorePlaceholders( "<li ws_id='11'>¡Hola!</li>", phmap );
        assertEquals( parameterizedText, y );
    }

    /*
     * This is a real example of the above case.
     */
    @Test
    public void testExtraEndTagsRemovedReal()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[5];
        wsphs[0] = new MockWSTextSegmentPlaceholder("</span>", 7);
        wsphs[1] = new MockWSTextSegmentPlaceholder("</span>", 8);
        wsphs[2] = new MockWSTextSegmentPlaceholder("</span>", 9);
        wsphs[3] = new MockWSTextSegmentPlaceholder("</span>", 10);
        wsphs[4] = new MockWSTextSegmentPlaceholder("</span>", 11);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText =
            "Soluciones de colaboración Cisco puede ayudar a usted reunir su teléfono, videoconferencia y sistemas de mensajería para que sus empleados colaboraran y comunican más efectivamente con sus clientes.{7}{8}{9}{10}{11}";
        String y =
            WSPlaceholderUtil.restorePlaceholders( "Soluciones de colaboración Cisco puede ayudar a usted reunir su teléfono, videoconferencia y sistemas de mensajería para que sus empleados colaboraran y comunican más efectivamente con sus clientes.",
                                                   phmap );
        assertEquals( parameterizedText, y );
    }

    /*
     * Test if restorePlaceholders() handles the case where Hub MT insert closing tags.
     */
    @Test
    public void testExtraEndTagsAdded()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[3];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<ol>", 14);
        wsphs[1] = new MockWSTextSegmentPlaceholder("<li>", 15);
        wsphs[2] = new MockWSTextSegmentPlaceholder("</li>", 16);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "{14}{15}¡Hola!{16}";
        String y = WSPlaceholderUtil.restorePlaceholders( "<ol ws_id='14'><li ws_id='15'>¡Hola!</li></ol>", phmap );
        assertEquals( parameterizedText, y );
    }

    /*
     * This is a real example of the above case.
     */
    @Test
    public void testExtraEndTagsAddedReal()
    {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[4];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<span style=\"color: #444444\">", 3);
        wsphs[1] = new MockWSTextSegmentPlaceholder("<span style=\"font-family: arial, helvetica, sans-serif\">", 4);
        wsphs[2] = new MockWSTextSegmentPlaceholder("<span style=\"font-size: 12px\">", 5);
        wsphs[3] = new MockWSTextSegmentPlaceholder("<span id=\"Field902\">", 6);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText =
            "{3}{4}{5}{6}Problemas técnicos tales como caídas llamadas, transferencias maltratadas o llamada de mala calidad pueden decepcionar a sus clientes y, en última instancia, las empresas están en riesgo.";
        String y = WSPlaceholderUtil.restorePlaceholders( "<span ws_id='3'>" // {3}
            + "<span ws_id='4'>" // {4}
            + "<span ws_id='5'>" // {5}
            + "<span ws_id='6'>" // {6}
            + "Problemas técnicos tales como caídas llamadas, transferencias maltratadas o llamada de mala calidad pueden decepcionar a sus clientes y, en última instancia, las empresas están en riesgo."
            + "</span></span></span></span>", phmap );
        assertEquals( parameterizedText, y );
    }

    @Test
    public void testPairedTags() {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[1];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<productname>The Big Cheese</productname>", 3);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "For more information on {3}, please click here.";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "For more information on <productname ws_id='3'>QFX0003</productname>, please click here.", x );
        String y = WSPlaceholderUtil.restorePlaceholders( x, phmap );
        assertEquals( parameterizedText, y );
    }

    // This is the behavior in Japanese
    @Test
    public void testProcessingInstruction() {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[1];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<?xm-replace_text The Big Cheese?>", 3);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "For more information on {3}, please click here.";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "For more information on <?xm-replace_text ws_id='3'?>, please click here.", x );
        String y = WSPlaceholderUtil.restorePlaceholders( x, phmap );
        assertEquals( parameterizedText, y );
    }

    // This is the behavior in German
    @Test
    public void testProcessingInstruction2() {
        WSTextSegmentPlaceholder[] wsphs = new WSTextSegmentPlaceholder[1];
        wsphs[0] = new MockWSTextSegmentPlaceholder("<?xm-replace_text The Big Cheese?>", 3);
        Map<Integer, PHData> phmap = WSPlaceholderUtil.makePlaceholderMap(wsphs);
        final String parameterizedText = "For more information on {3}, please click here.";
        String x = WSPlaceholderUtil.replacePlaceholders( parameterizedText, phmap );
        assertEquals( "For more information on <?xm-replace_text ws_id='3'?>, please click here.", x );
        String result = "For more information on <?xm-replace_text ws_id='3'> </?xm-replace_text>, please click here.";
        String y = WSPlaceholderUtil.restorePlaceholders( result, phmap );
        assertEquals( "For more information on {3} , please click here.", y );
    }

    /*
     * Test basic usage of findEndTagsAtBeginning
     */
    @Test
    public void testFindEndTagsAtBeginning()
    {
        assertEquals( "No closing tag at all", "", WSPlaceholderUtil.findEndTagsAtBeginning( "Hello" ) );
        assertEquals( "End tag in the middle", "", WSPlaceholderUtil.findEndTagsAtBeginning( "Hello<span>World" ) );
        assertEquals( "One closing tag at beginning", "</span>",
                      WSPlaceholderUtil.findEndTagsAtBeginning( "</span>Hello" ) );
        assertEquals( "Two closing tags at beginning", "</span></span>",
                      WSPlaceholderUtil.findEndTagsAtBeginning( "</span></span>Hello" ) );
        assertEquals( "Two closing tags with extra spaces at beginning", "</span> </span>",
                      WSPlaceholderUtil.findEndTagsAtBeginning( "</span> </span>Hello" ) );
        assertEquals( "One closing tag at beginning, another in middle", "</span>",
                      WSPlaceholderUtil.findEndTagsAtBeginning( "</span>Hello </span>" ) );
    }

    /*
     * Test basic usage of removeEndTags.
     */
    @Test
    public void testRemoveEndTags()
    {
        assertEquals( "No extra tags", "Hello", WSPlaceholderUtil.removeEndTags( "Hello" ) );
        assertEquals( "One extra tags", "Hello", WSPlaceholderUtil.removeEndTags( "Hello</span>" ) );
        assertEquals( "Two extra tags", "Hello", WSPlaceholderUtil.removeEndTags( "Hello</span></span>" ) );
        assertEquals( "Two extra tags and extra space in between", "Hello",
                      WSPlaceholderUtil.removeEndTags( "Hello</span> </span>" ) );
    }

    /**
     * A user encountered a case where bad input produced PHDat with an empty mtForm. This
     * caused an infinite loop during placeholder restoration.  Make sure we are defensive
     * and this doesn't happen again.
     */
    @Test
    public void testCorrectHandlingOfEmptyPlaceholderText() {
        Map<Integer, PHData> phdata = new HashMap<Integer, PHData>();
        PHData bad = new PHData(Type.STANDALONE, "foo", "<foo/>", "");
        bad.wsid = 1;
        phdata.put(1, bad);
        String result = WSPlaceholderUtil.restorePlaceholders("The quick brown fox jumps over <foo/>.", phdata);
        assertEquals("{1}The quick brown fox jumps over <foo/>.", result);
    }
}
