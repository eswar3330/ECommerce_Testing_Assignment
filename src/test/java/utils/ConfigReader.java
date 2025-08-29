package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        properties.setProperty("browser", "chrome");
        properties.setProperty("url", "https://automationteststore.com/");
        properties.setProperty("timeout", "15");
        properties.setProperty("headless", "false");

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            System.out.println("Loaded config from " + CONFIG_FILE_PATH);
        } catch (IOException e) {
            System.out.println("Config file not found; using default config values.");
        }
    }

    public static String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public static String getUrl() {
        return properties.getProperty("url", "https://automationteststore.com/");
    }

    public static int getTimeout() {
        try {
            return Integer.parseInt(properties.getProperty("timeout", "15"));
        } catch (NumberFormatException e) {
            return 15;
        }
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }
}
