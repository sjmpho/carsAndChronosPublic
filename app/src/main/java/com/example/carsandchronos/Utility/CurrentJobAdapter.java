package com.example.carsandchronos.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.carsandchronos.JobAdapter;
import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.R;
import com.example.carsandchronos.View_job_Details_Activity;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

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

public class CurrentJobAdapter  extends RecyclerView.Adapter<CurrentJobAdapter.CurrentViewHolder>{
    private List<Job> listOfJob;
    OkHttpClient client = new OkHttpClient();
    Context context;
    Date date;
    SimpleDateFormat dateFormat;
    String Todays_date;
    public CurrentJobAdapter(List<Job> Jobs , Context context) {
        this.listOfJob = Jobs;
        this.context = context;
        date = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Todays_date = dateFormat.format(date);
        sortJobsByDate();
    }
    String Api_String ;
    @NonNull
    @Override
    public CurrentJobAdapter.CurrentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_job_plate, parent, false);
        return new CurrentJobAdapter.CurrentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentJobAdapter.CurrentViewHolder holder, int position) {
        Job job = listOfJob.get(position);
        holder.BookingDetails.setText("Job Description :"+job.getJobDescription());
        holder.StartDate.setText("Start Date :"+Utility.convertToDashFormat(job.getStartDate()));
        holder.Reference.setText("Reference :"+String.valueOf(job.getBookingId()));
        holder.End_date.setText("Expected end date : "+Utility.convertToDashFormat(job.getEndDate()));
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/job/"+job.getJobId();

        holder.Complete.setOnClickListener(v -> {//long press for confirmation
            showDialog(job,position,holder.getAdapterPosition());
        });
        //******************************************compares dates
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse job's start date
            Date jobEndDate = dateFormat.parse(Utility.convertToDashFormat(job.getEndDate()));
            Log.d("DateCheck", "here."+job.getJobDescription());
            // Parse current date
            Date currentDate = dateFormat.parse(Todays_date);//todays date
            // Compare dates
            if (jobEndDate.before(currentDate)) {
                // Job start date is before the current date meaning the job is behind schedule
                Log.d("DateCheck", "Job start date is before the current date."+job.getJobDescription());
                holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.red));
            } else if (jobEndDate.after(currentDate)) {
                // Job start date is after the current date still on schedule
                Log.d("DateCheck", "Job start date is before the current date."+job.getJobDescription());
                holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.teal_200));
            } else {
                // Job start date is equal to the current date
                Log.d("DateCheck", "Job start date is before the current date."+job.getJobDescription());
                holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.teal_200));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("DateCheck", "here."+e.getMessage());
        }

        holder.itemView.setOnClickListener(v -> {
            // goes to view details activity
            Intent intent =new Intent(context, View_job_Details_Activity.class);
            intent.putExtra("Job", job);
            intent.putExtra("bool",true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
    private void sortJobsByDate() {
        Collections.sort(listOfJob, new Comparator<Job>() {
            @Override
            public int compare(Job job1, Job job2) {
                return job1.getStartDate().compareTo(job2.getStartDate());  // Ascending order
            }
        });
    }
    private void removeItem(int position)
    {//removes item from the rec-list
        listOfJob.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listOfJob.size());
    }
    private void showDialog(Job job,int position,int AdapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage("Current Job is  done?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the Yes button click
                        job.setJobStatus(2);
                        job.setDateCompleted(Utility.getCurrentDate());
                        CompleteJob(job,position);//to indicate a complete job = 2 on job status;
                        removeItem(AdapterPosition);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the No button click
                        dialog.dismiss();

                    }
                });
        builder.create().show();
    }

    private void CompleteJob(Job job, int position)
    {//remove it from appearing
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/job/"+job.getJobId();
        Gson gson = new Gson();
        String json = gson.toJson(job);
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(Api_String)
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
                 /*   listOfJob.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listOfJob.size());*/

                } else {
                    System.out.println("Failed to update job. Response code: " + response.code());
                    Log.d("Tester", "onResponse: "+"Failed to update job. Response code: " + response.code());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfJob.size();
    }
    static class CurrentViewHolder extends RecyclerView.ViewHolder{
        TextView Reference, StartDate, BookingDetails,End_date;
        MaterialCardView cardView;
        ImageButton Complete;
        public CurrentViewHolder(@NonNull View itemView) {
            super(itemView);
            End_date = itemView.findViewById(R.id.expected_completion);
            cardView = itemView.findViewById(R.id.current_job_cardView);
            Reference = itemView.findViewById(R.id.current_job_reference);
            StartDate = itemView.findViewById(R.id.current_job_StartDate);
            BookingDetails = itemView.findViewById(R.id.current_job_Details);
            Complete = itemView.findViewById(R.id.current_job_ImageButton_Complete);
        }
    }


}
