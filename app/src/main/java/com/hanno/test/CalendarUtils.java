package com.hanno.test;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;

public class CalendarUtils {

    public static LocalDate selectedDate;
    public static ArrayList<Integer> yearList = new ArrayList<>();
    public static boolean changedBundesland = false;
    public static boolean showedErrMsgApi = false;
    public static ArrayList<String> lastScreens = new ArrayList<>();

    /**
     * Diese Methode erstellt eine lesbare Zeit
     *
     * @param time die Zeit die umgewandelt werden soll
     * @return die umgewandelte Zeit als String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formattedTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("kk:mm");
        return time.format(formatter);
    }

    /**
     * Diese Methode erstellt ein lesbares Datum aus dem Namen des Monats und dem Jahr
     *
     * @param date das Datum, welches umgewandelt wird
     * @return das Datum als String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    /**
     * Erstellt eine Arraylist aus den Monatstagen
     *
     * @param date das Datum, zu dessem Monat die Liste erstellt wird
     * @return Eine Arraylist aus leeren Feldern und den Monatstagen
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        for (int i = 2; i < 44; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(null);
            } else {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    /**
     * Erstellt eine Arraylist aus den Wochentagen
     *
     * @param selectedDate das Datum, zu dem die Wochentagsliste gebildet wird
     * @return Eine Arraylist aus Wochentagen
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = mondayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }

        return days;
    }

    /**
     * Sucht den letzten Montag
     *
     * @param current der Referenztag
     * @return den Montag vor dem Referenztag
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate mondayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.MONDAY) {
                return current;
            }
            current = current.minusDays(1);
        }
        return null;
    }

    public static String shortBundesland(String BundeslandLang) {
        switch (BundeslandLang) {
            case "Bayern": {
                return "BY";
            }
            case "Baden-Württemberg": {
                return "BW";
            }
            case "Berlin": {
                return "BE";
            }
            case "Bremen": {
                return "HB";
            }
            case "Brandenburg": {
                return "BB";
            }
            case "Hamburg": {
                return "HH";
            }
            case "Hessen": {
                return "HE";
            }
            case "Mecklenburg-Vorpommern": {
                return "MV";
            }
            case "Niedersachsen": {
                return "NI";
            }
            case "Nordrhein-Westfalen": {
                return "NW";
            }
            case "Rheinland-Pfalz": {
                return "RP";
            }
            case "Saarland": {
                return "SL";
            }
            case "Sachsen": {
                return "SN";
            }
            case "Sachsen-Anhalt": {
                return "ST";
            }
            case "Schleswig-Holstein": {
                return "SH";
            }
            case "Thüringen": {
                return "TH";
            }
            default: {
                return null;
            }
        }
    }

    public static void addLastScreen(String s)
    {
        lastScreens.add(s);
    }

    public static String goBack()
    {
        String s = lastScreens.get(lastScreens.size()-1);
        lastScreens.remove(lastScreens.size()-1);
        return s;
    }

    public static void deleteLastScreens()
    {
        if(lastScreens.size()>0)
        {
            for(int i = lastScreens.size()-1; i > 0; i--)
            {
                lastScreens.remove(i);
            }
        }
    }
}
