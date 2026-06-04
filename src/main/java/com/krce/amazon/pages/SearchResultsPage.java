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
            log.warn("Search results did not load within timeout: {}", e.getMessage());
        }
    }

    public List<Product> extractProductDetails() {
        waitForResults();
        List<Product> products = new ArrayList<>();
        log.info("Extracting product details from {} results", searchResults.size());

        for (int i = 0; i < searchResults.size(); i++) {
            try {
                WebElement result = searchResults.get(i);
                Product product = extractSingleProduct(result, i + 1);
                if (product.getName() != null) {
                    products.add(product);
                }
            } catch (Exception e) {
                log.warn("Failed to extract product at index {}: {}", i + 1, e.getMessage());
            }
        }

        log.info("Successfully extracted {} products", products.size());
        return products;
    }

    private Product extractSingleProduct(WebElement result, int index) {
        Product product = new Product();

        try {
            WebElement nameEl = result.findElement(By.cssSelector("h2 a.a-link-normal span.a-text-normal, h2 a.a-link-normal"));
            product.setName(nameEl.getText().trim());
            product.setNameDisplayed(true);
        } catch (Exception e) {
            product.setNameDisplayed(false);
            log.warn("Product {}: Name not found", index);
        }

        try {
            String whole = result.findElement(By.cssSelector(".a-price .a-price-whole")).getText().trim();
            String fraction = "";
            try {
                fraction = result.findElement(By.cssSelector(".a-price .a-price-fraction")).getText().trim();
            } catch (Exception ignored) {
            }
            String price = "₹" + whole + (!fraction.isEmpty() ? "." + fraction : "");
            product.setPrice(price);
            product.setPriceAvailable(true);
        } catch (Exception e) {
            product.setPriceAvailable(false);
            log.warn("Product {}: Price not found", index);
        }

        try {
            WebElement ratingEl = result.findElement(By.cssSelector("span.a-icon-alt"));
            String ratingText = ratingEl.getAttribute("textContent");
            if (ratingText != null && !ratingText.isEmpty()) {
                String rating = ratingText.split(" ")[0];
                product.setRating(rating);
                product.setRatingAvailable(true);
            }
        } catch (Exception e) {
            product.setRatingAvailable(false);
            log.warn("Product {}: Rating not found", index);
        }

        try {
            WebElement reviewsEl = result.findElement(By.cssSelector("span.a-size-base.s-underline-text, a.a-size-base.s-underline-text"));
            product.setReviews(reviewsEl.getText().trim());
            product.setReviewsAvailable(true);
        } catch (Exception e) {
            product.setReviewsAvailable(false);
            log.warn("Product {}: Reviews not found", index);
        }

        try {
            result.findElement(By.cssSelector("i.a-icon-prime"));
            product.setPrimeAvailability("Yes");
            product.setPrimeInfoDisplayed(true);
        } catch (Exception e) {
            product.setPrimeAvailability("No");
            product.setPrimeInfoDisplayed(false);
        }

        return product;
    }

    public int getResultCount() {
        waitForResults();
        return searchResults.size();
    }
}
