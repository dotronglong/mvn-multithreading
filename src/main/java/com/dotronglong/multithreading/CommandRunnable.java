package com.dotronglong.multithreading;

import java.util.Random;

public class CommandRunnable implements Runnable {
    private Command command;
    private boolean randomSleep = false;
    private int sleepIn = 0;
    public static final int SLEEP_MAX = 10;

    CommandRunnable(String cmd) {
        this.command = new Command(cmd);
    }

    CommandRunnable(String cmd, boolean randomSleep) {
        this.command = new Command(cmd);
        this.randomSleep = randomSleep;
    }

    CommandRunnable(String cmd, int sleepIn) {
        this.command = new Command(cmd);
        this.sleepIn = sleepIn;
    }

    public Command getCommand() {
        return command;
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
        int sleepInMiliSeconds = sleepIn * 1000;
        try {
            System.out.println(String.format("→ [%s] Sleep in %s seconds", this.command.cmd, sleepIn));
            Thread.sleep(sleepInMiliSeconds);
            System.out.println(String.format("→ Wake up after %s seconds", sleepIn));
            this.command.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runInNormalMode() {
        this.command.run();
    }
}
