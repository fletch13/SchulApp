package com.hanno.test;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ArrayList<String> dailylessons;
    public final Event[] dailyEvents;
    public final TextView tvLessonNews, tvEventNews;
    private final NewsAdapter.OnItemListener onItemListener;

    public NewsViewHolder(@NonNull View itemView, NewsAdapter.OnItemListener onItemListener, ArrayList<String> dailylessons, Event[] dailyEvents) {
        super(itemView);
        this.dailylessons = dailylessons;
        this.dailyEvents = dailyEvents;
        tvEventNews = itemView.findViewById(R.id.tvEventNews);
        tvLessonNews = itemView.findViewById(R.id.tvLessonNews);
        this.onItemListener = onItemListener;
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), dailylessons.get(getAbsoluteAdapterPosition()));
    }
}
