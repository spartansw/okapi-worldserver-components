package com.spartansoftwareinc.ws.okapi.filters.json;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class JSONFilterTest {
    
    @DataProvider
    public static Object[][] testParsingAllTranslatableKeysResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("File"),
                new SegmentInfoHolder("New"),
                new SegmentInfoHolder("Open"),
                new SegmentInfoHolder("Close"),
                new SegmentInfoHolder("The " + WSFilter.PLACEHOLDER + " attribute should be a " +
                                      WSFilter.PLACEHOLDER + " placeholder since it's " + WSFilter.PLACEHOLDER,
                                      ":colon", ":1", ":alpha1"), 
                new SegmentInfoHolder("Test " + WSFilter.PLACEHOLDER + "HTML" + WSFilter.PLACEHOLDER +
                                      " entity " + WSFilter.PLACEHOLDER + " escaping",
                                      "<b>", "</b>", "&amp;"),
                new SegmentInfoHolder("Test")
            }
        }};
    }

    @Test
    @UseDataProvider("testParsingAllTranslatableKeysResults")
    public void testParsingAllTranslatableKeys(SegmentInfoHolder[] expected) throws Exception {
        JSONWSOkapiFilter filter = new TestJsonWSOkapiFilter();
        FilterTestHarness harness = new FilterTestHarness(filter);
        harness.extractAndExpectSegments("/TestFile.json", filter.getOkapiFilterConfiguration(),
                StandardCharsets.UTF_8, expected);
    }

    @DataProvider
    public static Object[][] testParsingWithExcludedKeysResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("The " + WSFilter.PLACEHOLDER + " attribute should be a " +
                                      WSFilter.PLACEHOLDER + " placeholder since it's " + WSFilter.PLACEHOLDER,
                                      ":colon", ":1", ":alpha1"), 
                new SegmentInfoHolder("Test")
            }
        }};
    }

    @Test
    @UseDataProvider("testParsingWithExcludedKeysResults")
    public void testParsingWithExcludedKeys(SegmentInfoHolder[] expected) throws Exception {
        JSONWSOkapiFilter filter = new TestJsonWSOkapiFilter("value", "html");
        FilterTestHarness harness = new FilterTestHarness(filter);
        harness.extractAndExpectSegments("/TestFile.json", filter.getOkapiFilterConfiguration(),
                StandardCharsets.UTF_8, expected);
    }

    @Test
    public void testParseExcludeEverything() throws Exception {
        JSONWSOkapiFilter filter = new TestJsonWSOkapiFilter("value", "html", "ph", "field");
        FilterTestHarness harness = new FilterTestHarness(filter);
        harness.extractAndExpectSegments("/TestFile.json", filter.getOkapiFilterConfiguration(),
                StandardCharsets.UTF_8, new SegmentInfoHolder[0]);
    }

    class TestJsonWSOkapiFilter extends JSONWSOkapiFilter {
        private JSONFilterConfigurationData testData = new JSONFilterConfigurationData();

        public TestJsonWSOkapiFilter(String... exceptions) {
            testData.setExcludedKeys(Arrays.asList(exceptions));
        }

        @Override
        protected JSONFilterConfigurationData getOkapiFilterConfiguration() {
            return testData;
        }
    }
}
