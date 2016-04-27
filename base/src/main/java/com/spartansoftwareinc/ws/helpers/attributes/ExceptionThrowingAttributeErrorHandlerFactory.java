package com.spartansoftwareinc.ws.helpers.attributes;

import org.apache.log4j.Logger;

import com.idiominc.wssdk.attribute.WSAttributeValue;

/**
 * A factory to generate {@link ExceptionThrowingAttributeErrorHandler}
 * instances.
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
        return new ExceptionThrowingAttributeErrorHandler<T>(log);
    }
}
