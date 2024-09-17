package com.example.carsandchronos.Utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.Mechanic_Activities.Notification_ViewPage;
import com.example.carsandchronos.Models.Notification;
import com.example.carsandchronos.Models.Request_tools;
import com.example.carsandchronos.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Notification_ViewHolder> {

    private List<Notification> listOfNotifications;
    Context context;

    public  NotificationAdapter(List<Notification> notifications, Context context) {
        this.listOfNotifications = notifications;
        this.context = context;

    }
    @NonNull
    @Override
    public NotificationAdapter.Notification_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_plate, parent, false);
        return new NotificationAdapter.Notification_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.Notification_ViewHolder holder, int position) {
        Notification noti = listOfNotifications.get(position);
       holder.Notification_data.setText(noti.getNotification_details());
       holder.Notification_type.setText(noti.getNotification_Type());
       holder.date.setText(noti.getNotifcation_Date());

    holder.itemView.setOnClickListener(v -> {
        Intent intent = new Intent(context, Notification_ViewPage.class);
        intent.putExtra("noti",noti);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    });

    }
    @Override
    public int getItemCount() {
        return listOfNotifications.size();
    }

    static class Notification_ViewHolder extends RecyclerView.ViewHolder {
        ImageButton clear;
        TextView Notification_type,date , Notification_data;

        public Notification_ViewHolder(@NonNull View itemView) {
            super(itemView);

            Notification_type =itemView.findViewById(R.id.notfication_type);
            date = itemView.findViewById(R.id.date_time);
            Notification_data = itemView.findViewById(R.id.Notification_data);


        }
    }


}

