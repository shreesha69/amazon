package com.krce.amazon.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
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
        File dir = new File(screenshotDir);
        if (!dir.exists()) dir.mkdirs();
        String filePath = screenshotDir + "/" + screenshotName + "_" + timestamp + ".png";
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File(filePath));
            log.info("Screenshot saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", filePath, e);
        }
        return filePath;
    }
}
