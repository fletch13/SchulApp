package com.hanno.test;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private final ArrayList<String> subjects;
    private final SubjectAdapter.OnItemListener onItemListener;

    public SubjectAdapter(ArrayList<String> subjects, OnItemListener onItemListener) {
        this.subjects = subjects;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.subject_list_cell, parent, false);
        return new SubjectViewHolder(view, onItemListener, subjects);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        final String subject = subjects.get(position);
        if (subject == null) {
            holder.listCell.setText("");
        } else {
            holder.listCell.setText(subject);
        }

        if(SettingsActivity.selection.contains(subject)){
            holder.llContainer.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.llContainer.setBackgroundColor(Color.TRANSPARENT);
        }

        if(SettingsActivity.acts.contains(subject)){
            holder.setActive.setChecked(true);
        } else if(!SettingsActivity.acts.contains(subject)){
            holder.setActive.setChecked(false);
        }
        holder.setActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsActivity.activeChange = true;
            }
        });

        if(SettingsActivity.vis){
            holder.setActive.setVisibility(View.VISIBLE);
        } else {
            holder.setActive.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String subject, View view);
    }
}
