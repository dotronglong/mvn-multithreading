package com.dotronglong.multithreading.app;
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

import com.dotronglong.multithreading.cli.CommandRunnable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * CliApp
 * <p>
 * Class description
 *
 * @author Long Do
 * @version 1.0.0
 * @since Jul 21, 2016
 */
public class CliApp extends BaseApp implements AppAware {
    private final static String XML_NODE_COMMAND = "command";
    private final static String XML_NODE_COMMAND_EXEC = "exec";

    /* List of commands read from Document */
    private ArrayList<Command> commands = new ArrayList<Command>();

    private ArrayList<Thread> threads = new ArrayList<Thread>();
    private ArrayList<CommandRunnable> runnables = new ArrayList<CommandRunnable>();

    public CliApp() {
        name = "cli";
    }

    class Command {
        public String exec;
    }

    private void loadContent() {
        Element elementContent = (Element) document.getElementsByTagName(XML_NODE_CONTENT).item(0);
        NodeList nodeCommands = elementContent.getElementsByTagName(XML_NODE_COMMAND);
        for (int i = 0; i < nodeCommands.getLength(); i++) {
            Element elementCommand = (Element) nodeCommands.item(i);

            Command command = new Command();
            command.exec = elementCommand.getElementsByTagName(XML_NODE_COMMAND_EXEC).item(0).getTextContent();
            commands.add(command);
        }
    }

    public void run(String[] args) {
        doInit();
        doShow();
        doStart();
        doWait();
        doClose();
    }

    private void doInit() {
        loadContent();
        for (Command command : commands) {
            CommandRunnable runnable = new CommandRunnable(command.exec);
            Thread thread = new Thread(runnable);
            threads.add(thread);
            runnables.add(runnable);
        }
    }

    private void doShow() {
        System.out.println("→ Found " + commands.size() + " commands.");
        for (Command command : commands) {
            System.out.println(command.exec);
        }
    }

    private void doStart() {
        System.out.println("→ Run commands ...");
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private void doWait() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doClose() {
        for (CommandRunnable runnable : runnables) {
            if (runnable.getExitCode() != 0) {
                System.exit(1);
            }
        }
    }
}
