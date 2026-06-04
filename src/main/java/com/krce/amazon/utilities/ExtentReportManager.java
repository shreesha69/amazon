package com.krce.amazon.utilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.krce.amazon.config.ConfigReader;

public class ExtentReportManager {
    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String reportPath;

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    public static String getReportPath() {
        if (reportPath == null) getInstance();
        return reportPath;
    }

    private static void createInstance() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportDir = ConfigReader.getReportDir();
        Path dir = Paths.get(reportDir);
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            log.error("Failed to create report directory", e);
        }
        reportPath = dir.resolve("AmazonTestReport_" + timestamp + ".html").toString();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Amazon Product Search Automation Report");
        sparkReporter.config().setReportName("Amazon Test Execution Report");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setTimelineEnabled(true);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", ConfigReader.getBrowser());
        extent.setSystemInfo("User", System.getProperty("user.name"));

        log.info("Extent Report initialized at: {}", reportPath);
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest extentTest = getInstance().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flush() {
        if (extent != null) {
            try {
                extent.flush();
                log.info("Extent Report flushed to: {}", reportPath);
            } catch (Exception e) {
                log.error("Failed to flush Extent Report", e);
            }
        }
    }
}
