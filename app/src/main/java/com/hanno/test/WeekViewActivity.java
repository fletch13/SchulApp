package com.hanno.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    public static String eft = "event";
    public static String textOfBtn = "bearbeiten";

    private Button btnEventTimetable, btnCreateChangeTable;
    private TextView monthYearText, tvSaturdayWeek, tvSundayWeek;
    private RecyclerView calendarRecyclerView;
    private LinearLayout layout1, layout2;
    private ImageButton floatingActionButton2;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        setWeekView();

        if(eft.equals("event")){
            btnEventTimetable.setText("Stundenplan");
            setEventAdapter();
        } else {
            btnEventTimetable.setText("Termine");
            setTimetableAdapter();
        }
    }

    /**
     * Initialisiert die Items aus der xml Datei
     */
    private void initWidgets() {
        btnEventTimetable = findViewById(R.id.btnEventTimetable);
        btnCreateChangeTable = findViewById(R.id.btnCreateChangeTable);
        calendarRecyclerView = findViewById(R.id.calenderRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        tvSaturdayWeek = findViewById(R.id.tvSaturdayWeek);
        tvSundayWeek = findViewById(R.id.tvSundayWeek);
        floatingActionButton2 = findViewById(R.id.floatingActionButton2);
    }

    /**
     * Setzt das Layout für die ausgewählte Woche
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new
                GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        if(eft.equals("event")){
            setEventAdapter();
        }
    }

    /**
     * Setzt das Layout der Listview für den ausgewählten Tag mit den entsprechenden Events
     */
    private void setEventAdapter() {
        EventList eventList = new EventList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, eventList);
        fragmentTransaction.commit();
    }

    /**
     *
     */
    private void setTimetableAdapter(){
        TimeTable timeTable = new TimeTable();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, timeTable);
        fragmentTransaction.commit();
    }

    /**
     * Setzt das Layout auf die nächste Woche
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    /**
     * Setzt das Layout auf die letzte Woche
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    /**
     * OnItemClick vom CalendarAdapter
     * Setzt den ausgewählten Tag mit dem angeklickten Tag gleich
     * @param position Die Nummer der angeklickten Zelle in der RecyclerView
     * @param date Das Datum das die angeklickte Zelle anzeigt
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
            CalendarUtils.selectedDate = date;
            setWeekView();
    }

    /**
     * Öffnet die Monats Activity und schließt diesehier
     */
    public void monthlyAction(View view) {
        eft = "event";
        CalendarUtils.addLastScreen("WeekView");
        startActivity(new Intent(this, MonthViewActivity.class));
        this.finish();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeEventTable(View view) {
        if(eft.equals("event")){
            btnEventTimetable.setText("Termine");
            btnCreateChangeTable.setVisibility(View.VISIBLE);
            calendarRecyclerView.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            tvSundayWeek.setVisibility(View.GONE);
            tvSaturdayWeek.setVisibility(View.GONE);
            floatingActionButton2.setVisibility(View.GONE);
            setWeekView();
            setTimetableAdapter();
            eft = "table";
        } else {
            btnEventTimetable.setText("Stundenplan");
            btnCreateChangeTable.setVisibility(View.GONE);
            calendarRecyclerView.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.VISIBLE);
            tvSaturdayWeek.setVisibility(View.VISIBLE);
            tvSundayWeek.setVisibility(View.VISIBLE);
            floatingActionButton2.setVisibility(View.VISIBLE);
            setWeekView();
            setEventAdapter();
            eft = "event";
        }
    }

    @SuppressLint("SetTextI18n")
    public void changeSubjects(View view) {
        Button btnWeekOpenMenu = findViewById(R.id.btnWeekOpenMenu);
        if(textOfBtn.equals("bearbeiten")){
            layout1.setVisibility(View.GONE);
            btnWeekOpenMenu.setVisibility(View.GONE);
            btnCreateChangeTable.setText("Fertig");
            textOfBtn = "fertig";
        } else {
            layout1.setVisibility(View.VISIBLE);
            btnWeekOpenMenu.setVisibility(View.VISIBLE);
            btnCreateChangeTable.setText("Bearbeiten");
            textOfBtn = "bearbeiten";
        }
        setTimetableAdapter();
    }

    public void openEventEdit(View view) {
        CalendarUtils.addLastScreen("WeekView");
        startActivity(new Intent(this, EventEditActivity.class));
        this.finish();
    }

    public void openMenu(View view) {
        SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("datacheck", true);
        editor.apply();
        eft = "event";
        CalendarUtils.deleteLastScreens();

        startActivity(new Intent(this, MenuActivity.class));
        this.finish();
    }

    public void onBackPressed()
    {
        switch(CalendarUtils.goBack())
        {
            case "MonthView":
            {
                startActivity(new Intent(this, MonthViewActivity.class));
                this.finish();
                break;
            }
            case "EventEdit":
            {
                startActivity(new Intent(this, EventEditActivity.class));
                this.finish();
                break;
            }
        }
    }
}