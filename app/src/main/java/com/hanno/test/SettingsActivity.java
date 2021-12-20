 package com.hanno.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements SubjectAdapter.OnItemListener, AdapterView.OnItemSelectedListener {

    private Dialog subjectListDialog, subNew, bundesland;
    private RecyclerView subjectRecyclerView;
    private Button btnDeleteSubjects, btnSaveSubject, b;
    private Spinner spinLessonCount;

    public static ArrayList<String> selection = new ArrayList<>();
    public static ArrayList<String> acts = Subject.getActiveNames();
    private final String[] mValues = {"Baden-Württemberg", "Bayern", "Berlin", "Brandenburg",
            "Bremen", "Hamburg", "Hessen", "Mecklenburg-Vorpommern", "Niedersachsen",
            "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Sachsen",
            "Sachsen-Anhalt", "Schleswig-Holstein", "Thüringen"};

    public static boolean vis, activeChange;

    DBHelper DB;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vis = true;
        activeChange = false;
        initWidgets();

        CheckBox themeSwitch = findViewById(R.id.themeChange);
        themeSwitch.setVisibility(View.VISIBLE);
        themeSwitch.setChecked(loadTheme().equals("Night"));

        b = (Button) findViewById(R.id.BundeslandPickerButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        b.setText(loadBundesland());

        DB = new DBHelper(this);
    }

    public void initWidgets(){
        subNew = new Dialog(this);
        subNew.setContentView(R.layout.create_subject_dialog);
        subNew.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        subjectListDialog = new Dialog(this);
        subjectListDialog.setContentView(R.layout.subject_list_dialog);
        subjectListDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btnDeleteSubjects = subjectListDialog.findViewById(R.id.btnDeleteSubjects);
        btnSaveSubject = subjectListDialog.findViewById(R.id.btnSaveSubject);

        subjectRecyclerView = subjectListDialog.findViewById(R.id.subjectRecyclerView);

        spinLessonCount = findViewById(R.id.spinLessonCount);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lessonCount, android.R.layout.simple_spinner_dropdown_item);
        spinLessonCount.setAdapter(adapter);
        SharedPreferences sharedPreferences = getSharedPreferences("lessonCount", MODE_PRIVATE);
        int count = sharedPreferences.getInt("lessonCount", 6);
        int position = adapter.getPosition(""+ count + "");
        spinLessonCount.setSelection(position);
        spinLessonCount.setOnItemSelectedListener(this);
    }

    public void resetCalAndTime(View view) {
        Subject.subjects.clear();
        TimeTable.lessons.clear();
        Event.eventList.clear();
        deleteDatabase(MenuActivity.databaseName);
        SharedPreferences sharedPreferences = getSharedPreferences("firstStart", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("firstStart", false);
        sharedPreferencesEditor.apply();
        SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("datacheck", false);
        editor.apply();
        SharedPreferences shared = getSharedPreferences("lessonCount", MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putInt("lessonCount", 6);
        edit.apply();
        CalendarUtils.changedBundesland = true;

        startActivity(new Intent(this, MenuActivity.class));
        this.finish();
    }

    public void giveSubjectlist(View view) {
        adaptList();
        subjectListDialog.show();
    }

    public void adaptList(){
        ArrayList<String> subjects = Subject.getNames();
        SubjectAdapter subjectAdapter = new SubjectAdapter(subjects, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        subjectRecyclerView.setLayoutManager(layoutManager);
        subjectRecyclerView.setAdapter(subjectAdapter);
    }

    @Override
    public void onItemClick(int position, String subject, View view) {
        if(btnDeleteSubjects.getText().toString().equals("Löschen")){
            if(!selection.contains(subject)){
                selection.add(subject);
            } else {
                selection.remove(subject);
            }
            subjectRecyclerView.getAdapter().notifyDataSetChanged();
        } else if(activeChange){
            if(acts.contains(subject)){
                acts.remove(subject);
            } else {
                acts.add(subject);
            }
            Subject selected = Subject.subjects.get(Subject.getNames().indexOf(subject));
            if(selected.getActive().equals("false")){
                Subject.subjects.get(Subject.getNames().indexOf(subject)).setActive("true");
            } else {
                Subject.subjects.get(Subject.getNames().indexOf(subject)).setActive("false");
                String shot = selected.getShortage();
                while (TimeTable.lessons.contains(shot)){
                    int e = TimeTable.lessons.indexOf(shot);
                    TimeTable.lessons.remove(shot);
                    TimeTable.lessons.add(e, "");
                }
            }
            subjectRecyclerView.getAdapter().notifyDataSetChanged();
            SQLiteDatabase database = getBaseContext().openOrCreateDatabase(MenuActivity.databaseName, MODE_PRIVATE, null);
            database.execSQL("UPDATE subject SET active = '" + selected.getActive() + "' WHERE name = '" + subject + "';");
            database.execSQL("DELETE FROM lesson WHERE subject = '" + selected.getShortage() + "';");
            database.close();
            activeChange = false;
        }
    }


    @SuppressLint("SetTextI18n")
    public void changeSubjectlist(View view) {
        Button btnChangeSubjectlist = subjectListDialog.findViewById(R.id.btnChangeSubjectlist);
        LinearLayout llBtnChangeSubjectlist = subjectListDialog.findViewById(R.id.llBtnChangeSubjectlist);
        if(btnChangeSubjectlist.getText().toString().equals("Bearbeiten")){
            btnChangeSubjectlist.setText("Fertig");
            llBtnChangeSubjectlist.setVisibility(View.VISIBLE);
        } else if(btnDeleteSubjects.getText().toString().equals("Entfernen")){
            btnChangeSubjectlist.setText("Bearbeiten");
            llBtnChangeSubjectlist.setVisibility(View.GONE);
        } else {
            vis = true;
            btnDeleteSubjects.setText("Entfernen");
            selection.clear();
            adaptList();
        }
    }

    @SuppressLint("SetTextI18n")
    public void deleteSubjects(View view) {
        if(btnDeleteSubjects.getText().toString().equals("Entfernen")){
            btnDeleteSubjects.setText("Löschen");
            vis = false;
            adaptList();
        } else {
            SQLiteDatabase databaseSubject = getBaseContext().openOrCreateDatabase(MenuActivity.databaseName, MODE_PRIVATE, null);
            for(int i=0;i<selection.size();i++){
                databaseSubject.execSQL("DELETE FROM subject WHERE name = '" + selection.get(i) + "';");
            }
            databaseSubject.close();
            SQLiteDatabase databaseLesson = getBaseContext().openOrCreateDatabase(MenuActivity.databaseName, Context.MODE_PRIVATE, null);
            while (selection.size()>0){
                String shot = Subject.subjects.get(Subject.getNames().indexOf(selection.get(0))).getShortage();
                databaseLesson.execSQL("DELETE FROM lesson WHERE subject = '" + shot + "';");
                while (TimeTable.lessons.contains(shot)){
                    int e = TimeTable.lessons.indexOf(shot);
                    TimeTable.lessons.remove(shot);
                    TimeTable.lessons.add(e, "");
                }
                Subject.subjects.remove(Subject.getNames().indexOf(selection.get(0)));
                selection.remove(0);
            }
            databaseLesson.close();
            adaptList();
        }
    }

    public void addSubjects(View view) {
        Button btnSaveSubject = subNew.findViewById(R.id.btnSaveSubject);
        EditText etSubjectName = subNew.findViewById(R.id.etSubjectName);
        EditText etSubjectShortage = subNew.findViewById(R.id.etSubjectShortage);
        etSubjectName.clearComposingText();
        etSubjectShortage.clearComposingText();
        btnSaveSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subnam = etSubjectName.getText().toString().trim();
                String subsho = etSubjectShortage.getText().toString().trim();
                boolean nam = true;
                boolean sho = true;
                for(String name : Subject.getNames()){
                    if(name.equalsIgnoreCase(subnam)){
                        nam = false;
                    }
                }
                for(String shortage : Subject.getShortages()){
                    if(shortage.equalsIgnoreCase(subsho)){
                        sho = false;
                    }
                }
                if(subnam.equals("")){
                    Toast.makeText(getApplicationContext(), "Der Name des Fachs fehlt", Toast.LENGTH_SHORT).show();
                } else if(subsho.equals("")){
                    Toast.makeText(getApplicationContext(), "Das Kürzel des Fachs fehlt", Toast.LENGTH_SHORT).show();
                } else if(!nam){
                    Toast.makeText(getApplicationContext(), "Der Name existiert bereits", Toast.LENGTH_SHORT).show();
                    etSubjectName.clearComposingText();
                } else if(!sho){
                    Toast.makeText(getApplicationContext(), "Das Kürzel existiert bereits", Toast.LENGTH_SHORT).show();
                    etSubjectShortage.clearComposingText();
                } else {
                    Subject.addSubject(subsho, subnam, "true");
                    SQLiteDatabase databaseSubject = getBaseContext().openOrCreateDatabase(MenuActivity.databaseName, MODE_PRIVATE, null);
                    databaseSubject.execSQL("INSERT INTO subject VALUES('" + subsho + "', '" + subnam + "', 'true')");
                    databaseSubject.close();
                    adaptList();
                    subNew.dismiss();
                }
            }
        });
        subNew.show();
    }

    public void DarkThemeOn(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();

        if(checked)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveTheme("Night");
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveTheme("Light");
        }
    }

    public void saveTheme(String value)
    {
        SharedPreferences activeTheme = getSharedPreferences("Theme", MODE_PRIVATE);
        SharedPreferences.Editor editorTheme = activeTheme.edit();
        editorTheme.putString("Theme", value);
        editorTheme.apply();
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
        SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("datacheck", true);
        editor.apply();
        CalendarUtils.deleteLastScreens();

        startActivity(new Intent(this, MenuActivity.class));
        this.finish();
    }

    public void saveBundesland(String value)
    {
        SharedPreferences Bundesland = getSharedPreferences("Bundesland", MODE_PRIVATE);
        SharedPreferences.Editor editorBundesland = Bundesland.edit();
        editorBundesland.putString("Bundesland", value);
        editorBundesland.apply();
    }

    public int getPositionOfBundesland(String Bundesland)
    {
        for(int i = 0; i < mValues.length; i++)
        {
            if(Bundesland.equals(mValues[i]))
            {
                return i;
            }
        }
        return 0;
    }

    public String loadTheme()
    {
        SharedPreferences activeTheme = getSharedPreferences("Theme", MODE_PRIVATE);
        return activeTheme.getString("Theme", "");
    }

    public String loadBundesland()
    {
        SharedPreferences Bundesland = getSharedPreferences("Bundesland", MODE_PRIVATE);
        return Bundesland.getString("Bundesland", "");
    }

    public void show()
    {
        final Dialog bundesland = new Dialog(SettingsActivity.this);
        bundesland.setTitle("Wähle ein Bundesland");
        bundesland.setContentView(R.layout.bundesland_selection_dialog);
        Button b1 = (Button) bundesland.findViewById(R.id.btnConfirmBundesland);
        Button b2 = (Button) bundesland.findViewById(R.id.btnCancelBundesland);
        final NumberPicker bundeslandPicker = (NumberPicker) bundesland.findViewById(R.id.BundeslandPicker);
        bundeslandPicker.setMaxValue(mValues.length-1);
        bundeslandPicker.setMinValue(0);
        bundeslandPicker.setWrapSelectorWheel(true);
        bundeslandPicker.setDisplayedValues(mValues);
        bundeslandPicker.setValue(getPositionOfBundesland(loadBundesland()));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                b.setText(mValues[bundeslandPicker.getValue()]);
                saveBundesland(mValues[bundeslandPicker.getValue()]);
                CalendarUtils.changedBundesland = true;
                bundesland.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundesland.dismiss();
            }
        });
        bundesland.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = getSharedPreferences("lessonCount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int oldLines = sharedPreferences.getInt("lessonCount", 6);
        switch (parent.getItemAtPosition(position).toString()){
            case "6":
                if(oldLines != 6){
                    editor.putInt("lessonCount", 6);
                    editor.apply();
                    removeTableLine(oldLines - 6);
                }
                break;
            case "7":
                if(oldLines > 7){
                    editor.putInt("lessonCount", 7);
                    editor.apply();
                    removeTableLine(oldLines - 7);
                } else if(oldLines < 7){
                    editor.putInt("lessonCount", 7);
                    editor.apply();
                    for(int i=(oldLines*5); i<(7*5); i++){
                        TimeTable.lessons.add("");
                    }
                }
                break;
            case "9":
                if(oldLines > 9){
                    editor.putInt("lessonCount", 9);
                    editor.apply();
                    removeTableLine(oldLines - 9);
                } else if(oldLines < 9){
                    editor.putInt("lessonCount", 9);
                    editor.apply();
                    for(int i=(oldLines*5); i<(9*5); i++){
                        TimeTable.lessons.add("");
                    }
                }
                break;
            case "11":
                if(oldLines < 11){
                    editor.putInt("lessonCount", 11);
                    editor.apply();
                    for(int i=(oldLines*5); i<(11*5); i++){
                        TimeTable.lessons.add("");
                    }
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), "FEHLER", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void removeTableLine(int countLines){
        for(int i=0; i<(countLines*5); i++){
            int last = TimeTable.lessons.size() - 1;
            TimeTable.lessons.remove(last);
            SQLiteDatabase databaseLesson = openOrCreateDatabase(MenuActivity.databaseName, MODE_PRIVATE, null);
            databaseLesson.execSQL("DELETE FROM lesson WHERE position = " + last + ";");
            databaseLesson.close();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}