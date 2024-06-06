package com.spartansoftwareinc.ws.okapi.mt.mshub;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.idiominc.wssdk.linguistic.WSLanguage;
import com.idiominc.wssdk.linguistic.WSLanguagePair;

@RunWith(MockitoJUnitRunner.class)
public class WSMicrosoftMTAdapterV3Test {

    @Test
    public void testSupportedLanguages() {
        WSMicrosoftMTAdapterV3 connector = new WSMicrosoftMTAdapterV3();

        WSLanguage english = mock(WSLanguage.class);
        when(english.getLocale()).thenReturn(Locale.ENGLISH);

        WSLanguage french = mock(WSLanguage.class);
        when(french.getLocale()).thenReturn(Locale.FRENCH);

        WSLanguage afarNotSupported = mock(WSLanguage.class);
        Locale afarLocale = new Locale("aa");
        when(afarNotSupported.getLocale()).thenReturn(afarLocale);

        WSLanguage[] languages = {english, french, afarNotSupported};
        WSLanguagePair[] langPairs = connector.composeLanguagePairs(languages);
        assertEquals(2, langPairs.length);
    }
}
