package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import utils.DriverFactory;
import utils.ConfigReader;
import utils.TestListener;

public class BaseTestClass {
    
    @BeforeClass
    public void classSetup() {
        TestListener.log("Setting up test class: " + this.getClass().getSimpleName());
    }
    
    @AfterClass
    public void classTearDown() {
        TestListener.log("Tearing down test class: " + this.getClass().getSimpleName());
    }
    
    protected void navigateToHomePage() {
        DriverFactory.getDriver().get(ConfigReader.getUrl());
    }
    
    protected void takeScreenshot(String testName) {
        utils.ScreenshotUtil.captureScreenshot(DriverFactory.getDriver(), testName);
    }
}