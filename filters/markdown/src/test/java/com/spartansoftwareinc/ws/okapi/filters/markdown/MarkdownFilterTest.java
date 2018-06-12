package com.spartansoftwareinc.ws.okapi.filters.markdown;

import java.nio.charset.StandardCharsets;

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
public class MarkdownFilterTest {
    private FilterTestHarness testHarness;

    @DataProvider
    public static Object[][] testHelloWorld() {
        return new Object[][]{{
                new SegmentInfoHolder[]{
                        new SegmentInfoHolder("Hello, World! I am a " + WSFilter.PLACEHOLDER +"Markdown Filter" + WSFilter.PLACEHOLDER + " for " + WSFilter.PLACEHOLDER + "WorldServer" + WSFilter.PLACEHOLDER + ".",
                        			"**", "**", "_", "_"),
                }
        }};
    }
    
    @Before
    public void setup() {
	testHarness = new FilterTestHarness(new MarkdownWSOkapiFilter());
    }
    
    @Test
    @UseDataProvider("testHelloWorld")
    public void testHelloWorld(SegmentInfoHolder[] expected) throws Exception {
	testHarness.extractAndExpectSegments("/HelloWorld.md", new MarkdownFilterConfigurationData(), StandardCharsets.UTF_8, expected);
    }
}
