package com.dotronglong.multithreading;

import com.dotronglong.multithreading.util.StreamProcessor;

public class Command {
    public final static String TYPE_ERROR  = "ERROR";
    public final static String TYPE_OUTPUT = "OUTPUT";

    public final static String BASH_SHELL   = "/bin/sh";
    public final static String BASH_COMMAND = "-c";

    protected String cmd;
    private int exitCode = 0;

    Command(String cmd) {
        this.cmd = cmd;
    }

    public int run() {
        return executeCommand();
    }

    public int getExitCode() {
        return exitCode;
    }

    private int executeCommand() {
        try {
            String[] cmd = {BASH_SHELL, BASH_COMMAND, this.cmd};
            Process p = Runtime.getRuntime().exec(cmd);

            // any error message?
            StreamProcessor errorStream = new
                    StreamProcessor(p.getErrorStream(), TYPE_ERROR);

            // any output?
            StreamProcessor outputStream = new
                    StreamProcessor(p.getInputStream(), TYPE_OUTPUT);

            // kick them off
            errorStream.start();
            outputStream.start();

            exitCode = p.waitFor();
        } catch (Throwable t) {
            t.printStackTrace();
            exitCode = 1;
        }

        return exitCode;
    }
}
