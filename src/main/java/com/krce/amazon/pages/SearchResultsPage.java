package com.krce.amazon.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.krce.amazon.models.Product;

public class SearchResultsPage {
    private static final Logger log = LogManager.getLogger(SearchResultsPage.class);
    private WebDriver driver;

    @FindBy(css = "[data-component-type='s-search-result']")
    private List<WebElement> searchResults;

    private static final String SELECTOR_NAME = "h2 a.a-link-normal span.a-text-normal";
    private static final String SELECTOR_PRICE_WHOLE = ".a-price .a-price-whole";
    private static final String SELECTOR_PRICE_FRACTION = ".a-price .a-price-fraction";
    private static final String SELECTOR_RATING = "span.a-icon-alt";
    private static final String SELECTOR_REVIEWS = "span.a-size-base.s-underline-text";
    private static final String SELECTOR_PRIME = "i.a-icon-prime";

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public List<Product> extractProductDetails() {
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
            WebElement nameEl = result.findElement(By.cssSelector(SELECTOR_NAME));
            product.setName(nameEl.getText().trim());
            product.setNameDisplayed(true);
        } catch (Exception e) {
            product.setNameDisplayed(false);
            log.warn("Product {}: Name not found", index);
        }

        try {
            String whole = result.findElement(By.cssSelector(SELECTOR_PRICE_WHOLE)).getText().trim();
            String fraction = "";
            try {
                fraction = result.findElement(By.cssSelector(SELECTOR_PRICE_FRACTION)).getText().trim();
            } catch (Exception ignored) {
            }
            String price = "₹" + whole + (fraction.isEmpty() ? "" : "." + fraction);
            product.setPrice(price);
            product.setPriceAvailable(true);
        } catch (Exception e) {
            product.setPriceAvailable(false);
            log.warn("Product {}: Price not found", index);
        }

        try {
            String ratingText = result.findElement(By.cssSelector(SELECTOR_RATING)).getAttribute("textContent");
            if (ratingText != null) {
                String rating = ratingText.split(" ")[0];
                product.setRating(rating);
                product.setRatingAvailable(true);
            }
        } catch (Exception e) {
            product.setRatingAvailable(false);
            log.warn("Product {}: Rating not found", index);
        }

        try {
            WebElement reviewsEl = result.findElement(By.cssSelector(SELECTOR_REVIEWS));
            product.setReviews(reviewsEl.getText().trim());
            product.setReviewsAvailable(true);
        } catch (Exception e) {
            product.setReviewsAvailable(false);
            log.warn("Product {}: Reviews not found", index);
        }

        try {
            result.findElement(By.cssSelector(SELECTOR_PRIME));
            product.setPrimeAvailability("Yes");
            product.setPrimeInfoDisplayed(true);
        } catch (Exception e) {
            product.setPrimeAvailability("No");
            product.setPrimeInfoDisplayed(false);
        }

        return product;
    }

    public int getResultCount() {
        return searchResults.size();
    }
}
