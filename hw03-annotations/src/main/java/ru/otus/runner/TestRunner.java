package ru.otus.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestRunner {

    private TestRunner() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static void runTests(String className) {
        logger.info("Starting test execution for class: {}", className);
        TestResult result = new TestResult();

        try {
            Class<?> testClass = Class.forName(className);

            List<Method> beforeMethods = findAnnotatedMethods(testClass, Before.class);
            List<Method> testMethods = findAnnotatedMethods(testClass, Test.class);
            List<Method> afterMethods = findAnnotatedMethods(testClass, After.class);

            if (testMethods.isEmpty()) {
                logger.warn("No @Test methods found in class: {}", className);
                printStatistics(result);
                return;
            }

            for (Method testMethod : testMethods) {
                executeSingleTestCycle(testClass, testMethod, beforeMethods, afterMethods, result);
            }

        } catch (ClassNotFoundException e) {
            logger.error("Test class not found: {}", className, e);
            result.failedTests = -1;
        } catch (Exception e) {
            logger.error(
                    "An unexpected error occurred during test execution setup for class {}: {}",
                    className,
                    e.getMessage(),
                    e);
            result.failedTests = -1;
        } finally {
            printStatistics(result);
        }
    }

    private static List<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> annotatedMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    private static Object createTestInstance(Class<?> testClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = testClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static void invokeMethods(Object instance, List<Method> methods, String phase) {
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (InvocationTargetException e) {
                logger.error(
                        "Exception in {} method '{}' on instance {}: {}",
                        phase,
                        method.getName(),
                        instance.hashCode(),
                        e.getTargetException().getMessage(),
                        e.getTargetException());
                throw new RuntimeException(
                        "Failure in " + phase + " method: " + method.getName(), e.getTargetException());
            } catch (IllegalAccessException e) {
                logger.error(
                        "Illegal access to {} method '{}' on instance {}: {}",
                        phase,
                        method.getName(),
                        instance.hashCode(),
                        e.getMessage(),
                        e);
                throw new RuntimeException("Illegal access in " + phase + " method: " + method.getName(), e);
            }
        }
    }

    private static void executeSingleTestCycle(
            Class<?> testClass,
            Method testMethod,
            List<Method> beforeMethods,
            List<Method> afterMethods,
            TestResult result) {
        Object testInstance = null;
        boolean testPassed = true;
        logger.info("Starting test: {}.{}", testClass.getSimpleName(), testMethod.getName());

        try {
            testInstance = createTestInstance(testClass);
            invokeMethods(testInstance, beforeMethods, "@Before");

            try {
                testMethod.setAccessible(true);
                testMethod.invoke(testInstance);
                logger.info("Test '{}' PASSED", testMethod.getName());
            } catch (InvocationTargetException e) {
                testPassed = false;
                logger.error(
                        "Test '{}' FAILED: {}",
                        testMethod.getName(),
                        e.getTargetException().getMessage(),
                        e.getTargetException());
            } catch (IllegalAccessException e) {
                testPassed = false;
                logger.error("Test '{}' FAILED due to illegal access: {}", testMethod.getName(), e.getMessage(), e);
            } catch (Throwable t) {
                testPassed = false;
                logger.error("Test '{}' FAILED with unexpected error: {}", testMethod.getName(), t.getMessage(), t);
            }

        } catch (Exception e) {
            testPassed = false;
            logger.error(
                    "Error during test setup or @Before execution for {}.{}: {}",
                    testClass.getSimpleName(),
                    testMethod.getName(),
                    e.getMessage(),
                    e);
        } finally {
            if (testInstance != null) {
                try {
                    invokeMethods(testInstance, afterMethods, "@After");
                } catch (Exception e) {
                    logger.error(
                            "Exception in @After method for test {}.{} (instance {}): {}",
                            testClass.getSimpleName(),
                            testMethod.getName(),
                            testInstance.hashCode(),
                            e.getMessage(),
                            e);
                }
            }

            if (testPassed) {
                result.recordPass();
            } else {
                result.recordFail();
            }
            logger.info("Finished test: {}.{}", testClass.getSimpleName(), testMethod.getName());
        }
    }

    private static void printStatistics(TestResult result) {
        logger.info("Test Execution Summary:");
        if (result.failedTests == -1 && result.totalTests == 0) {
            logger.info("  Could not run tests. Please check previous errors.");
        } else {
            logger.info("  Total tests run: {}", result.totalTests);
            logger.info("  Tests passed: {}", result.passedTests);
            logger.info("  Tests failed: {}", result.failedTests);
        }
    }
}
