package com.example.carsandchronos.Models;

import java.io.Serializable;

public class Customer implements Serializable {
    private int customerId;
    private String name;
    private String surname;
    private String number;
    private String email;
    private String password;
    private String Identifier ;

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
    }

    // Default constructor
    public Customer() {
    }

    // Parameterized constructor
    public Customer(int customerId, String name, String surname, String number, String email, String password) {
        this.customerId = customerId;
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", number='" + number + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

