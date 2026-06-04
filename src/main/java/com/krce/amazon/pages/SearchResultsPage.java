package com.krce.amazon.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
import com.krce.amazon.models.Product;

public class SearchResultsPage {
    private static final Logger log = LogManager.getLogger(SearchResultsPage.class);
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css = "[data-component-type='s-search-result']")
    private List<WebElement> searchResults;

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    public void waitForResults() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("[data-component-type='s-search-result']")));
        } catch (Exception e) {
            log.warn("Results not loaded: {}", e.getMessage());
        }
    }

    public List<Product> extractProductDetails() {
        waitForResults();
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < searchResults.size(); i++) {
            try {
                Product p = extractSingleProduct(searchResults.get(i));
                if (p.getName() != null) products.add(p);
            } catch (Exception e) {
                log.warn("Failed to extract product {}: {}", i + 1, e.getMessage());
            }
        }
        log.info("Extracted {} products", products.size());
        return products;
    }

    private Product extractSingleProduct(WebElement el) {
        Product p = new Product();
        try {
            WebElement n = el.findElement(By.cssSelector("h2 a"));
            p.setName(n.getText().trim());
            p.setNameDisplayed(true);
        } catch (Exception e) { p.setNameDisplayed(false); }

        try {
            String w = el.findElement(By.cssSelector(".a-price-whole")).getText().trim();
            String f = "";
            try { f = el.findElement(By.cssSelector(".a-price-fraction")).getText().trim(); } catch (Exception ignored) {}
            p.setPrice("\u20B9" + w + (f.isEmpty() ? "" : "." + f));
            p.setPriceAvailable(true);
        } catch (Exception e) { p.setPriceAvailable(false); }

        try {
            String t = el.findElement(By.cssSelector("span.a-icon-alt")).getAttribute("textContent");
            if (t != null && !t.isEmpty()) { p.setRating(t.split(" ")[0]); p.setRatingAvailable(true); }
        } catch (Exception e) { p.setRatingAvailable(false); }

        try {
            WebElement r = el.findElement(By.cssSelector("span.a-size-base.s-underline-text"));
            p.setReviews(r.getText().trim());
            p.setReviewsAvailable(true);
        } catch (Exception e) { p.setReviewsAvailable(false); }

        try {
            el.findElement(By.cssSelector("i.a-icon-prime"));
            p.setPrimeAvailability("Yes");
            p.setPrimeInfoDisplayed(true);
        } catch (Exception e) {
            p.setPrimeAvailability("No");
            p.setPrimeInfoDisplayed(false);
        }
        return p;
    }
}
