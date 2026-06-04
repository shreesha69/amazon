package com.krce.amazon.base;

import java.io.File;
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
import com.krce.amazon.utilities.ExtentReportManager;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
    private static final Logger log = LogManager.getLogger(BaseTest.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeSuite
    public void setUpSuite() {
        ExtentReportManager.getInstance();
        log.info("Extent Report initialized for test suite");
    }

    public WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(initializeDriver());
        }
        return driver.get();
    }

    private WebDriver initializeDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();
        WebDriver webDriver;

        log.info("Initializing browser: {}, headless: {}", browser, headless);

        switch (browser) {
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless");
                edgeOptions.addArguments("--disable-notifications");
                edgeOptions.addArguments("--disable-popup-blocking");
                webDriver = new EdgeDriver(edgeOptions);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                webDriver = new FirefoxDriver(firefoxOptions);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                webDriver = new ChromeDriver(chromeOptions);
            }
        }

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));

        log.info("WebDriver initialized for browser: {}", browser);
        return webDriver;
    }

    public void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            log.info("WebDriver quit successfully");
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        ExtentReportManager.flush();
        log.info("Test suite teardown complete");
    }
}
