package com.example.gettour_api.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 This class is used for taking datas from config file
 */

public class Config {

    public static String getConfigProp(String property){
        Properties prop = new Properties();
        String fileName = "app.config";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {

        }
        try {
            prop.load(is);
        } catch (IOException ex) {

        }
        return prop.getProperty(property);
    }
}