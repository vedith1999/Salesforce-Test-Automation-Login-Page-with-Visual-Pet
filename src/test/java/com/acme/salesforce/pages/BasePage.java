package com.acme.salesforce.pages;

import com.acme.salesforce.config.TestConfig;
import com.acme.salesforce.util.VisualAgent;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.timeout());
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void announce(String message) {
        VisualAgent.announce(driver, message);
    }

    protected WebElement firstVisible(By... locators) {
        return wait.ignoring(StaleElementReferenceException.class).until(ignored -> {
            for (By locator : locators) {
                for (WebElement element : driver.findElements(locator)) {
                    try {
                        if (element.isDisplayed()) {
                            return element;
                        }
                    } catch (StaleElementReferenceException exception) {
                        return null;
                    }
                }
            }

            return null;
        });
    }

    protected WebElement firstClickable(By... locators) {
        WebElement element = firstVisible(locators);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        return element;
    }

    protected boolean isVisible(By... locators) {
        try {
            firstVisible(locators);
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected void type(By locator, String value) {
        WebElement element = visible(locator);
        VisualAgent.highlight(driver, element, "Typing into a Salesforce field.");
        element.clear();
        element.sendKeys(value);
    }

    protected void typeFirst(String value, By... locators) {
        WebElement element = firstVisible(locators);
        VisualAgent.highlight(driver, element, "Typing into a Salesforce field.");
        element.clear();
        element.sendKeys(value);
    }

    protected void clickFirst(By... locators) {
        WebElement element = firstClickable(locators);
        VisualAgent.highlight(driver, element, "Clicking the highlighted Salesforce control.");
        element.click();
    }

    protected String textIfVisible(By... locators) {
        try {
            return firstVisible(locators).getText().trim();
        } catch (TimeoutException exception) {
            return "";
        }
    }
}
