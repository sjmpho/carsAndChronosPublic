package com.example.carsandchronos.Mechanic_Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.JobAdapter;
import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.JobViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Schedule_viewer extends AppCompatActivity {
    Mechanic mechanic;
    int mechId;
    ImageButton back;
    Boolean complete = false;
    List<Job> jobList = new ArrayList<>();
    JobAdapter jobAdapter;
    private JobViewModel jobViewModel;
    private HashSet<String> appointmentDates;
    private CalendarView calendarView;
    RecyclerView recyclerView;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    String date ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //"yyyy/MM/dd"
        back = findViewById(R.id.backPressed);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.rec_schedule_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(v -> {
            finish();
        });
        jobAdapter = new JobAdapter(jobList,getApplicationContext(),false,complete,true);
        Log.d("things", "testing things: 2 ");
        recyclerView.setAdapter(jobAdapter);

        if(getIntent() != null)
        {
            mechanic = (Mechanic) getIntent().getSerializableExtra("mechanic");
            mechId = mechanic.getMechId();
            jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);
            jobViewModel.getJobs().observe(this, jobs -> {
                jobList.clear();
                for (Job job : jobs) {
                    if (job.getJobStatus() == 0) {
                        jobList.add(job);
                    }
                }
                Log.d("things", "testing things: 4 ");
                jobAdapter.notifyDataSetChanged();
                Log.d("things", "testing things: 5 ");
                //  LoadingWall_Activator();

            });
            Log.d("things", "testing things: 6 ");
            jobViewModel.fetchJobs(mechId);
            Log.d("things", "testing things: 7 ");
        }//yyyy/MM/dd

        // Example input date string
      /*  Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JULY, 24);
        Date highlightDate = calendar.getTime();*/

        // Highlight the date with the specified color

        calendar = Calendar.getInstance();
        calendarView.setDate(calendar.getTimeInMillis(), false, true);
        calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance);
        calendarView.setWeekDayTextAppearance(R.style.CustomDateTextAppearance);


        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        calendar = Calendar.getInstance();
        // Set listener for calendar date changes
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateRec(calendar.getTimeInMillis());
        });

    }

    private void updateDateRec(long timeInMillis) {
        calendar.setTimeInMillis(timeInMillis);
        String formattedDate = dateFormat.format(calendar.getTime());

        jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);
        jobViewModel.getJobs().observe(this, jobs -> {
            jobList.clear();
            for (Job job : jobs) {
                if (job.getStartDate().equals(formattedDate)) {
                    jobList.add(job);
                }
            }
            Log.d("things", "testing things: 4 ");
            jobAdapter.notifyDataSetChanged();
            Log.d("things", "testing things: 5 ");
            //  LoadingWall_Activator();

        });
        Log.d("things", "testing things: 6 ");
        jobViewModel.fetchJobs(mechId);
        Log.d("things", "testing things: 7 ");
        Log.d("date_string", "updateDateText: date " + formattedDate);
    }
}

