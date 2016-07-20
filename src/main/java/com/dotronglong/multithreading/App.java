package com.dotronglong.multithreading;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class App {
    public static final String TASK_XML_FILE = "tasks.xml";
    public static final String TASK_TEXT_FILE = "tasks.txt";

    public static final String XML_NODE_COMMAND = "command";
    public static final String XML_NODE_EXEC = "exec";

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        System.out.println("Look for tasks...");
        String[] tasks = this.readForTasks();

        System.out.println("====> Found " + tasks.length + " tasks.");
        this.showTasks(tasks);

        System.out.println("====> Running...");
        for (String task : tasks) {
            this.runTask(task);
        }
    }

    private String[] readForTasks() {
        return readXmlTasks();
    }

    private String[] readTextTasks() {
        String tasks[] = new String[0];
        Path path = FileSystems.getDefault().getPath(TASK_TEXT_FILE);
        try {
            Stream<String> stream = Files.lines(path);
            Object[] lines = stream.toArray();
            stream.close();

            tasks = new String[lines.length];
            for (int i = 0; i < lines.length; i++) {
                tasks[i] = String.valueOf(lines[i]);
            }
        } catch (IOException e) {
        }

        return tasks;
    }

    private String[] readXmlTasks() {
        XmlReader xml = new XmlReader(TASK_XML_FILE);
        Document doc = xml.getDocument();
        NodeList nodes = doc.getElementsByTagName(XML_NODE_COMMAND);
        String[] tasks = new String[nodes.getLength()];

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                tasks[i] = element.getElementsByTagName(XML_NODE_EXEC).item(0).getTextContent();
            }
        }
        return tasks;
    }

    private void showTasks(String[] tasks) {
        for (String task : tasks) {
            System.out.println(task);
        }
    }

    private void runTask(String task) {
        Thread t = new Thread(new CommandRunnable(task));
        t.start();
    }
}
