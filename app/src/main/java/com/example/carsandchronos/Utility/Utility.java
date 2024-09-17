package com.example.carsandchronos.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utility {
    static int MechID;
    public static void SetMechID(int mechID)
    {
        Utility.MechID = mechID = 0;
    }
    public static int getMechID()
    {
        return Utility.MechID;
    }

    // Static variables to store IP address and port
    public static String port = "";
    public static String IP_Adress = "192.168.10.207"; // Default IP address

    // Static method to set IP address and port
    public static void setSocket(String IP_Adress, String Port) {
        Utility.IP_Adress = IP_Adress;
        Utility.port = Port;
    }

    // Method to get the current date in yyyy/MM/dd format
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Method to generate a random string using UUID
    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}
