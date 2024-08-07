package com.example.carsandchronos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.Models.booking;
import com.example.carsandchronos.Models.bookingModel;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<booking> bookings;
    Context context;

    public BookingAdapter(List<booking> bookings,Context context) {
        this.bookings = bookings;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_plate, parent, false);
        return new BookingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        booking booking = bookings.get(position);
        holder.customerName.setText("Client name : " + booking.getCustomerId());
        holder.CustomerVehice.setText("Vehicle name :" + booking.getVehicleMake() +" "+booking.getVehicleModel() + " "+booking.getVehicleYear());
        holder.CustomerVehicleDetails.setText("Details: " + booking.getBookingDetail());

        holder.itemView.setOnClickListener(v -> {
            //remeber to pass context
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, View_job_Details_Activity.class);
            intent.putExtra("booking",booking);
            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

      TextView customerName,CustomerVehice,CustomerVehicleDetails;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
         customerName = itemView.findViewById(R.id.Customer_name);
         CustomerVehice = itemView.findViewById(R.id.Customer_vehicle);
         CustomerVehicleDetails = itemView.findViewById(R.id.cutormer_vehicle_details);
        }
    }
}