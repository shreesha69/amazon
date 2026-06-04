package com.krce.amazon.tests;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.krce.amazon.base.BaseTest;
import com.krce.amazon.config.ConfigReader;
import com.krce.amazon.models.Product;
import com.krce.amazon.pages.HomePage;
import com.krce.amazon.pages.SearchResultsPage;
import com.krce.amazon.utilities.AnalyticsUtil;
import com.krce.amazon.utilities.CsvReader;
import com.krce.amazon.utilities.CsvWriter;
import com.krce.amazon.utilities.ExcelReader;
import com.krce.amazon.utilities.ExcelWriter;
import com.krce.amazon.utilities.ExtentReportManager;
import com.krce.amazon.utilities.ScreenshotUtil;
import com.krce.amazon.utilities.ScreenshotUtil.ScreenshotResult;

public class AmazonProductSearchTest extends BaseTest {
    private static final Logger log = LogManager.getLogger(AmazonProductSearchTest.class);
    private static List<Product> allProducts = new ArrayList<>();

    @DataProvider(name = "keywords")
    public Object[][] getKeywords() {
        List<String> list = new ArrayList<>();
        String xl = ConfigReader.getInputExcel();
        String csv = ConfigReader.getInputCsv();
        if (new java.io.File(xl).exists()) list = ExcelReader.readSearchKeywords(xl);
        else if (new java.io.File(csv).exists()) list = CsvReader.readSearchKeywords(csv);
        if (list.isEmpty()) list.add("toys");
        return list.stream().map(k -> new Object[]{k}).toArray(Object[][]::new);
    }

    @Test(dataProvider = "keywords")
    public void searchAndExtract(String keyword) {
        ExtentTest et = ExtentReportManager.createTest("Search: " + keyword);
        WebDriver driver = getDriver();
        log.info("=== Keyword: {} ===", keyword);

        try {
            new HomePage(driver).searchProduct(keyword);

            SearchResultsPage results = new SearchResultsPage(driver);
            results.waitForResults();

            ScreenshotResult sr = ScreenshotUtil.captureScreenshot(driver, keyword.replaceAll("[^a-zA-Z0-9]", "_"));
            et.addScreenCaptureFromPath(sr.relativePath, keyword);

            List<Product> products = results.extractProductDetails();
            et.log(Status.INFO, "Found " + products.size() + " products");

            for (Product p : products) {
                if (p.isNameDisplayed()) et.log(Status.PASS, "Name: " + truncate(p.getName(), 60));
                else et.log(Status.WARNING, "Name missing");
                if (p.isPriceAvailable()) et.log(Status.PASS, "Price: " + p.getPrice());
                else et.log(Status.WARNING, "Price missing");
                if (p.isRatingAvailable()) et.log(Status.PASS, "Rating: " + p.getRating());
                else et.log(Status.WARNING, "Rating missing");
                if (p.isReviewsAvailable()) et.log(Status.PASS, "Reviews: " + p.getReviews());
                else et.log(Status.WARNING, "Reviews missing");
                et.log(p.isPrimeInfoDisplayed() ? Status.PASS : Status.INFO,
                        "Prime: " + p.getPrimeAvailability());
            }
            allProducts.addAll(products);
            et.log(Status.PASS, "Completed");

            exportData();
        } catch (Exception e) {
            log.error("Failed for '{}': {}", keyword, e.getMessage());
            try {
                ScreenshotResult sr = ScreenshotUtil.captureScreenshot(driver, "FAIL_" + keyword.replaceAll("[^a-zA-Z0-9]", "_"));
                et.addScreenCaptureFromPath(sr.relativePath, "Failure");
            } catch (Exception ignored) {}
            et.fail("Failed: " + e.getMessage());
        }
    }

    private void exportData() {
        if (allProducts.isEmpty()) return;
        try {
            String xlOut = ConfigReader.getOutputExcel();
            String csvOut = ConfigReader.getOutputCsv();
            new java.io.File(xlOut).getParentFile().mkdirs();
            new java.io.File(csvOut).getParentFile().mkdirs();
            ExcelWriter.writeProductsToExcel(allProducts, xlOut);
            CsvWriter.writeProductsToCsv(allProducts, csvOut);
            ExtentReportManager.getTest().log(Status.INFO, "Data exported to " + xlOut);
            AnalyticsUtil.generateSummary(allProducts);
        } catch (Exception e) {
            log.warn("Export failed: {}", e.getMessage());
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
