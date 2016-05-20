package com.spartansoftwareinc.ws.okapi.mt.mshub;

import org.junit.Test;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

import static org.junit.Assert.*;

public class MTRequestConverterTest {
    private MTRequestConverter converter = new MTRequestConverter();

    @Test
    public void testNoCodes() {
        assertEquals(new TextFragment("Hello world."),
                     converter.toTextFragment("Hello world."));
        assertEquals("Hello world.", converter.fromTextFragment(new TextFragment("Hello world.")));
    }

    @Test
    public void testCodes() {
        TextFragment tf = new TextFragment(); 
        tf.append("Hello ");
        tf.append(ph(1));
        tf.append("world");
        tf.append(ph(2));
        tf.append(".");
        assertEquals(tf, converter.toTextFragment("Hello {1}world{2}."));
        assertEquals("Hello {1}world{2}.", converter.fromTextFragment(tf));
    }

    @Test
    public void testCodesWithLargerIds() {
        TextFragment tf = new TextFragment(); 
        tf.append("Hello ");
        tf.append(ph(31));
        tf.append("world");
        tf.append(ph(32));
        tf.append(".");
        assertEquals(tf, converter.toTextFragment("Hello {31}world{32}."));
        assertEquals("Hello {31}world{32}.", converter.fromTextFragment(tf));
    }

    @Test
    public void testCodesWithoutText() {
        TextFragment tf = new TextFragment(); 
        tf.append(ph(1));
        tf.append(ph(2));
        assertEquals(tf, converter.toTextFragment("{1}{2}"));
        assertEquals("{1}{2}", converter.fromTextFragment(tf));
    }

    private Code ph(int id) {
        Code code = new Code();
        code.setTagType(TagType.PLACEHOLDER);
        code.setId(id);
        return code;
    }
}
