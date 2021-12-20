package com.hanno.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements NewsAdapter.OnItemListener {

    public static final String databaseName = "database.db";
    public boolean firstStart = false;
    public static boolean isShuffle = false;
    public boolean exit;

    private RecyclerView newsRecyclerview;
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if(firstAppStart()){
            firstStart = true;
            createDatabase();
            saveBundesland("Bayern");
        }
        newAppstart();
        Subject.sort();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            if(LoadTheme().equals("Night"))
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        if(!CalendarUtils.changedBundesland) {
            checkForPulledYears();
            CalendarUtils.changedBundesland = false;
        }

        else if(CalendarUtils.changedBundesland)
        {
            for(int i = CalendarUtils.yearList.size() - 1; i > -1; i--)
            {
                CalendarUtils.yearList.remove(i);
            }
            for(int i = Event.eventList.size() - 1; i > -1; i--)
            {
                if(Event.eventList.get(i).getName().equals("Ferien"))
                {
                    Event.eventList.remove(i);
                }
            }
            CalendarUtils.changedBundesland = false;
            CalendarUtils.showedErrMsgApi = false;
            checkForPulledYears();
        }

        if(LoadTheme().equals("Night"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if(LoadTheme().equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setNews();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNews() {
        newsRecyclerview = findViewById(R.id.newsRecyclerview);
        Event[] dailyevents = Event.getNextEvents();
        ArrayList<String> dailylesson = TimeTable.dailyTable();
        NewsAdapter newsAdapter = new NewsAdapter(dailylesson, this, dailyevents);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        newsRecyclerview.setLayoutManager(layoutManager);
        newsRecyclerview.setAdapter(newsAdapter);
    }

    /**
     * Checkt, ob die App das erste Mal auf dem Handy gestartet wird
     * @return wird die App das erste Mal gestartet?
     */
    public boolean firstAppStart(){
        boolean first = false;
        SharedPreferences sharedPreferences = getSharedPreferences("firstStart", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if(!sharedPreferences.getBoolean("firstStart", false)){
            first = true;
            sharedPreferencesEditor.putBoolean("firstStart", true);
            sharedPreferencesEditor.apply();
        }
        return first;
    }

    /**
     * Erzeugt eine neue Datenbank
     */
    public void createDatabase(){
        SQLiteDatabase database = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE event(name TEXT, minute INT, hour INT, day INT, month INT, year INT)");
        database.execSQL("CREATE TABLE subject(shortage TEXT, name TEXT, active TEXT)");
        database.execSQL("CREATE TABLE lesson(position INT, subject TEXT)");
        database.close();
    }

    /**
     * Prüft, ob die App neu gestartet wurde:
     * Wenn dies der Fall ist...
     * ...wird das aktuelle Datum neu bestimmt
     * ...werden die Daten aus der Datenbank geladen
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void newAppstart() {
        SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
        if(!preferences.getBoolean("datacheck", false)){
            CalendarUtils.selectedDate = LocalDate.now();
            initDatabase();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("datacheck", false);
            editor.apply();
        }
    }

    /**
     * Ruft die Events, Subjects und Lessons aus der Datenbank ab und überträgt diese in die entsprechenden Arraylists
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initDatabase() {
        Event.eventList.clear();
        SQLiteDatabase databaseEvent = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        Cursor cursorEvent = databaseEvent.rawQuery("SELECT COUNT(*) FROM event", null);
        cursorEvent.moveToFirst();
        int countEvent = cursorEvent.getInt(0);
        Cursor cursorCopy = databaseEvent.rawQuery("SELECT * FROM event", null);
        cursorCopy.moveToFirst();
        int e=0;
        while (e<countEvent){
            Event event = new Event(cursorCopy.getString(0), cursorCopy.getInt(1), cursorCopy.getInt(2), cursorCopy.getInt(3), cursorCopy.getInt(4), cursorCopy.getInt(5));
            if(Event.eventList.size() == 0){
                Event.eventList.add(event);
            } else {
                LocalDateTime localDateTime = LocalDateTime.of(event.getDate(), event.getTime());
                int i = 0;
                while (i<Event.eventList.size() && LocalDateTime.of(Event.eventList.get(i).getDate(), Event.eventList.get(i).getTime()).isBefore(localDateTime)){
                    i++;
                }
                if(i<Event.eventList.size()){
                    Event.eventList.add(i, event);
                } else {
                    Event.eventList.add(event);
                }
            }
            e = e+1;
            cursorCopy.moveToNext();
        }
        cursorCopy.close();
        cursorEvent.close();
        databaseEvent.close();

        Subject.subjects.clear();
        SQLiteDatabase databaseSubject = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if(!firstStart){
            Cursor cursorSubject = databaseSubject.rawQuery("SELECT COUNT(*) FROM subject", null);
            cursorSubject.moveToFirst();
            int countSubject = cursorSubject.getInt(0);
            Cursor cursorPaste = databaseSubject.rawQuery("SELECT * FROM subject", null);
            cursorPaste.moveToFirst();
            int s=0;
            while (s<countSubject){
                Subject.subjects.add(new Subject(cursorPaste.getString(0), cursorPaste.getString(1), cursorPaste.getString(2)));
                s = s+1;
                cursorPaste.moveToNext();
            }
            cursorPaste.close();
            cursorSubject.close();
        } else {
            firstStart = false;
            Subject.initialize();
            for(int i=0; i<Subject.subjects.size(); i++){
                databaseSubject.execSQL("INSERT INTO subject VALUES('" + Subject.subjects.get(i).getShortage() + "', '" + Subject.subjects.get(i).getName() + "', '" + Subject.subjects.get(i).getActive() + "')");
            }
        }
        databaseSubject.close();

        TimeTable.lessons.clear();
        SQLiteDatabase databaseLesson = getBaseContext().openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        Cursor cursorLesson = databaseLesson.rawQuery("SELECT COUNT(*) FROM lesson", null);
        cursorLesson.moveToFirst();
        int countLesson = cursorLesson.getInt(0);
        Cursor cursorSet = databaseLesson.rawQuery("SELECT * FROM lesson", null);
        cursorSet.moveToFirst();
        int l=0;
        SharedPreferences sharedPreferences = getSharedPreferences("lessonCount", MODE_PRIVATE);
        int lines = sharedPreferences.getInt("lessonCount", 6);
        for (int i=0; i<(lines*5); i++){
            TimeTable.lessons.add("");
        }
        while (l<countLesson){
            TimeTable.lessons.remove(cursorSet.getInt(0));
            TimeTable.lessons.add(cursorSet.getInt(0), cursorSet.getString(1));
            l = l+1;
            cursorSet.moveToNext();
        }
        cursorSet.close();
        cursorLesson.close();
        databaseLesson.close();
    }

    public String LoadTheme()
    {
        SharedPreferences activeTheme = getSharedPreferences("Theme", MODE_PRIVATE);
        return activeTheme.getString("Theme", "");
    }

    public void openCalendar(View view) {
        CalendarUtils.addLastScreen("Menu");
        startActivity(new Intent(this, MonthViewActivity.class));
        this.finish();
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
        this.finish();
    }

    public void openCalculator(View view) {
        startActivity(new Intent(this, CalculatorActivity.class));
        this.finish();
    }

    public void openTranslator(View view) {
        CalendarUtils.addLastScreen("Menu");
        startActivity(new Intent(this, TranslatorActivity.class));
        this.finish();
    }

    public String getBundesland()
    {
        SharedPreferences Bundesland = getSharedPreferences("Bundesland", MODE_PRIVATE);
        return Bundesland.getString("Bundesland", "");
    }

    public void saveBundesland(String value)
    {
        SharedPreferences Bundesland = getSharedPreferences("Bundesland", MODE_PRIVATE);
        SharedPreferences.Editor editorBundesland = Bundesland.edit();
        editorBundesland.putString("Bundesland", value);
        editorBundesland.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkForPulledYears()
    {
        boolean checked;
        checked = false;
        if(CalendarUtils.yearList.size() == 0)
        {
            getHolidays();
        }
        else {
            for(int i = 0; i < CalendarUtils.yearList.size(); i++)
            {
                if(CalendarUtils.selectedDate.getYear() == CalendarUtils.yearList.get(i))
                {
                    checked = true;
                }
            }
            if(!checked)
            {
                getHolidays();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHolidays() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://ferien-api.de/api/v1/holidays/" + CalendarUtils.shortBundesland(getBundesland()) + "/" + CalendarUtils.selectedDate.getYear();

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    CalendarUtils.yearList.add(CalendarUtils.selectedDate.getYear());
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
                    Toast.makeText(MenuActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                HolidayCatchUp(1,dayE,monthS+1,monthE,yearS,yearE);

            }
            else
            {
                dayCatchUp(dayS,28,monthS,yearS);
                HolidayCatchUp(1,dayE,monthS+1,monthE,yearS,yearE);
            }
        }
        else
        {
            dayCatchUp(dayS,28,monthS,yearS);
            HolidayCatchUp(1,dayE,monthS+1,monthE,yearS,yearE);
        }
    }

    @Override
    public void onItemClick(int position, String lesson) {
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}