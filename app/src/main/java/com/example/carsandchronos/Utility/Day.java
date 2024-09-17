package com.example.carsandchronos.Utility;

public class Day {
    private String date;
    private boolean isSelected;

    public Day(String date, boolean isSelected) {
        this.date = date;
        this.isSelected = isSelected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

