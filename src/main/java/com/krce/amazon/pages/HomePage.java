package com.krce.amazon.pages;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.krce.amazon.config.ConfigReader;

public class HomePage {
    private static final Logger log = LogManager.getLogger(HomePage.class);
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "searchDropdownBox")
    private WebElement categoryDropdown;

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
        wait.until(ExpectedConditions.presenceOfElementLocated(
                org.openqa.selenium.By.id("searchDropdownBox")));
        log.info("Navigated to Amazon URL: {}", url);
    }

    public void selectCategory(String category) {
        wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown));
        Select select = new Select(categoryDropdown);
        select.selectByVisibleText(category);
        log.info("Selected category: {}", category);
    }

    public void enterSearchKeyword(String keyword) {
        wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        searchBox.clear();
        searchBox.sendKeys(keyword);
        log.info("Entered search keyword: {}", keyword);
    }

    public SearchResultsPage clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        searchButton.click();
        log.info("Clicked search button");
        return new SearchResultsPage(driver);
    }

    public SearchResultsPage searchProduct(String category, String keyword) {
        navigateToHomePage();
        selectCategory(category);
        enterSearchKeyword(keyword);
        return clickSearch();
    }
}
