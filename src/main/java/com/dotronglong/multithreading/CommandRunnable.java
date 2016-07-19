package com.dotronglong.multithreading;

public class CommandRunnable implements Runnable {
    private Command command;
    private Thread t;

    CommandRunnable(String cmd) {
        this.command = new Command(cmd);
    }

    public void run() {
        int sleepIn = (int) (Math.random() * 10000);
        int sleepInSeconds = sleepIn / 1000;
        try {
            System.out.println(String.format("→ [%s] Sleep in %s seconds", this.command.cmd, sleepInSeconds));
            Thread.sleep(sleepIn);
            System.out.println("→ Wake up after " + (sleepIn / 1000) + " seconds");
            this.command.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
