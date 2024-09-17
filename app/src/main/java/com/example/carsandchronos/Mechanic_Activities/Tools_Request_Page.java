package com.example.carsandchronos.Mechanic_Activities;

import android.content.Intent;
import android.opengl.EGLImage;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.Models.Request_tools;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.Utility;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Tools_Request_Page extends AppCompatActivity {

    Mechanic mechanic;
    ArrayList<String> selected_List;
    OkHttpClient client;
    int Mech_ID;
    LinearLayout linearLayout;
    boolean startup = false;
    EditText booking_number, Engine_Number , Tools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tools_request_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        booking_number = findViewById(R.id.bookingNumber_tools);
        Engine_Number = findViewById(R.id.engineNumber_tools);
        Tools = findViewById(R.id.toolsRequest_tools);
        selected_List = new ArrayList<>();
        linearLayout = findViewById(R.id.LinearLaynout);

        Intent intent = getIntent();

        if(intent != null)
        {
            mechanic = (Mechanic) intent.getSerializableExtra("mechanic");
            Mech_ID = mechanic.getMechId();
        }
        Spinner spinner = findViewById(R.id.specialist_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpecialist = parent.getItemAtPosition(position).toString();
                // Handle the selected item
                if(startup)
                {
                    AddSpecialist(selectedSpecialist);
                    addTolist(selectedSpecialist);
                }else
                {
                    startup = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }
        });
    }

    private void addTolist(String selectedSpecialist) {
        selected_List.add(selectedSpecialist);
        //adds a speacilaist to the list
        Log.d("list", "List: "+selected_List);
    }

    private void AddSpecialist(String specialist)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View specialistView = inflater.inflate(R.layout.selected_specialist_plate, linearLayout, false);

        // Set the specialist name
        TextView specialistTextView = specialistView.findViewById(R.id.Specialist_request);
        specialistTextView.setText(specialist);

        // Set up the remove button
        ImageButton removeButton = specialistView.findViewById(R.id.btn_specialist_plate_remove);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the LinearLayout
                String data = specialist;
                linearLayout.removeView(specialistView);
                removeFromList(data);
            }
        });

        // Add the new view to the LinearLayout
        linearLayout.addView(specialistView);
    }

    private void removeFromList(String data) {
    // removes speciallist from a list
     selected_List.remove(data);
        Log.d("list", "removeFromList: "+selected_List);
    }

    public void Back(View view) {
        finish();
    }


    public Request_tools RetriveData()
    {
        Request_tools requestTools = new Request_tools();

        requestTools.setRequested_Date(Utility.getCurrentDate());
        requestTools.setRequest_ID(0);

        requestTools.setRequested_Mechanics(selected_List.toString());
        requestTools.setRequest_Vehicle_ID(Integer.parseInt(Engine_Number.getText().toString()));
        requestTools.setBooking_ID(Integer.parseInt(booking_number.getText().toString()));
        requestTools.setRequest_Details(Tools.getText().toString());
        requestTools.setRequested_State("PENDING");
        return requestTools;
    }

    public void Request(View view) {
        Log.d("Tester", "Request: clicked");
        Request_tools requestTools = RetriveData();
         client = new OkHttpClient();


        String Api_String ="http://"+ Utility.IP_Adress +":5132/api/Tools_Request/post_tools_request";
        Gson gson = new Gson();

        String json = gson.toJson(requestTools);
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(Api_String)
                .post(body)
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
}