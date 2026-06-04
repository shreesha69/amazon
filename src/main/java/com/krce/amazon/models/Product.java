package com.krce.amazon.models;

public class Product {
    private String name;
    private String price;
    private String rating;
    private String reviews;
    private String primeAvailability;
    private boolean nameDisplayed;
    private boolean priceAvailable;
    private boolean ratingAvailable;
    private boolean reviewsAvailable;
    private boolean primeInfoDisplayed;

    public Product() {
    }

    public Product(String name, String price, String rating, String reviews, String primeAvailability) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.reviews = reviews;
        this.primeAvailability = primeAvailability;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getReviews() { return reviews; }
    public void setReviews(String reviews) { this.reviews = reviews; }

    public String getPrimeAvailability() { return primeAvailability; }
    public void setPrimeAvailability(String primeAvailability) { this.primeAvailability = primeAvailability; }

    public boolean isNameDisplayed() { return nameDisplayed; }
    public void setNameDisplayed(boolean nameDisplayed) { this.nameDisplayed = nameDisplayed; }

    public boolean isPriceAvailable() { return priceAvailable; }
    public void setPriceAvailable(boolean priceAvailable) { this.priceAvailable = priceAvailable; }

    public boolean isRatingAvailable() { return ratingAvailable; }
    public void setRatingAvailable(boolean ratingAvailable) { this.ratingAvailable = ratingAvailable; }

    public boolean isReviewsAvailable() { return reviewsAvailable; }
    public void setReviewsAvailable(boolean reviewsAvailable) { this.reviewsAvailable = reviewsAvailable; }

    public boolean isPrimeInfoDisplayed() { return primeInfoDisplayed; }
    public void setPrimeInfoDisplayed(boolean primeInfoDisplayed) { this.primeInfoDisplayed = primeInfoDisplayed; }

    public String[] toStringArray() {
        return new String[]{name, price, rating, reviews, primeAvailability};
    }

    public static String[] headers() {
        return new String[]{"Product Name", "Price", "Rating", "Reviews", "Prime Availability"};
    }
}
