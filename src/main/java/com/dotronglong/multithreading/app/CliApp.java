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
import com.dotronglong.multithreading.plugin.PluginAware;
import com.dotronglong.multithreading.plugin.junit.Behat;
import org.w3c.dom.Element;
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
public class CliApp extends BaseApp {
    private final static String XML_NODE_BASH = "sh";
    private final static String XML_NODE_SLEEP = "sleep";
    private final static String XML_NODE_COMMAND = "command";
    private final static String XML_NODE_COMMAND_EXEC = "exec";
    private final static String XML_NODE_ATTR_NAME = "name";
    private final static String XML_NODE_ATTR_BASH_COMMAND = "command";
    private final static String XML_NODE_ATTR_RANDOM_SLEEP = "randomSleep";
    private final static String XML_NODE_ATTR_SLEEPIN = "sleepIn";
    private final static String XML_NODE_ATTR_SLEEPMAX = "sleepMax";
    private final static String XML_BOOLEAN_TRUE = "true";
    private final static String XML_BOOLEAN_FALSE = "false";

    /* List of commands read from Document */
    private ArrayList<Command> commands = new ArrayList<Command>();

    private ArrayList<Thread> threads = new ArrayList<Thread>();
    private ArrayList<CommandRunnable> runnables = new ArrayList<CommandRunnable>();
    private ArrayList<PluginAware> plugins = new ArrayList<PluginAware>();

    private Config config = new Config();

    class Command {
        public String exec;
    }

    class Config {
        public String bashShell = null;
        public String bashCommand = null;
        public boolean randomSleep = false;
        public int sleepIn = 0;
        public int sleepMax = CommandRunnable.SLEEP_MAX;
    }

    private void loadPlugins() {
        plugins.add(new Behat());
    }

    private void loadConfig() {
        Element elementConfig = (Element) document.getElementsByTagName(XML_NODE_CONFIG).item(0);
        NodeList bashShellNodes = elementConfig.getElementsByTagName(XML_NODE_BASH);
        if (bashShellNodes.getLength() > 0) {
            Element bashElement = (Element) bashShellNodes.item(0);
            config.bashShell = bashElement.getTextContent();
            if (bashElement.hasAttribute(XML_NODE_ATTR_BASH_COMMAND)) {
                config.bashCommand = bashElement.getAttribute(XML_NODE_ATTR_BASH_COMMAND);
            }
        }

        NodeList sleepNodes = elementConfig.getElementsByTagName(XML_NODE_SLEEP);
        if (sleepNodes.getLength() > 0) {
            Element sleepElement = (Element) sleepNodes.item(0);
            if (sleepElement.hasAttribute(XML_NODE_ATTR_RANDOM_SLEEP)) {
                config.randomSleep = sleepElement.getAttribute(XML_NODE_ATTR_RANDOM_SLEEP).equals(XML_BOOLEAN_TRUE) ? true : false;
            }
            if (sleepElement.hasAttribute(XML_NODE_ATTR_SLEEPIN)) {
                config.sleepIn = Integer.parseInt(sleepElement.getAttribute(XML_NODE_ATTR_SLEEPIN));
            }
            if (sleepElement.hasAttribute(XML_NODE_ATTR_SLEEPMAX)) {
                config.sleepMax = Integer.parseInt(sleepElement.getAttribute(XML_NODE_ATTR_SLEEPMAX));
            }
        }
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

    public String getName() {
        return "cli";
    }

    public void run(String[] args) {
        doInit();
        doShow();
        doStart();
        doWait();
        doClose();
        doShutdown();
    }

    private void doInit() {
        loadConfig();
        loadPlugins();
        loadContent();

        if (config.bashShell != null) {
            CommandRunnable.BASH_SHELL = config.bashShell;
        }
        if (config.bashCommand != null) {
            CommandRunnable.BASH_COMMAND = config.bashCommand;
        }
        CommandRunnable.SLEEP_MAX = config.sleepMax;
        for (Command command : commands) {
            CommandRunnable runnable;
            if (config.randomSleep == true) {
                runnable = new CommandRunnable(command.exec, true);
            } else {
                runnable = new CommandRunnable(command.exec, config.sleepIn);
            }
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
        Element elementPlugins = (Element) document.getElementsByTagName(XML_NODE_PLUGINS).item(0);
        NodeList nodePlugins = elementPlugins.getElementsByTagName(XML_NODE_PLUGIN);
        for (int i = 0; i < nodePlugins.getLength(); i++) {
            Element elementPlugin = (Element) nodePlugins.item(i);
            String pluginName = elementPlugin.getAttribute(XML_NODE_ATTR_NAME);
            for (PluginAware plugin : plugins) {
                if (plugin.getName().equals(pluginName)) {
                    plugin.setApp(this);
                    plugin.setPluginElement(elementPlugin);
                    plugin.run();
                }
            }
        }
    }

    private void doShutdown() {
        for (CommandRunnable runnable : runnables) {
            if (runnable.getExitCode() != 0) {
                System.exit(1);
            }
        }
    }
}
