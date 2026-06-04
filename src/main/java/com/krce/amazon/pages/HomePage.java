package com.krce.amazon.pages;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.krce.amazon.config.ConfigReader;

public class HomePage {
    private static final Logger log = LogManager.getLogger(HomePage.class);
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    public void navigateToHomePage() {
        String url = ConfigReader.getUrl();
        driver.get(url);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        dismissPopups();
        log.info("Navigated to Amazon URL: {}", url);
    }

    private void dismissPopups() {
        try {
            WebElement alert = driver.findElement(By.cssSelector("[data-action-type='DISMISS']"));
            alert.click();
            log.info("Dismissed popup");
        } catch (Exception ignored) {}
        try {
            WebElement cookies = driver.findElement(By.cssSelector("input[data-testid='accept-cookie-button'], input[name='accept']"));
            cookies.click();
            log.info("Accepted cookies");
        } catch (Exception ignored) {}
    }

    public void enterSearchKeyword(String keyword) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("twotabsearchtextbox")));
        } catch (Exception e) {
            log.warn("Search box not found, reloading page");
            driver.get(ConfigReader.getUrl());
            try { Thread.sleep(3000); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("twotabsearchtextbox")));
        }
        searchBox.clear();
        searchBox.sendKeys(keyword);
        log.info("Entered keyword: {}", keyword);
    }

    public SearchResultsPage clickSearch() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-search-submit-button")));
        } catch (Exception e) {
            log.warn("Search button not clickable, using Enter key");
            searchBox.submit();
            return new SearchResultsPage(driver);
        }
        searchButton.click();
        log.info("Clicked search button");
        return new SearchResultsPage(driver);
    }

    public SearchResultsPage searchProduct(String keyword) {
        navigateToHomePage();
        enterSearchKeyword(keyword);
        return clickSearch();
    }
}
