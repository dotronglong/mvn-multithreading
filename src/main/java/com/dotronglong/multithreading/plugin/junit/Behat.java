package com.dotronglong.multithreading.plugin.junit;
/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Do Trong Long
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.dotronglong.multithreading.plugin.BasePlugin;
import com.dotronglong.multithreading.util.XmlParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Behat
 * <p>
 * Collect jUnit reports and join into one report
 *
 * @author Long Do
 * @version 1.0.0
 * @since Jul 21, 2016
 */
public class Behat extends BasePlugin {
    private final static String XML_NODE_ATTR_OUTPUT = "output";
    private final static String XML_NODE_FILE = "file";
    private final static String XML_NODE_TESTSUITES = "testSuites";
    private final static String XML_NODE_TESTSUITE = "testSuite";
    
    private final static String PLUGIN_NAME = "junit.behat";

    /* Path of file to output result */
    private String outputPath = "";

    /* List of xml file paths */
    private ArrayList<String> files = new ArrayList<String>();

    /* XML documents of all log files */
    private ArrayList<Document> logs = new ArrayList<Document>();

    private Document out;
    private Element testSuites;

    public String getName() {
        return PLUGIN_NAME;
    }

    public void run() {
        loadPluginContent();
        initDocument();
        generateContent();

        try {
            writeDocument();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void loadPluginContent() {
        outputPath = element.getAttribute(XML_NODE_ATTR_OUTPUT);
        NodeList files = element.getElementsByTagName(XML_NODE_FILE);
        String path;
        for (int i = 0; i < files.getLength(); i++) {
            Element file = (Element) files.item(i);
            path = file.getTextContent();
            this.files.add(path);

            XmlParser xml = new XmlParser(path);
            try {
                System.out.println(String.format("→ [%s] Found file %s", PLUGIN_NAME, path));
                logs.add(xml.parse());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            out = docBuilder.newDocument();
            testSuites = out.createElement(XML_NODE_TESTSUITES);
            out.appendChild(testSuites);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void generateContent() {
        for (Document doc : logs) {
            NodeList nodes = doc.getElementsByTagName(XML_NODE_TESTSUITE);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node testSuite = nodes.item(i);
                testSuites.appendChild(out.importNode(testSuite, true));
            }
        }
    }

    private void writeDocument() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(out);
        StreamResult result = new StreamResult(new File(outputPath));
        transformer.transform(source, result);
        System.out.println(String.format("→ [%s] Wrote to file %s", PLUGIN_NAME, outputPath));
    }
}
