package com.spartansoftwareinc.ws.okapi.filters;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class TestBaseFilter {

    @DataProvider
    public static Object[][] testParseResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("Hello world."), 
                new SegmentInfoHolder("Hello " + WSFilter.PLACEHOLDER + ".", "{1}")
            }
        }};
    }

    @Test
    @UseDataProvider("testParseResults")
    public void testParse(SegmentInfoHolder[] expected) throws Exception {
        new FilterTestHarness(new DummyWSOkapiFilter())
            .extractAndExpectSegments("/test.properties", Charset.forName("UTF-8"), expected);
    }

    @DataProvider
    public static Object[][] translations() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("Bonjour tout le monde."),
                new SegmentInfoHolder("Bonjour {1}.", "{1}")
            }
        }};
    }

    @Test
    @UseDataProvider("translations")
    public void testMerge(SegmentInfoHolder[] translations) throws Exception {
        new FilterTestHarness(new DummyWSOkapiFilter())
            .mergeAndVerifyOutput("/test.properties", "/out.properties", Charset.forName("UTF-8"),
                                  Arrays.asList(translations));
    }
}
