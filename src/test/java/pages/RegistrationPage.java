package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.CSVUtils;
import utils.TestListener;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class RegistrationPage extends BasePage {
    
    @FindBy(css = "#AccountFrm_firstname, [name='firstname'], #firstname")
    private WebElement firstNameField;
    
    @FindBy(css = "#AccountFrm_lastname, [name='lastname'], #lastname")
    private WebElement lastNameField;
    
    @FindBy(css = "#AccountFrm_email, [name='email'], #email")
    private WebElement emailField;
    
    @FindBy(css = "#AccountFrm_telephone, [name='telephone'], #telephone")
    private WebElement phoneField;
    
    @FindBy(css = "#AccountFrm_company, [name='company'], #company")
    private WebElement companyField;
    
    @FindBy(css = "#AccountFrm_address_1, [name='address_1'], #address1")
    private WebElement address1Field;
    
    @FindBy(css = "#AccountFrm_city, [name='city'], #city")
    private WebElement cityField;
    
    @FindBy(css = "#AccountFrm_country_id, [name='country_id'], #country")
    private WebElement countryDropdown;
    
    @FindBy(css = "#AccountFrm_zone_id, [name='zone_id'], #state")
    private WebElement stateDropdown;
    
    @FindBy(css = "#AccountFrm_postcode, [name='postcode'], #postcode")
    private WebElement postcodeField;
    
    @FindBy(css = "#AccountFrm_password, [name='password'], #password")
    private WebElement passwordField;
    
    @FindBy(css = "#AccountFrm_confirm, [name='confirm'], #confirm_password")
    private WebElement confirmPasswordField;
    
    @FindBy(css = "#AccountFrm_agree, [name='agree'], .agree")
    private WebElement agreeCheckbox;
    
    @FindBy(css = ".btn-orange, .btn-primary, [title*='Continue'], [value*='Continue']")
    private WebElement continueButton;
    
    @FindBy(css = ".alert-error, .error, .has-error, .field-validation-error")
    private List<WebElement> errorMessages;
    
    public RegistrationPage(WebDriver driver) {
        super(driver);
        waitForPageLoad();
        TestListener.log("On registration page: " + getCurrentUrl());
    }
    
    public void fillRegistrationForm() {
        Map<String, String> userData = CSVUtils.getRandomUserData();
        fillFormWithData(userData);
    }
    
    public boolean performNegativeTest() {
        TestListener.log("Starting negative test - leaving required field empty");
        
        try {
            Map<String, String> userData = CSVUtils.getUserDataForNegativeTest();
            fillFormWithData(userData);
            submitForm();
            boolean errorFound = checkForValidationErrors();
            
            if (errorFound) {
                TestListener.logValidation("PASS - Negative test successful: Validation error displayed for empty required field");
                return true;
            } else {
                TestListener.logValidation("FAIL - Negative test failed: No validation error shown for empty required field");
                return false;
            }
            
        } catch (Exception e) {
            TestListener.log("Error during negative test: " + e.getMessage());
            return false;
        }
    }
    
    private void fillFormWithData(Map<String, String> userData) {
    try {
        TestListener.log("Filling registration form with user data");
        fillFieldSafely(firstNameField, userData.getOrDefault("firstName", "John"));
        fillFieldSafely(lastNameField, userData.getOrDefault("lastName", ""));
        fillFieldSafely(emailField, userData.getOrDefault("email", "test@example.com"));
        fillFieldSafely(phoneField, userData.getOrDefault("telephone", "1234567890"));
        fillFieldSafely(companyField, userData.getOrDefault("company", ""));
        fillFieldSafely(address1Field, userData.getOrDefault("address1", "123 Test St"));
        fillFieldSafely(cityField, userData.getOrDefault("city", "Test City"));
        fillFieldSafely(postcodeField, userData.getOrDefault("postcode", "12345"));
        
        selectDropdownValue(countryDropdown, userData.getOrDefault("country", "United States"));
 
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver -> {
    Select stateSelect = new Select(stateDropdown);
    return stateSelect.getOptions().size() > 1;
});

        selectDropdownValue(stateDropdown, userData.getOrDefault("zone", ""));
        
        fillFieldSafely(passwordField, userData.getOrDefault("password", "TestPass123"));
        fillFieldSafely(confirmPasswordField, userData.getOrDefault("password", "TestPass123"));
        
        checkAgreementBox();
        
        TestListener.log("Registration form filled successfully");
        
    } catch (Exception e) {
        TestListener.log("Error filling registration form: " + e.getMessage());
    }
}
    private void fillFieldSafely(WebElement field, String value) {
        try {
            if (field != null && isElementDisplayed(field)) {
                enterText(field, value);
            } else {
                WebElement alternativeField = findFieldAlternatively(field);
                if (alternativeField != null) {
                    enterText(alternativeField, value);
                }
            }
        } catch (Exception e) {
            TestListener.log("Could not fill field: " + e.getMessage());
        }
    }
    
    private WebElement findFieldAlternatively(WebElement originalField) {
        return null;
    }
    
    private void selectDropdownValue(WebElement dropdown, String value) {
        try {
            if (dropdown != null && isElementDisplayed(dropdown) && !value.isEmpty()) {
                Select select = new Select(dropdown);
                
                try {
                    select.selectByVisibleText(value);
                } catch (Exception e) {
                    List<WebElement> options = select.getOptions();
                    for (WebElement option : options) {
                        if (option.getText().contains(value)) {
                            select.selectByVisibleText(option.getText());
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestListener.log("Could not select dropdown value: " + value + " - " + e.getMessage());
        }
    }
    
    private void checkAgreementBox() {
        try {
            if (agreeCheckbox != null && isElementDisplayed(agreeCheckbox)) {
                if (!agreeCheckbox.isSelected()) {
                    click(agreeCheckbox);
                    TestListener.log("Checked agreement checkbox");
                }
            } else {
                List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
                for (WebElement checkbox : checkboxes) {
                    if (checkbox.isDisplayed() && !checkbox.isSelected()) {
                        click(checkbox);
                        TestListener.log("Checked agreement checkbox (alternative method)");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            TestListener.log("Could not check agreement checkbox: " + e.getMessage());
        }
    }
    
    private void submitForm() {
    try {
        WebElement submitButton = findSubmitButton();
        if (submitButton != null) {
            click(submitButton);
            TestListener.log("Submitted registration form");

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")),
                    ExpectedConditions.urlContains("account/success")
                )
            );
        } else {
            TestListener.log("Submit button not found");
        }
    } catch (Exception e) {
        TestListener.log("Error submitting form: " + e.getMessage());
    }
}
    
    private WebElement findSubmitButton() {
        List<String> submitSelectors = List.of(
            ".btn-orange", ".btn-primary", "[title*='Continue']", 
            "[value*='Continue']", ".btn-submit", "input[type='submit']"
        );
        
        for (String selector : submitSelectors) {
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
    public void clearRegistrationForm() {
    try {
        WebElement loginName = driver.findElement(By.id("loginFrm_loginname"));
        WebElement email = driver.findElement(By.id("loginFrm_email"));
        WebElement firstName = driver.findElement(By.id("loginFrm_firstname"));
        WebElement lastName = driver.findElement(By.id("loginFrm_lastname"));
        WebElement password = driver.findElement(By.id("loginFrm_password"));
        WebElement confirmPassword = driver.findElement(By.id("loginFrm_confirm"));
        
        loginName.clear();
        email.clear();
        firstName.clear();
        lastName.clear();
        password.clear();
        confirmPassword.clear();
        
        TestListener.log("Cleared registration form fields");
    } catch (Exception e) {
        TestListener.log("Error clearing registration form: " + e.getMessage());
    }
}
    
    private boolean checkForValidationErrors() {
    try {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-error")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".has-error"))
            )
        );
        
        List<String> errorSelectors = List.of(
            ".alert-error", ".error", ".has-error", 
            ".field-validation-error", ".help-block", ".invalid-feedback"
        );
        
        for (String selector : errorSelectors) {
            List<WebElement> errors = driver.findElements(By.cssSelector(selector));
            for (WebElement error : errors) {
                if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
                    TestListener.log("Validation error found: " + error.getText());
                    return true;
                }
            }
        }
        
        return false;
        
    } catch (Exception e) {
        TestListener.log("No validation errors were found within the time limit.");
        return false;
    }
}
}