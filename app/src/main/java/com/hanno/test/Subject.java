package com.hanno.test;

import java.util.ArrayList;
import java.util.Collections;

public class Subject {

    public static ArrayList<Subject> subjects = new ArrayList<>();

    private final String shortage;
    private final String name;
    private String active;

    public Subject(String shortage, String name, String active){
        this.shortage = shortage;
        this.name = name;
        this.active = active;
    }

    public String getShortage(){
        return shortage;
    }

    public String getName(){
        return name;
    }

    public String getActive() { return active; }

    public void setActive(String act) { active = act;}

    /**
     * fügt subjects ein neues Fach hinzu
     * @param shot das Kürzel des Fachs
     * @param nam der Name des Fachs
     */
    public static void addSubject(String shot, String nam, String act){
        int i;
        int e = 0;
        do{
            i = nam.compareTo(subjects.get(e).getName());
            e++;
        } while (i>0 && e<subjects.size());
        if(e<subjects.size()){
            subjects.add(e-1, new Subject(shot, nam, act));
        } else {
            subjects.add(new Subject(shot, nam, act));
        }
    }

    public static void sort(){
        if(subjects.size() > 1){
            ArrayList<Subject> old = new ArrayList<>();
            old.add(Subject.subjects.get(0));
            for(int i=1;i<Subject.subjects.size();i++) {
                int b = 0;
                while (b < old.size() && Subject.subjects.get(i).getName().compareTo(old.get(b).getName()) < 0) {
                    b++;
                }
                old.add(b, Subject.subjects.get(i));
            }
            Collections.reverse(old);
            Subject.subjects.clear();
            Subject.subjects.addAll(old);
        }
    }

    /**
     * Erzeugt ein Array aus den Namen in der Arraylist 'subjects'
     * @return Ein String Array aus Fächernamen
     */
    public static ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for(int i=0; i<subjects.size(); i++){
            names.add(subjects.get(i).getName());
        }
        return names;
    }

    public static ArrayList<String> getActiveNames(){
        ArrayList<String> names = new ArrayList<>();
        for(int i=0; i<subjects.size(); i++){
            if(subjects.get(i).getActive().equals("true")){
                names.add(subjects.get(i).getName());
            }
        }
        return names;
    }

    /**
     * Erzeugt ein Array aus den Kürzeln in der Arraylist 'subjects'
     * @return Ein String Array aus Kürzeln
     */
    public static String[] getShortages(){
            String[] shortages = new String[subjects.size()];
            for(int i=0; i<subjects.size(); i++){
                shortages[i] = subjects.get(i).getShortage();
            }
            return shortages;
    }

    public static ArrayList<String> getActiveShortages(){
        ArrayList<String> shortages = new ArrayList<>();
        for(int i=0; i<subjects.size(); i++){
            if(subjects.get(i).getActive().equals("true")){
                shortages.add(subjects.get(i).getShortage());
            }
        }
        return shortages;
    }

    public static ArrayList<String> getActives(){
        ArrayList<String> actives = new ArrayList<>();
        for(int i=0; i<subjects.size(); i++){
            actives.add(subjects.get(i).getActive());
        }
        return actives;
    }

    /**
     * Füllt die Arraylist 'subjects' mit einer auswahl von Fächern
     */
    public static void initialize(){
        subjects.add(new Subject("B", "Biologie", "true"));
        subjects.add(new Subject("Ch", "Chemie", "true"));
        subjects.add(new Subject("D", "Deutsch", "true"));
        subjects.add(new Subject("E", "Englisch", "true"));
        subjects.add(new Subject("Eth", "Ethik", "true"));
        subjects.add(new Subject("Ev", "Evangelisch", "true"));
        subjects.add(new Subject("F", "Französisch", "true"));
        subjects.add(new Subject("G", "Geschichte", "true"));
        subjects.add(new Subject("Inf", "Informatik", "true"));
        subjects.add(new Subject("Geo", "Geographie", "true"));
        subjects.add(new Subject("K", "Katholisch", "true"));
        subjects.add(new Subject("Ku", "Kunst", "true"));
        subjects.add(new Subject("L", "Latein", "true"));
        subjects.add(new Subject("M", "Mathematik", "true"));
        subjects.add(new Subject("Mu", "Musik", "true"));
        subjects.add(new Subject("Ph", "Physik", "true"));
        subjects.add(new Subject("S", "Spanisch", "true"));
        subjects.add(new Subject("Sk", "Sozialkunde", "true"));
        subjects.add(new Subject("Sp", "Sport", "true"));
        subjects.add(new Subject("Wr", "Wirtschaft", "true"));
    }
}
