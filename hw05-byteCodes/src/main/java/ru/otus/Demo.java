package ru.otus;

import ru.otus.impl.TestLogging;
import ru.otus.interfaces.TestLoggingInterface;
import ru.otus.proxy.Ioc;

public class Demo {
    public static void main(String[] args) {
        System.out.println("Starting Demo application with Dynamic Proxies and SLF4J logging...");
        System.out.println("--- Creating TestLogging instance ---");

        TestLoggingInterface myClassImpl = new TestLogging();

        TestLoggingInterface myClass = Ioc.createLoggedInstance(myClassImpl, TestLoggingInterface.class);

        System.out.println("\n--- Calling calculation(int) ---");
        myClass.calculation(6);

        System.out.println("\n--- Calling calculation(int, int) ---");
        myClass.calculation(10, 20);

        System.out.println("\n--- Calling calculation(int, int, String) ---");
        myClass.calculation(30, 40, "fifty");

        System.out.println("\n--- Calling withoutLog() ---");
        myClass.withoutLog();

        System.out.println("\nDemo application finished.");
    }
}
