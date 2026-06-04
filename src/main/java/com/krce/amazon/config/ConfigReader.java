package com.krce.amazon.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            log.info("Configuration loaded from {}", CONFIG_FILE);
        } catch (IOException e) {
            log.error("Failed to load config file: {}", CONFIG_FILE, e);
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getBrowser() { return get("browser", "chrome"); }
    public static boolean isHeadless() { return Boolean.parseBoolean(get("headless", "false")); }
    public static int getImplicitWait() { return Integer.parseInt(get("implicit.wait", "10")); }
    public static int getExplicitWait() { return Integer.parseInt(get("explicit.wait", "15")); }
    public static int getPageLoadTimeout() { return Integer.parseInt(get("page.load.timeout", "30")); }
    public static String getUrl() { return get("url", "https://www.amazon.in"); }
    public static String getCategory() { return get("category", "Toys"); }
    public static String getInputExcel() { return get("input.excel"); }
    public static String getInputCsv() { return get("input.csv"); }
    public static String getOutputExcel() { return get("output.excel"); }
    public static String getOutputCsv() { return get("output.csv"); }
    public static String getScreenshotDir() { return get("screenshot.dir"); }
    public static String getReportDir() { return get("report.dir"); }
    public static int getRetryCount() { return Integer.parseInt(get("retry.count", "2")); }

    public static String getEmailTo() { return get("email.to"); }
    public static String getEmailFrom() { return get("email.from"); }
    public static String getEmailHost() { return get("email.host"); }
    public static String getEmailPort() { return get("email.port"); }
    public static String getEmailUsername() { return get("email.username"); }
    public static String getEmailPassword() { return get("email.password"); }
    public static String getEmailSubject() { return get("email.subject"); }
}
