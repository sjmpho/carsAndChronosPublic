package com.example.carsandchronos.Models;

import java.io.Serializable;

public class Notification implements Serializable {
    private Integer notification_ID;
    private String notification_Type; // nullable
    private Integer booking_ID; // nullable
    private String notification_details; // nullable
    private String notification_header; // nullable
    private String notifcation_Date; // nullable
    private Integer notification_Sender_ID;
    private Integer notification_Reciver_ID;
    private String notication_FCM; // nullable

    // Getters and Setters
    public Integer getNotification_ID() {
        return notification_ID;
    }

    public void setNotification_ID(Integer notification_ID) {
        this.notification_ID = notification_ID;
    }

    public String getNotification_Type() {
        return notification_Type;
    }

    public void setNotification_Type(String notification_Type) {
        this.notification_Type = notification_Type;
    }

    public Integer getBooking_ID() {
        return booking_ID;
    }

    public void setBooking_ID(Integer booking_ID) {
        this.booking_ID = booking_ID;
    }

    public String getNotification_details() {
        return notification_details;
    }

    public void setNotification_details(String notification_details) {
        this.notification_details = notification_details;
    }

    public String getNotification_header() {
        return notification_header;
    }

    public void setNotification_header(String notification_header) {
        this.notification_header = notification_header;
    }

    public String getNotifcation_Date() {
        return notifcation_Date;
    }

    public void setNotifcation_Date(String notifcation_Date) {
        this.notifcation_Date = notifcation_Date;
    }

    public Integer getNotification_Sender_ID() {
        return notification_Sender_ID;
    }

    public void setNotification_Sender_ID(Integer notification_Sender_ID) {
        this.notification_Sender_ID = notification_Sender_ID;
    }

    public Integer getNotification_Reciver_ID() {
        return notification_Reciver_ID;
    }

    public void setNotification_Reciver_ID(Integer notification_Reciver_ID) {
        this.notification_Reciver_ID = notification_Reciver_ID;
    }

    public String getNotication_FCM() {
        return notication_FCM;
    }

    public void setNotication_FCM(String notication_FCM) {
        this.notication_FCM = notication_FCM;
    }
}
