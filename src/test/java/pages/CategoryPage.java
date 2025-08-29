package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestListener;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class CategoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean hasAtLeastThreeProducts() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".product-thumb, .thumbnails .thumbnail, .product-grid")));
            
            List<WebElement> products = driver.findElements(
                By.cssSelector(".product-thumb, .thumbnails .thumbnail, .product-grid .product"));
            
            TestListener.log("Category page has " + products.size() + " products");
            return products.size() >= 3;
            
        } catch (Exception e) {
            TestListener.log("Error checking product count: " + e.getMessage());
            return false;
        }
    }

   public ProductPage selectRandomProduct() {
    try {
        TestListener.log("Attempting to select random product...");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".product-grid, .thumbnails, .product-list")));
        
        List<WebElement> productLinks = wait.until(ExpectedConditions
            .presenceOfAllElementsLocatedBy(By.cssSelector(
                ".product-thumb h4 a, " +         
                ".name a, " +                      
                ".thumbnails .thumbnail h4 a, " +  
                "a[href*='product_id']:not(.btn)" 
            )));
        
        if (productLinks.isEmpty()) {
            throw new RuntimeException("No product links found on category page");
        }
        
        TestListener.log("Found " + productLinks.size() + " product links");
        
        List<WebElement> visibleProducts = productLinks.stream()
            .filter(WebElement::isDisplayed)
            .filter(WebElement::isEnabled)
            .filter(el -> {
                String text = el.getText().trim();
                return !text.isEmpty() && 
                       !text.equalsIgnoreCase("Add to Cart") &&
                       !text.equalsIgnoreCase("Buy Now");
            })
            .toList();
        
        if (visibleProducts.isEmpty()) {
            throw new RuntimeException("No valid product links found");
        }
        
        TestListener.log(visibleProducts.size() + " valid product links found");
        Random random = new Random();
        WebElement selectedProduct = visibleProducts.get(random.nextInt(visibleProducts.size()));
        
        String productName = selectedProduct.getText().trim();
        TestListener.log("Selected random product: " + productName);
        
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectedProduct);
        TestListener.log("Successfully clicked on product");
        
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".productpage")),
            ExpectedConditions.visibilityOfElementLocated(By.id("product")),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-info")),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-details"))
        ));
        
        return new ProductPage(driver);
        
    } catch (Exception e) {
        TestListener.log("Failed to select random product: " + e.getMessage());
        throw new RuntimeException("Failed to select product: " + e.getMessage(), e);
    }
}

    public WebElement retryFindElement(By locator, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            } catch (StaleElementReferenceException e) {
                TestListener.log("Stale element, retrying... (" + (i + 1) + "/" + maxRetries + ")");
                if (i == maxRetries - 1) throw e;
            }
        }
        return null;
    }

    public String getCategoryName() {
        try {
            WebElement categoryTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".heading-title, .category-title, h1")));
            return categoryTitle.getText();
        } catch (Exception e) {
            return "Unknown Category";
        }
    }
}