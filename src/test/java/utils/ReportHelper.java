package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportHelper {
    private static List<ProductEntry> addedProducts = new ArrayList<>();
    private static int cartItemCount = 0;

    public static void addProductToReport(String productName, String price, String url) {
        addedProducts.add(new ProductEntry(productName, price, url));
        TestListener.logProductInfo(productName, price, url);
    }

    public static void incrementCartCount() {
        cartItemCount++;
    }

    public static int getExpectedCartCount() {
        return cartItemCount;
    }

    public static Map<String, String> getAddedProducts() {
        Map<String, String> map = new HashMap<>();
        int idx = 1;
        for (ProductEntry p : addedProducts) {
            String key = p.name;
            if (map.containsKey(key)) {
                key = key + " (" + idx + ")";
            }
            map.put(key, p.price);
            idx++;
        }
        return map;
    }

    public static double calculateExpectedTotal() {
    double total = 0.0;
    for (ProductEntry p : addedProducts) {
        try {
            String priceValue = p.price.replaceAll("[^0-9.]", "");
            if (!priceValue.isEmpty()) {
                double parsed = Double.parseDouble(priceValue);
                if (parsed > 0 && parsed < 10000) { 
                    total += parsed;
                } else {
                    TestListener.log("[WARNING] Ignoring suspicious price: " + p.price);
                }
            }
        } catch (NumberFormatException e) {
            TestListener.log("[WARNING] Could not parse price for total calculation: " + p.price);
        }
    }
    return total;
}


    public static boolean validateCartTotal(String actualTotal) {
        double expectedTotal = calculateExpectedTotal();
        String expectedTotalStr = String.format("%.2f", expectedTotal);
        try {
            String actualTotalValue = actualTotal.replaceAll("[^0-9.]", "");
            double actualTotalDouble = Double.parseDouble(actualTotalValue);
            boolean isValid = Math.abs(expectedTotal - actualTotalDouble) < 0.01; 
            TestListener.logCartValidation(isValid, "$" + expectedTotalStr, actualTotal);
            return isValid;
        } catch (Exception e) {
            TestListener.logCartValidation(false, "$" + expectedTotalStr, actualTotal);
            return false;
        }
    }

    public static void reset() {
        addedProducts.clear();
        cartItemCount = 0;
    }

    private static class ProductEntry {
        String name;
        String price;
        String url;
        ProductEntry(String n, String p, String u) {
            name = n; price = p; url = u;
        }
    }
}
