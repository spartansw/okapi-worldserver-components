package com.spartansoftwareinc.ws.okapi.filters.idml;

import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;

@RunWith(DataProviderRunner.class)
public class IDMLFilterTest {
    private FilterTestHarness testHarness;

    @DataProvider
    public static Object[][] testIdml() {
        return new Object[][]{{
                new SegmentInfoHolder[]{
                        new SegmentInfoHolder("This is a first sentence."),
                        new SegmentInfoHolder("And a second line in that same paragraph." + WSFilter.PLACEHOLDER + "and a third one after Ctrl+Enter", "<content-1/>"),
                        new SegmentInfoHolder("\\=bs, &=amp, <=lt, “=quot, ^=caret"),
                        new SegmentInfoHolder("Text in " + WSFilter.PLACEHOLDER + "a different font" + WSFilter.PLACEHOLDER + ".", "<content-1>", "</content-1>"),
                        new SegmentInfoHolder("Text before the table."),
                        new SegmentInfoHolder(WSFilter.PLACEHOLDER, "<content-NCF494F15-rg1-1/>"),
                        new SegmentInfoHolder("Text after the table."),
                        new SegmentInfoHolder("Before the note " + WSFilter.PLACEHOLDER + " and after it.", "<content-1/>"),
                        new SegmentInfoHolder("Variables: " + WSFilter.PLACEHOLDER + "=filename. \t=tab, " + WSFilter.PLACEHOLDER + "=curPageNum, “=dlq and ”=drq.", "<content-1/>", "<content-2/>"),
                        new SegmentInfoHolder(" text"),
                        new SegmentInfoHolder("Cell 1"),
                        new SegmentInfoHolder(WSFilter.PLACEHOLDER + " " + WSFilter.PLACEHOLDER, "<content-1>", "</content-1>"),
                        new SegmentInfoHolder("Cell 2"),
                        new SegmentInfoHolder("Last cell."),
                        new SegmentInfoHolder("Second line of last cell.")
                }
        }};
    }

    @Before
    public void setup() {
        testHarness = new FilterTestHarness(new IDMLWSOkapiFilter());
    }

    @Test
    @UseDataProvider("testIdml")
    public void testIdml(SegmentInfoHolder[] expected) throws Exception {
        testHarness.extractAndExpectSegments("/idmltest.idml", new IDMLFilterConfigurationData(), StandardCharsets.UTF_8, expected);
    }
}
