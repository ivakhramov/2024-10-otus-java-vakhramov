package ru.otus.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestClass {

    private static final Logger logger = LoggerFactory.getLogger(TestClass.class);

    public TestClass() {
        logger.info("Constructor TestClass(): New instance created hashcode: {}", this.hashCode());
    }

    @Before
    public void setUp1() {
        logger.info("setUp1(): Executing @Before method. Instance: {}", this.hashCode());
    }

    @Before
    public void setUp2() {
        logger.info("setUp2(): Executing another @Before method. Instance: {}", this.hashCode());
    }

    @Test
    public void firstTest() {
        logger.info("firstTest(): Executing @Test method. Instance: {}", this.hashCode());
        if (1 + 1 != 2) { // Simple assertion
            throw new AssertionError("1 + 1 should be 2");
        }
    }

    @Test
    public void secondTest() {
        logger.info("secondTest(): Executing @Test method that will fail. Instance: {}", this.hashCode());
        throw new RuntimeException("This test is designed to fail");
    }

    @Test
    public void thirdTest() {
        logger.info("thirdTest(): Executing another successful @Test method. Instance: {}", this.hashCode());
        // No assertions, implicitly passes if no exception
    }

    @After
    public void tearDown1() {
        logger.info("tearDown1(): Executing @After method. Instance: {}", this.hashCode());
    }

    @After
    public void tearDown2() {
        logger.info("tearDown2(): Executing another @After method. Instance: {}", this.hashCode());
    }
}
