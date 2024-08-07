package com.example.carsandchronos.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carsandchronos.Mechanic_Activities.Mech_Assigned_Jobs;
import com.example.carsandchronos.Mechanic_Activities.Mech_Current_jobs;
import com.example.carsandchronos.Mechanic_Activities.Mech_past_jobs;
import com.example.carsandchronos.Mechanic_Activities.Schedule_viewer;
import com.example.carsandchronos.Models.Mechanic;
import com.example.carsandchronos.Models.testing_activity;
import com.example.carsandchronos.R;


public class MainFragment extends Fragment {

    TextView Notifications,Assigned_Jobs,Current_Jobs,Past_Jobs,Request_tools,schedule;
    View view;
    private static final String ARG_KEY = "my_key";
    Mechanic mechanic;

    public static MainFragment newInstance(Mechanic param1) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_main, container, false);
        if (getArguments() != null) {
            mechanic = (Mechanic) getArguments().getSerializable(ARG_KEY);
            Log.d("things", "onCreateView: MechID is " + mechanic.getName());
        }
        schedule = view.findViewById(R.id.scheduler);
        Notifications = view.findViewById(R.id.tv_new_notifications);
        Assigned_Jobs = view.findViewById(R.id.tv_new_Assigmened);
        Current_Jobs  = view.findViewById(R.id.tv_new_current_jobs);
        Past_Jobs     = view.findViewById(R.id.tv_new_past_jobs);
        Request_tools = view.findViewById(R.id.request_tools);

        schedule.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Schedule_viewer.class);
            intent.putExtra("mechanic", mechanic);
            startActivity(intent);
        });
        Notifications.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), testing_activity.class);
            intent.putExtra("mechanic", mechanic);
            startActivity(intent);
        });
        Assigned_Jobs.setOnClickListener(v -> {
            Log.d("things", "onCreateView: pressed");
            Intent intent = new Intent(getContext(), Mech_Assigned_Jobs.class);
            intent.putExtra("mechanic", mechanic);
            startActivity(intent);
        });
        Current_Jobs.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Mech_Current_jobs.class);
            intent.putExtra("mechanic", mechanic);
            startActivity(intent);
        });
        Past_Jobs.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Mech_past_jobs.class);
            intent.putExtra("mechanic", mechanic);
            startActivity(intent);
        });
        Request_tools.setOnClickListener(v -> {

        });
        return view;
    }
}