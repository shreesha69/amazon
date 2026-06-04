package com.krce.amazon.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.krce.amazon.config.ConfigReader;

public class HomePage {
    private static final Logger log = LogManager.getLogger(HomePage.class);
    private WebDriver driver;

    @FindBy(id = "searchDropdownBox")
    private WebElement categoryDropdown;

    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void navigateToHomePage() {
        String url = ConfigReader.getUrl();
        driver.get(url);
        log.info("Navigated to Amazon URL: {}", url);
    }

    public void selectCategory(String category) {
        Select select = new Select(categoryDropdown);
        select.selectByVisibleText(category);
        log.info("Selected category: {}", category);
    }

    public void enterSearchKeyword(String keyword) {
        searchBox.clear();
        searchBox.sendKeys(keyword);
        log.info("Entered search keyword: {}", keyword);
    }

    public SearchResultsPage clickSearch() {
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
