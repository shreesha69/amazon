package com.krce.amazon.utilities;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.krce.amazon.models.Product;

public class AnalyticsUtil {
    private static final Logger log = LogManager.getLogger(AnalyticsUtil.class);

    public static class AnalyticsSummary {
        public int totalProducts;
        public long primeEligibleCount;
        public double averageRating;
        public String highestRatedProduct;
        public double highestRating;
        public String lowestPricedProduct;
        public double lowestPrice;

        @Override
        public String toString() {
            return String.format("""
                    ===== Analytics Summary =====
                    Total Products Found: %d
                    Prime Eligible Products: %d
                    Average Rating: %.2f
                    Highest Rated Product: %s (%.1f)
                    Lowest Priced Product: %s (₹%.2f)
                    =============================""",
                    totalProducts, primeEligibleCount, averageRating,
                    highestRatedProduct, highestRating,
                    lowestPricedProduct, lowestPrice);
        }
    }

    public static AnalyticsSummary generateSummary(List<Product> products) {
        AnalyticsSummary summary = new AnalyticsSummary();
        summary.totalProducts = products.size();

        summary.primeEligibleCount = products.stream()
                .filter(p -> "Yes".equalsIgnoreCase(p.getPrimeAvailability()))
                .count();

        List<Product> withRating = products.stream()
                .filter(p -> p.getRating() != null && !p.getRating().isEmpty())
                .toList();

        if (!withRating.isEmpty()) {
            DoubleSummaryStatistics ratingStats = withRating.stream()
                    .mapToDouble(p -> parseDoubleSafe(p.getRating()))
                    .summaryStatistics();
            summary.averageRating = ratingStats.getAverage();

            Optional<Product> highest = withRating.stream()
                    .max(Comparator.comparingDouble(p -> parseDoubleSafe(p.getRating())));
            if (highest.isPresent()) {
                summary.highestRatedProduct = highest.get().getName();
                summary.highestRating = parseDoubleSafe(highest.get().getRating());
            }
        }

        List<Product> withPrice = products.stream()
                .filter(p -> p.getPrice() != null && !p.getPrice().isEmpty())
                .toList();

        if (!withPrice.isEmpty()) {
            Optional<Product> lowest = withPrice.stream()
                    .min(Comparator.comparingDouble(p -> parsePriceDouble(p.getPrice())));
            if (lowest.isPresent()) {
                summary.lowestPricedProduct = lowest.get().getName();
                summary.lowestPrice = parsePriceDouble(lowest.get().getPrice());
            }
        }

        log.info("Analytics summary generated:\n{}", summary);
        return summary;
    }

    private static double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static double parsePriceDouble(String value) {
        try {
            return Double.parseDouble(value.replaceAll("[₹,\\s]", ""));
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }
}
