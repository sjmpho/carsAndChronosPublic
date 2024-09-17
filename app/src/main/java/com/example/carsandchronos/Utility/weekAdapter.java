package com.example.carsandchronos.Utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsandchronos.R;

import java.util.List;

public class weekAdapter extends RecyclerView.Adapter<weekAdapter.ViewHolder> {

    private List<Day> daysList;
    private OnDayClickListener onDayClickListener;

    public interface OnDayClickListener {
        void onDayClick(int position);
    }

    public weekAdapter(List<Day> daysList, OnDayClickListener onDayClickListener) {
        this.daysList = daysList;
        this.onDayClickListener = onDayClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = daysList.get(position);
        holder.textViewDate.setText(day.getDate());
        holder.textViewDayName.setText("Monday");
        holder.itemView.setSelected(day.isSelected());
        holder.itemView.setOnClickListener(v -> onDayClickListener.onDayClick(position));
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewDayName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDayName = itemView.findViewById(R.id.textViewDayName);
        }
    }
}

