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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.JobAdapter;
import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.CurrentJobAdapter;
import com.example.carsandchronos.Utility.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Mech_Current_jobs extends AppCompatActivity {
    List<Job> filteredJobList = new ArrayList<>();
    EditText Search;
    private Gson gson;
    private int data;
    private OkHttpClient client;
    private CurrentJobAdapter Current_jobAdapter;
    private List<Job> jobList;
    RecyclerView recyclerView;
    Mechanic mechanic;
    int MechID;
    RelativeLayout loading_wall;
    TextView loading_wall_text;
    ProgressBar loading_wall_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_current_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Search = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.rec_current_jobs);
        Intent intent = getIntent();
        if(intent != null)
        {
            mechanic = (Mechanic) intent.getSerializableExtra("mechanic");
            MechID = mechanic.getMechId();
            data = MechID;


            client = new OkHttpClient();
            gson = new Gson();

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            jobList = new ArrayList<>();
            Current_jobAdapter = new CurrentJobAdapter(jobList,this);
            recyclerView.setAdapter(Current_jobAdapter);

            ListAssignedBookings();
        }

        loading_wall = findViewById(R.id.RL_loading_wall);
        loading_wall_text = findViewById(R.id.LL_text);
        loading_wall_progressBar = findViewById(R.id.LL_progress_Bar);


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
                    Current_jobAdapter = new CurrentJobAdapter(jobList,getApplicationContext());
                    recyclerView.setAdapter(Current_jobAdapter);
                    Current_jobAdapter.notifyDataSetChanged();
                }else {
                    filterJobs(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });



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
                        Current_jobAdapter = new CurrentJobAdapter(filteredJobList,getApplicationContext());
                        recyclerView.setAdapter(Current_jobAdapter);
                        Current_jobAdapter.notifyDataSetChanged();
                        return;
                    }
                }catch (Exception ex)
                {
                    Log.d("dude", "filterJobs: exception "+ex.getMessage());
                }

            }
        }
        List<Job> clere = new ArrayList<>();

        Current_jobAdapter = new CurrentJobAdapter(clere,getApplicationContext());
        recyclerView.setAdapter(Current_jobAdapter);
        Current_jobAdapter.notifyDataSetChanged();
    }

    private void ListAssignedBookings() {
        String url = "http://"+ Utility.IP_Adress +":5132/api/Job/GetAssignedJobs/" + data;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("tester", "onFailure: failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type bookingListType = new TypeToken<List<Job>>() {}.getType();
                    List<Job> jobs = gson.fromJson(json, bookingListType);
                    Log.d("tester", "onResponse: success");

                    runOnUiThread(() -> {
                        jobList.clear();

                        // jobList.addAll(jobs);
                        for (Job job: jobs) {
                            if (job.getJobStatus() == 1) {
                                jobList.add(job);
                            }
                        }

                        Current_jobAdapter.notifyDataSetChanged();
                        LoadingWall_Activator();
                    });
                }
            }
        });
    }
    private void LoadingWall_Activator()
    {
        if(Current_jobAdapter == null){
            loading_wall.setVisibility(View.VISIBLE);
        }
        else if(Current_jobAdapter.getItemCount()>0)
        {
            loading_wall.setVisibility(View.GONE);
        }else if(Current_jobAdapter.getItemCount() == 0)
        {
            loading_wall_progressBar.setVisibility(View.GONE);
            loading_wall_text.setText("No new Jobs");
        }
    }
}