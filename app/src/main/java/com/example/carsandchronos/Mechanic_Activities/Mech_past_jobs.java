package com.example.carsandchronos.Mechanic_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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

import java.util.ArrayList;
import java.util.List;

public class Mech_past_jobs extends AppCompatActivity {
    RecyclerView recyclerView;
    private JobViewModel jobViewModel;
    Boolean complete = true;
    JobAdapter jobAdapter;
    List<Job> jobList = new ArrayList<>();
    List<Job> filteredJobList = new ArrayList<>();
    RelativeLayout loading_wall;
    TextView loading_wall_text;
    ProgressBar loading_wall_progressBar;
    EditText Search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_past_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loading_wall = findViewById(R.id.RL_loading_wall);
        loading_wall_text = findViewById(R.id.LL_text);
        loading_wall_progressBar = findViewById(R.id.LL_progress_Bar);

        Search = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.rec_past_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobAdapter = new JobAdapter(jobList,getApplicationContext(),false ,complete,false);
        recyclerView.setAdapter(jobAdapter);


        recyclerView.setAdapter(jobAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            Mechanic mechanic = (Mechanic) intent.getSerializableExtra("mechanic");
            int mechId = mechanic.getMechId();

            jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);
            jobViewModel.getJobs().observe(this, jobs -> {
                jobList.clear();
                for (Job job : jobs) {
                    if (job.getJobStatus() == 2) {
                        jobList.add(job);
                    }
                }

                jobAdapter.notifyDataSetChanged();
                LoadingWall_Activator();
            });

            jobViewModel.fetchJobs(mechId);
        }
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("checking", "onTextChanged: changed to "+charSequence.toString());
                String input = String.valueOf(charSequence).trim();
                if(input.isEmpty())
                {
                    Log.d("dude", "onTextChanged: it is empty");
                    jobAdapter = new JobAdapter(jobList,getApplicationContext(),false ,complete,false);
                    recyclerView.setAdapter(jobAdapter);
                    jobAdapter.notifyDataSetChanged();
                }else {
                    filterJobs(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }
    private void LoadingWall_Activator()
    {
        if(jobAdapter == null){
            loading_wall.setVisibility(View.VISIBLE);
        }
        else if(jobAdapter.getItemCount()>0)
        {
            loading_wall.setVisibility(View.GONE);
        }else if(jobAdapter.getItemCount() == 0)
        {
            loading_wall_progressBar.setVisibility(View.GONE);
            loading_wall_text.setText("No no past jobs");
        }
    }
    private void filterJobs(String query) {
        filteredJobList.clear();
        if (query.isEmpty()) {
            filteredJobList.addAll(jobList);
        } else {
            for (Job job : jobList) {

                try{
                    int searched_text = Integer.parseInt(query);
                    Log.d("checkList", "filterJobs: here"+ searched_text+" must be eqaul to "+ job.getBookingId());
                    if (searched_text == job.getBookingId()) {
                        filteredJobList.add(job);
                        Log.d("checkList", "filterJobs: " + filteredJobList.toString());
                        jobAdapter = new JobAdapter(filteredJobList,getApplicationContext(),false ,complete,false);
                        recyclerView.setAdapter(jobAdapter);
                        jobAdapter.notifyDataSetChanged();
                        return;
                    }
                }catch (Exception ex)
                {
                    Log.d("dude", "filterJobs: exception "+ex.getMessage());
                }

            }
        }
        List<Job> clere = new ArrayList<>();

        jobAdapter = new JobAdapter(clere,getApplicationContext(),false,complete,false);
        Log.d("things", "testing things: 2 ");
        recyclerView.setAdapter(jobAdapter);
        jobAdapter.notifyDataSetChanged();
    }

}