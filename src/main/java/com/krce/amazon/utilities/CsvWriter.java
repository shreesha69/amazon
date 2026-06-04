package com.krce.amazon.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.krce.amazon.models.Product;
import com.opencsv.CSVWriter;

public class CsvWriter {
    private static final Logger log = LogManager.getLogger(CsvWriter.class);

    public static void writeProductsToCsv(List<Product> products, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(Product.headers());
            for (Product p : products) {
                writer.writeNext(p.toStringArray());
            }
            log.info("CSV report written to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to write CSV file: {}", filePath, e);
        }
    }
}
