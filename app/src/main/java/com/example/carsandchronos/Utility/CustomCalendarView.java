package com.example.carsandchronos.Utility;

import android.content.Context;
import android.widget.CalendarView;

import androidx.annotation.NonNull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carsandchronos.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class CustomCalendarView extends LinearLayout {

    private TextView monthTextView;
    private GridLayout calendarGrid;
    private Calendar calendar;
    private Set<Integer> highlightedDays = new HashSet<>();

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_calendar_view, this, true);

        monthTextView = findViewById(R.id.calendar_month);
        calendarGrid = findViewById(R.id.calendar_grid);

        calendar = Calendar.getInstance();
        updateCalendar();
    }

    public void setHighlightedDays(Set<Integer> days) {
        highlightedDays.clear();
        highlightedDays.addAll(days);
        updateCalendar();
    }

    private void updateCalendar() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getContext().getResources().getConfiguration().locale);
        int year = calendar.get(Calendar.YEAR);
        monthTextView.setText(String.format("%s %d", month, year));

        calendarGrid.removeAllViews();

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarGrid.addView(createEmptyTextView());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayView = createDayTextView(day);
            if (highlightedDays.contains(day)) {
                dayView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }
            calendarGrid.addView(dayView);
        }
    }

    private TextView createEmptyTextView() {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new GridLayout.LayoutParams());
        return textView;
    }

    private TextView createDayTextView(int day) {
        TextView textView = new TextView(getContext());
        textView.setText(String.valueOf(day));
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setLayoutParams(new GridLayout.LayoutParams());
        return textView;
    }
}
