package com.hanno.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

public class EventEditActivity extends AppCompatActivity {

    private int minute, hour, day, month, year;

    private EditText eventNameET;
    private Button btnEventDate, btnEventTime;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();

        btnEventDate.setText(getDate());
        btnEventTime.setText(getMomentsTime());

        initDatePicker();
        initTimePicker();
    }

    /**
     * Initialisiert die Items aus der xml Datei
     * Initalisiert die Kalenderdaten
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWidgets() {
        eventNameET = findViewById(R.id.etEventName);
        btnEventTime = findViewById(R.id.btnEventTime);
        btnEventDate = findViewById(R.id.btnEventDate);

        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        day = CalendarUtils.selectedDate.getDayOfMonth();
        month = CalendarUtils.selectedDate.getMonthValue();
        month = month - 1;
        year = CalendarUtils.selectedDate.getYear();
    }

    /**
     * Erzeugt einen String mit dem ausgewählten Datum
     * @return Datum als String
     */
    private String getDate() { return makeDateString(day, month, year); }

    /**
     * Erzeugt einen String mit der ausgewählten Zeit
     * @return Zeit als String
     */
    private String getMomentsTime(){ return makeTimeString(minute, hour); }

    /**
     * Macht einen Datum-String aus den übergebenen Parametern
     * @param dayOfMonth Der Tag des Monats
     * @param month Der Monat als int
     * @param year Das Jahr
     * @return Datum als String
     */
    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + ". " + getMonthFormat(month) + " " + year;
    }

    /**
     * Macht aus der Zahl eines Monats sein kürzel
     * @param month Der umzuwandelnde Monat
     * @return Kürzel des Monats als String
     */
    private String getMonthFormat(int month)
    {
        if(month == 0)
            return "JAN";
        if(month == 1)
            return "FEB";
        if(month == 2)
            return "MÄR";
        if(month == 3)
            return "APR";
        if(month == 4)
            return "MAI";
        if(month == 5)
            return "JUN";
        if(month == 6)
            return "JUL";
        if(month == 7)
            return "AUG";
        if(month == 8)
            return "SEP";
        if(month == 9)
            return "OKT";
        if(month == 10)
            return "NOV";
        if(month == 11)
            return "DEZ";
        return "PROBLEM";
    }

    /**
     * Macht einen Zeit-String aus den übergebenen Parametern
     * @param minute Die Minute
     * @param hourOfDay Stunde des Tages im 24 Stunden Format
     * @return Zeit als String
     */
    private String makeTimeString(int minute, int hourOfDay){
        String hours = "" + hourOfDay;
        String minutes = "" + minute;
        if(hourOfDay < 10){
            hours = "0" + hourOfDay;
        }
        if(minute < 10){
            minutes = "0" + minute;
        }
        return hours + " : " + minutes;
    }

    /**
     * Erstellt den DateListener, welcher den DatePickerDialog konfiguriert
     */
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearPick, int monthPick, int dayOfMonth) {
                day = dayOfMonth;
                month = monthPick;
                year = yearPick;
                String date = makeDateString(dayOfMonth, monthPick, yearPick);
                btnEventDate.setText(date);
            }
        };

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Erstellt den TimeListener, welcher den TimePickerDialog konfiguriert
     */
    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutePick) {
                minute = minutePick;
                hour = hourOfDay;
                String time = makeTimeString(minutePick, hourOfDay);
                btnEventTime.setText(time);
            }
        };

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Fügt der eventlist das erstellte Event hinzu und übergibt sie auch der Datenbank
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString().trim();
        int mon = month + 1;
        Event newEvent = new Event(eventName, minute, hour, day, mon, year);

        if(Event.eventList.size() == 0){
            Event.eventList.add(newEvent);
        } else {
            LocalDateTime localDateTime = LocalDateTime.of(newEvent.getDate(), newEvent.getTime());
            int i = 0;
            while (LocalDateTime.of(Event.eventList.get(i).getDate(), Event.eventList.get(i).getTime()).isBefore(localDateTime) && i<Event.eventList.size()){
                i++;
            }
            if(i<Event.eventList.size()){
                Event.eventList.add(i, newEvent);
            } else {
                Event.eventList.add(newEvent);
            }
        }

        SQLiteDatabase databaseEvent = getBaseContext().openOrCreateDatabase(MenuActivity.databaseName, MODE_PRIVATE, null);
        databaseEvent.execSQL("INSERT INTO event VALUES('" + eventName + "', '" + minute + "', '" + hour + "', '" + day + "', '" + mon + "', '" + year +"')");
        databaseEvent.close();

        CalendarUtils.addLastScreen("EventEdit");

        CalendarUtils.selectedDate = LocalDate.of(year, mon, day);
        startActivity(new Intent(this, WeekViewActivity.class));
        this.finish();
    }

    /**
     * Zeigt den DatePickerDialog an
     */
    public void openDatePicker(View view) { datePickerDialog.show(); }

    /**
     * Zeigt den TimePickerDialog an
     */
    public void openTimePicker(View view){ timePickerDialog.show(); }

    public void exitEventEdit(View view) {
        CalendarUtils.addLastScreen("EventEdit");
        startActivity(new Intent(this, WeekViewActivity.class));
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
            case "WeekView":
            {
                startActivity(new Intent(this, WeekViewActivity.class));
                this.finish();
                break;
            }
        }
    }
}