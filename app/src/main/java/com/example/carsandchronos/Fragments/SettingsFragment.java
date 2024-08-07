package com.example.carsandchronos.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.R;
import com.example.carsandchronos.Utility.Utility;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SettingsFragment extends Fragment {
    private static final String ARG_KEY = "my_key";
    Mechanic mechanic;
    private Gson gson;
    RelativeLayout relativeLayout;
    private int data;
    private OkHttpClient client;
    SwitchCompat leave_switch;
    TextView name, lastname,email,leaveStatus,number,role;
    View view;
    int MechID;
    public static SettingsFragment newInstance(Mechanic param1) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY, param1);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        name = view.findViewById(R.id.Settings_new_name);
        lastname = view.findViewById(R.id.Settings_new_Last_name);
        email = view.findViewById(R.id.Settings_new_email);
        leaveStatus = view.findViewById(R.id.Settings_new_leave_status);
        number = view.findViewById(R.id.Settings_new_number);
        role = view.findViewById(R.id.Settings_new_Role);
        leave_switch = view.findViewById(R.id.Settings_new_leave_status_switch);
        relativeLayout = view.findViewById(R.id.offlinebar);
        if(getArguments() != null) {
            mechanic = (Mechanic) getArguments().getSerializable(ARG_KEY);
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
                    Toast.makeText(requireContext(), " you are Online", Toast.LENGTH_SHORT).show();
                } else {
                    // The switch is turned off
                   UpdateLeave(0);
                    //update leave status to off
                    Toast.makeText(requireContext(), "you are Offline", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
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

                        getActivity().runOnUiThread(() -> {
                            leave_switch.setChecked(true);
                                leaveStatus.setText("Leave Status : OnLine");
                        relativeLayout.setVisibility(View.GONE);
                        });

                    }else {
                        getActivity().runOnUiThread(() ->{
                            leave_switch.setChecked(false);
                            leaveStatus.setText("Leave Status : Offline");
                            relativeLayout.setVisibility(View.VISIBLE);
                        });

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