package com.hanno.test;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Das Fragment, das den Stundenplan verwaltet
 */
public class TimeTable extends Fragment implements TableAdapter.OnItemListener, View.OnClickListener {

    public static ArrayList<String> lessons = new ArrayList<>();

    private int pos;

    private RecyclerView timetableRecyclerView;
    private Dialog dialog;
    private NumberPicker subjectPicker, shortagePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets(view);
        adapt(view);
    }

    /**
     * Setzt die RecyclerView auf den erstellten Stundenplan
     */
    private void adapt(View view){
        TableAdapter tableAdapter = new TableAdapter(lessons, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 5);
        timetableRecyclerView.setLayoutManager(layoutManager);
        timetableRecyclerView.setAdapter(tableAdapter);
    }

    /**
     * Initialisiert die Items aus der Xml-Datei
     * Initiealisiert den Dialog
     * Initialisiert die Numberpicker und Buttons vom Dialog
     * Verknüpft die Numberpicker beim scrollen
     */
    private void initWidgets(View view){
        timetableRecyclerView = view.findViewById(R.id.timetableRecyclerView);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.subject_selection_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnTakeSubject = dialog.findViewById(R.id.btnTakeSubject);
        subjectPicker = dialog.findViewById(R.id.subjectPicker);
        shortagePicker = dialog.findViewById(R.id.shoragePicker);

        btnCancel.setOnClickListener(this);
        btnTakeSubject.setOnClickListener(this);

        subjectPicker.setMinValue(0);
        subjectPicker.setMaxValue(Subject.getActiveNames().size() - 1);
        subjectPicker.setDisplayedValues(Subject.getActiveNames().toArray(new String[0]));
        subjectPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        subjectPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                shortagePicker.setValue(newVal);
            }
        });
        
        shortagePicker.setMinValue(0);
        shortagePicker.setMaxValue(Subject.getActiveShortages().size() - 1);
        shortagePicker.setDisplayedValues(Subject.getActiveShortages().toArray(new String[0]));
        shortagePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        shortagePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                subjectPicker.setValue(newVal);
            }
        });
    }

    /**
     * Wird aufgerufen, wenn ein Feld der RecyclerView angeklickt wird
     * Prüft, ob der Bearbeitungsmodus aktiv ist
     * Wenn ja, dann...
     * ...wird der Dialog geöffnet
     * @param position die Position, der angeklickten Zelle, in der RecyclerView
     * @param lesson das Kürzel der angeklickten Zelle
     */
    @Override
    public void onItemClick(int position, String lesson) {
        if(WeekViewActivity.textOfBtn.equals("fertig")) {
            dialog.show();
            pos = position;
        }
    }

    /**
     * Prüft, welcher Button im Dialog geklickt wurde
     * Wurde Abbrechen gedrückt, wird der Dialog geschlossen
     * Wurde Bestätigen gedrückt, dann...
     * ...wird das Kürzel des ausgewählten Fachs der 'lessons' Arraylist hinzugefügt
     * ...wird das Kürzel in der Datenbank gespeichert
     * ...wird das Layout aktualisiert
     * ...wird der Dialog geschlossen
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancel:
                dialog.dismiss();
                break;
            case R.id.btnTakeSubject:
                SQLiteDatabase databaseLesson = requireContext().openOrCreateDatabase(MenuActivity.databaseName, Context.MODE_PRIVATE, null);
                String str = Subject.getActiveShortages().get(subjectPicker.getValue());
                databaseLesson.execSQL("DELETE FROM lesson WHERE position = '" + pos + "';");
                lessons.remove(pos);
                lessons.add(pos, str);
                databaseLesson.execSQL("INSERT INTO lesson VALUES('" + pos + "', '" + str + "')");
                databaseLesson.close();
                adapt(v);
                dialog.dismiss();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<String> dailyTable(){
        ArrayList<String> dailylesson = new ArrayList<>();
        int weekDay = LocalDate.now().getDayOfWeek().getValue() -1;
        if(weekDay < 5){
            for(int i=weekDay; i<lessons.size(); i=i+5){
                dailylesson.add(lessons.get(i));
            }
        } else {
            for(int i=0; i<6; i++){
                dailylesson.add("");
            }
        }
        return dailylesson;
    }
}