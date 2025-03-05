package com.example.carsandchronos;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.booking;
import com.example.carsandchronos.Utility.Utility;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> listOfJob;
    Context context;
    Boolean Current_job = false;
    Boolean complete = false;
    Boolean isAssignment = false;
    Date date;
    SimpleDateFormat dateFormat;
    String Todays_date;
    public JobAdapter(List<Job> Jobs,Context context,Boolean stat,Boolean complete,Boolean Assignment) {
        this.listOfJob = Jobs;
        this.context = context;
        this.Current_job = stat;
        this.complete = complete;
        date = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Todays_date = dateFormat.format(date);

        this.isAssignment = Assignment;
        sortJobsByDate();
    }
    String Api_String ;
    //  url = "http://"+ Utility.IP_Adress +":5132/api/Job/GetAssignedJobs/" + data;
    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_assign_plate, parent, false);
        return new JobViewHolder(view);
    }

    private void sortJobsByDate() {
        Collections.sort(listOfJob, new Comparator<Job>() {
            @Override
            public int compare(Job job1, Job job2) {
                return job1.getStartDate().compareTo(job2.getStartDate());  // Ascending order
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = listOfJob.get(position);
        holder.Reference.setText("Reference :"+String.valueOf(job.getBookingId()));
        holder.StartDate.setText("Start Date :"+Utility.convertToDashFormat(job.getStartDate()));
        holder.BookingDetails.setText("Job Description :"+job.getJobDescription());
        holder.end_detail.setText("Expected Completion date :" + Utility.convertToDashFormat(job.getEndDate()));
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/Job/";

       //******************************************compares dates

        if(job.getJobStatus() == 1)
        {
            //remove
        }
        if(complete)
        {
            holder.Decline.setVisibility(View.GONE);
            holder.Accept.setVisibility(View.GONE);
          //  holder.end_detail.setVisibility(View.VISIBLE);
            holder.end_detail.setText("Completion date :"+Utility.convertToDashFormat(job.getDateCompleted()));
        }else
        {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                // Parse job's start date
                Date jobStartDate = dateFormat.parse(Utility.convertToDashFormat(job.getStartDate()));
                Log.d("DateCheck", "here."+job.getJobDescription());
                // Parse current date
                Date currentDate = dateFormat.parse(Todays_date);
                // Compare dates
                if (jobStartDate.before(currentDate)) {
                    // Job start date is before the current date
                    Log.d("DateCheck", "Job start date is before the current date."+job.getJobDescription());
                    holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.red));
                } else if (jobStartDate.after(currentDate)) {
                    // Job start date is after the current date

                } else {
                    // Job start date is equal to the current date

                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("DateCheck", "here."+e.getMessage());
            }
            //****************************************************************
        }
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context,View_job_Details_Activity.class);
            intent.putExtra("Job",job);
            intent.putExtra("bool",Current_job);
            intent.putExtra("ShowButtons",isAssignment);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


        });
        holder.Decline.setOnClickListener(v -> {
            DeclineJob(job, holder.getAdapterPosition());
            Log.d("tester", "onBindViewHolder: Decline is pressed");
            holder.itemView.setVisibility(View.GONE);
        });
        holder.Accept.setOnClickListener(v -> {
            AcceptJob(job,holder.getAdapterPosition());
        });
    }
    private void AcceptJob(Job job, int position) {
        //we send the job nodel here, 1 for accepted , 0 for declined
        job.setJobStatus(1);
        job.setStartDate(Utility.getCurrentDate());
        post_It(job);
        removeItem(position);
    }
    private void removeItem(int position)
    {//removes item from the rec-list
        listOfJob.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listOfJob.size());
    }
    private void DeclineJob(Job job, int position) {
        job.setJobStatus(0);
        post_It(job);
        removeItem(position);
    }
    public void post_It(Job job)
    {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(job);
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(Api_String+job.getJobId())
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Job updated successfully.");
                    Log.d("Tester", "onResponse: Job updated successfully.");
                } else {
                    System.out.println("Failed to update job. Response code: " + response.code());
                    Log.d("Tester", "onResponse: "+"Failed to update job. Response code: " + response.message());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listOfJob.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {

        TextView Reference, StartDate, BookingDetails,end_detail;
        MaterialCardView cardView;
        ImageButton Accept, Decline;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            end_detail = itemView.findViewById(R.id.Job_assign_Completion_date);
            Accept = itemView.findViewById(R.id.ImageButton_Accept);
            Decline = itemView.findViewById(R.id.ImageButton_Decline);
            Reference = itemView.findViewById(R.id.Job_assign_reference);
            StartDate = itemView.findViewById(R.id.Job_assign_StartDate);
            BookingDetails = itemView.findViewById(R.id.Job_assign_Details);
        }
    }


}
