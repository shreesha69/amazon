package com.krce.amazon.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
    private static final Logger log = LogManager.getLogger(ExcelReader.class);

    public static List<String> readSearchKeywords(String filePath) {
        List<String> keywords = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(0);
                    if (cell != null) {
                        String value = getCellValueAsString(cell);
                        if (!value.isEmpty()) {
                            keywords.add(value);
                        }
                    }
                }
            }
            log.info("Read {} search keywords from Excel: {}", keywords.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to read Excel file: {}", filePath, e);
        }
        return keywords;
    }

    private static String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
