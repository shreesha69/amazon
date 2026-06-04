package com.krce.amazon.utilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;

public class CsvReader {
    private static final Logger log = LogManager.getLogger(CsvReader.class);

    public static List<String> readSearchKeywords(String filePath) {
        List<String> keywords = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                if (line.length > 0 && !line[0].trim().isEmpty()) {
                    keywords.add(line[0].trim());
                }
            }
            log.info("Read {} search keywords from CSV: {}", keywords.size(), filePath);
        } catch (Exception e) {
            log.error("Failed to read CSV file: {}", filePath, e);
        }
        return keywords;
    }
}
