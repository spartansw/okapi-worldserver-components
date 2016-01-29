package com.spartansoftwareinc.ws.helpers.attributes;

import com.idiominc.wssdk.attribute.WSAttributeValue;

/**
 * A factory that can create appropriately types instances of
 * an {@link AttributeErrorHandler} on demand.
 */
public interface AttributeErrorHandlerFactory {
    <T extends WSAttributeValue> AttributeErrorHandler<T> newInstance();
}
