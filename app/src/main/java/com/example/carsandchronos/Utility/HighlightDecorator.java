package com.example.carsandchronos.Utility;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;


import com.google.android.material.datepicker.DayViewDecorator;

import java.util.Calendar;
import java.util.Date;

public class HighlightDecorator {

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable highlightDrawable;
    private final int color;
    private Date date;

    public HighlightDecorator(int color, Date date) {
        highlightDrawable = new ColorDrawable(color);
        this.color = color;
        this.date = date;
    }


}

