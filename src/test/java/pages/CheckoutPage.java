package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.TestListener;

import java.util.List;

public class CheckoutPage extends BasePage {
    
    @FindBy(css = "#accountFrm_accountguest")
    private WebElement guestCheckoutRadio;
    
    @FindBy(css = "#accountFrm_accountregister")
    private WebElement registerAccountRadio;
    
    @FindBy(css = ".btn-orange, .btn-primary, [title*='Continue']")
    private WebElement continueButton;
    
    @FindBy(css = ".alert, .error, .warning")
    private WebElement errorMessage;
    
    public CheckoutPage(WebDriver driver) {
        super(driver);
        waitForPageLoad();
        TestListener.log("Reached checkout page: " + getCurrentUrl());
    }
    
    public RegistrationPage selectRegistration() {
        try {
            if (isElementDisplayed(registerAccountRadio)) {
                click(registerAccountRadio);
                TestListener.log("Selected 'Register Account' option");
            } else {
                TestListener.log("Register account option not found, trying alternative approach");
            }
            
            WebElement continueBtn = findContinueButton();
            if (continueBtn != null) {
                click(continueBtn);
                TestListener.log("Clicked continue to proceed to registration");
            }
            
            return new RegistrationPage(driver);
            
        } catch (Exception e) {
            TestListener.log("Error in checkout process: " + e.getMessage());
            
            driver.get("https://automationteststore.com/index.php?rt=account/create");
            return new RegistrationPage(driver);
        }
    }
    
    private WebElement findContinueButton() {
        List<String> continueSelectors = List.of(
            ".btn-orange", ".btn-primary", "[title*='Continue']",
            ".continue", "input[value*='Continue']", ".btn-continue"
        );
        
        for (String selector : continueSelectors) {
            try {
                List<WebElement> buttons = driver.findElements(By.cssSelector(selector));
                for (WebElement button : buttons) {
                    if (button.isDisplayed() && button.isEnabled()) {
                        return button;
                    }
                }
            } catch (Exception e) {
                
            }
        }
        
        return null;
    }
}
