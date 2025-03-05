package com.example.carsandchronos.Utility;

import com.example.carsandchronos.Models.Mechanic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utility {
    static int MechID;
    public static void SetMechID(int mechID)
    {
        Utility.MechID = mechID;
    }
    static Mechanic mechanic;
    public static int getMechID()
    {
        return Utility.MechID;
    }

    public static Mechanic getMechanic() {
        return mechanic;
    }

    public static void setMechanic(Mechanic mechanic) {
        Utility.mechanic = mechanic;
    }

    // Static variables to store IP address and port
    public static String port = "";//192.168.10.249
    public static String IP_Adress = "192.168.10.249"; // Default IP address

    // Static method to set IP address and port
    public static void setSocket(String IP_Adress, String Port) {
        Utility.IP_Adress = IP_Adress;
        Utility.port = Port;
    }

    public static String get_time()
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        // Get the current date and time
        Date currentDate = new Date();

        // Format the time as a string
        String currentTime = timeFormat.format(currentDate);

        return currentTime;
    }

    // Method to get the current date in yyyy/MM/dd format
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    //converts from
    public static String convertToDashFormat(String inputDate) {
        // Regular expression to check if the input is in the format yyyy/MM/dd
        String slashFormatRegex = "\\d{4}/\\d{2}/\\d{2}";

        // If the input matches the yyyy/MM/dd format
        if (inputDate.matches(slashFormatRegex)) {
            try {
                // Parse the input date in yyyy/MM/dd format
                SimpleDateFormat slashDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = slashDateFormat.parse(inputDate);

                // Format the date to yyyy-MM-dd
                SimpleDateFormat dashDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return dashDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the exception as needed
            }
        }

        // If the date is already in the correct format or doesn't match the slash format, return it as is
        return inputDate;
    }


    // Method to generate a random string using UUID
    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}
