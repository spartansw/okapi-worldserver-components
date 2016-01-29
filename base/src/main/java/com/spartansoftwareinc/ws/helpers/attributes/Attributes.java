package com.spartansoftwareinc.ws.helpers.attributes;

import com.idiominc.wssdk.WSAttributeNotSupportedException;
import com.idiominc.wssdk.attribute.WSAttributable;
import com.idiominc.wssdk.attribute.WSAttributeValue;
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

public class Attributes {
    public static WSStringAttributeValue getStringValue(WSAttributable attributable, String attributeName,
                    AttributeErrorHandler<WSStringAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSStringAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSStringAttributeValue)val;
    }

    public static WSIntegerAttributeValue getIntegerValue(WSAttributable attributable, String attributeName,
                            AttributeErrorHandler<WSIntegerAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSIntegerAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSIntegerAttributeValue)val;
    }

    /**
     * Fetch the WSBooleanAttributeValue instance for the specified object and attribute name,
     * handling errors as specified.
     * <b>WARNING</b>: due to a bug in the WSSDK, calling <tt>getValue()</tt> on WSBooleanAttributeValue
     * instances will throw an exception if no value is set in WorldServer.  The recommended idiom for
     * checking a WSBooleanAttributeValue instance is:
     * <code>
     * WSBooleanAttributeValue value = ...;
     * Boolean b = Boolean.parseBoolean(value.toString());
     * </code>
     *
     * @param attributable
     * @param attributeName
     * @param errorHandler
     * @return
     */
    public static WSBooleanAttributeValue getBooleanValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSBooleanAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSBooleanAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSBooleanAttributeValue)val;
    }

    public static WSUrlAttributeValue getUrlValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSUrlAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSUrlAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSUrlAttributeValue)val;
    }

    public static WSCommentAttributeValue getCommentValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSCommentAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSCommentAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSCommentAttributeValue)val;
    }

    public static WSDateAttributeValue getDateValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSDateAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSDateAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSDateAttributeValue)val;
    }

    public static WSFileAttributeValue getFileValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSFileAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSFileAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSFileAttributeValue)val;
    }

    public static WSHtmlAttributeValue getHtmlValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSHtmlAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSHtmlAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSHtmlAttributeValue)val;
    }

    public static WSImageAttributeValue getImageValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSImageAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSImageAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSImageAttributeValue)val;
    }

    public static WSLargeStringAttributeValue getLargeStringValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSLargeStringAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSLargeStringAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSLargeStringAttributeValue)val;
    }

    public static WSListAttributeValue getListValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSListAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSListAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSListAttributeValue)val;
    }

    public static WSMultiSelectStringAttributeValue getMultiSelectStringValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSMultiSelectStringAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSMultiSelectStringAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSMultiSelectStringAttributeValue)val;
    }

    public static WSSelectStringAttributeValue getSelectStringValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSSelectStringAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSSelectStringAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSSelectStringAttributeValue)val;
    }

    public static WSUserAttributeValue getUserValue(WSAttributable attributable, String attributeName,
            AttributeErrorHandler<WSUserAttributeValue> errorHandler) {
        WSAttributeValue val = getValue(attributable, attributeName, errorHandler);
        if (!(val instanceof WSUserAttributeValue)) {
            return errorHandler.attributeIsIncorrectType(attributable, attributeName);
        }
        return (WSUserAttributeValue)val;
    }

    private static WSAttributeValue getValue(WSAttributable attributable, String attributeName,
                            AttributeErrorHandler<?> errorHandler) {
        try {
            return attributable.getAttributeValue(attributeName);
        } catch (WSAttributeNotSupportedException e){
            return errorHandler.attributeDoesNotExist(attributable, attributeName);
        }
    }
}
