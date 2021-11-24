package com.revature.ormnl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.revature.ormnl.util.PropertiesSingleton.loadProperties;

public class ConnectionSingleton {
    private static Properties properties;
    //    private static final String propertiesPath = "src/main/resources/application.properties"; // for testing, remove in package
//    private static final String propertiesPath = "application.properties"; // for package
//
    private static Connection instance;
//
//    private static void loadProperties(){
//        properties = new Properties();
//        //        try (InputStream stream = new FileInputStream(new File(propertiesPath).getAbsolutePath())) {
//        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath)) {
//            properties.load(stream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private ConnectionSingleton(){

    }

    public static Connection getInstance(){
        if (properties == null) {
            properties = loadProperties();
        }
        try {
            if(instance == null || instance.isClosed()){
                try{
                    Class.forName("org.postgresql.Driver");
                    instance = DriverManager.getConnection(
                            properties.getProperty("url"),
                            properties.getProperty("username"),
                            properties.getProperty("password"));
                }
                catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

}
