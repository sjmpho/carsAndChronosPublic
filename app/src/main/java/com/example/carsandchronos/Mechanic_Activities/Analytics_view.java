package com.example.carsandchronos.Mechanic_Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.carsandchronos.JobAdapter;
import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.JobViewModel;
import com.example.carsandchronos.Utility.Utility;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Analytics_view extends AppCompatActivity {


    private JobViewModel jobViewModel;
    Boolean complete = true;
    JobAdapter jobAdapter;
    int rating;
    int job_estimate = 0;
    List<Job> jobList = new ArrayList<>();
    int positive_job_days ;
    int negetive_job_days ;
    List<Job> positiveJobs =  new ArrayList();//contain jops with ontime finish
    List<Job> NegetiveJobs =  new ArrayList();// contain jobs with overtime finish
    Mechanic mechanic ;
    int mechanicId ;
    TextView total_Jobs,Total_onTime,Total_OverTime;
    String mech_role;
    PieChart pieChart;
    int num_jobs_completed ;
    RatingBar ratingbar;///api/MechanicProfile/Update_Rating/
    private static final String Job_estimates_url = "http://"+Utility.IP_Adress+":5132/api/Job/get_job_estimates/";
    private static final String ratings_Save_url = "http://"+Utility.IP_Adress+":5132/api/MechanicProfile/Update_Rating/";
    private static final String BASE_URL = "http://"+ Utility.IP_Adress +":5132/api/MechanicProfile/Rating/"; // Replace with your actual base URL
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ratingbar = findViewById(R.id.ratingBar);
        pieChart = findViewById(R.id.Pie_chart);
        Total_onTime = findViewById(R.id.Analytics_Ontime_txt);
        Total_OverTime = findViewById(R.id.Analytics_overtime_txt);
        total_Jobs = findViewById(R.id.Analytics_total_jobs_done_txt);

        Intent intent = getIntent();
        if (intent != null) {
            mechanic = (Mechanic) intent.getSerializableExtra("mechanic");
           mechanicId = mechanic.getMechId();
            Retrive_Rating();//retrives average rating
            Log.d("testing", "onCreate: mechanig "+mechanic.getRole());
            mech_role = mechanic.getRole();
            get_job_Estimates();
            retrive_completed_Jobs();
        }

        //DayCounter();
    }

    private void addToPie(int positive , int negative)
    {//score
        pieChart.setClickable(false);
        pieChart.update();
                pieChart.setUseInnerValue(true);
                pieChart.setInnerValueSize(100);
                pieChart.setInnerValueString(String.valueOf(positive)+"%");



        pieChart.setInnerValueColor(Color.parseColor("#CCA152"));
        pieChart.addPieSlice(new PieModel("first Slice ",positive, Color.parseColor("#CCA152")));
        pieChart.addPieSlice(new PieModel("secound Slice ",negative, Color.parseColor("#FF0000")));
        pieChart.startAnimation();

    }

    private  void Retrive_Rating()
    {
        String url = BASE_URL +mechanicId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    rating = Integer.parseInt(responseBody);

                    Log.d("Rating", "onResponse: success :"+rating);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          ratingbar.setRating(rating);

                        }
                    });

                } else if (response.code() == 204) {
                    // Handle the case where no content (rating is null)
                    Log.d("Rating", "onResponse: failed 204 :"+response.message());
                } else {
                    // Handle other HTTP error codes
                    Log.d("Rating", "onResponse: failed :"+response.message());
                }
            }
        });
    }

    private void get_job_Estimates()
    {//retrives default job estimates
        String url = Job_estimates_url+mech_role;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("tester ", "onResponse: get estimate " + responseBody);
                    job_estimate = Integer.parseInt(responseBody);
                    // Handle the rating value (e.g., update UI)
                    Log.d("Rating", "onResponse: success :"+rating);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                } else if (response.code() == 204) {
                    // Handle the case where no content (rating is null)
                    Log.d("Rating", "onResponse: failed 204 :"+response.message());
                } else {
                    // Handle other HTTP error codes
                    Log.d("Rating", "onResponse: failed :"+response.message());
                }
            }
        });
    }
    private void DayCounter()
    {

        for(Job job_1 : jobList)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Parse the dates using the formatter
            LocalDate startDate = LocalDate.parse(job_1.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(job_1.getDateCompleted(), formatter);

            // Calculate the number of days between the dates
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

            if(daysBetween <= job_estimate)
            {
                positiveJobs.add(job_1);
                positive_job_days+=daysBetween;
            }else
            {
                NegetiveJobs.add(job_1);
                negetive_job_days+=daysBetween;
            }
            // Print the result


        }
        total_Jobs.setText("Total Jobs : "+jobList.size());
        Total_OverTime.setText("Total Over time jobs: "+NegetiveJobs.size());
        Total_onTime.setText("Total On time jobs: "+positiveJobs.size());

        pieChart_calculations();

    }

    private void pieChart_calculations() {

        int num_positive = positiveJobs.size();

// num_jobs_completed is integer
        int score = (int) (((double) num_positive / num_jobs_completed) * 100);
        int neg_score = 100 - score;




        //===========test average
        int estimate_days = jobList.size() * job_estimate; // Add tolerance
        Log.d("days", "pieChart_calculations: estimate days " + estimate_days);
        int total_days = negetive_job_days + positive_job_days;
        Log.d("days", "pieChart_calculations: total days " + total_days);



        /*4
        * Detailing*
        * Wrap or Respray*
        * Wheel Refurbishment*
        * Sound Installation*
        * Performance Upgrade
        * */
// Calculate the rating with tolerance
        if (total_days <= estimate_days) {
            ratingbar.setRating(5);
            Log.d("days", "pieChart_calculations: rating 5");
            Update_Rating(5);

        } else if (total_days <= estimate_days * 1.2) {
            ratingbar.setRating(4);
            Log.d("days", "pieChart_calculations: rating 4");
            Update_Rating(4);
        } else if (total_days <= estimate_days * 1.5) {
            ratingbar.setRating(3);
            Log.d("days", "pieChart_calculations: rating 3");
            Update_Rating(3);
        } else if (total_days <= estimate_days * 2) {
            ratingbar.setRating(2);
            Log.d("days", "pieChart_calculations: rating 2");
            Update_Rating(2);
        } else {
            ratingbar.setRating(1);
            Log.d("days", "pieChart_calculations: rating 1");
            Update_Rating(1);
        }


        //=====================================

        addToPie(score,neg_score);
      //add the rating



    }
    private void Update_Rating(int rating)
    {
        OkHttpClient client = new OkHttpClient();

        // Define the URL
        String url =ratings_Save_url + mechanicId + "/" + rating;

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(null," " )) // Empty body for PUT request
                .build();

        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful response
                    String responseBody = response.body().string();
                    Log.d("Update Rating", "Rating updated successfully: " + responseBody);
                } else {
                    // Handle unsuccessful response
                    Log.e("Update Rating", "Failed to update rating: " + response.message());
                }
            }
        });
    }

    private void retrive_completed_Jobs()
    {
        jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);
        jobViewModel.getJobs().observe(this, jobs -> {
            jobList.clear();
            for (Job job : jobs) {
                if (job.getJobStatus() == 2) {
                    jobList.add(job);
                    Log.d("checker", "retrive_completed_Jobs: "+job.getJobDescription());
                }
            }
            num_jobs_completed = jobList.size();
            DayCounter();
        });
        jobViewModel.fetchJobs(mechanicId);


    }

    public void Back(View view) {
        finish();
    }
}