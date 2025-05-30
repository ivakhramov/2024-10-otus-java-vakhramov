package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.runner.TestRunner;

public class StartApp {

    private static final Logger logger = LoggerFactory.getLogger(StartApp.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.warn("No test class specified as a command-line argument.");
            logger.info("Usage: java ru.otus.StartApp <fully.qualified.TestClassName>");
            logger.info("Running default example: ru.otus.tests.TestClass");
            TestRunner.runTests("ru.otus.tests.TestClass");
            return;
        }

        String testClassName = args[0];
        logger.info("Attempting to run tests for class: {}", testClassName);
        TestRunner.runTests(testClassName);
    }
}
