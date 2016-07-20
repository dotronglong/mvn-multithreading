package com.dotronglong.multithreading;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class App {
    public static final String TASK_XML_FILE = "tasks.xml";
    public static final String TASK_TEXT_FILE = "tasks.txt";

    public static final String XML_NODE_COMMAND = "command";
    public static final String XML_NODE_EXEC = "exec";

    private ArrayList<Thread> threads;
    private ArrayList<CommandRunnable> runnables;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        String[] tasks = this.readForTasks();

        System.out.println("→ Found " + tasks.length + " tasks.");
        this.showTasks(tasks);

        System.out.println("→ Run tasks ...");
        threads = new ArrayList<Thread>();
        runnables = new ArrayList<CommandRunnable>();
        for (String task : tasks) {
            CommandRunnable runnable = new CommandRunnable(task);
            Thread thread = new Thread(runnable);
            threads.add(thread);
            runnables.add(runnable);
        }
        runTasks();
        waitTasks();
        closeTasks();
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

    private void runTasks() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private void waitTasks() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeTasks() {
        for (CommandRunnable runnable : runnables) {
            if (runnable.getCommand().getExitCode() != 0) {
                System.exit(1);
            }
        }
    }
}
