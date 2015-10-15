package com.spartansoftware.ws.okapi.filters.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.user.WSLocale;

/**
 * A read/write mock WSNode backed by a File.
 */
public class FileMockWSNode extends MockWSNode {
    private File file;

    public FileMockWSNode(File file, Charset charset, WSLocale locale) {
        super(charset, locale);
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getPath() {
        return file.getAbsolutePath();
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public InputStream getInputStream() throws WSAisException {
        try {
            return new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            throw new WSAisException(e);
        }
    }

    @Override
    public OutputStream getOutputStream() throws WSAisException {
        try {
            return new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            throw new WSAisException(e);
        }
    }
}
