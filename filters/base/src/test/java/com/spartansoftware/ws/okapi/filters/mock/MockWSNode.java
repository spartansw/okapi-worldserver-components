package com.spartansoftware.ws.okapi.filters.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import com.idiominc.wssdk.WSException;
import com.idiominc.wssdk.WSObject;
import com.idiominc.wssdk.ais.WSAclPermission;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.ais.WSNodeType;
import com.idiominc.wssdk.ais.WSSystemPropertyKey;
import com.idiominc.wssdk.security.acl.WSAcl;
import com.idiominc.wssdk.user.WSLocale;
import com.idiominc.wssdk.user.WSUser;
import com.spartansoftwareinc.ws.okapi.filters.utils.FilterUtil;

/**
 * Mock WSNode; many methods are unimplemented.
 */
public abstract class MockWSNode implements WSNode {
    protected Charset charset;
    protected WSLocale locale;

    public MockWSNode(Charset charset, WSLocale locale) {
        this.charset = charset;
        this.locale = locale;
    }

    @Override
    public WSUser getLockOwner() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<?, ?> getProperties() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<?, ?> getProperties(boolean arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(String arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(WSSystemPropertyKey key) throws WSAisException {
        if (key.equals(WSSystemPropertyKey.LOCALE)) {
            return locale;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(String arg0, boolean arg1) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(WSSystemPropertyKey key, boolean inherited)
            throws WSAisException {
        if (key.equals(WSSystemPropertyKey.LOCALE)) {
            return locale;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public WSObject getPropertyObject(String arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lock() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lock(WSUser arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(String arg0, Object arg1) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(String arg0, String arg1) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(WSSystemPropertyKey arg0, Object arg1) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsetProperty(String arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsetProperty(WSSystemPropertyKey arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkPermission(WSAclPermission arg0) throws WSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkPermission(WSAclPermission arg0, WSUser arg1)
            throws WSException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSAcl getAcl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyFrom(WSNode arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyFrom(File arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(WSNode arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(File dest) throws WSAisException {
        try {
            FilterUtil.copy(getInputStream(), dest);
        }
        catch (IOException e) {
            throw new WSAisException(e);
        }
    }

    @Override
    public WSNode[] getAllChildren(boolean arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSNode[] getChildren() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getConnectorProperties() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConnectorProperty(String arg0) throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEncoding() throws WSAisException {
        return charset.toString();
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFingerprint() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getLastModified() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream getOutputStream() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WSNode getParent() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getReader() throws WSAisException {
        return new InputStreamReader(getInputStream(), charset);
    }

    @Override
    public WSNodeType getType() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer getWriter() throws WSAisException {
        return new OutputStreamWriter(getOutputStream(), charset);
    }

    @Override
    public boolean isContainer() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isContent() throws WSAisException {
        return true;
    }

    @Override
    public boolean isMultiFielded() throws WSAisException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isText() throws WSAisException {
        return true;
    }

    @Override
    public boolean setConnectorProperty(String arg0, String arg1)
            throws WSAisException {
        throw new UnsupportedOperationException();
    }

}
