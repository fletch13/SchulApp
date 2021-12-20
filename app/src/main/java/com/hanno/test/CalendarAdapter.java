package com.hanno.test;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener
            onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size()>15) {
            layoutParams.height = (int) (parent.getHeight() * 0.1666666666);
        }
        else
            layoutParams.height = parent.getHeight();
        return new CalendarViewHolder(view, onItemListener, days);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        int color1 = MaterialColors.getColor(holder.dayOfMonth, R.attr.colorSecondary);
        int color2 = MaterialColors.getColor(holder.dayOfMonth, R.attr.colorOnSecondary);
        int color3 = MaterialColors.getColor(holder.dayOfMonth, R.attr.colorSecondaryVariant);

        final LocalDate date = days.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(Event.getEventCountforDate(date) != 0){
                holder.cellDayEvents.setText(Event.getEventCountforDate(date) + " ");
                if(Event.checkObFerien(date)) {
                    holder.dayOfMonth.setBackgroundColor(color2);
                }
                else
                {
                    holder.dayOfMonth.setBackgroundColor(color1);
                }
            }
            holder.cellDayEvents.setBackgroundColor(color1);
            if(date.equals(CalendarUtils.selectedDate)) {
                holder.dayOfMonth.setBackgroundColor(color3);

            }


        }
        if(WeekViewActivity.eft.equals("event")){
            holder.cellDayEvents.setVisibility(View.VISIBLE);
        } else {
            holder.cellDayEvents.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }
}
