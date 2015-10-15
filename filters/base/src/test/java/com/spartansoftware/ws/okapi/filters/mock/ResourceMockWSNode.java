package com.spartansoftware.ws.okapi.filters.mock;

import java.io.InputStream;
import java.nio.charset.Charset;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.user.WSLocale;

/**
 * A read-only mock WSNode based on a resource on the classpath.
 */
public class ResourceMockWSNode extends MockWSNode {
    private String resourceName;

    public ResourceMockWSNode(String resourceName, Charset charset, WSLocale locale) {
        super(charset, locale);
        this.resourceName = resourceName;
    }

    @Override
    public String getName() {
        int i = resourceName.lastIndexOf("/");
        return (i == -1) ? resourceName : resourceName.substring(i + 1);
    }

    @Override
    public String getPath() {
        return resourceName;
    }

    @Override
    public InputStream getInputStream() throws WSAisException {
        return getClass().getResourceAsStream(resourceName);
    }
}
