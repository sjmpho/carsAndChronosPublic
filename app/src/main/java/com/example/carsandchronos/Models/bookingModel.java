package com.example.carsandchronos.Models;

import java.sql.Timestamp;

public class bookingModel {
    private int id, Payment ,Assigned;
    private String userID;
    private String bookingDetails,Pstatus,Preference,Package;

    private String Job_ID,Reference_number,Vehicle_make,Vehicle_Model,Production_year,booking_type;

    public int getPayment() {
        return Payment;
    }

    public void setPayment(int payment) {
        Payment = payment;
    }

    public int getAssigned() {
        return Assigned;
    }

    public void setAssigned(int assigned) {
        Assigned = assigned;
    }

    public String getPstatus() {
        return Pstatus;
    }

    public void setPstatus(String pstatus) {
        Pstatus = pstatus;
    }

    public String getPreference() {
        return Preference;
    }

    public void setPreference(String preference) {
        Preference = preference;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public String getJob_ID() {
        return Job_ID;
    }

    public void setJob_ID(String job_ID) {
        Job_ID = job_ID;
    }

    public String getReference_number() {
        return Reference_number;
    }

    public void setReference_number(String reference_number) {
        Reference_number = reference_number;
    }

    public String getVehicle_make() {
        return Vehicle_make;
    }

    public void setVehicle_make(String vehicle_make) {
        Vehicle_make = vehicle_make;
    }

    public String getVehicle_Model() {
        return Vehicle_Model;
    }

    public void setVehicle_Model(String vehicle_Model) {
        Vehicle_Model = vehicle_Model;
    }

    public String getProduction_year() {
        return Production_year;
    }

    public void setProduction_year(String production_year) {
        Production_year = production_year;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(String bookingDetails) {
        this.bookingDetails = bookingDetails;
    }
}
