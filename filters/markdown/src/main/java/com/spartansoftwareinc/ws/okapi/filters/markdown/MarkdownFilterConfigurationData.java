package com.spartansoftwareinc.ws.okapi.filters.markdown;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.idiominc.wssdk.WSContext;
import com.idiominc.wssdk.ais.WSAisException;
import com.idiominc.wssdk.ais.WSAisManager;
import com.idiominc.wssdk.ais.WSNode;
import com.spartansoftwareinc.ws.okapi.filters.WSOkapiFilterConfigurationData;

import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.filters.markdown.Parameters;

public class MarkdownFilterConfigurationData extends WSOkapiFilterConfigurationData<Parameters> {
    /**
     * The path of the folder in the AIS where okf_html@<i>some_name</i>.fprm files
     * should be uploaded.
     */
    public static final String FILTER_CONFIG_DIR_AIS_PATH = "/Customization/okapi_subfilter"; 
    
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownFilterConfigurationData.class);
    private static final long serialVersionUID = 1L;
    
    /* These are not real configuration items. They are here so that the information can be passed
     * between the components.
     */
    private String filterConfigDirPath = null; // The file system path of the AIS path FILTER_CONFIG_DIR_AIS_PATH
    private List<String> availableConfigs = new ArrayList<>(); // Available custom HTML filter IDs.

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
    
    // urlToTranslatePattern
    public String getUrlToTranslatePattern() {
	return getParameters().getUrlToTranslatePattern();
    }
    
    public void setUrlToTranslatePattern(String urlToTranslatePattern) {
        Parameters params = getParameters();
        params.setUrlToTranslatePattern(urlToTranslatePattern);
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
    
    // htmlSubfilter
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
    
    
    // filterConfigDirPath --- not part of Okapi Markdown Filter
    /**
     * Sets the directory where the custom HTML configuration files (okf_html@<i>name</i>.fprm) should be saved.
     * Call {@link #initializeFilterConfigDirPath(WSContext)} once, before calling this.
     * @return
     */
    public String getFilterConfigDirPath() {
	return filterConfigDirPath;
    }
    
    // For unit test only.
    /*package*/void setFilterConfigDirPath(String filterConfigDirPath) {
	this.filterConfigDirPath = filterConfigDirPath;
    }
    
    public void initializeFilterConfigDirPath(WSContext context) {
	LOG.debug("Entering MarkdownFilterConfigurationData({}).initializeConfigDir(WSContext)", ((Object)this).toString());
	LOG.debug("filterConfigDirPath={}", filterConfigDirPath);
	if (filterConfigDirPath != null)
	    return; // It's been done already.
	File configDir = null;
	WSAisManager aismgr = context.getAisManager();
	if (aismgr == null)
	    return; // When running a unit test, this happens.
	try {

	    WSNode configDirNode = aismgr.getNode(FILTER_CONFIG_DIR_AIS_PATH);
	    if (configDirNode == null) {
		LOG.warn("The AIS node {} doesn't exist!", FILTER_CONFIG_DIR_AIS_PATH);
	    } else {
		configDir = configDirNode.getFile();
		if (!configDir.isDirectory() || configDir.listFiles(htmlFprmFilter).length < 1) {
		    configDir = null;
		} else {
		    LOG.debug("Available .fprm files...");
		    for (File f : configDir.listFiles(htmlFprmFilter)) {
			LOG.debug("   {}", f.getName());
			availableConfigs.add(f.getName().split("\\.")[0]); // The part of the file before "." is the id.
		    }
		}
	    }
	} catch (WSAisException e) {
	    LOG.error("Error accessing Okapi filter configuration directory.", e);
	}
	filterConfigDirPath = (configDir == null) ? null : configDir.getAbsolutePath();
    }

    /**
     * Returns the list of available HTML configuration IDs.
     * Note, {@link #initializeFilterConfigDirPath(WSContext)} must be called once before this can return the configuration list.
     * @return a list of available configuration IDs
     */
    public List<String> getAvailableConfigs() {
	return availableConfigs;
    }
    
    private static final Pattern htmlFprmPat = Pattern.compile("okf_html@[\\w\\d]+\\.fprm");
    private static final FilenameFilter htmlFprmFilter = new FilenameFilter() {
	public boolean accept(File dir, String name) {
	    return htmlFprmPat.matcher(name).matches();
	}
    };

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
        Node fcdp = parent.appendChild(doc.createElement("filterConfigDirPath"));
        if (filterConfigDirPath!=null) {
            Text t = doc.createTextNode(filterConfigDirPath);
            fcdp.appendChild(t);
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
        } else if (configNode.getNodeName().equals("filterConfigDirPath")) {
            String s = configNode.getTextContent();
            if (s==null || s.trim().isEmpty()) {
        	filterConfigDirPath = null;
            } else {
        	filterConfigDirPath = s.trim();
            }
        }       
    } 
 }
