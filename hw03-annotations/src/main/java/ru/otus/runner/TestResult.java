package ru.otus.runner;

public class TestResult {
    int totalTests = 0;
    int passedTests = 0;
    int failedTests = 0;

    void recordPass() {
        totalTests++;
        passedTests++;
    }

    void recordFail() {
        totalTests++;
        failedTests++;
    }
}
