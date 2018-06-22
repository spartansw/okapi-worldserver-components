package com.spartansoftwareinc.ws.okapi.filters.markdown;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.filters.markdown.Parameters;

public class MarkdownFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Parameters getDefaultParameters() {
        Parameters parameters = new Parameters();
        parameters.reset();
        return parameters;
    }
    
    // translateUrl
    public boolean getTranslateUrls() {
        return getParameters().getTranslateUrls();
    }

    public void setTranslateUrls(boolean translateUrls) {
        Parameters params = getParameters();
        params.setTranslateUrls(translateUrls);
        setParameters(params);
    }
    
    // translateCodeBlocks
    public boolean getTranslateCodeBlocks() {
        return getParameters().getTranslateCodeBlocks();
    }

    public void setTranslateCodeBlockse(boolean translateCodeBlocks) {
        Parameters params = getParameters();
        params.setTranslateCodeBlocks(translateCodeBlocks);
        setParameters(params);
    }

    // translateHeaderMetadata
    public boolean getTranslateHeaderMetadata() {
        return getParameters().getTranslateHeaderMetadata();
    }

    public void setTranslateHeaderMetadata(boolean translateHeaderMetadata) {
        Parameters params = getParameters();
        params.setTranslateHeaderMetadata(translateHeaderMetadata);
        setParameters(params);
    }
    
    // translateImageAltText
    public boolean getTranslateImageAltText() {
        return getParameters().getTranslateImageAltText();
    }

    public void setTranslateImageAltText(boolean translateImageAltText) {
        Parameters params = getParameters();
        params.setTranslateImageAltText(translateImageAltText);
        setParameters(params);
    }
    
    public String getHtmlSubfilter() {
	return getParameters().getHtmlSubfilter();
    }
    
    public void setHtmlSubfilter(String htmlSubfilterId) {
        Parameters params = getParameters();
        params.setHtmlSubfilter(htmlSubfilterId);
        setParameters(params);
    }
    
    // useCodeFinder
    public boolean getUseCodeFinder() {
	return getParameters().getUseCodeFinder();
    }
    
    public void setUseCodeFinder(boolean useCodeFinder) {
        Parameters params = getParameters();
        params.setUseCodeFinder(useCodeFinder);
        setParameters(params);
    }
    
    // codeFinderRules
    public List<String> getCodeFinderRules() {
	return getParameters().getCodeFinder().getRules();
    }
    
    public void setCodeFinderRules(List<String> regexes) {
        Parameters params = getParameters();
        InlineCodeFinder cf = params.getCodeFinder();
        cf.reset();
        for (String regex: regexes) {
            cf.addRule(regex);
        }
        setParameters(params);
    }
    
    
    @Override
    protected void saveAdditionalConfiguration(Document doc, Node parent) {
        Node rules = parent.appendChild(doc.createElement("codeFinderRules"));
        for (String rule : getCodeFinderRules()) {
            Node ruleNode = rules.appendChild(doc.createElement("rule"));
            if (rule != null) {
                Text t = doc.createTextNode(rule);
                ruleNode.appendChild(t);
            }
        }
    }

    @Override
    protected void loadAdditionalConfiguration(Node configNode) {
        if (configNode.getNodeName().equals("codeFinderRules")) {
            List<String> rules = new ArrayList<String>();
            NodeList nl = ((Element)configNode).getElementsByTagName("rule");
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                rules.add(n.getTextContent().trim());
            }
            setCodeFinderRules(rules);
        }
    }
    
    
}
