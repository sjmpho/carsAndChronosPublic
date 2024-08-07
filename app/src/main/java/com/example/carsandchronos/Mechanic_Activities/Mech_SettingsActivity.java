package com.example.carsandchronos.Mechanic_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Mech_SettingsActivity extends AppCompatActivity {
    Mechanic mechanic;
    private Gson gson;
    private int data;
    private OkHttpClient client;
    SwitchCompat leave_switch;
    TextView name, lastname,email,leaveStatus,number,role;
    int MechID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mech_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            name = findViewById(R.id.Settings_Name);
            lastname = findViewById(R.id.Settings_LastName);
            email = findViewById(R.id.Settings_Email);
            leaveStatus = findViewById(R.id.Settings_LeaveStatus);
            number = findViewById(R.id.Settings_number);
            leave_switch = findViewById(R.id.Settings_leave_status_switch);
            role = findViewById(R.id.Settings_Role);
            Intent intent = getIntent();
            if(intent != null) {
                mechanic = (Mechanic) intent.getSerializableExtra("Mech");
                MechID = mechanic.getMechId();
                //data = MechID;
                name.setText("Name : "+mechanic.getName());
                lastname.setText("Last name : "+mechanic.getSurname());
                email.setText("Email : "+mechanic.getEmail());

                number.setText("Cell number : "+mechanic.getNumber());
                role.setText("Role : "+mechanic.getRole());
                if(mechanic.getLeave_Status() == 1)
                {
                   leaveStatus.setText("Leave Status : Online");
                   leave_switch.setChecked(true);
                }else {
                    leaveStatus.setText("Leave Status : Offline");
                    leave_switch.setChecked(false);
                }

            }

            leave_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The switch is turned on
                        UpdateLeave(1);
                        //update leave status to online
                        Toast.makeText(getApplicationContext(), "Switch is ON", Toast.LENGTH_SHORT).show();
                    } else {
                        // The switch is turned off
                        UpdateLeave(0);
                        //update leave status to off
                        Toast.makeText(getApplicationContext(), "Switch is OFF", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return insets;
        });
    }
    private void UpdateLeave(int leave)
    { mechanic.setLeave_Status(leave);
        String url = "http://"+ Utility.IP_Adress +":5132/api/MechanicProfile/" + MechID;
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(mechanic);
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(url)
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
                    if(leave == 1)
                    {
                        leave_switch.setChecked(true);
                    }else {
                        leave_switch.setChecked(false);
                    }
                    System.out.println("mechnic updated successfully.");
                    Log.d("Tester", "onResponse: Job updated successfully.");
                } else {
                    System.out.println("Failed to update mech. Response code: " + response.code());
                    Log.d("Tester", "onResponse: "+"Failed to update mechanic. Response code: " + response.message());
                }
            }
        });
    }

}