package com.krce.amazon.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.krce.amazon.config.ConfigReader;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private final int maxRetryCount;

    public RetryAnalyzer() {
        this.maxRetryCount = ConfigReader.getRetryCount();
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            log.info("Retrying test '{}' - attempt {}/{}",
                    result.getName(), retryCount, maxRetryCount);
            return true;
        }
        return false;
    }
}
