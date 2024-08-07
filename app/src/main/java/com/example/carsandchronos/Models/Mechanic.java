package com.example.carsandchronos.Models;

import java.io.Serializable;

public class Mechanic  implements Serializable {

    String name , surname,role,number,email,password;
    int mechId,leave_Status,identifier;

    public Mechanic() {
    }

    public Mechanic(String name, String surname, String role, String number, String email, String password, int mechId, int leave_Status, int identifier) {
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.number = number;
        this.email = email;
        this.password = password;
        this.mechId = mechId;
        this.leave_Status = leave_Status;
        this.identifier = identifier;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public int getMechId() {
        return mechId;
    }

    public void setMechId(int mechId) {
        this.mechId = mechId;
    }

    public int getLeave_Status() {
        return leave_Status;
    }

    public void setLeave_Status(int leave_Status) {
        this.leave_Status = leave_Status;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
