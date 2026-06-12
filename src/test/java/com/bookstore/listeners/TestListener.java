package com.bookstore.listeners;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "target/screenshots/";

    @Override
    public void onStart(ITestContext context) {
        System.out.println("========================================");
        System.out.println("TEST GROUP STARTED: " + context.getName());
        System.out.println("Start Time: " + new Date());
        System.out.println("========================================");
        new File(SCREENSHOT_DIR).mkdirs();
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("========================================");
        System.out.println("TEST GROUP FINISHED: " + context.getName());
        System.out.println("Passed  : " + context.getPassedTests().size());
        System.out.println("Failed  : " + context.getFailedTests().size());
        System.out.println("Skipped : " + context.getSkippedTests().size());
        System.out.println("End Time: " + new Date());
        System.out.println("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("------------------------------------------");
        System.out.println("TEST STARTED : " + result.getName());
        System.out.println("Class        : " + result.getTestClass().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("TEST PASSED  : " + result.getName());
        System.out.println("Time Taken   : " + (result.getEndMillis() - result.getStartMillis()) + " ms");
        takeScreenshot(result, "PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("TEST FAILED  : " + result.getName());
        System.out.println("Reason       : " + result.getThrowable().getMessage());
        System.out.println("Time Taken   : " + (result.getEndMillis() - result.getStartMillis()) + " ms");
        takeScreenshot(result, "FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("TEST SKIPPED : " + result.getName());
        System.out.println("Reason       : " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("TEST FAILED WITHIN SUCCESS PERCENTAGE: " + result.getName());
    }

    private void takeScreenshot(ITestResult result, String status) {
        Object testInstance = result.getInstance();
        WebDriver driver = null;

        try {
            java.lang.reflect.Field driverField = testInstance.getClass().getDeclaredField("driver");
            driverField.setAccessible(true);
            driver = (WebDriver) driverField.get(testInstance);
        } catch (Exception e) {
            return;
        }

        if (driver != null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = SCREENSHOT_DIR + status + "_"
                    + result.getName() + "_" + timestamp + ".png";
            try {
                File screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), Paths.get(fileName));
                System.out.println("Screenshot   : " + fileName);
            } catch (IOException e) {
                System.out.println("Screenshot failed: " + e.getMessage());
            }
        }
    }
}