package com.hanno.test;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {

    public static ArrayList<Event> eventList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventList) {
            if (event.getDate().equals(date)) {
                events.add(event);
            }
        }
        return events;
    }

    public static boolean checkObFerien(LocalDate date)
    {
        boolean Ferien = false;
        for (Event event : eventList) {
            if (event.getDate().equals(date)) {
                if (event.getName().equals("Ferien"))
                {
                    Ferien = true;
                }
            }
        }
        return Ferien;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Event[] getNextEvents(){
        ArrayList<Event> daily = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for(Event event: eventList) {
            if(LocalDateTime.of(event.getDate(), event.getTime()).isAfter(now)){
                if(daily.size() == 0){
                    daily.add(event);
                } else {
                        int i = 0;
                    while(i<daily.size() && LocalDateTime.of(daily.get(i).getDate(), daily.get(i).getTime()).isAfter(now)){
                        i++;
                    }
                    if(i<daily.size()){
                        daily.add(i, event);
                    } else {
                        daily.add(event);
                    }
                }
            }
        }
        while (daily.size()>3){
            daily.remove(daily.size()-1);
        }
        return daily.toArray(new Event[0]);
    }

    public static int getEventCountforDate(LocalDate date) {
        int count = 0;
        for (Event event : eventList) {
            if (event.getDate().equals(date)) {
                count++;
            }
        }
        return count;
    }

    private String name;
    private LocalDate date;
    private LocalTime time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(String name, int minute, int hour, int day, int month, int year) {
        this.name = name;
        date = LocalDate.of(year, month, day);
        time = LocalTime.of(hour, minute);
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void removeEvent(Event e){eventList.remove(e);}
}
