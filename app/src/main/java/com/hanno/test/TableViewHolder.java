package com.hanno.test;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ArrayList<String> lessons;
    public final View dadView;
    public final TextView textView;
    private final TableAdapter.OnItemListener onItemListener;

    public TableViewHolder(@NonNull View itemView, TableAdapter.OnItemListener onItemListener, ArrayList<String> lessons) {
        super(itemView);
        this.lessons = lessons;
        dadView = itemView.findViewById(R.id.dadView);
        textView = itemView.findViewById(R.id.cellShortText);
        itemView.setOnClickListener(this);
        this.onItemListener = onItemListener;
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), lessons.get(getAbsoluteAdapterPosition()));
    }
}
