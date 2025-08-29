# E-Commerce Automated Testing Suite

## Project Overview

This project is a comprehensive automated testing suite for the e-commerce website [https://automationteststore.com/](https://automationteststore.com/). It demonstrates real-world UI automation capabilities including dynamic element detection, robust error handling, and professional reporting.

## Architecture & Design Patterns

### Page Object Model (POM)
- **BasePage**: Foundation class with common WebDriver operations
- **Page Classes**: Encapsulate page-specific elements and actions
- **Dynamic Locators**: No hardcoded selectors - adaptable to UI changes

### Test Framework
- **TestNG**: For test execution, annotations, and parallel execution
- **WebDriverManager**: Automatic driver management
- **CSV Data-Driven**: External test data management
- **Automatic Reporting**: TestListener for screenshots and logging

## Project Structure

```
ECommerce_Testing_Assignment/
│
├── src/
│   ├── pages/
│   │   ├── BasePage.java          # Common page functionality
│   │   ├── HomePage.java          # Homepage operations
│   │   ├── CategoryPage.java      # Category browsing
│   │   ├── ProductPage.java       # Product details & cart operations
│   │   ├── CartPage.java          # Shopping cart validation
│   │   ├── CheckoutPage.java      # Checkout process
│   │   └── RegistrationPage.java  # User registration & validation
│   │
│   ├── tests/
│   │   ├── ECommerceTest.java     # Main test class
│   │   └── BaseTestClass.java     # Common test functionality
│   │
│   └── utils/
│       ├── DriverFactory.java     # WebDriver management
│       ├── ConfigReader.java      # Configuration handling
│       ├── CSVReader.java         # Test data reading
│       ├── ScreenshotUtil.java    # Screenshot capture
│       ├── TestListener.java      # TestNG listener for reporting
│       └── ReportHelper.java      # Report generation utilities
│
├── testdata.csv                   # External test data
├── screenshots/                   # Auto-generated failure screenshots
├── report.txt                     # Execution log and summary
├── README.md                      # This documentation
├── pom.xml                        # Maven dependencies
├── testng.xml                     # TestNG configuration
└── .gitignore                     # Git ignore rules
```

## Key Features

### Test Scenarios Implemented

1. **Homepage & Category Verification**
   - Dynamic category detection (no hardcoded names)
   - Random category navigation
   - Product count validation (≥3 products)

2. **Product Selection & Cart Addition**
   - Random product selection (2 products)
   - Product information capture (name, price, URL)
   - Out-of-stock handling with logging
   - Cart counter validation

3. **Cart & Checkout Workflow**
   - Cart item validation (name, price, total)
   - Checkout process navigation
   - Registration form completion with CSV data

4. **Negative Scenario Testing**
   - Required field validation
   - Error message verification
   - Automatic failure screenshot capture


### Technical Implementation

- **WebDriverWait**: No `Thread.sleep()` except for strategic delays
- **Dynamic Element Location**: Multiple fallback selectors
- **Error Recovery**: Graceful failure handling with logging
- **Screenshot on Failure**: Automatic capture via TestListener
- **CSV Data Management**: External test data with random selection

## Prerequisites

### System Requirements
- **Java**: Version 11 or higher
- **Maven**: Version 3.6 or higher
- **Browser**: Chrome, Firefox, or Edge

### Dependencies (Auto-managed by Maven)
- Selenium WebDriver 4.15.0
- TestNG 7.8.0
- WebDriverManager 5.6.2
- OpenCSV 5.8
- Apache Commons IO 2.11.0

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/eswar3330/ECommerce_Testing_Assignment
cd ECommerce_Testing_Assignment
```

### 2. Install Dependencies
```bash
mvn clean compile
```

### 3. Verify Test Data
Ensure `testdata.csv` contains valid test data:
```csv
firstName,lastName,email,telephone,company,address1,city,country,zone,postcode,password
John,Doe,john.doe.test@example.com,+1234567890,Acme Corp,123 Main St,New York,United States,New York,10001,SecurePass123!
```

## ▶Running Tests

### Basic Execution
```bash
# Run all tests with default browser (Chrome)
mvn test

# Run with specific browser
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge

# Run specific test suite
mvn test -Dtest=ECommerceTest
```

### Alternative Execution Methods
```bash
# Using TestNG XML directly
mvn test -Dtest=testng.xml

# Run with Maven profiles
mvn test -Pchrome
mvn test -Pfirefox

# Clean previous results and run
mvn clean test
```

### IDE Execution
- Right-click `testng.xml` → Run As → TestNG Suite
- Right-click `ECommerceTest.java` → Run As → TestNG Test

## Reports & Results

### 1. Execution Report (`report.txt`)
- Detailed test execution log
- Product information captured
- Validation results
- Error messages and stack traces
- Final summary with totals

### 2. Screenshots (`screenshots/` folder)
- Automatically captured on test failures
- Timestamped for easy identification
- Named by test method for clarity

### 3. TestNG Reports
- HTML reports in `target/surefire-reports/`
- Test execution timeline
- Pass/fail statistics

## Test Results Interpretation

### Success Indicators
✅ All categories detected dynamically  
✅ Products successfully added to cart  
✅ Cart totals calculated correctly  
✅ Negative validation triggers error messages  

### Common Issues & Solutions

| Issue | Likely Cause | Solution |
|-------|--------------|----------|
| No categories found | Website structure changed | Update locators in HomePage.java |
| Products not adding to cart | Out of stock or button missing | Check product availability logic |
| Screenshot capture fails | Permissions or storage issue | Verify screenshots/ directory exists |
| CSV reading errors | File not found or malformed | Verify testdata.csv format and location |

## Configuration

### Browser Configuration
Edit `testng.xml` to change default browser:
```xml
<parameter name="browser" value="firefox"/>
```

### Timeout Configuration
Modify timeouts in `ConfigReader.java`:
```java
properties.setProperty("timeout", "20"); // Increase for slower systems
```

### Test Data
Add more test users in `testdata.csv` for varied testing scenarios.

## Known Issues & Limitations

1. **Website Dependency**: Tests depend on external website availability
2. **Dynamic Content**: Some elements may load slowly requiring timeout adjustments  
3. **Browser Compatibility**: Tested primarily on Chrome; other browsers may need locator adjustments
4. **Parallel Execution**: Currently disabled due to shared cart state

For issues or questions:
1. Check the `report.txt` for detailed error information
2. Review screenshots in the `screenshots/` folder
3. Verify all prerequisites are installed correctly
4. Consult TestNG documentation for framework-specific issues

---

**Author**: Eswar Reddy

> **Note**: This testing suite demonstrates professional automation practices including dynamic element handling, comprehensive error management, and detailed reporting suitable for real-world e-commerce testing scenarios.