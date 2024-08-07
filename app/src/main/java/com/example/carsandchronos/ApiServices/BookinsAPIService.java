package com.example.carsandchronos.ApiServices;

import com.example.carsandchronos.Models.bookingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BookinsAPIService {
    @GET("api/ClientSideBookings")
    Call<List<bookingModel>> getBookings();
}
