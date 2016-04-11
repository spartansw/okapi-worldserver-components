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
public class AttributesWrapper<T extends WSAttributable> {
    private static final Logger LOG = Logger.getLogger(AttributesWrapper.class);
    private T attributable;
    private AttributeErrorHandlerFactory errorHandlerFactory;

    public AttributesWrapper(T attributable) {
        this(attributable, new ExceptionThrowingAttributeErrorHandlerFactory(LOG));
    }

    public AttributesWrapper(T attributable, AttributeErrorHandlerFactory errorHandlerFactory) {
        this.attributable = attributable;
        this.errorHandlerFactory = errorHandlerFactory;
    }

    /**
     * Return the wrapped instance.
     * @return
     */
    public T get() {
        return attributable;
    }

    /**
     * Return the value of the Boolean attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
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

    /**
     * Return the value of the String attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getString(String attributeName) {
        AttributeErrorHandler<WSStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getStringValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setString(String attributeName, String value) {
        AttributeErrorHandler<WSStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getStringValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value of the Integer attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public Integer getInteger(String attributeName) {
        AttributeErrorHandler<WSIntegerAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getIntegerValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setInteger(String attributeName, Integer value) {
        AttributeErrorHandler<WSIntegerAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getIntegerValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the text value of the URL attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getUrl(String attributeName) {
        AttributeErrorHandler<WSUrlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getUrlValue(attributable, attributeName, errorHandler).getValue();
    }

    /**
     * Return the address value of the URL attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getUrlAdress(String attributeName) {
        AttributeErrorHandler<WSUrlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getUrlValue(attributable, attributeName, errorHandler).getAddress();
    }

    public void setUrl(String attributeName, String text, String address) {
        AttributeErrorHandler<WSUrlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getUrlValue(attributable, attributeName, errorHandler).setValue(text, address);
    }

    /**
     * Return the value of the Comment attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getComment(String attributeName) {
        AttributeErrorHandler<WSCommentAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getCommentValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setComment(String attributeName, String value) {
        AttributeErrorHandler<WSCommentAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getCommentValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value of the Date attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public Date getDate(String attributeName) {
        AttributeErrorHandler<WSDateAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getDateValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setDate(String attributeName, Date value) {
        AttributeErrorHandler<WSDateAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getDateValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value(s) of the File attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public File[] getFiles(String attributeName) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getFileValue(attributable, attributeName, errorHandler).getValues();
    }

    public void setFiles(String attributeName, File[] values) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getFileValue(attributable, attributeName, errorHandler).setValues(values);
    }

    /**
     * Return a single value of the File attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public File getFile(String attributeName) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getFileValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setFile(String attributeName, File value) {
        AttributeErrorHandler<WSFileAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getFileValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value of the HTML attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getHtml(String attributeName) {
        AttributeErrorHandler<WSHtmlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getHtmlValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setHtml(String attributeName, String value) {
        AttributeErrorHandler<WSHtmlAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getHtmlValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value of the Image attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public File getImage(String attributeName) {
        AttributeErrorHandler<WSImageAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getImageValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setImage(String attributeName, File value) {
        AttributeErrorHandler<WSImageAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getImageValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value of the Large String attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getLargeString(String attributeName) {
        AttributeErrorHandler<WSLargeStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getLargeStringValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setLargeString(String attributeName, String value) {
        AttributeErrorHandler<WSLargeStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getLargeStringValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the values of the List attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String[] getList(String attributeName) {
        AttributeErrorHandler<WSListAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getListValue(attributable, attributeName, errorHandler).getValues();
    }

    public void setList(String attributeName, String values[]) {
        AttributeErrorHandler<WSListAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getListValue(attributable, attributeName, errorHandler).setValues(values);
    }

    /**
     * Return the values of the MultiSelect attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String[] getMultiSelectString(String attributeName) {
        AttributeErrorHandler<WSMultiSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getMultiSelectStringValue(attributable, attributeName, errorHandler).getValues();
    }

    public void setMultiSelect(String attributeName, String values[]) {
        AttributeErrorHandler<WSMultiSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getMultiSelectStringValue(attributable, attributeName, errorHandler).setValues(values);
    }

    /**
     * Return the value of the Select String attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public String getSelectString(String attributeName) {
        AttributeErrorHandler<WSSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getSelectStringValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setSelectString(String attributeName, String value) {
        AttributeErrorHandler<WSSelectStringAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getSelectStringValue(attributable, attributeName, errorHandler).setValue(value);
    }

    /**
     * Return the value of the User attribute with the specified name. If the attribute
     * does not exist or is the wrong type, raise an error with the registered error handler.
     * @param attributeName
     * @return boolean attribute value
     */
    public WSUser getUser(String attributeName) {
        AttributeErrorHandler<WSUserAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        return Attributes.getUserValue(attributable, attributeName, errorHandler).getValue();
    }

    public void setUser(String attributeName, WSUser value) {
        AttributeErrorHandler<WSUserAttributeValue> errorHandler = errorHandlerFactory.newInstance();
        Attributes.getUserValue(attributable, attributeName, errorHandler).setValue(value);
    }
}
