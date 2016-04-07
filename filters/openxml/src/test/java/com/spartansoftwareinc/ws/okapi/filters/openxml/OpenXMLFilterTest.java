package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;

@RunWith(DataProviderRunner.class)
public class OpenXMLFilterTest {

    @DataProvider
    public static Object[][] testDefaultParametersParsingProvider() {
        return new Object[][] {{
                new SegmentInfoHolder[] {
                        new SegmentInfoHolder("One two three." + WSFilter.PLACEHOLDER, "<tags1/>"),
                        new SegmentInfoHolder("Andriy Kundyukov"),
                }
        }};
    }

    @Test
    @UseDataProvider("testDefaultParametersParsingProvider")
    public void testDefaultParametersParsing(SegmentInfoHolder[] expected) throws Exception {
        FilterTestHarness harness = new FilterTestHarness(new TestOpenXMLWSOkapiFilter());
        harness.extractAndExpectSegments("/TestDocWithNoBreakHyphen.docx", StandardCharsets.UTF_8, expected);
    }

    @DataProvider
    public static Object[][] testParsingWithChangedParametersProvider() {
        return new Object[][] {{
                new SegmentInfoHolder[] {
                        new SegmentInfoHolder("One two three." + "-"),
                        new SegmentInfoHolder("Andriy Kundyukov"),
                }
        }};
    }

    @Test
    @UseDataProvider("testParsingWithChangedParametersProvider")
    public void testParsingWithChangedParameters(SegmentInfoHolder[] expected) throws Exception { //TODO why it works differently without Test filter?
        FilterTestHarness harness = new FilterTestHarness(new TestOpenXMLWSOkapiFilterBuilder().replaceNoBreakHyphenTag(true).build());
        harness.extractAndExpectSegments("/TestDocWithNoBreakHyphen.docx", StandardCharsets.UTF_8, expected);
    }

    private class TestOpenXMLWSOkapiFilter extends OpenXMLWSOkapiFilter {
        private OpenXMLFilterConfigurationData testData = new OpenXMLFilterConfigurationData();

        @Override
        protected OpenXMLFilterConfigurationData getOpenXMLFilterConfiguration() {
            return testData;
        }
    }

    private class TestOpenXMLWSOkapiFilterBuilder {
        private TestOpenXMLWSOkapiFilter filter = new TestOpenXMLWSOkapiFilter();

        TestOpenXMLWSOkapiFilter build() {
            return filter;
        }

        TestOpenXMLWSOkapiFilterBuilder replaceNoBreakHyphenTag(boolean param) {
            this.filter.getOpenXMLFilterConfiguration().setReplaceNoBreakHyphenTag(param);
            return this;
        }

        TestOpenXMLWSOkapiFilterBuilder automaticallyAcceptRevisions(boolean param) {
            this.filter.getOpenXMLFilterConfiguration().setAutomaticallyAcceptRevisions(param);
            return this;
        }
    }
}
