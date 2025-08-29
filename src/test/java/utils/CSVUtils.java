package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CSVUtils {
    private static final String CSV_FILE_PATH = "testdata.csv";

    public static Map<String, String> getRandomUserData() {
        Map<String, String> userData = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            List<String[]> records = csvReader.readAll();

            if (records.size() > 1) {
                String[] headers = records.get(0);

                Random random = new Random();
                int randomIndex = random.nextInt(records.size() - 1) + 1;
                String[] dataRow = records.get(randomIndex);

                for (int i = 0; i < headers.length && i < dataRow.length; i++) {
                    userData.put(headers[i].trim(), dataRow[i].trim());
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            userData.put("firstName", "John");
            userData.put("lastName", "Doe");
            userData.put("email", "john.doe@example.com");
            userData.put("password", "TestPass123");
        }

        return userData;
    }

    public static Map<String, String> getUserDataForNegativeTest() {
        Map<String, String> userData = getRandomUserData();
        userData.put("lastName", "");
        return userData;
    }
}
