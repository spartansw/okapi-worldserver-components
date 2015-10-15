package com.spartansoftwareinc.ws.okapi.filters.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.ais.WSSystemPropertyKey;
import com.idiominc.wssdk.asset.WSMarkupSegment;
import com.idiominc.wssdk.asset.WSSegment;
import com.idiominc.wssdk.component.filter.WSSegmentReader;
import com.idiominc.wssdk.component.filter.WSSegmentWriter;
import com.idiominc.wssdk.user.WSLocale;

import net.sf.okapi.common.LocaleId;

public class FilterUtil {

    public static LocaleId getOkapiLocaleId(WSNode content) throws WSAisException, IllegalStateException {
        WSLocale wsLocale = (WSLocale) content.getProperty(WSSystemPropertyKey.LOCALE);
        if (wsLocale != null) {
            if (wsLocale.getLanguage() != null) {
                Locale locale = wsLocale.getLanguage().getLocale();
                return new LocaleId(locale.getLanguage(), locale.getCountry());
            }
        }
        throw new IllegalStateException("Could not find locale in AIS content repository" +
                content.toString());
    }

    public static String detectEncoding(WSNode content, String defaultEncoding) throws WSAisException {
        return content.getEncoding() == null ? defaultEncoding : content.getEncoding();
    }

    /**
     * Retrieve the original source AIS content to export the same PO/JSON/YAML metadata when exporting the
     * translations.
     *
     * @param context       - context
     * @param segmentReader - WorldServer segments
     * @return original source AIS content
     * @throws IllegalStateException, WSAisException, IOException
     */
    public static WSNode parseSourceAisPathSegment(WSContext context, WSSegmentReader segmentReader)
                        throws IllegalStateException, WSAisException, IOException {
        WSSegment srcAisSegment = segmentReader.read();
        if (srcAisSegment == null || !(srcAisSegment instanceof WSMarkupSegment)) {
            throw new IllegalStateException("Missing Source AIS segment for writing file");
        }
        return context.getAisManager().getNode(srcAisSegment.getContent());
    }

    public static File convertAisContentIntoFile(WSNode aisContent) throws IOException,
            WSAisException {
        Path tempFile = Files.createTempFile("wsokapi", getFileExtension(aisContent.getName()));
        Files.copy(aisContent.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toFile();
    }

    private static String getFileExtension(String path) {
        int i = path.lastIndexOf('.');
        if (i == -1) {
            return "";
        }
        return path.substring(i);
    }

    /**
     * During import into WorldServer, we keep track of the source AIS path to use during export to preserve any
     * metadata in the PO file by writing a markup segment containing the AIS path. We will later reparse this during
     * export.
     *
     * @param srcContent    - AIS content
     * @param segmentWriter - Writer to communicate with WorldServer
     */
    public static void writeSourceAisPathSegment(WSNode srcContent, WSSegmentWriter segmentWriter) {
        segmentWriter.writeMarkupSegment(srcContent.getPath());
    }


    public static String join(String[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
            sb.append(delimiter);
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
