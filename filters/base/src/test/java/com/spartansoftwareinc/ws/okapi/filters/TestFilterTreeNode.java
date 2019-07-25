package com.spartansoftwareinc.ws.okapi.filters;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import com.spartansoftwareinc.ws.okapi.filters.model.FilterTreeNode;
import com.spartansoftwareinc.ws.okapi.filters.model.FilterTreeRawDocumentNode;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.json.JSONFilter;
import net.sf.okapi.filters.xml.XMLFilter;

public class TestFilterTreeNode {


    @Test
    public void testBuild() throws Exception {

        OkapiMultiFilterBridge filterBridge = new OkapiMultiFilterBridge();
        RawDocument srcRawDocument = rawDocumentFromTestFile("json_mixed_input.xml", LocaleId.ENGLISH, LocaleId.ENGLISH);

        IFilter xmlFilter = getXMLFIlter("xml_filter_params.xml");
        IFilter jsonFilter = getJSONFilter();
        IFilter htmlFilter = getHTMLFilter("html_filter_params.yml");

        // Layer 1
        FilterTreeRawDocumentNode root = new FilterTreeRawDocumentNode(srcRawDocument);
        root.applyFilterAndCreateChildren(filterBridge, xmlFilter);

        // Layer 2
        List<FilterTreeNode> secondLayer = root.getLeaves();
        for (FilterTreeNode node : secondLayer) {
            node.applyFilterAndCreateChildren(filterBridge, jsonFilter);
        }

        // Layer 3
        List<FilterTreeNode> thirdLayer = root.getLeaves();
        for (FilterTreeNode node : thirdLayer) {
            node.applyFilterAndCreateChildren(filterBridge, htmlFilter);
        }

        // Update the tree and output the data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        root.setTranslationOutput(byteArrayOutputStream);
        for (FilterTreeNode node : root.postOrderTraversal()) {
            if (!node.isLeaf()) {
                node.updateWithChildrensTranslations(filterBridge);
            }
        }

        root.closeRawDocument();

        String result = byteArrayOutputStream.toString().trim();
        InputStream originalFileInputStream = loadFromResources("json_mixed_input.xml");
        String originalFile = getStringFromInputStream(originalFileInputStream);
        assertEquals(originalFile.trim(), result.trim());


    }

    private RawDocument rawDocumentFromTestFile(String filename, LocaleId source, LocaleId target) throws FileNotFoundException, URISyntaxException {
        InputStream is = loadFromResources(filename);
        return new RawDocument(is, "UTF-8", source, target);

    }


    private InputStream loadFromResources(String fileName) throws FileNotFoundException {
        InputStream resource = getClass().getResourceAsStream(fileName);
        if (resource == null) {
            throw new FileNotFoundException("Unable to load Resource " + fileName
                    + " stored in package resources.");
        }
        return resource;
    }

    private IFilter getXMLFIlter(String paramsFile) throws IOException {
        XMLFilter xmlFilter = new XMLFilter();
        net.sf.okapi.filters.its.Parameters parameters = xmlFilter.getParameters();
        InputStream originalFileInputStream = loadFromResources(paramsFile);
        String xmlParams = getStringFromInputStream(originalFileInputStream);
        parameters.fromString(xmlParams);
        return xmlFilter;
    }

    private IFilter getJSONFilter() {
        JSONFilter jsonFilter = new JSONFilter();
        net.sf.okapi.filters.json.Parameters parameters = jsonFilter.getParameters();
        parameters.setExtractStandalone(true);
        return jsonFilter;
    }

    private IFilter getHTMLFilter(String paramsFile) throws IOException {
        HtmlFilter htmlFilter = new HtmlFilter();
        IParameters parameters = htmlFilter.getParameters();
        InputStream originalFileInputStream = loadFromResources(paramsFile);
        String htmlParams = getStringFromInputStream(originalFileInputStream);
        parameters.fromString(htmlParams);
        return htmlFilter;
    }

    private String getStringFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }


}
