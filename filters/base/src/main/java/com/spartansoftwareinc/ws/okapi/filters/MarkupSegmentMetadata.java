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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.idiominc.wssdk.ais.WSNode;
import com.idiominc.wssdk.asset.WSMarkupSegment;

/**
 * Representation of the metadata we store in markup headers.
 */
public class MarkupSegmentMetadata<T extends WSOkapiFilterConfigurationData<?>> {
    private static Logger LOG = Logger.getLogger(MarkupSegmentMetadata.class);

    private String asset;
    private T config;

    public String getAsset() {
        return asset;
    }

    public T getConfig() {
        return config;
    }

    private MarkupSegmentMetadata(String asset, T config) {
        this.asset = asset;
        this.config = config;
    }

    /**
     * Decode a markup segment into a metadata object.  If the markup segment contains a new-style
     * metadata block, the asset and configuration data is decoded from it; if it's an old-style metadata
     * block (asset path only), the provided configuration is returned as part of the metadata.
     * @param segment
     * @param config
     * @return
     */
    public static <T extends WSOkapiFilterConfigurationData<?>> MarkupSegmentMetadata<T>
                            fromSegment(WSMarkupSegment segment, T config) {
        try {
            return parseXML(segment.getContent(), config);
        }
        catch (Exception e) {
            String asset = segment.getContent();
            LOG.info("Couldn't parse metadata from markup segment, falling back to asset path " + asset);
            return new MarkupSegmentMetadata<T>(asset, config);
        }
    }

    /**
     * Return an new metadata object for the specified asset and filter configuration.
     * @param node
     * @param config
     * @return
     */
    public static <T extends WSOkapiFilterConfigurationData<?>> MarkupSegmentMetadata<T> fromAsset(WSNode node, T config) {
        return new MarkupSegmentMetadata<T>(node.getPath(), config);
    }

    /**
     * Write the metadata out as XML suitable for inclusion in a markup segment.
     * @return String representation of the XML
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public String toXML() throws ParserConfigurationException, TransformerException {
        Document doc = buildDocument();
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        return sw.toString();
    }

    static final String VERSION_ATTR = "metadataVersion";
    static final String METADATA_VERSION_STRING = "1";

    static final String ROOT = "filter";
    static final String ASSET = "asset";
    static final String CONFIG = "config";
    static final String CONFIG_PARAMS = "params";

    private static <T extends WSOkapiFilterConfigurationData<?>> MarkupSegmentMetadata<T> parseXML(String text, T config)
                throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(text)));
        Element root = doc.getDocumentElement();
        if (!root.getNodeName().equals(ROOT)) {
            logAndThrow("Unexpected markup data XML root node: " + root.getNodeName());
        }
        if (!METADATA_VERSION_STRING.equals(root.getAttribute(VERSION_ATTR))) {
            logAndThrow("Unepxected filter metadata version: " + root.getAttribute(VERSION_ATTR));
        }
        Element assetEl = getFirstElementByName(root, ASSET);
        Element configEl = getFirstElementByName(root, CONFIG);
        Element paramsEl = getFirstElementByName(configEl, CONFIG_PARAMS);
        String assetName = assetEl.getTextContent();
        config.fromXML(paramsEl);
        LOG.info("Loaded asset=" + assetName + ", config=" + config);
        return new MarkupSegmentMetadata<T>(assetName, config);
    }

    private Element createRootElement(Document doc) {
        Element root = doc.createElement(ROOT);
        root.setAttribute(VERSION_ATTR, METADATA_VERSION_STRING);
        doc.appendChild(root);
        return root;
    }

    private Element addAssetElement(Document doc, Element parent) {
        Element assetEl = doc.createElement(ASSET);
        assetEl.setTextContent(this.asset);
        parent.appendChild(assetEl);
        return assetEl;
    }

    private Element addConfigElement(Document doc, Element parent) {
        Element configEl = doc.createElement(CONFIG);
        Node filterConfig = doc.importNode(config.toXML(), true);
        configEl.appendChild(filterConfig);
        parent.appendChild(configEl);
        return configEl;
    }

    private Document buildDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = createRootElement(doc);
        addAssetElement(doc, root);
        addConfigElement(doc, root);
        return doc;
    }

    private static Element getFirstElementByName(Element el, String name) {
        NodeList nl = el.getElementsByTagName(name);
        if (nl.getLength() == 0) {
            logAndThrow("Element " + el.getNodeName() + " has no child '" + name + "'");
        }
        return (Element)nl.item(0);
    }

    private static void logAndThrow(String message) {
        LOG.error(message);
        throw new IllegalStateException(message);
    }
}
