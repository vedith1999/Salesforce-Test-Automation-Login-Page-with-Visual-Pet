package com.acme.salesforce.listeners;

import com.acme.salesforce.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotOnFailureListener implements ITestListener {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriverOrNull();
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return;
        }

        try {
            Path screenshotDirectory = Path.of("target", "screenshots");
            Files.createDirectories(screenshotDirectory);

            String fileName = result.getMethod().getMethodName()
                    + "-"
                    + LocalDateTime.now().format(TIMESTAMP)
                    + ".png";
            Path destination = screenshotDirectory.resolve(fileName);

            Files.copy(screenshotDriver.getScreenshotAs(OutputType.FILE).toPath(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);
            Reporter.log("Saved failure screenshot: " + destination.toAbsolutePath(), true);
        } catch (IOException exception) {
            Reporter.log("Unable to save failure screenshot: " + exception.getMessage(), true);
        }
    }
}
