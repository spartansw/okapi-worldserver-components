package com.spartansoftwareinc.ws.okapi.filters.openxml;

import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class OpenXMLFilterTest {

    @Test //TODO remove this later
    public void emptyTest()  throws Exception {
        FilterTestHarness harness = new FilterTestHarness(new TestOpenXMLWSOkapiFilterBuilder().replaceNoBreakHyphenTag(true).build());
    }

//    @Test
//    //TODO
//    public void testDefaultParametersParsing() throws Exception {
//        FilterTestHarness harness = new FilterTestHarness(new TestOpenXMLWSOkapiFilter());
//        harness.extractAndExpectSegments("/TestDocWithNoBreakHyphen.docx", StandardCharsets.UTF_8, null); //TODO
//    }

//    @Test TODO
//    @UseDataProvider("testParsingWithChangedParametersProvider")
//    public void testParsingWithChangedParameters(SegmentInfoHolder[] expected) throws Exception {
//        FilterTestHarness harness = new FilterTestHarness(new TestOpenXMLWSOkapiFilterBuilder().replaceNoBreakHyphenTag(true).build());
//        harness.extractAndExpectSegments("/TestDocWithNoBreakHyphen.docx", StandardCharsets.UTF_8, expected);
//    }

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
    }
}
