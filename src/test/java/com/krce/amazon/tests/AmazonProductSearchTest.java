package com.krce.amazon.tests;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
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
import com.krce.amazon.utilities.AnalyticsUtil.AnalyticsSummary;
import com.krce.amazon.utilities.CsvReader;
import com.krce.amazon.utilities.CsvWriter;
import com.krce.amazon.utilities.ExcelReader;
import com.krce.amazon.utilities.ExcelWriter;
import com.krce.amazon.utilities.ExtentReportManager;
import com.krce.amazon.utilities.RetryAnalyzer;
import com.krce.amazon.utilities.ScreenshotUtil;

public class AmazonProductSearchTest extends BaseTest {
    private static final Logger log = LogManager.getLogger(AmazonProductSearchTest.class);
    private WebDriver driver;
    private HomePage homePage;
    private List<Product> allProducts;

    @BeforeMethod
    public void setUp() {
        driver = getDriver();
        homePage = new HomePage(driver);
        allProducts = new ArrayList<>();
    }

    @DataProvider(name = "searchKeywords")
    public Object[][] getSearchKeywords() {
        List<String> keywords = new ArrayList<>();

        String excelPath = ConfigReader.getInputExcel();
        String csvPath = ConfigReader.getInputCsv();

        if (new java.io.File(excelPath).exists()) {
            keywords = ExcelReader.readSearchKeywords(excelPath);
        } else if (new java.io.File(csvPath).exists()) {
            keywords = CsvReader.readSearchKeywords(csvPath);
        }

        if (keywords.isEmpty()) {
            keywords.add(ConfigReader.getCategory());
        }

        return keywords.stream().map(k -> new Object[]{k}).toArray(Object[][]::new);
    }

    @Test(dataProvider = "searchKeywords", retryAnalyzer = RetryAnalyzer.class)
    public void testAmazonProductSearch(String keyword) {
        ExtentTest extentTest = ExtentReportManager.createTest("Amazon Product Search - " + keyword);
        log.info("Starting test for keyword: {}", keyword);

        try {
            extentTest.log(Status.INFO, "Searching for: " + keyword);

            String category = ConfigReader.getCategory();
            SearchResultsPage resultsPage = homePage.searchProduct(category, keyword);
            ScreenshotUtil.captureScreenshot(driver, "SearchResults_" + keyword.replace(" ", "_"));
            extentTest.log(Status.PASS, "Amazon launched and search performed for: " + keyword);

            List<Product> products = resultsPage.extractProductDetails();
            extentTest.log(Status.INFO, "Found " + products.size() + " products for: " + keyword);

            for (Product product : products) {
                validateProduct(product, extentTest);
            }

            allProducts.addAll(products);

            extentTest.log(Status.PASS, "Test completed for keyword: " + keyword);
        } catch (Exception e) {
            String screenshotPath = ScreenshotUtil.captureScreenshot(driver, "Failure_" + keyword.replace(" ", "_"));
            extentTest.fail("Test failed for keyword: " + keyword + " - " + e.getMessage());
            extentTest.addScreenCaptureFromPath(screenshotPath);
            log.error("Test failed for keyword: {}", keyword, e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    private void validateProduct(Product product, ExtentTest extentTest) {
        if (product.isNameDisplayed()) {
            extentTest.log(Status.PASS, "Product Name displayed: " + product.getName());
        } else {
            extentTest.log(Status.WARNING, "Product Name not displayed");
        }

        if (product.isPriceAvailable()) {
            extentTest.log(Status.PASS, "Price available: " + product.getPrice());
        } else {
            extentTest.log(Status.WARNING, "Price not available");
        }

        if (product.isRatingAvailable()) {
            extentTest.log(Status.PASS, "Rating available: " + product.getRating());
        } else {
            extentTest.log(Status.WARNING, "Rating not available");
        }

        if (product.isReviewsAvailable()) {
            extentTest.log(Status.PASS, "Reviews count available: " + product.getReviews());
        } else {
            extentTest.log(Status.WARNING, "Reviews count not available");
        }

        if (product.isPrimeInfoDisplayed()) {
            extentTest.log(Status.PASS, "Prime availability displayed: Yes");
        } else {
            extentTest.log(Status.INFO, "Prime availability: No");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (!allProducts.isEmpty()) {
            String excelOutput = ConfigReader.getOutputExcel();
            String csvOutput = ConfigReader.getOutputCsv();

            new java.io.File(excelOutput).getParentFile().mkdirs();
            new java.io.File(csvOutput).getParentFile().mkdirs();

            ExcelWriter.writeProductsToExcel(allProducts, excelOutput);
            CsvWriter.writeProductsToCsv(allProducts, csvOutput);

            ExtentReportManager.getTest().log(Status.INFO, "Data exported to: " + excelOutput + " and " + csvOutput);

            AnalyticsSummary summary = AnalyticsUtil.generateSummary(allProducts);
            ExtentReportManager.getTest().log(Status.INFO, "<pre>" + summary.toString() + "</pre>");
        }

        ScreenshotUtil.captureScreenshot(driver, "AfterTest_");
        quitDriver();
    }
}
