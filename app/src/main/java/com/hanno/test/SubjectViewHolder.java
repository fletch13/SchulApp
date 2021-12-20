package com.hanno.test;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ArrayList<String> subjects;
    public final View llContainer;
    public final TextView listCell;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public final Switch setActive;
    private final SubjectAdapter.OnItemListener onItemListener;

    public SubjectViewHolder(@NonNull View itemView, SubjectAdapter.OnItemListener onItemListener, ArrayList<String> subjects) {
        super(itemView);
        this.subjects = subjects;
        llContainer = itemView.findViewById(R.id.llcontainer);
        listCell = itemView.findViewById(R.id.listCell);
        setActive = itemView.findViewById(R.id.setActive);
        itemView.setOnClickListener(this);
        setActive.setOnClickListener(this);
        this.onItemListener = onItemListener;
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), subjects.get(getAbsoluteAdapterPosition()), v);
    }
}
