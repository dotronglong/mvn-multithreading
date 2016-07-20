package com.dotronglong.multithreading.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlParser {
    private File file;
    private DocumentBuilderFactory documentFactory;
    private DocumentBuilder documentBuilder;
    private Document document;

    public XmlParser() {

    }

    public XmlParser(String path) {
        file = new File(path);
    }

    public Document parse() throws IOException, SAXException, ParserConfigurationException {
        documentFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentFactory.newDocumentBuilder();
        document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
        return document;
    }

    public Document parse(String path) throws ParserConfigurationException, SAXException, IOException {
        file = new File(path);
        return parse();
    }
}