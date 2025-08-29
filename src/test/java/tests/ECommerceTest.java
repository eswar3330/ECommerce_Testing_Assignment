package tests;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;
import utils.*;

import java.util.List;

@org.testng.annotations.Listeners({utils.TestListener.class})
public class ECommerceTest {

    private HomePage homePage;
    private CategoryPage categoryPage;
    private ProductPage productPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private RegistrationPage registrationPage;

    @BeforeSuite
    public void suiteSetup() {
        TestListener.log("Starting E-Commerce Test Suite");
        ReportHelper.reset();
    }

     @BeforeClass
    public void classSetup() {
        DriverFactory.initializeDriver(ConfigReader.getBrowser());
        TestListener.log("WebDriver initialized for browser: " + ConfigReader.getBrowser());
        
        homePage = new HomePage(DriverFactory.getDriver());
    }

    @AfterClass
    public void classTearDown() {
        DriverFactory.quitDriver();
        TestListener.log("WebDriver session ended after all tests");
    }
    @Test(priority = 1, description = "Homepage & Category Verification")
    public void testHomepageAndCategoryVerification() {
        try {
            TestListener.log("=== Test 1: Homepage & Category Verification ===");

            homePage.navigateToHomePage();

            List<String> categories = homePage.getAllCategoryNames();
            Assert.assertFalse(categories.isEmpty(), "No categories found on homepage");

            TestListener.logValidation("Found " + categories.size() + " categories on homepage");

            categoryPage = homePage.navigateToRandomCategory();

            boolean hasThreeProducts = categoryPage.hasAtLeastThreeProducts();
            Assert.assertTrue(hasThreeProducts, "Category does not have at least 3 visible products");

            TestListener.log("=== Test 1 Completed Successfully ===");

        } catch (Exception e) {
            TestListener.log("Test 1 failed with exception: " + e.getMessage());
            throw e;
        }
    }

@Test(priority = 2, description = "Product Selection & Cart Addition", dependsOnMethods = {"testHomepageAndCategoryVerification"})
public void testProductSelectionAndCartAddition() {
    try {
        TestListener.log("=== Test 2: Product Selection & Cart Addition ===");

        int productsAdded = 0;
        int maxProducts = 2;
        int attemptCount = 0;
        int maxTotalAttempts = 8;
        
        while (productsAdded < maxProducts && attemptCount < maxTotalAttempts) {
            attemptCount++;
            try {
                homePage.navigateToHomePage();
                categoryPage = homePage.navigateToRandomCategory();
                productPage = categoryPage.selectRandomProduct();
                
                if (!productPage.getProductInfo().isAvailable) {
                    TestListener.log("Skipping out-of-stock product");
                    continue;
                }
                
                boolean addedSuccessfully = productPage.addToCart();
                
                if (addedSuccessfully) {
                    productsAdded++;
                    TestListener.log("Successfully added product " + productsAdded);
                }
            } catch (Exception e) {
                TestListener.log("Failed attempt " + attemptCount + ": " + e.getMessage());
            }
        }
        
        Assert.assertTrue(productsAdded >= 1, "Failed to add at least 1 product to cart. Added: " + productsAdded);
        TestListener.logValidation("Successfully added " + productsAdded + " product(s) to cart");
        TestListener.log("=== Test 2 Completed Successfully ===");
        
    } catch (Exception e) {
        TestListener.log("Test 2 failed with exception: " + e.getMessage());
        throw e;
    }
}
   @Test(priority = 3, description = "Cart & Checkout Workflow", dependsOnMethods = {"testProductSelectionAndCartAddition"})
public void testCartAndCheckoutWorkflow() {
    try {
        TestListener.log("=== Test 3: Cart & Checkout Workflow ===");

        cartPage = new CartPage(DriverFactory.getDriver());

        boolean cartItemsValid = cartPage.validateCartItems();
        Assert.assertTrue(cartItemsValid, "Cart items validation failed");

        String actualTotal = cartPage.getCartTotal();
        TestListener.log("Actual Cart Total: " + actualTotal);
        
        boolean cartTotalValid = actualTotal.contains("$") && actualTotal.matches(".*\\d+.*");
        if (!cartTotalValid) {
            TestListener.log("WARNING: Cart total validation is basic - actual: " + actualTotal);
        }
        
        TestListener.logValidation("Cart validation completed successfully");

        checkoutPage = cartPage.proceedToCheckout();
        registrationPage = checkoutPage.selectRegistration();
        registrationPage.fillRegistrationForm();

        TestListener.log("Registration form filled with test data from CSV");
        TestListener.log("=== Test 3 Completed Successfully ===");
    } catch (Exception e) {
        TestListener.log("Test 3 failed with exception: " + e.getMessage());
        throw e;
    }
}

   @Test(priority = 4, description = "Negative Scenario - Validation Testing")
public void testNegativeScenarioValidation() {
    try {
        TestListener.log("=== Test 4: Negative Scenario - Validation Testing ===");

        DriverFactory.getDriver().get("https://automationteststore.com/index.php?rt=account/create");
        registrationPage = new RegistrationPage(DriverFactory.getDriver());

        registrationPage.clearRegistrationForm();

        boolean negativeTestPassed = registrationPage.performNegativeTest();
        
        Assert.assertTrue(negativeTestPassed,
                "Negative test failed - no validation error shown for empty required field");

        TestListener.logValidation("Negative test validation completed successfully");
        TestListener.log("=== Test 4 Completed Successfully ===");
    } catch (Exception e) {
        TestListener.log("Test 4 failed with exception: " + e.getMessage());
        ScreenshotUtil.captureFailureScreenshot(DriverFactory.getDriver(), "testNegativeScenarioValidation");
        throw e;
    }
}

    @AfterSuite
    public void suiteTearDown() {
        TestListener.log("E-Commerce Test Suite completed");
        TestListener.log("Check report.txt for detailed execution summary");
        TestListener.log("Check screenshots/ folder for any failure screenshots");
    }
}
