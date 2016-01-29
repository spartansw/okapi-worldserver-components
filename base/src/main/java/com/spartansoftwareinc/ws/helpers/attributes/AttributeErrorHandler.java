package com.spartansoftwareinc.ws.helpers.attributes;

import com.idiominc.wssdk.attribute.WSAttributable;
import com.idiominc.wssdk.attribute.WSAttributeValue;

/**
 * A class which will be called to handle certain error cases when fetching
 * WSAttributeValue instances for reading or writing.  Most implementations
 * are expected to be generic across all WSAttributeValue subtypes.
 */
public interface AttributeErrorHandler<T extends WSAttributeValue> {

    /**
     * Called when an expected attribute is not found on the WSAttributable
     * instance. This method should either return a non-null instance (by
     * providing a dummy or default object) or throw an exception.
     * @param attributable instance from which the attribute is being read or written
     * @param attributeName name of the attribute being read or written
     * @return an object of the appropriate type
     */
    T attributeDoesNotExist(WSAttributable attributable, String attributeName);

    /**
     * Called when an expected attribute is found on the WSAttributable
     * instance, but the attribute's value is not the expected type.
     * This method should either return a non-null instance (by
     * providing a dummy or default object) or throw an exception.
     * @param attributable instance from which the attribute is being read or written
     * @param attributeName name of the attribute being read or written
     * @return an object of the appropriate type
     */
    T attributeIsIncorrectType(WSAttributable attributable, String attributeName);
}