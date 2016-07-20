package com.dotronglong.multithreading.cli;
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

import com.dotronglong.multithreading.util.StreamProcessor;

import java.util.Random;

/**
 * CommandRunnable
 * <p>
 * Run command in separate thread with error stream controlling
 *
 * @author Long Do
 * @version 1.0.0
 * @since Jul 21, 2016
 */
public class CommandRunnable implements Runnable {
    public final static String TYPE_ERROR  = "ERROR";
    public final static String TYPE_OUTPUT = "OUTPUT";

    public final static String BASH_SHELL   = "/bin/sh";
    public final static String BASH_COMMAND = "-c";

    /**
     * Maximum time of random sleep (seconds)
     */
    public static final int SLEEP_MAX = 10;

    /**
     * Process exit code
     */
    private int exitCode;

    /**
     * Determine to use random sleep mode
     */
    private boolean randomSleep = false;

    /**
     * Predefined time of sleep (seconds)
     */
    private int sleepIn = 0;

    /**
     * Command string
     */
    private String cmd;

    public CommandRunnable(String cmd) {
        this.cmd = cmd;
    }

    public CommandRunnable(String cmd, boolean randomSleep) {
        this(cmd);
        this.randomSleep = randomSleep;
    }

    public CommandRunnable(String cmd, int sleepIn) {
        this(cmd);
        this.sleepIn = sleepIn;
    }

    public String getCmd() {
        return cmd;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void run() {
        if (randomSleep) {
            runInRandomSleepMode();
        } else if (sleepIn > 0) {
            runInSleepMode(sleepIn);
        } else {
            runInNormalMode();
        }
    }

    private void runInRandomSleepMode() {
        Random random = new Random();
        int sleepIn = random.nextInt(SLEEP_MAX);
        runInSleepMode(sleepIn);
    }

    private void runInSleepMode(int sleepIn) {
        try {
            System.out.println(String.format("→ [%s] Sleep in %s seconds", cmd, sleepIn));
            Thread.sleep(sleepIn * 1000);
            System.out.println(String.format("→ Wake up after %s seconds", sleepIn));
            executeCommand();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runInNormalMode() {
        executeCommand();
    }

    protected int executeCommand() {
        try {
            String[] cmd = {BASH_SHELL, BASH_COMMAND, this.cmd};
            Process p = Runtime.getRuntime().exec(cmd);

            // streaming for error
            StreamProcessor errorStream = new
                    StreamProcessor(p.getErrorStream(), TYPE_ERROR);

            // streaming for output
            StreamProcessor outputStream = new
                    StreamProcessor(p.getInputStream(), TYPE_OUTPUT);

            // let's start
            errorStream.start();
            outputStream.start();

            // wait for exit code
            exitCode = p.waitFor();
        } catch (Throwable t) {
            t.printStackTrace();
            exitCode = 1;
        }

        return exitCode;
    }
}
