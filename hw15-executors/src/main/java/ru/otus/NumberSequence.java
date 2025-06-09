package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberSequence {

    private static final Logger logger = LoggerFactory.getLogger(NumberSequence.class);

    private String lastThread = "Thread-2";
    private final Object monitor = new Object();

    public static void main(String[] args) {
        NumberSequence numberSequence = new NumberSequence();

        Thread t1 = new Thread(() -> numberSequence.printNumbers("Thread-1"));
        Thread t2 = new Thread(() -> numberSequence.printNumbers("Thread-2"));

        t1.start();
        t2.start();
    }

    public void printNumbers(String threadName) {
        int number = 1;
        int direction = 1;

        while (!Thread.currentThread().isInterrupted()) {
            synchronized (monitor) {
                try {
                    while (lastThread.equals(threadName)) {
                        monitor.wait();
                    }

                    logger.info("{}: {}", threadName, number);

                    if (number == 10) {
                        direction = -1;
                    } else if (number == 1) {
                        direction = 1;
                    }
                    number += direction;

                    lastThread = threadName;

                    sleep();

                    monitor.notifyAll();

                } catch (InterruptedException e) {
                    logger.error("Thread was interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
