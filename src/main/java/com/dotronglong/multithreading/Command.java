package com.dotronglong.multithreading;

import com.dotronglong.multithreading.util.StreamProcessor;

public class Command {
    public String cmd;

    Command(String cmd) {
        this.cmd = cmd;
    }

    public void run() {
        executeCommand();
    }

    private void executeCommand() {
        try {
            Process p = Runtime.getRuntime().exec(this.cmd);

            // any error message?
            StreamProcessor errorStream = new
                    StreamProcessor(p.getErrorStream(), "ERROR");

            // any output?
            StreamProcessor outputStream = new
                    StreamProcessor(p.getInputStream(), "OUTPUT");

            // kick them off
            errorStream.start();
            outputStream.start();

//            System.exit(p.waitFor());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
