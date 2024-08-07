package com.example.carsandchronos.ApiServices;

import com.example.carsandchronos.Models.bookingModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BookingApiService {
    @GET("api/bookings/{id}")
    Call<bookingModel> getBookingById(@Path("id") int id);
}
