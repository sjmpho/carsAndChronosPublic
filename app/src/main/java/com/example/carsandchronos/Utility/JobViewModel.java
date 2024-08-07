package com.example.carsandchronos.Utility;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carsandchronos.Models.Job;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JobViewModel extends ViewModel {
    private MutableLiveData<List<Job>> jobs;
    private OkHttpClient client;
    private Gson gson;

    public JobViewModel() {
        jobs = new MutableLiveData<>();
        client = new OkHttpClient();
        gson = new Gson();
    }

    public LiveData<List<Job>> getJobs() {
        return jobs;
    }

    public void fetchJobs(int mechId) {
        String url = "http://" + Utility.IP_Adress + ":5132/api/Job/GetAssignedJobs/" + mechId;

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
                    List<Job> jobList = gson.fromJson(json, bookingListType);
                    jobs.postValue(jobList);
                }
            }
        });
    }
}

