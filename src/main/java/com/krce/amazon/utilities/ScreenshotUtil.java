package com.krce.amazon.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public static class ScreenshotResult {
        public final String absolutePath;
        public final String relativePath;

        public ScreenshotResult(String absolutePath, String relativePath) {
            this.absolutePath = absolutePath;
            this.relativePath = relativePath;
        }
    }

    public static ScreenshotResult captureScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotDir = ConfigReader.getScreenshotDir();
        Path dir = Paths.get(screenshotDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Failed to create screenshot directory: {}", screenshotDir, e);
        }
        String fileName = screenshotName + "_" + timestamp + ".png";
        Path absolutePath = dir.resolve(fileName);
        try {
            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), absolutePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot saved: {}", absolutePath);
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", absolutePath, e);
        }

        String relativeFromReport = computeRelativePath(absolutePath.toString());
        return new ScreenshotResult(absolutePath.toString(), relativeFromReport);
    }

    private static String computeRelativePath(String screenshotAbsolutePath) {
        try {
            Path reportDir = Paths.get(ConfigReader.getReportDir()).toAbsolutePath().normalize();
            Path screenshotPath = Paths.get(screenshotAbsolutePath).toAbsolutePath().normalize();
            return reportDir.relativize(screenshotPath).toString().replace("\\", "/");
        } catch (Exception e) {
            return screenshotAbsolutePath;
        }
    }
}
