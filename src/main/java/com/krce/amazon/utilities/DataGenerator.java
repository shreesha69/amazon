package com.krce.amazon.utilities;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataGenerator {
    private static final Logger log = LogManager.getLogger(DataGenerator.class);

    private static final List<String> DEFAULT_KEYWORDS = List.of(
            "toys", "toys under 500", "toys above 500", "toys for kids"
    );

    public static void main(String[] args) {
        generateSampleInputExcel("src/main/resources/testdata/input.xlsx", DEFAULT_KEYWORDS);
        generateSampleInputCsv("src/main/resources/testdata/input.csv", DEFAULT_KEYWORDS);
    }

    public static void generateSampleInputExcel(String filePath, List<String> keywords) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SearchKeywords");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            Cell hc = header.createCell(0);
            hc.setCellValue("SearchKeyword");
            hc.setCellStyle(headerStyle);

            for (int i = 0; i < keywords.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Cell cell = row.createCell(0);
                cell.setCellValue(keywords.get(i));
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
                cell.setCellStyle(cellStyle);
            }
            sheet.autoSizeColumn(0);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            log.info("Sample input Excel generated: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to generate input Excel: {}", filePath, e);
        }
    }

    public static void generateSampleInputCsv(String filePath, List<String> keywords) {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("SearchKeyword\n");
            for (String kw : keywords) {
                fw.write(kw + "\n");
            }
            log.info("Sample input CSV generated: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to generate input CSV: {}", filePath, e);
        }
    }
}
