package com.spartansoftware.ws.okapi.filters.mock;

import java.util.Locale;

import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.user.WSLocale;
import com.idiominc.wssdk.user.WSUser;

/**
 * Mock WSLocale, wrapping a Java locale and generating a dummy WSLanguage.
 * Some methods unimplemented.
 */
public class MockWSLocale implements WSLocale {
    private Locale locale;

    public static final MockWSLocale ENGLISH = new MockWSLocale(Locale.ENGLISH);
    public static final MockWSLocale FRENCH = new MockWSLocale(Locale.FRENCH);

    public MockWSLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void addUser(WSUser arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return locale.getDisplayName();
    }

    @Override
    public WSUser[] getUsers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeUser(WSUser arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDisplayString() {
        return locale.getDisplayName();
    }

    @Override
    public int getId() {
        return locale.hashCode();
    }

    @Override
    public WSLanguage getLanguage() {
        return new WSLanguage() {
            @Override
            public int getId() {
                return locale.hashCode();
            }
            
            @Override
            public String getDisplayString() {
                return locale.getDisplayLanguage();
            }
            
            @Override
            public String getName() {
                return locale.getLanguage();
            }
            
            @Override
            public Locale getLocale() {
                return locale;
            }
        };
    }

    @Override
    public void setUsers(WSUser[] arg0) {
        throw new UnsupportedOperationException();
    }
}
