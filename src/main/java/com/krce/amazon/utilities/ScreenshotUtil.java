package com.krce.amazon.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.krce.amazon.config.ConfigReader;

public class ScreenshotUtil {
    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotDir = ConfigReader.getScreenshotDir();
        Path dir = Paths.get(screenshotDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Failed to create screenshot directory: {}", screenshotDir, e);
        }
        String fileName = screenshotName + "_" + timestamp + ".png";
        Path filePath = dir.resolve(fileName);
        try {
            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", filePath, e);
        }
        return filePath.toString();
    }
}
