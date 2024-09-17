package com.example.carsandchronos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.Notification;
import com.example.carsandchronos.Utility.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class notes_create_page extends AppCompatActivity {
    private OkHttpClient client;
    private Gson gson;
    private List<Job> jobList;
    private List<String> booking_Ids;
    Spinner bookings_id;
    String selectedItem;
    EditText notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_create_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        notes = findViewById(R.id.notes_tools);
        bookings_id= findViewById(R.id.booking_spinner);
        jobList =new ArrayList<>();
        client = new OkHttpClient();
        gson = new Gson();
        ListAssignedBookings();
     //   getBookings
    }
    private void updateSpinner()
    {
        bookings_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpecialist = parent.getItemAtPosition(position).toString();
                // Handle the selected item
                selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }
        });
    }
    private void Extract_Booking_information()
    {
        booking_Ids = new ArrayList<>();
        for(Job job : jobList)
        {
            booking_Ids.add(String.valueOf(job.getBookingId()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, booking_Ids);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        bookings_id.setAdapter(adapter);
        updateSpinner();
        Log.d("tester", "check spinner");
    }
    private void ListAssignedBookings() {
        String url = "http://"+ Utility.IP_Adress +":5132/api/Job/GetAssignedJobs/" + Utility.getMechID();//update with mech ID

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
                            if (job.getJobStatus() < 3) {//change this back to 2
                                jobList.add(job);
                            }
                        }
                        Extract_Booking_information();

                    });
                }
            }
        });
    }

    public void back_to_page(View view) {
    }

    public void btn_note(View view) {

        Notification notification = new Notification();
        notification.setBooking_ID(Integer.parseInt(selectedItem));
        notification.setNotification_Type("Note");
        notification.setNotification_details(notes.getText().toString());
        notification.setNotification_header("Notice for Booking: "+selectedItem);
        notification.setNotification_Reciver_ID(0);
        notification.setNotification_Sender_ID(Utility.getMechID());//change to the mechID
        notification.setNotication_FCM("set to mech fcm");
        notification.setNotifcation_Date(Utility.getCurrentDate());

        String url = "http://"+ Utility.IP_Adress +":5132/api/AdminProfile/Notification";
         MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("notification_ID", 0); // Assuming the ID is auto-generated in the backend
            jsonBody.put("notification_Type",notification.getNotification_Type());
            jsonBody.put("booking_ID", notification.getBooking_ID());
            jsonBody.put("notification_details", notification.getNotification_details());
            jsonBody.put("notification_header", notification.getNotification_header());
            jsonBody.put("notifcation_Date", notification.getNotifcation_Date());
            jsonBody.put("notification_Sender_ID", notification.getNotification_Sender_ID());
            jsonBody.put("notification_Reciver_ID", notification.getNotification_Reciver_ID());
            jsonBody.put("notication_FCM", notification.getNotication_FCM());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(JSON,jsonBody.toString());

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json") // Add headers if needed
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                // Show failure message on the UI thread if necessary
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(view.getContext(), "Failed to send notification", Toast.LENGTH_SHORT).show();
                    //    Log.d("tester", "run: error "+response.message());
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                // Handle success
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    // Process the response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "Notification sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    // Handle error response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            Log.d("tester", "run: error "+response);
                        }
                    });
                }
            }
        });
    }
}