package com.hanno.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    private final ArrayList<String> lessons;
    private final OnItemListener onItemListener;

    public TableAdapter(ArrayList<String> lessons, OnItemListener onItemListener) {
        this.lessons = lessons;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.table_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = parent.getWidth() / 5;
        return new TableViewHolder(view, onItemListener, lessons);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        final String lesson = lessons.get(position);
        if(lesson == null)
            holder.textView.setText("");
        else {
            holder.textView.setText(lesson);
        }
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String lesson);
    }
}
