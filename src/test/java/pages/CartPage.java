package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.TestListener;
import utils.ReportHelper;
import java.util.List;
import java.util.Map;


public class CartPage extends BasePage {

    private By checkoutButtonLocator = By.cssSelector(".checkout, .btn-checkout, [href*='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
        navigateToCart();
    }

    private void navigateToCart() {
        try {
            List<String> cartSelectors = List.of(".topcart", ".cart-link", "[href*='cart']", ".shopping-cart", "a[href*='checkout/cart']");
            boolean navigatedToCart = false;
            for (String selector : cartSelectors) {
                try {
                    List<WebElement> cartLinks = driver.findElements(By.cssSelector(selector));
                    for (WebElement cartLink : cartLinks) {
                        if (cartLink.isDisplayed()) {
                            click(cartLink);
                            navigatedToCart = true;
                            break;
                        }
                    }
                    if (navigatedToCart) break;
                } catch (Exception e) {
                }
            }
            if (!navigatedToCart) {
                driver.get("https://automationteststore.com/index.php?rt=checkout/cart");
            }
            waitForPageLoad();
            TestListener.log("Navigated to cart page: " + getCurrentUrl());
        } catch (Exception e) {
            TestListener.log("Failed to navigate to cart: " + e.getMessage());
        }
    }


public boolean validateCartItems() {
    try {
        
        List<WebElement> cartElements = driver.findElements(
            By.cssSelector(".cart-item, tr, .product, a")
        );
        TestListener.log("DEBUG: Found " + cartElements.size() + " elements in cart");
        for (WebElement el : cartElements) {
            if (el.isDisplayed()) {
                TestListener.log("DEBUG Cart element: '" + el.getText().trim() + "'");
            }
        }
        
        Map<String, String> expectedProducts = ReportHelper.getAddedProducts();
        
        if (expectedProducts.isEmpty()) {
            TestListener.logValidation("No products were expected in cart");
            return true;
        }
        
        TestListener.log("Validating cart contains " + expectedProducts.size() + " expected products");
        
        boolean allItemsFound = true;
        
        for (String expectedProductName : expectedProducts.keySet()) {
            boolean productFound = isProductInCart(expectedProductName);
            if (productFound) {
                TestListener.logValidation("FOUND in cart: " + expectedProductName);
            } else {
                TestListener.logValidation("MISSING from cart: " + expectedProductName);
                allItemsFound = false;
            }
        }
        
        return allItemsFound;
    } catch (Exception e) {
        TestListener.log("Error validating cart items: " + e.getMessage());
        return false;
    }
}
public String getCartTotal() {
    try {
        List<By> totalLocators = List.of(
            By.cssSelector(".grand-total, .cart-total, .checkout-total, .total"),
            By.xpath("//td[contains(text(),'Total')]/following-sibling::td"),
            By.xpath("//*[contains(text(),'Total') and contains(text(),'$')]")
        );

        for (By locator : totalLocators) {
            try {
                WebElement el = driver.findElement(locator);
                if (el.isDisplayed()) {
                    String raw = el.getText().trim();
                    String cleaned = raw.replaceAll("[^0-9.,$]", "");
                    if (cleaned.contains("$") || cleaned.matches(".*\\d+.*")) {
                        return cleaned.startsWith("$") ? cleaned : "$" + cleaned;
                    }
                }
            } catch (Exception ignored) {}
        }

        TestListener.log("WARNING: Could not find cart total, returning $0.00");
        return "$0.00";
    } catch (Exception e) {
        TestListener.log("Error getting cart total: " + e.getMessage());
        return "$0.00";
    }
}
private boolean isProductInCart(String expectedProductName) {
    try {
        String cartText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        String searchName = expectedProductName.toLowerCase();

        String[] words = searchName.split(" ");
        if (words.length >= 2) {
            return cartText.contains(words[0] + " " + words[1]);
        }
        return cartText.contains(searchName);
    } catch (Exception e) {
        TestListener.log("Error finding product in cart: " + e.getMessage());
        return false;
    }
}

public boolean validateCartTotal() {
    try {
        String actualTotal = getCartTotal();
        boolean isValid = ReportHelper.validateCartTotal(actualTotal);
        
        if (isValid) {
            TestListener.logValidation("Cart total validation passed: " + actualTotal);
        } else {
            TestListener.logValidation("Cart total validation failed. Expected pattern, got: " + actualTotal);
        }
        
        return isValid;
        
    } catch (Exception e) {
        TestListener.log("Error validating cart total: " + e.getMessage());
        return false;
    }
}
    public CheckoutPage proceedToCheckout() {
        try {
            List<WebElement> checkoutButtons = driver.findElements(checkoutButtonLocator);
            for (WebElement btn : checkoutButtons) {
                if (btn.isDisplayed() && btn.isEnabled()) {
                    click(btn);
                    TestListener.log("Proceeding to checkout");
                    return new CheckoutPage(driver);
                }
            }
            throw new RuntimeException("Checkout button not found or not clickable");
        } catch (Exception e) {
            TestListener.log("Failed to proceed to checkout: " + e.getMessage());
            throw e;
        }
    }
}
