package com.example.carsandchronos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Mechanic_Activities.Analytics_view;
import com.example.carsandchronos.Mechanic_Activities.mech_main_new;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.Utility.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

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

public class SplashScreen_V2 extends AppCompatActivity {

    String Token ;
    int UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen_v2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        },2000);*/
        GetFCM();
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
                        loginUser(Token);
                        // sendTokenToServer(token);
                    }
                });
    }
    private boolean Auto_Login(String token)
    {

        return false;
    }

    private void loginUser(String fcm_token) {
        // Ensure the token is not null or empty
        if (fcm_token == null || fcm_token.isEmpty()) {
            Toast.makeText(SplashScreen_V2.this, "FCM token is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Build the URL and include the FCM token
        String url = "http://" + Utility.IP_Adress + ":5132/api/Auth/Auto_login/" + fcm_token;

        // Create an empty JSON object since the FCM token is passed in the URL
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(SplashScreen_V2.this, "Failed to connect to server: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    final String responseData = response.body().string();
                    Log.d("tester", "Response: " + responseData);
                    runOnUiThread(() -> handleLoginResponse(responseData));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SplashScreen_V2.this, "Server response error.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void handleLoginResponse(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);

            if (jsonObject.has("isValid") && jsonObject.getBoolean("isValid")) { // Check for isValid key
                String role = jsonObject.getString("role");

                JSONObject userObject = jsonObject.getJSONObject(role.toLowerCase());
                Log.d("tester", "handleLoginResponse: role " + role);
                Gson gson = new Gson();

                switch (role) {
                    case "Mechanic":
                        Log.d("tester", "handleLoginResponse: mechanic role " + role);
                        Mechanic mechanic = gson.fromJson(userObject.toString(), Mechanic.class);
                        // Store or use the mechanic object as needed
                        UserID = mechanic.getMechId();
                        Utility.SetMechID(UserID);
                        Intent mechanicIntent = new Intent(SplashScreen_V2.this, mech_main_new.class);
                        mechanicIntent.putExtra("mechanic", mechanic);
                        startActivity(mechanicIntent);
                        finish();
                        break;

                    default:
                        Toast.makeText(SplashScreen_V2.this, "Unknown role", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(SplashScreen_V2.this, " Auto Login failed: " + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                Intent mechanicIntent = new Intent(SplashScreen_V2.this, LoginActivity.class);
                startActivity(mechanicIntent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("tester", "run: error" + e.getMessage());
            Toast.makeText(SplashScreen_V2.this, "JSON Error occurred", Toast.LENGTH_SHORT).show();
        }
    }


}