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
        public final String relativePath;
        public ScreenshotResult(String relativePath) { this.relativePath = relativePath; }
    }

    public static ScreenshotResult captureScreenshot(WebDriver driver, String screenshotName) {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dir = ConfigReader.getScreenshotDir();
        Path abs = Paths.get(dir, screenshotName + "_" + ts + ".png");
        try {
            Files.createDirectories(abs.getParent());
            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), abs, StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot: {}", abs);
        } catch (IOException e) {
            log.error("Screenshot failed", e);
        }
        String relative = Paths.get(ConfigReader.getReportDir()).relativize(abs).toString().replace("\\", "/");
        return new ScreenshotResult(relative);
    }
}
