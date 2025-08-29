package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.TestListener;
import utils.ReportHelper;
import java.util.List;
import java.util.Arrays;
import java.time.Duration;


public class ProductPage extends BasePage {

    @FindBy(css = ".productname h1, .product-name h1, h1")
    private WebElement productName;

    @FindBy(css = ".productfilneprice, .price, .product-price")
    private WebElement productPrice;

    @FindBy(css = ".cart, .btn-cart, [title*='Add to Cart'], .productcart")
    private WebElement addToCartButton;

    @FindBy(css = ".nostock, .outstock, .out-of-stock")
    private WebElement outOfStockIndicator;

    @FindBy(css = ".topcart .label, .cart-info .badge, .cart-total")
    private WebElement cartCounter;

    public ProductPage(WebDriver driver) {
        super(driver);
        waitForPageLoad();
    }

    public ProductInfo getProductInfo() {
    ProductInfo info = new ProductInfo();

    try {
        info.name = getProductName();
        
        if ("Unknown Product".equalsIgnoreCase(info.name)) {
            try {
                WebElement metaName = driver.findElement(By.cssSelector("meta[property='og:title']"));
                info.name = metaName.getAttribute("content");
            } catch (Exception e) {
                try {
                    WebElement title = driver.findElement(By.cssSelector("title"));
                    info.name = title.getText().replace("Automation Test Store", "").trim();
                } catch (Exception ex) {
                    info.name = "Unknown Product";
                }
            }
        }

        info.price = getProductPrice();
        info.url = getCurrentUrl();
        info.isAvailable = isProductAvailable();
        
        TestListener.log("Product Info Retrieved - Name: " + info.name +
                ", Price: " + info.price + ", Available: " + info.isAvailable);

    } catch (Exception e) {
        TestListener.log("Error getting product info: " + e.getMessage());
        info.isAvailable = false;
    }

    return info;
}

    private boolean isProductAvailable() {
        try {
            List<WebElement> outs = driver.findElements(By.cssSelector(".nostock, .outstock"));
            for (WebElement o : outs) {
                if (o.isDisplayed() && !o.getText().trim().isEmpty()) {
                    return false;
                }
            }
            return findAddToCartButton() != null;
        } catch (Exception e) {
            return true;
        }
    }

    private WebElement findAddToCartButton() {
        try {
            WebElement cartButton = driver.findElement(By.cssSelector("a.cart"));
            return cartButton;
        } catch (Exception e) {
            return null;
        }
    }

   public boolean addToCart() {
    ProductInfo info = getProductInfo();
    
    if (!info.isAvailable) {
        TestListener.logSkippedElement("Product: " + info.name, "Out of stock or Add to Cart button missing");
        return false;
    }
    
    try {
        int initialCartCount = ReportHelper.getExpectedCartCount();

        WebElement addToCartButton = findAddToCartButton();
        if (addToCartButton != null) {
            click(addToCartButton);
        } else {
            TestListener.log("Add to Cart button not found or not clickable.");
            return false;
        }

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElementLocated(
                    By.cssSelector(".topcart .label"),
                    String.valueOf(initialCartCount)
                )
            )
        );

        ReportHelper.addProductToReport(info.name, info.price, driver.getCurrentUrl());
        TestListener.log("Product successfully added to cart: " + info.name);
        TestListener.log("[PRODUCT] Name: " + info.name + " | Price: " + info.price + " | URL: " + driver.getCurrentUrl());
        
        return true;
    } catch (Exception e) {
        TestListener.log("Failed to add product to cart: " + e.getMessage());
        return false;
    }
}
    public static class ProductInfo {
        public String name;
        public String price;
        public String url;
        public boolean isAvailable;

        public ProductInfo() {
            this.name = "";
            this.price = "$0.00";
            this.url = "";
            this.isAvailable = false;
        }
    }
    private String getProductName() {
    try {
        List<By> nameSelectors = Arrays.asList(
            By.cssSelector(".product-name h1, .productname h1, h1"),
            By.id("product-name"),
            By.cssSelector("[itemprop='name']")
        );

        for (By selector : nameSelectors) {
            try {
                WebElement nameElement = driver.findElement(selector);
                if (nameElement.isDisplayed()) {
                    String text = nameElement.getText().trim();
                    if (!text.isEmpty()) return text;
                }
            } catch (Exception ignored) {}
        }

        try {
            WebElement metaName = driver.findElement(By.cssSelector("meta[property='og:title']"));
            return metaName.getAttribute("content").trim();
        } catch (Exception ignored) {}

        return "Unknown Product";
    } catch (Exception e) {
        return "Unknown Product";
    }
}

private String getProductPrice() {
    try {
        List<By> priceSelectors = Arrays.asList(
            By.cssSelector(".productfilneprice, .price, .product-price, .current-price, [itemprop='price']"),
            By.xpath("//*[contains(text(),'$')]")
        );

        for (By selector : priceSelectors) {
            try {
                WebElement priceElement = driver.findElement(selector);
                if (priceElement.isDisplayed()) {
                    String raw = priceElement.getText().trim();
                    String cleaned = raw.replaceAll("[^0-9.,$]", "");
                    if (cleaned.contains("$")) {
                        return cleaned;
                    }
                }
            } catch (Exception ignored) {}
        }

        return "$0.00";
    } catch (Exception e) {
        return "$0.00";
    }
}
}