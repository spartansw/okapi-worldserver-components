package com.spartansoftwareinc.ws.autoactions.xliff;

import org.junit.Test;
import static org.junit.Assert.*;

import com.idiominc.wssdk.component.filter.WSFilter;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class WSTextSegmentDataTest {

    @Test
    public void fromOkapiSegment() {
        TextFragment tf = new TextFragment("Hello ");
        tf.append(new Code(TagType.OPENING, "bold", "<b>"));
        tf.append("world");
        tf.append(new Code(TagType.CLOSING, "bold", "</b>"));
        Segment segment = new Segment("seg1", tf);
        WSTextSegmentData data = WSTextSegmentData.fromOkapiSegment(segment);
        assertEquals("Hello " + WSFilter.PLACEHOLDER + "world" + WSFilter.PLACEHOLDER, data.getText());
        assertArrayEquals(new String[] { "<b>", "</b>" }, data.getPlaceholders());
    }
}
