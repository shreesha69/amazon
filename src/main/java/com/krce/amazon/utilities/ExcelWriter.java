package com.krce.amazon.utilities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.krce.amazon.models.Product;

public class ExcelWriter {
    private static final Logger log = LogManager.getLogger(ExcelWriter.class);

    public static void writeProductsToExcel(List<Product> products, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Amazon Products");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = Product.headers();
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            for (int i = 0; i < products.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Product p = products.get(i);
                row.createCell(0).setCellValue(p.getName() != null ? p.getName() : "");
                row.createCell(1).setCellValue(p.getPrice() != null ? p.getPrice() : "");
                row.createCell(2).setCellValue(p.getRating() != null ? p.getRating() : "");
                row.createCell(3).setCellValue(p.getReviews() != null ? p.getReviews() : "");
                row.createCell(4).setCellValue(p.getPrimeAvailability() != null ? p.getPrimeAvailability() : "");
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            log.info("Excel report written to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to write Excel file: {}", filePath, e);
        }
    }
}
