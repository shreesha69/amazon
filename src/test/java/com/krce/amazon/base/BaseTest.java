package com.krce.amazon.base;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.krce.amazon.config.ConfigReader;
import com.krce.amazon.utilities.EmailUtil;
import com.krce.amazon.utilities.ExtentReportManager;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
    private static final Logger log = LogManager.getLogger(BaseTest.class);
    private static WebDriver driver;

    @BeforeSuite
    public void setUpSuite() {
        ExtentReportManager.getInstance();
        try {
            Files.createDirectories(Paths.get(ConfigReader.getScreenshotDir()));
            Files.createDirectories(Paths.get(ConfigReader.getOutputExcel()).getParent());
        } catch (Exception e) {
            log.warn("Failed to create directories", e);
        }
        log.info("Suite initialized");
    }

    public WebDriver getDriver() {
        if (driver == null) {
            driver = createDriver();
        }
        return driver;
    }

    private WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();
        log.info("Starting browser: {}, headless: {}", browser, headless);

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions fo = new FirefoxOptions();
                if (headless) fo.addArguments("--headless");
                fo.addPreference("dom.webnotifications.enabled", false);
                WebDriver d = new FirefoxDriver(fo);
                d.manage().window().maximize();
                return d;
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions eo = new EdgeOptions();
                if (headless) eo.addArguments("--headless");
                eo.addArguments("--disable-notifications");
                WebDriver d = new EdgeDriver(eo);
                d.manage().window().maximize();
                return d;
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions co = new ChromeOptions();
                if (headless) co.addArguments("--headless=new");
                co.addArguments("--disable-notifications");
                co.addArguments("--no-sandbox");
                co.addArguments("--disable-dev-shm-usage");
                WebDriver d = new ChromeDriver(co);
                d.manage().window().maximize();
                return d;
            }
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception e) { log.warn("Driver quit error", e); }
            driver = null;
        }
        ExtentReportManager.flush();
        if (ConfigReader.isEmailConfigured()) {
            try {
                EmailUtil.sendEmailWithAttachments(new String[]{
                        ExtentReportManager.getReportPath(),
                        ConfigReader.getOutputExcel(),
                        ConfigReader.getOutputCsv()
                });
            } catch (Exception e) {
                log.error("Email failed", e);
            }
        }
        log.info("Suite finished");
    }
}
