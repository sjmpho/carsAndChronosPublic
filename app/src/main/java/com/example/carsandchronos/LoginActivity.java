package com.example.carsandchronos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Mechanic_Activities.mech_main_new;
import com.example.carsandchronos.Models.Admin;
import com.example.carsandchronos.Models.Customer;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.Utility.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText Email,Password;
    ProgressBar progressBar;
    String Token;
    int UserID;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {


                finishAffinity();
            }
        });
            Email = findViewById(R.id.Login_email_ET);
            Password = findViewById(R.id.Login_password_ET);
            progressBar = findViewById(R.id.progress_circular);
            login = findViewById(R.id.Login_Button);
            GetFCM();


    }
    private void ProgressBarVisible(boolean bool)
    {
        if(bool)
        {
            progressBar.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }
    private void loginUser() {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            ProgressBarVisible(false);
        }

        RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://"+ Utility.IP_Adress +":5132/api/Auth/login")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(() -> {
                    ProgressBarVisible(false);
                    Toast.makeText(LoginActivity.this, "Failed to connect to server"+e.getMessage(), Toast.LENGTH_SHORT).show();

                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String responseData = response.body().string();
                Log.d("tester", "Response: " + responseData);
                runOnUiThread(() -> handleLoginResponse(responseData));

            }
        });
    }

    private void handleLoginResponse(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);

            if (jsonObject.has("message") && jsonObject.getString("message").equals("Login successful.")) {
                String role = jsonObject.getString("role");

                JSONObject userObject = jsonObject.getJSONObject(role.toLowerCase());
               // JSONObject user1 = jsonObject.getJSONObject("user");
                Log.d("tester", "handleLoginResponse: role "+role);
                Gson gson = new Gson();
                switch (role) {

                    case "Mechanic":
                        Log.d("tester", "handleLoginResponse:admin role "+role);
                       Mechanic mechanic = gson.fromJson(userObject.toString(), Mechanic.class);
                        // Store or use the mechanic object as needed
                        UserID = mechanic.getMechId();
                        Utility.SetMechID(UserID);
                        Utility.setMechanic(mechanic);
                        sendTokenToServer();
                        Intent mechanicIntent = new Intent(LoginActivity.this, mech_main_new.class);
                        mechanicIntent.putExtra("mechanic", mechanic);

                        startActivity(mechanicIntent);
                        finish();
                        break;

                    default:
                        Toast.makeText(LoginActivity.this, "Unknown role", Toast.LENGTH_SHORT).show();
                        ProgressBarVisible(false);
                        break;
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                ProgressBarVisible(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("tester", "run: error" + e.getMessage());
            Toast.makeText(LoginActivity.this, "JSON Error occurred", Toast.LENGTH_SHORT).show();
            ProgressBarVisible(false);
        }
    }

public void GetFCM()
{
    FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM", token);

                    // Send token to your server
                    Token = token;
                   // sendTokenToServer(token);
                }
            });
}

    private void sendTokenToServer() {
        // Implement network request to send token to your server
        String fcm_api = "http://"+Utility.IP_Adress+":5132/api/Auth/CollectFCM";
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mechId", UserID);
            jsonObject.put("fcm", Token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());

        Request request = new Request.Builder()
                .url(fcm_api)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->{
                    ProgressBarVisible(false);
                    Toast.makeText(LoginActivity.this, "Failed to connect to server"+e.getMessage(), Toast.LENGTH_SHORT).show();

                });
                }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String responseData = response.body().string();
                Log.d("FCM", "Response: " + responseData);

            }
        });
    }



    public void Login(View view) {

        ProgressBarVisible(true);
        loginUser();
    }

    public void SetIP(View view) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_set_ip, null);

        // Initialize the EditTexts
        EditText etIpAddress = dialogView.findViewById(R.id.et_ip_address);
        EditText etPort = dialogView.findViewById(R.id.et_port);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Set IP and Port")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ipAddress = etIpAddress.getText().toString().trim();
                        String port = etPort.getText().toString().trim();

                        Utility.setSocket(ipAddress,port);
                        // Handle the input (for example, save or validate)
                        Toast.makeText(LoginActivity.this, "IP: " + Utility.IP_Adress + ", Port: " + Utility.port, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog

                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}