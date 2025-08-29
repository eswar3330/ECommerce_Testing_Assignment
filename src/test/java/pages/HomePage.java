package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestListener;
import utils.ConfigReader;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomePage extends BasePage {

    private By categoryLinksLocator = By.cssSelector("ul.categorymenu > li > a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToHomePage() {
        driver.get(ConfigReader.getUrl());
        waitForPageLoad();
        TestListener.log("Navigated to homepage: " + getCurrentUrl());
    }

    public List<String> getAllCategoryNames() {
    List<String> categoryNames = new ArrayList<>();
    try {
        List<WebElement> categoryLinks = waitForElementsLocated(categoryLinksLocator);
        
        List<String> emptyCategories = List.of();
        
        for (WebElement categoryLink : categoryLinks) {
            String categoryText = categoryLink.getText().trim();
            if (!categoryText.isEmpty() && !emptyCategories.contains(categoryText.toUpperCase())) {
                categoryNames.add(categoryText);
            }
        }
        
        TestListener.log("Dynamic category detection found " + categoryNames.size() + " categories:");
        for (String category : categoryNames) {
            TestListener.log("   - " + category);
        }
    } catch (Exception e) {
        TestListener.log("Failed to get categories dynamically: " + e.getMessage());
        throw new RuntimeException("Category links not found or not visible.");
    }
    return categoryNames;
}

    public CategoryPage navigateToRandomCategory() {
        List<String> categories = getAllCategoryNames();

        if (categories.isEmpty()) {
            throw new RuntimeException("No valid categories with products found to navigate to.");
        }

        Random random = new Random();
        String selectedCategory = categories.get(random.nextInt(categories.size()));
        
        try {
            WebElement categoryElement = findCategoryElement(selectedCategory);
            if (categoryElement != null) {
                click(categoryElement);
                TestListener.log("Successfully navigated to random category: " + selectedCategory);
                return new CategoryPage(driver);
            } else {
                throw new RuntimeException("Could not find clickable element for category: " + selectedCategory);
            }
        } catch (Exception e) {
            TestListener.log("Failed to navigate to category '" + selectedCategory + "': " + e.getMessage());
            throw new RuntimeException("Failed to navigate to category: " + selectedCategory, e);
        }
    }
    
    private WebElement findCategoryElement(String categoryName) {
        List<By> categoryLocators = List.of(
                By.linkText(categoryName),
                By.partialLinkText(categoryName),
                By.xpath("//a[contains(text(), '" + categoryName + "')]"),
                By.cssSelector("ul.categorymenu a[title*='" + categoryName + "']")
        );

        for (By locator : categoryLocators) {
            try {
                WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(locator));
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}