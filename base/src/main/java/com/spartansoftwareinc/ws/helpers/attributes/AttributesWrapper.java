package com.spartansoftwareinc.ws.helpers.attributes;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.attribute.WSAttributable;
import com.idiominc.wssdk.attribute.WSBooleanAttributeValue;
import com.idiominc.wssdk.attribute.WSCommentAttributeValue;
import com.idiominc.wssdk.attribute.WSDateAttributeValue;
import com.idiominc.wssdk.attribute.WSFileAttributeValue;
import com.idiominc.wssdk.attribute.WSHtmlAttributeValue;
import com.idiominc.wssdk.attribute.WSImageAttributeValue;
import com.idiominc.wssdk.attribute.WSIntegerAttributeValue;
import com.idiominc.wssdk.attribute.WSLargeStringAttributeValue;
import com.idiominc.wssdk.attribute.WSListAttributeValue;
import com.idiominc.wssdk.attribute.WSMultiSelectStringAttributeValue;
import com.idiominc.wssdk.attribute.WSSelectStringAttributeValue;
import com.idiominc.wssdk.attribute.WSStringAttributeValue;
import com.idiominc.wssdk.attribute.WSUrlAttributeValue;
import com.idiominc.wssdk.attribute.WSUserAttributeValue;
import com.idiominc.wssdk.user.WSUser;

/**
 * Wrapper class that exposes get and set methods for anything that supports
 * <tt>WSAttributable</tt>.  Failures due to missing or incorrectly-typed
 * attributes are handled via an AttributeErrorHandler.
 * <p>
 * Sample usage:
 * <pre>
 *   WSProject project = ...;
 *   AttributesWrapper wrapper = new AttributesWrapper(project);
 *   String attr1 = wrapper.getString("attribute_1");
 *   wrapper.setString("attribute_2", "my_value");
 * </pre>
 */
public class AttributesWrapper {
    private static final Logger LOG = Logger.getLogger(AttributesWrapper.class);
    private WSAttributable attributable;
    private AttributeErrorHandlerFactory errorHandlerFactory;

    public AttributesWrapper(WSAttributable attributable) {
        this(attributable, new ExceptionThrowingAttributeErrorHandlerFactory(LOG));
    }

    public AttributesWrapper(WSAttributable attributable, AttributeErrorHandlerFactory errorHandlerFactory) {
        this.attributable = attributable;
        this.errorHandlerFactory = errorHandlerFactory;
    }

    public Boolean getBoolean(String attributeName) {
        // There is a bug in the WSSDK (as of 10.x) - if a boolean attribute value is
        // unassigned, calling WSBooleanAttribute#getValue() will crash with a
        // NullPointerException. Therefore we take a more circuitous route.
        AttributeErrorHandler<WSBooleanAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        WSBooleanAttributeValue val = Attributes.getBooleanValue(attributable, attributeName, errorHandler);
        return Boolean.parseBoolean(val.toString());
    }

    public void setBoolean(String attributeName, Boolean value) {
        AttributeErrorHandler<WSBooleanAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getBooleanValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public String getString(String attributeName) {
        AttributeErrorHandler<WSStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getStringValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setString(String attributeName, String value) {
        AttributeErrorHandler<WSStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getStringValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public Integer getInteger(String attributeName) {
        AttributeErrorHandler<WSIntegerAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getIntegerValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setInteger(String attributeName, Integer value) {
        AttributeErrorHandler<WSIntegerAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getIntegerValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public String getUrl(String attributeName) {
        AttributeErrorHandler<WSUrlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getUrlValue(attributable, attributeName, errorHandler).getValue();
    }

    public String getUrlAdress(String attributeName) {
        AttributeErrorHandler<WSUrlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getUrlValue(attributable, attributeName, errorHandler).getAddress();
    }

    public void setUrl(String attributeName, String text, String address) {
        AttributeErrorHandler<WSUrlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getUrlValue(attributable, attributeName, errorHandler).setValue(text, address);
    }

    public String getComment(String attributeName) {
        AttributeErrorHandler<WSCommentAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getCommentValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setComment(String attributeName, String value) {
        AttributeErrorHandler<WSCommentAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getCommentValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public Date getDate(String attributeName) {
        AttributeErrorHandler<WSDateAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getDateValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setDate(String attributeName, Date value) {
        AttributeErrorHandler<WSDateAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getDateValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public File[] getFiles(String attributeName) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getFileValue(attributable, attributeName, errorHandler).getValues();
    }

    public void setFiles(String attributeName, File[] values) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getFileValue(attributable, attributeName, errorHandler).setValues(values);
    }

    public File getFile(String attributeName) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getFileValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setFile(String attributeName, File value) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getFileValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public String getHtml(String attributeName) {
        AttributeErrorHandler<WSHtmlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getHtmlValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setHtml(String attributeName, String value) {
        AttributeErrorHandler<WSHtmlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getHtmlValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public File getImage(String attributeName) {
        AttributeErrorHandler<WSImageAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getImageValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setImage(String attributeName, File value) {
        AttributeErrorHandler<WSImageAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getImageValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public String getLargeString(String attributeName) {
        AttributeErrorHandler<WSLargeStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getLargeStringValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setLargeString(String attributeName, String value) {
        AttributeErrorHandler<WSLargeStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getLargeStringValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public String[] getList(String attributeName) {
        AttributeErrorHandler<WSListAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getListValue(attributable, attributeName, errorHandler).getValues();
    }

    public void setList(String attributeName, String values[]) {
        AttributeErrorHandler<WSListAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getListValue(attributable, attributeName, errorHandler).setValues(values);
    }

    public String[] getMultiSelectString(String attributeName) {
        AttributeErrorHandler<WSMultiSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getMultiSelectStringValue(attributable, attributeName, errorHandler).getValues();
    }

    public void setMultiSelect(String attributeName, String values[]) {
        AttributeErrorHandler<WSMultiSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getMultiSelectStringValue(attributable, attributeName, errorHandler).setValues(values);
    }

    public String getSelectString(String attributeName) {
        AttributeErrorHandler<WSSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getSelectStringValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setSelectString(String attributeName, String value) {
        AttributeErrorHandler<WSSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getSelectStringValue(attributable, attributeName, errorHandler).setValue(value);
    }

    public WSUser getUser(String attributeName) {
        AttributeErrorHandler<WSUserAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getUserValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setUser(String attributeName, WSUser value) {
        AttributeErrorHandler<WSUserAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getUserValue(attributable, attributeName, errorHandler).setValue(value);
    }
}
