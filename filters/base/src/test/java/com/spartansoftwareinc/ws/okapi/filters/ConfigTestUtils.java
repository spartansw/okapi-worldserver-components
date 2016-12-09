package com.spartansoftwareinc.ws.okapi.filters;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;

public class ConfigTestUtils {

    public static String toXML(WSFilterConfigurationData config) throws TransformerException {
        Node node = config.toXML();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString().replaceAll("\\r\\n", "\\\n"); //TODO was is supposed to fail on CRLF/LF difference?
    }

    public static WSFilterConfigurationData fromXML(WSFilterConfigurationData config, String xml) 
            throws TransformerException, SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Node node = builder.parse(new InputSource(new StringReader(xml))).getFirstChild();
        config.fromXML(node);
        return config;
    }
}
