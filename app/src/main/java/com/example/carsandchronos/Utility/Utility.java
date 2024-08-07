package com.example.carsandchronos.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utility {

    public static String IP_Adress ="192.168.10.249";//10.254.29.42 //
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String generateRandomString() {
        // Generate a random UUID and convert it to a string
        return UUID.randomUUID().toString();
    }
}
