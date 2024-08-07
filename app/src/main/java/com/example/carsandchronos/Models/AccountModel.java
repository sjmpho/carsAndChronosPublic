package com.example.carsandchronos.Models;

public class AccountModel {

    private String name, lastName, emailAdress, password;
    private String phone_number;
  private int identifier;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getUserIdentifier() {
        return UserIdentifier;
    }

    public void setUserIdentifier(int userIdentifier) {
        UserIdentifier = userIdentifier;
    }

    private int UserID,UserIdentifier;

    public AccountModel() {
    }

    public AccountModel(String name, String lastname, String emailAdress, String userPassword, int userID, int userIdentifier) {
        this.name = name;
        lastName = lastname;
        this.emailAdress = emailAdress;
        password = userPassword;
        UserID = userID;
        UserIdentifier = userIdentifier;
    }
}
