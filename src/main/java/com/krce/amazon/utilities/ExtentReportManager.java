package com.krce.amazon.utilities;

import java.io.File;
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

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static void createInstance() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportDir = ConfigReader.getReportDir();
        File dir = new File(reportDir);
        if (!dir.exists()) dir.mkdirs();
        String reportPath = reportDir + "/AmazonTestReport_" + timestamp + ".html";

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
            extent.flush();
            log.info("Extent Report flushed");
        }
    }
}
