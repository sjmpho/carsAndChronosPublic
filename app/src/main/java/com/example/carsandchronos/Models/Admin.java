package com.example.carsandchronos.Models;

import java.io.Serializable;

public class Admin implements Serializable {

    String name,surname,number,email,password;
    int adminId,identifier;

    public Admin(String name, String surname, String number, String email, String password, int adminId, int identifier) {
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.email = email;
        this.password = password;
        this.adminId = adminId;
        this.identifier = identifier;
    }

    public Admin() {
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

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
