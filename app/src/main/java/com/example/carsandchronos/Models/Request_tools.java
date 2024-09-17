package com.example.carsandchronos.Models;

public class Request_tools {

    private int booking_ID;
    private int request_ID;
    private String request_Details;
    private int request_Vehicle_ID;
    private String requested_Mechanics;
    private String requested_Date;
    private String requested_State;

    // Getter and Setter for booking_ID
    public int getBooking_ID() {
        return booking_ID;
    }

    public void setBooking_ID(int booking_ID) {
        this.booking_ID = booking_ID;
    }

    // Getter and Setter for request_ID
    public int getRequest_ID() {
        return request_ID;
    }

    public void setRequest_ID(int request_ID) {
        this.request_ID = request_ID;
    }

    // Getter and Setter for request_Details
    public String getRequest_Details() {
        return request_Details;
    }

    public void setRequest_Details(String request_Details) {
        this.request_Details = request_Details;
    }

    // Getter and Setter for request_Vehicle_ID
    public int getRequest_Vehicle_ID() {
        return request_Vehicle_ID;
    }

    public void setRequest_Vehicle_ID(int request_Vehicle_ID) {
        this.request_Vehicle_ID = request_Vehicle_ID;
    }

    // Getter and Setter for requested_Mechanics
    public String getRequested_Mechanics() {
        return requested_Mechanics;
    }

    public void setRequested_Mechanics(String requested_Mechanics) {
        this.requested_Mechanics = requested_Mechanics;
    }

    // Getter and Setter for requested_Date
    public String getRequested_Date() {
        return requested_Date;
    }

    public void setRequested_Date(String requested_Date) {
        this.requested_Date = requested_Date;
    }

    // Getter and Setter for requested_State
    public String getRequested_State() {
        return requested_State;
    }

    public void setRequested_State(String requested_State) {
        this.requested_State = requested_State;
    }
    public Request_tools()
    {

    }
}
