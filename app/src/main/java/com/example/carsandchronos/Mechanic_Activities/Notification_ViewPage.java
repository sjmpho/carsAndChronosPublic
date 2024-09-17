package com.example.carsandchronos.Mechanic_Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Models.Notification;
import com.example.carsandchronos.R;

public class Notification_ViewPage extends AppCompatActivity {

    TextView noti_type,noti_data,Noti_header;
    Notification noti_model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_view_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        noti_data = findViewById(R.id.Notification_detail);
        noti_type = findViewById(R.id.Noti_type);
        Noti_header = findViewById(R.id.header);

        if(getIntent().getSerializableExtra("noti") != null)
        {
            noti_model = (Notification) getIntent().getSerializableExtra("noti");
            noti_data.setText(noti_model.getNotification_details());
            noti_type.setText(noti_model.getNotification_Type());
            Noti_header.setText(noti_model.getNotification_header());
        }


    }

    public void btn_back(View view) {
        finish();
    }

    public void btn_reply(View view) {
    }
}