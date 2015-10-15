package com.spartansoftwareinc.ws.okapi.filters.po;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

import net.sf.okapi.filters.po.Parameters;

public class POFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {
    private static final long serialVersionUID = 1L;

    private boolean copyToPoFile = false;

    @Override
    protected Parameters getDefaultParameters() {
        Parameters filterParams = new Parameters();
        // Apply overrides to the default Okapi configuration, including extra codeFinder rules
        filterParams.setAllowEmptyOutputTarget(true);
        filterParams.getCodeFinder().addRule("<.+?>");
        filterParams.getCodeFinder().addRule("\\[\\[.+?\\]\\]");
        filterParams.getCodeFinder().addRule("\\[.+?\\]");
        return filterParams;
    }

    public boolean getCopyToPO() {
        return copyToPoFile;
    }

    public void setCopyToPO(boolean copyToPO) {
        this.copyToPoFile = copyToPO;
    }

    @Override
    protected void saveAdditionalConfiguration(Document doc, Node parent) {
        Node node = doc.createElement("copyTargetToPO");
        node.appendChild(doc.createTextNode(Boolean.toString(copyToPoFile)));
        parent.appendChild(node);
    }

    @Override
    protected void loadAdditionalConfiguration(Node configNode) {
        if (configNode.getNodeName().equals("copyTargetToPO")) {
            copyToPoFile = Boolean.valueOf(configNode.getTextContent());
        }
    }
}
