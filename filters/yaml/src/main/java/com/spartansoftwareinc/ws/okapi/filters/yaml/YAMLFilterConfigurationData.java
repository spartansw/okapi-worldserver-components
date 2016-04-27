package com.spartansoftwareinc.ws.okapi.filters.yaml;

import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

import net.sf.okapi.filters.yaml.Parameters;

public class YAMLFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters>{
    private static final long serialVersionUID = 1L;
    private Set<String> excludedKeys = new LinkedHashSet<String>();

    @Override
    protected Parameters getDefaultParameters() {
        return new Parameters();
    }

    public Set<String> getExcludedKeys() {
        return excludedKeys;
    }

    public void setExcludedKeys(Set<String> excludedKeys) {
        if (excludedKeys != null) {
            this.excludedKeys = excludedKeys;
        }
    }

    @Override
    protected void saveAdditionalConfiguration(Document doc, Node parent) {
        Node excludes = parent.appendChild(doc.createElement("excludedKeys"));
        for (String key : excludedKeys) {
            Node keyNode = excludes.appendChild(doc.createElement("key"));
            if (key != null) {
                Text t = doc.createTextNode(key);
                keyNode.appendChild(t);
            }
        }
    }

    @Override
    protected void loadAdditionalConfiguration(Node configNode) {
        if (configNode.getNodeName().equals("excludedKeys")) {
            NodeList nl = ((Element)configNode).getElementsByTagName("key");
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                excludedKeys.add(n.getTextContent().trim());
            }
        }
    }
}
