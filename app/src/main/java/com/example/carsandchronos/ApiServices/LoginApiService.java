package com.example.carsandchronos.ApiServices;

import com.example.carsandchronos.Models.AccountModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("/WeatherForecast/login")
    Call<List<AccountModel>> getUser();
}
