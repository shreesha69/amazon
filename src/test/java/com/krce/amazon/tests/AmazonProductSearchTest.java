package com.krce.amazon.tests;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class AmazonProductSearchTest {
    private static final Logger log = LogManager.getLogger(AmazonProductSearchTest.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private List<String[]> results = new ArrayList<>();

    @BeforeClass
    public void setup() {
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--disable-blink-features=AutomationControlled");
        co.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        co.setExperimentalOption("useAutomationExtension", false);
        co.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        co.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
        driver = new ChromeDriver(co);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        results.add(new String[]{"Product Name", "Price", "Rating", "Reviews", "Prime"});
        log.info("Browser started");
    }

    @Test
    public void searchProducts() throws Exception {
        List<String> keywords = readKeywords("src/main/resources/testdata/input.csv");
        log.info("Keywords to search: {}", keywords);

        for (String keyword : keywords) {
            log.info("Searching: {}", keyword);
            try {
                driver.get("https://www.amazon.in/s?k=" + java.net.URLEncoder.encode(keyword, "UTF-8"));
                Thread.sleep(3000);

                List<WebElement> items = driver.findElements(By.cssSelector("[data-component-type='s-search-result']"));
                log.info("Found {} items for '{}'", items.size(), keyword);

                for (int i = 0; i < Math.min(items.size(), 10); i++) {
                    try {
                        WebElement item = items.get(i);
                        String name = item.findElement(By.cssSelector("h2 a")).getText().trim();
                        String price = "";
                        try { price = item.findElement(By.cssSelector(".a-price-whole")).getText().trim(); } catch (Exception ignored) {}
                        String rating = "";
                        try { rating = item.findElement(By.cssSelector("span.a-icon-alt")).getAttribute("textContent").split(" ")[0]; } catch (Exception ignored) {}
                        String reviews = "";
                        try { reviews = item.findElement(By.cssSelector("span.a-size-base.s-underline-text")).getText().trim(); } catch (Exception ignored) {}
                        String prime = "No";
                        try { item.findElement(By.cssSelector("i.a-icon-prime")); prime = "Yes"; } catch (Exception ignored) {}
                        results.add(new String[]{name, price, rating, reviews, prime});
                    } catch (Exception e) {
                        log.warn("Failed to extract item {}: {}", i + 1, e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Failed for keyword '{}': {}", keyword, e.getMessage());
            }
        }
    }

    @AfterClass
    public void teardown() throws Exception {
        if (driver != null) driver.quit();

        Files.createDirectories(Paths.get("output/data"));

        try (CSVWriter w = new CSVWriter(new FileWriter("output/data/AmazonProductData.csv"))) {
            w.writeAll(results);
        }
        log.info("CSV written: output/data/AmazonProductData.csv ({} rows)", results.size());

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Products");
            for (int i = 0; i < results.size(); i++) {
                Row r = sh.createRow(i);
                String[] row = results.get(i);
                for (int j = 0; j < row.length; j++) r.createCell(j).setCellValue(row[j]);
            }
            Files.createDirectories(Paths.get("output/data"));
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream("output/data/AmazonProductData.xlsx")) {
                wb.write(fos);
            }
        }
        log.info("Excel written: output/data/AmazonProductData.xlsx");
        log.info("Total products extracted: {}", results.size() - 1);
    }

    private List<String> readKeywords(String path) throws Exception {
        List<String> list = new ArrayList<>();
        try (CSVReader r = new CSVReader(new FileReader(path))) {
            r.readNext();
            String[] line;
            while ((line = r.readNext()) != null) {
                if (line.length > 0 && !line[0].trim().isEmpty()) list.add(line[0].trim());
            }
        }
        return list;
    }
}
