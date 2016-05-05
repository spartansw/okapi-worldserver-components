package com.spartansoftwareinc.ws.okapi.filters.po;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test parsing sample PO files, verifying the WSSegmentWriter is getting the
 * proper encoded text and placeholder array it expects.
 */
@RunWith(DataProviderRunner.class)
public class TestPoWSOkapiFilter {
    private FilterTestHarness testHarness;

    @Before
    public void setup() {
        testHarness = new FilterTestHarness(new PoWSOkapiFilter());
    }

    @DataProvider
    public static Object[][] testInlineFieldParseResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder(WSFilter.PLACEHOLDER, "[term:description]"), 
                new SegmentInfoHolder("Hello World."),
                new SegmentInfoHolder(WSFilter.PLACEHOLDER, "[site:url]"),
                new SegmentInfoHolder(WSFilter.PLACEHOLDER, "[site:name]"),
                new SegmentInfoHolder("website"),
                new SegmentInfoHolder("Here & there & everywhere"),
                new SegmentInfoHolder("This filter will convert " + WSFilter.PLACEHOLDER + " tags into markup.",
                                      "[[{type:media... ]]")
            }
        }};
    }

    @Test
    @UseDataProvider("testInlineFieldParseResults")
    public void testInlineField(SegmentInfoHolder[] expected) throws Exception {
        testHarness.extractAndExpectSegments("/inline_fields.pot", new POFilterConfigurationData(),
                    StandardCharsets.UTF_8, expected);
    }

    @DataProvider
    public static Object[][] testInlineFieldMergeResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("{1}", "[term:description]"), 
                new SegmentInfoHolder("Bounjour tout le monde."),
                new SegmentInfoHolder("{2}", "[site:url]"),
                new SegmentInfoHolder("{3}", "[site:name]"),
                new SegmentInfoHolder("french website"),
                new SegmentInfoHolder("Ici & là & partout"),
                new SegmentInfoHolder("Ce programme convertit {4} les balises dans balisage.",
                                      "[[{type:media... ]]")
            }
        }};
    }

    @Test
    @UseDataProvider("testInlineFieldMergeResults")
    public void testInlineFieldMerge(SegmentInfoHolder[] translations) throws IOException {
        testHarness.mergeAndVerifyOutput("/inline_fields.pot", "/translated_inline_fields.po", StandardCharsets.UTF_8,
                             Arrays.asList(translations));
    }

    @DataProvider
    public static Object[][] testEmbeddedHtmlParseResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder(WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER + "*" + WSFilter.PLACEHOLDER +
                                      " Field is required." + WSFilter.PLACEHOLDER,
                        "<div class=\\\"required-info\\\">", "<strong>", "</strong>", "</div>"),
                new SegmentInfoHolder(WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER +
                        "Let’s stay in touch. We’ve got news and announcements coming." +
                        WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER,
                        "<div class=\\\"header\\\">", "<p>", "</p>", "</div>"),
                new SegmentInfoHolder(WSFilter.PLACEHOLDER + "Sign up for upcoming news." + WSFilter.PLACEHOLDER,
                        "<p>", "</p>"),
                new SegmentInfoHolder(WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER +
                        "Fill out the form:" + WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER +
                            "* All fields required" + WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER,
                        "<div class=\\\"header\\\">", "<p>", "</p>",
                        "<div class=\\\"fields-required light\\\">", "</div>", "</div>")
            }
        }};
    }

    @Test
    @UseDataProvider("testEmbeddedHtmlParseResults")
    public void testEmbeddedHtml(SegmentInfoHolder[] expected) throws Exception {
        testHarness.extractAndExpectSegments("/embedded_html.pot", new POFilterConfigurationData(),
                StandardCharsets.UTF_8, expected);
    }

    @DataProvider
    public static Object[][] testEmbeddedHtmlMergeResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("{1}{2}*{3} fIELD IS REQUIRED.{4}",
                        "<div class=\\\"required-info\\\">", "<strong>", "</strong>", "</div>"),
                new SegmentInfoHolder("{5}{6}lET'S STAY IN TOUCH. wE'VE GOT NEWS AND ANNOUNCEMENTS COMING.{7}{8}",
                        "<div class=\\\"header\\\">", "<p>", "</p>", "</div>"),
                new SegmentInfoHolder("{9}sIGN UP FOR UPCOMING NEWS.{10}",
                        "<p>", "</p>"),
                new SegmentInfoHolder("{11}{12}fILL OUT THE FORM:{13}{14}* aLL FIELDS REQUIRED{15}{16}",
                        "<div class=\\\"header\\\">", "<p>", "</p>",
                        "<div class=\\\"fields-required light\\\">", "</div>", "</div>")
            }
        }};
    }

    @Test
    @UseDataProvider("testEmbeddedHtmlMergeResults")
    public void testEmbeddedHtmlMerge(SegmentInfoHolder[] translations) throws IOException {
        testHarness.mergeAndVerifyOutput("/embedded_html.pot", "/translated_embedded_html.po", StandardCharsets.UTF_8,
                             Arrays.asList(translations));
    }

    @DataProvider
    public static Object[][] testLineBreakParseResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder(WSFilter.PLACEHOLDER + " " + WSFilter.PLACEHOLDER, "[Test One]", "[Test:two]"), 
                new SegmentInfoHolder("Hello " + WSFilter.PLACEHOLDER + "," + WSFilter.PLACEHOLDER +
                                      WSFilter.PLACEHOLDER +
                                      "Something has happened " + WSFilter.PLACEHOLDER + WSFilter.PLACEHOLDER +
                                      WSFilter.PLACEHOLDER +
                                      "- - -" + WSFilter.PLACEHOLDER +
                                      "Thanks, " + WSFilter.PLACEHOLDER,
                                      "[field:name]", "\\r\\n", "\\r\\n",
                                      "[some:where]", "\\r\\n", "\\r\\n", "\\r\\n",
                                      "[field:name2]"),
                new SegmentInfoHolder("You are confirmed for " + WSFilter.PLACEHOLDER +
                                      "." + WSFilter.PLACEHOLDER +
                                      WSFilter.PLACEHOLDER +
                                      "Thank you! look forward to seeing you." + WSFilter.PLACEHOLDER,
                                      "[field:name]", "\\r\\n", "\\r\\n", "\\r\\n")
            }
        }};
    }

    @Test
    @UseDataProvider("testLineBreakParseResults")
    public void testLineBreak(SegmentInfoHolder[] expected) throws Exception {
        testHarness.extractAndExpectSegments("/line_breaks.pot", new POFilterConfigurationData(),
                StandardCharsets.UTF_8, expected);
    }

    @DataProvider
    public static Object[][] testLineBreakMergeResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("{1} {2}", "[Test One]", "[Test:two]"), 
                new SegmentInfoHolder("こんにちは{3},{4}" +
                                      "{5}" +
                                      "何かが起こりました{6}{7}" +
                                      "{8}" +
                                      "- - -{9}" +
                                      "ありがとう{10}",
                                      "[field:name]", "\\r\\n", "\\r\\n",
                                      "[some:where]", "\\r\\n", "\\r\\n", "\\r\\n",
                                      "[field:name2]"),
                new SegmentInfoHolder("あなたがのために確認されています{11}.{12}{13}" +
                                      "ありがとう！ご参加をお待ちしております。{14}",
                                      "[field:name]", "\\r\\n", "\\r\\n", "\\r\\n")
            }
        }};
    }

    @Test
    @UseDataProvider("testLineBreakMergeResults")
    public void testLineBreakMerge(SegmentInfoHolder[] translations) throws IOException {
        testHarness.mergeAndVerifyOutput("/line_breaks.pot", "/translated_line_breaks.po", StandardCharsets.UTF_8,
                             Arrays.asList(translations));
    }
}
