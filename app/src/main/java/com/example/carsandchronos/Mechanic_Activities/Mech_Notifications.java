package com.example.carsandchronos.Mechanic_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.Notification;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.CurrentJobAdapter;
import com.example.carsandchronos.Utility.Day;

import com.example.carsandchronos.Utility.NotificationAdapter;
import com.example.carsandchronos.Utility.Utility;
import com.example.carsandchronos.Utility.weekAdapter;
import com.example.carsandchronos.notes_create_page;
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

public class Mech_Notifications extends AppCompatActivity {
    private Gson gson;
    private NotificationAdapter notificationAdapter;
    RecyclerView recyclerView;
    private int data;
    private OkHttpClient client;
    private List<Notification> Notification_List;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_notifications);
        client = new OkHttpClient();
        gson = new Gson();
        Notification_List = new ArrayList<>();
        recyclerView= findViewById(R.id.rec_notificfations);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(Notification_List,this);
        recyclerView.setAdapter(notificationAdapter);
        ListNotifications();
    }

    private void ListNotifications() {
        String url = "http://"+ Utility.IP_Adress +":5132/api/AdminProfile/Notifications_Specific/" + Utility.getMechID();

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
                    Type bookingListType = new TypeToken<List<Notification>>() {}.getType();
                    List<Notification> notis = gson.fromJson(json, bookingListType);
                    Log.d("tester", "onResponse: success");

                    runOnUiThread(() -> {
                        Notification_List.clear();

                        // jobList.addAll(jobs);
                        for (Notification noti: notis) {
                            Notification_List.add(noti);
                        }

                        notificationAdapter.notifyDataSetChanged();

                    });
                }
            }
        });
    }

    public void btn_to_note(View view) {
        Intent intent = new Intent(this, notes_create_page.class);
        startActivity(intent);
    }

    public void Back_btn_notifications(View view) {
        finish();
    }
}
