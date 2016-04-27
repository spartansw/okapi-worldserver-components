package com.spartansoftwareinc.ws.helpers.attributes;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.attribute.WSAttributable;
import com.idiominc.wssdk.attribute.WSAttributeValue;

/**
 * An {@link AttributeErrorHandler} that optionally logs a message and then
 * throws an exception in all failure cases.
 */
public class ExceptionThrowingAttributeErrorHandler<T extends WSAttributeValue> implements AttributeErrorHandler<T> {
    private Logger log;
    public ExceptionThrowingAttributeErrorHandler() {
        this(null);
    }
    public ExceptionThrowingAttributeErrorHandler(Logger log) {
        this.log = log;
    }
    @Override
    public T attributeDoesNotExist(WSAttributable attributable, String attributeName) {
        logAndThrow(log, "'" + attributeName + "' attribute does not exist.");
        return null;
    }
    @Override
    public T attributeIsIncorrectType(WSAttributable attributable, String attributeName) {
        logAndThrow(log, "'" + attributeName + "' attribute is not the right type.");
        return null;
    }
    private static void logAndThrow(Logger log, String msg) {
        if (log != null) {
            log.error(msg);
        }
        throw new WSRuntimeException(msg);
    }
}
