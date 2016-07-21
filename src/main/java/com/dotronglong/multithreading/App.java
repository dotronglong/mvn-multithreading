package com.dotronglong.multithreading;
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

import com.dotronglong.multithreading.app.AppAware;
import com.dotronglong.multithreading.app.CliApp;
import com.dotronglong.multithreading.util.XmlParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * App
 *
 * Control application
 *
 * @author Long Do
 * @version 1.0.0
 * @since Jul 21, 2016
 */
public class App {
    public static final String TASK_XML_FILE = "mit.xml";
    public static final String XML_NODE_CONFIG = "config";
    public static final String XML_NODE_NAME = "name";

    private static Document document;
    private static Config config = new Config();

    public static void main(String[] args) {
        loadDocument();
        loadConfig();

        ArrayList<AppAware> apps = new ArrayList<AppAware>();
        apps.add(new CliApp());

        for (AppAware app : apps) {
            if (app.getName().equals(config.appName)) {
                app.setDocument(document);
                app.run(args);
                break;
            }
        }
    }

    public static void loadDocument() {
        if (document == null) {
            XmlParser xml = new XmlParser();
            try {
                document = xml.parse(TASK_XML_FILE);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadConfig() {
        assert document != null;

        NodeList nodes = document.getElementsByTagName(XML_NODE_CONFIG);
        Element elementConfig = (Element) nodes.item(0);
        Node nodeName = elementConfig.getElementsByTagName(XML_NODE_NAME).item(0);
        config.appName = nodeName.getTextContent();
    }
}
