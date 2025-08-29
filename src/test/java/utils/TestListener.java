package utils;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestListener implements ITestListener, ISuiteListener {
    private static final String REPORT_FILE = "report.txt";
    private static PrintWriter reportWriter;
    private static List<String> failureDetails = new ArrayList<>();
    private static List<String> skippedElements = new ArrayList<>();
    private static double totalProductCost = 0.0;
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static int skippedTests = 0;

    @Override
    public void onStart(ISuite suite) {
        initializeReport();
        log("=".repeat(80));
        log("E-COMMERCE AUTOMATION TEST EXECUTION REPORT");
        log("=".repeat(80));
        log("Test Suite: " + suite.getName());
        log("Start Time: " + getCurrentTimestamp());
        log("Browser: " + ConfigReader.getBrowser());
        log("URL: " + ConfigReader.getUrl());
        log("=".repeat(80));
    }

    @Override
    public void onFinish(ISuite suite) {
        log("=".repeat(80));
        log("TEST EXECUTION SUMMARY");
        log("=".repeat(80));
        log("End Time: " + getCurrentTimestamp());
        log("Total Tests: " + totalTests);
        log("Passed: " + passedTests);
        log("Failed: " + failedTests);
        log("Skipped: " + skippedTests);
        log("Total Product Cost: $" + String.format("%.2f", totalProductCost));
        log("");

        if (!failureDetails.isEmpty()) {
            log("FAILURE DETAILS:");
            log("-".repeat(40));
            for (String failure : failureDetails) {
                log(failure);
            }
            log("");
        }

        if (!skippedElements.isEmpty()) {
            log("SKIPPED ELEMENTS:");
            log("-".repeat(40));
            for (String skipped : skippedElements) {
                log(skipped);
            }
            log("");
        }

        log("=".repeat(80));
        closeReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        totalTests++;
        log("\n[TEST START] " + result.getMethod().getMethodName());
        log("Description: " + result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        log("[PASS] " + result.getMethod().getMethodName() + " - Execution Time: " +
            (result.getEndMillis() - result.getStartMillis()) + "ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        String testName = result.getMethod().getMethodName();
        String errorMessage = (result.getThrowable() != null) ? result.getThrowable().getMessage() : "No message";

        log("[FAIL] " + testName);
        log("Error: " + errorMessage);

        try {
            String screenshotPath = ScreenshotUtil.captureFailureScreenshot(
                DriverFactory.getDriver(), testName);
            if (screenshotPath != null) {
                log("Screenshot saved: " + screenshotPath);
            }
        } catch (Exception e) {
            log("Failed to capture screenshot: " + e.getMessage());
        }

        failureDetails.add("Test: " + testName + " | Error: " + errorMessage);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        String reason = "No reason";
        if (result.getThrowable() != null && result.getThrowable().getMessage() != null) {
            reason = result.getThrowable().getMessage();
        }
        log("[SKIP] " + result.getMethod().getMethodName());
        log("Reason: " + reason);
    }

    public static void log(String message) {
        String timestamp = getCurrentTimestamp();
        String logEntry = "[" + timestamp + "] " + message;
        System.out.println(logEntry);

        if (reportWriter != null) {
            reportWriter.println(logEntry);
            reportWriter.flush();
        }
    }

    public static void logValidation(String validation) {
        log("[VALIDATION] " + validation);
    }

    public static void logProductInfo(String productName, String price, String url) {
        log("[PRODUCT] Name: " + productName + " | Price: " + price + " | URL: " + url);

        try {
            String priceValue = price.replaceAll("[^0-9.]", "");
            if (!priceValue.isEmpty()) {
                totalProductCost += Double.parseDouble(priceValue);
            }
        } catch (NumberFormatException e) {
            log("[WARNING] Could not parse price: " + price);
        }
    }

    public static void logSkippedElement(String elementType, String reason) {
        String skippedInfo = elementType + " - " + reason;
        log("[SKIPPED] " + skippedInfo);
        skippedElements.add(skippedInfo);
    }

    public static void logCartValidation(boolean isValid, String expectedTotal, String actualTotal) {
        if (isValid) {
            log("[CART VALIDATION] PASSED - Expected: " + expectedTotal + ", Actual: " + actualTotal);
        } else {
            log("[CART VALIDATION] FAILED - Expected: " + expectedTotal + ", Actual: " + actualTotal);
        }
    }

    private static void initializeReport() {
        try {
            reportWriter = new PrintWriter(new FileWriter(REPORT_FILE, false));
        } catch (IOException e) {
            System.err.println("Failed to initialize report file: " + e.getMessage());
        }
    }

    private static void closeReport() {
        if (reportWriter != null) {
            reportWriter.close();
        }
    }

    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
