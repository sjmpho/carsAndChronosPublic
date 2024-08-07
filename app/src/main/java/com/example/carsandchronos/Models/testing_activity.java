package com.example.carsandchronos.Models;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.CustomCalendarView;

import java.util.HashSet;
import java.util.Set;

public class testing_activity extends AppCompatActivity {

    private CustomCalendarView customCalendarView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        customCalendarView = findViewById(R.id.custom_calendar_view2);
        // Add logic to retrieve the days with appointments and add them to the highlightedDays set
        // For example:
        if (customCalendarView != null) {
            Set<Integer> highlightedDays = new HashSet<>();
            // Add logic to retrieve the days with appointments and add them to the highlightedDays set
            highlightedDays.add(5); // Example: Highlight day 5
            highlightedDays.add(15); // Example: Highlight day 15
            highlightedDays.add(25); // Example: Highlight day 25
            customCalendarView.setHighlightedDays(highlightedDays);
        } else {
            Log.e("CustomCalendarView", "CustomCalendarView is null. Check the layout and IDs.");
        }
    }
}