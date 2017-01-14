package com.spartansoftwareinc.ws.okapi.filters.xliff;

import net.sf.okapi.common.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class TestXLIFFWSOkapiFilter {
    private FilterTestHarness testHarness;

    @Before
    public void setup() {
        testHarness = new FilterTestHarness(new XLIFFWSOkapiFilter());
    }

    @DataProvider
    public static Object[][] testXliff() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("Hello"),
                new SegmentInfoHolder("goodbye " + WSFilter.PLACEHOLDER + "etc" + WSFilter.PLACEHOLDER + ".",
                                      "<g id=\"1\">", "</g>")
            }
        }};
    }

    @Test
    @UseDataProvider("testXliff")
    public void testXliff(SegmentInfoHolder[] expected) throws Exception {
        testHarness.extractAndExpectSegments("/test.xlf", new XLIFFFilterConfigurationData(),
                    StandardCharsets.UTF_8, expected);
    }
}
