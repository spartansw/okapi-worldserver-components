package com.spartansoftwareinc.ws.helpers.attributes;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.attribute.WSAttributable;
import com.idiominc.wssdk.attribute.WSAttributeValue;

/**
 * An attribute validator that optionally logs an error and then throws an exception
 * in all failure cases.
 */
public class ExceptionThrowingAttributeErrorHandlerFactory implements AttributeErrorHandlerFactory {
    private Logger log;
    public ExceptionThrowingAttributeErrorHandlerFactory() {
        this(null);
    }
    public ExceptionThrowingAttributeErrorHandlerFactory(Logger log) {
        this.log = log;
    }
    @Override
    public <T extends WSAttributeValue> AttributeErrorHandler<T> newInstance() {
        return new AttributeErrorHandler<T>() {
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
        };
    }
    private static void logAndThrow(Logger log, String msg) {
        if (log != null) {
            log.error(msg);
        }
        throw new WSRuntimeException(msg);
    }
}