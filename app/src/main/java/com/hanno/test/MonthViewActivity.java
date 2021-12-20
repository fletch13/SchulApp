package com.hanno.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.util.ArrayList;


public class MonthViewActivity extends AppCompatActivity implements
        CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);
        initWidgets();
        setMonthView();
    }

    /**
     * Initialisiert die Items aus der xml Datei
     */
    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calenderRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    /**
     * Bestimmt und setzt den Text für den aktuellen Monat und das aktuelle Jahr
     * Setzt das Layout der RecyclerView auf den ausgewählten Monat
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = CalendarUtils.daysInMonthArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    /**
     * Setzt das ausgewählte Datum einen Monat zurück
     * Aktualisiert das Layout
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        checkForStartAndEndOfYear();
        setMonthView();
    }

    /**
     * Setzt das ausgewälte Datum einen Monat vor
     * Aktualisiert das Layout
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        checkForStartAndEndOfYear();
        setMonthView();
    }

    /**
     * Setzt den ausgewählten Tag mit dem angeklickten Tag gleich
     * @param position Die Nummer der angeklickten Zelle in der RecyclerView
     * @param date Das Datum das die angeklickte Zelle anzeigt
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    /**
     * Öffnet die WeekViewActivity und schließt diesehier
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void weeklyAction(View view) {
        CalendarUtils.addLastScreen("MonthView");
        startActivity(new Intent(this, WeekViewActivity.class));
        this.finish();
    }

    public void openEventEdit(View view) {
        CalendarUtils.addLastScreen("MonthView");
        startActivity(new Intent(this, EventEditActivity.class));
       this.finish();
    }

    public String getBundesland()
    {
        SharedPreferences Bundesland = getSharedPreferences("Bundesland", MODE_PRIVATE);
        return Bundesland.getString("Bundesland", "");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkForStartAndEndOfYear()
    {
        checkForPulledYears(CalendarUtils.selectedDate.getYear());
        if(CalendarUtils.selectedDate.getMonth().toString().equals("FEBRUARY"))
        {
            checkForPulledYears(CalendarUtils.selectedDate.getYear()-1);
        }
        else if(CalendarUtils.selectedDate.getMonth().toString().equals("DECEMBER"))
        {
            checkForPulledYears(CalendarUtils.selectedDate.getYear()+1);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkForPulledYears(int year)
    {
        boolean checked = false;

        if(CalendarUtils.yearList.size() == 0)
        {
            getHolidays(year);
        }
        else {
            for(int i = 0; i < CalendarUtils.yearList.size(); i++)
            {
                if(year == CalendarUtils.yearList.get(i))
                {
                    checked = true;
                }
            }
            if(!checked)
            {
                getHolidays(year);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHolidays(int currentYear) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://ferien-api.de/api/v1/holidays/" + CalendarUtils.shortBundesland(getBundesland()) + "/" + currentYear;

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    CalendarUtils.yearList.add(currentYear);
                    for(int i = 0; i < 7; i++) {
                        String start = response.getJSONObject(i).toString();
                        String temp = start;
                        start = start.substring(10,20);
                        temp = temp.substring(36,46);

                        String[] DateS = start.split("-");
                        String[] DateE = temp.split("-");

                        int yearS = Integer.parseInt(DateS[0]);
                        int monthS = Integer.parseInt(DateS[1]);
                        int dayS = Integer.parseInt(DateS[2]);

                        int yearE = Integer.parseInt(DateE[0]);
                        int monthE = Integer.parseInt(DateE[1]);
                        int dayE = Integer.parseInt(DateE[2]);

                        HolidayCatchUp(dayS,dayE,monthS,monthE,yearS,yearE);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!CalendarUtils.showedErrMsgApi)
                {
                    Toast.makeText(MonthViewActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    CalendarUtils.showedErrMsgApi = true;
                }
            }
        });
        queue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void HolidayCatchUp(int dayS, int dayE, int monthS, int monthE, int yearS, int yearE)
    {
        if(yearS != yearE)
        {
            yearCatchUp(dayS,dayE,monthS,monthE,yearS,yearE);

        }
        else if(monthS != monthE)
        {
            monthCatchUp(dayS,dayE,monthS,monthE,yearS,yearE);

        }
        else if(dayS != dayE)
        {
            dayCatchUp(dayS, dayE, monthS, yearS);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dayCatchUp(int S, int E, int month, int year)
    {
        for(int i = S; i < E+1; i++)
        {
            Event newEvent = new Event("Ferien", 00, 00, i, month, year);
            Event.eventList.add(newEvent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void yearCatchUp(int dayS, int dayE, int monthS, int monthE, int yearS, int yearE)
    {
        if(monthS != 12)
        {
            monthCatchUp(dayS,dayE,monthS,monthE,yearS,yearE);
        }
        else
        {
            dayCatchUp(dayS,31,monthS,yearS);
            HolidayCatchUp(1,dayE,1,monthE,yearS+1,yearE);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void monthCatchUp(int dayS,int dayE, int monthS, int monthE, int yearS,int yearE)
    {
        if(monthS == 4 || monthS == 6 || monthS == 9 || monthS == 11)
        {
            dayCatchUp(dayS,30,monthS,yearS);
            HolidayCatchUp(1,dayE,monthS+1,monthE,yearS,yearE);

        }
        else if(monthS == 2)
        {
            february(dayS,dayE,monthS,monthE,yearS,yearE);
        }
        else if(monthS == 1 || monthS == 3 || monthS == 5 || monthS == 7 || monthS == 8 || monthS == 10)
        {
            dayCatchUp(dayS,31,monthS,yearS);
            HolidayCatchUp(1,dayE,monthS+1,monthE,yearS,yearE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void february(int dayS, int dayE, int monthS, int monthE, int yearS, int yearE)
    {
        if(yearS % 4 == 0)
        {
            if(yearS % 2000 == 0)
            {
                dayCatchUp(dayS,29,monthS,yearS);

            }
            else
            {
                dayCatchUp(dayS,28,monthS,yearS);
            }
        }
        else
        {
            dayCatchUp(dayS,28,monthS,yearS);
        }
        HolidayCatchUp(1,dayE,monthS+1,monthE,yearS,yearE);
    }

    public void openMenu(View view) {
        SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("datacheck", true);
        editor.apply();
        CalendarUtils.deleteLastScreens();


        startActivity(new Intent(this, MenuActivity.class));
        this.finish();
    }

    public void onBackPressed()
    {
        if(CalendarUtils.goBack().equals("Menu"))
        {
            CalendarUtils.deleteLastScreens();
            SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("datacheck", true);
            editor.apply();
            startActivity(new Intent(this, MenuActivity.class));
        }else {
            startActivity(new Intent(this, WeekViewActivity.class));
        }
        this.finish();
    }
}
