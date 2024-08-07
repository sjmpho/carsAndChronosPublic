package com.example.carsandchronos.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.JobCard;
import com.example.carsandchronos.R;
import com.example.carsandchronos.View_job_Details_Activity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CurrentJobAdapter  extends RecyclerView.Adapter<CurrentJobAdapter.CurrentViewHolder>{
    private List<JobCard> listOfJob;
    private List<Job> jobs;
    OkHttpClient client = new OkHttpClient();
    Context context;
    public CurrentJobAdapter(List<JobCard> Jobs , Context context) {
        this.listOfJob = Jobs;
        this.context = context;
        this.jobs = new ArrayList<>();
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
        JobCard job = listOfJob.get(position);
        holder.BookingDetails.setText("Job Description :"+job.getJobDescription());
        holder.StartDate.setText("Start Date :"+job.getStartDate());
        holder.Reference.setText("Reference :"+String.valueOf(job.getJobId()));
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/job/"+job.getJobId();

        holder.Complete.setOnLongClickListener(v -> {//long press for confirmation
               showDialog(job,position,holder.getAdapterPosition()); //update this so flags the jobCard as complete
                return true; // Return true to indicate the event was handled

        });
        holder.itemView.setOnClickListener(v -> {
            // goes to view details activity
            Intent intent =new Intent(context, View_job_Details_Activity.class);
          intent.putExtra("Job", job);
            intent.putExtra("bool",true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
    public void getJob(JobCard job)
    {  Api_String ="http://"+ Utility.IP_Adress +":5132/api/job/"+job.getJobId();
        Gson gson = new Gson();



    }
    private void removeItem(int position)
    {//removes item from the rec-list
        listOfJob.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listOfJob.size());
    }
    private void showDialog(JobCard job, int position, int AdapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage("Current Job is  done?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the Yes button click

                        job.setEndDate(Utility.getCurrentDate());
                        CompleteJob(job);//to indicate a complete job = 2 on job status;
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

    private void CompleteJob(JobCard job)
    {//                                                /MECHid JOBid , sTATUS/api/Job/UpdateMechanicJobCard/
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/Job/UpdateMechanicJobCard/"+job.getJobId()+"/"+job.getMechId()+"/COMPLETE";
        Gson gson = new Gson();
        job.setEndDate(Utility.getCurrentDate());
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
                    Log.d("Tester","onResponse: Job updated successfully."+response.message());


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
    static class CurrentViewHolder extends RecyclerView.ViewHolder{
        TextView Reference, StartDate, BookingDetails;
        ImageButton Complete;
        public CurrentViewHolder(@NonNull View itemView) {
            super(itemView);
            Reference = itemView.findViewById(R.id.current_job_reference);
            StartDate = itemView.findViewById(R.id.current_job_StartDate);
            BookingDetails = itemView.findViewById(R.id.current_job_Details);
            Complete = itemView.findViewById(R.id.current_job_ImageButton_Complete);
        }
    }


}
