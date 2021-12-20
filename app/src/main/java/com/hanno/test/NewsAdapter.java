package com.hanno.test;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private final ArrayList<String> dailylesson;
    private final NewsAdapter.OnItemListener onItemListener;
    private final Event[] dailyevents;

    public NewsAdapter(ArrayList<String> dailylesson, NewsAdapter.OnItemListener onItemListener, Event[] dailyevents) {
        this.dailylesson = dailylesson;
        this.onItemListener = onItemListener;
        this.dailyevents = dailyevents;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.news_cell, parent, false);
        return new NewsViewHolder(view, onItemListener, dailylesson, dailyevents);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        String lesson = dailylesson.get(position);
        if(lesson == null)
            holder.tvLessonNews.setText("");
        else {
            holder.tvLessonNews.setText(lesson);
        }
        int not = 0;
        for(String less : dailylesson){
            if(!less.trim().equals("")){
                not++;
            }
        }

        if(not > 0){
            if(position == dailylesson.size()-1){
                holder.tvLessonNews.setBackgroundResource(R.drawable.border_top_bottom_left);
            }
        } else {
            holder.tvLessonNews.setBackground(null);
        }

        if(position < dailyevents.length){
            Event event = dailyevents[position];
            String eventTitle = CalendarUtils.formattedTime(event.getTime()) + "   " + event.getName();
            holder.tvEventNews.setText(eventTitle);
        } else {
            holder.tvEventNews.setText(" ");
        }
    }

    @Override
    public int getItemCount() {
        return dailylesson.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String lesson);
    }
}
