package com.revature.ormnl.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesSingleton {
    private static Properties properties;
    //    private static final String propertiesPath = "src/main/resources/application.properties"; // for testing, remove in package
    private static final String propertiesPath = "application.properties"; // for package

    private static void loadPropertiesFromFile(){
        properties = new Properties();
        //        try (InputStream stream = new FileInputStream(new File(propertiesPath).getAbsolutePath())) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath)) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadProperties() {
        if (properties == null) {
            loadPropertiesFromFile();
        }
        return properties;
    }
}
