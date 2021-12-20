package com.hanno.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class TranslatorActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLearn, btnNewVocab, btnShuffle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        btnLearn = findViewById(R.id.btnLearn);
        btnNewVocab = findViewById(R.id.btnNewVocab);
        btnShuffle =findViewById(R.id.btnShuffle);
        btnLearn.setOnClickListener(this);
        btnNewVocab.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLearn:
                CalendarUtils.addLastScreen("Vokabeln");
                Intent intent2 = new Intent(this, SecondActivity.class);
                MenuActivity.isShuffle =false;
                startActivity(intent2);
                this.finish();
                break;
            case R.id.btnShuffle:
                CalendarUtils.addLastScreen("Vokabeln");
                Intent intent = new Intent(this, SecondActivity.class);
                MenuActivity.isShuffle =true;
                startActivity(intent);
                this.finish();
                break;
            case R.id.btnNewVocab:
                CalendarUtils.addLastScreen("Vokabeln");
                Intent intent1 = new Intent(this, ThirdActivity.class);
                startActivity(intent1);
                this.finish();
                break;
        }
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
        switch (CalendarUtils.goBack())
        {
            case "Menu":
            {
                SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("datacheck", true);
                editor.apply();
                CalendarUtils.deleteLastScreens();

                startActivity(new Intent(this, MenuActivity.class));
                this.finish();
                break;
            }
            case "Lernen":
            {
                startActivity(new Intent(this, SecondActivity.class));
                this.finish();
                break;
            }
            case "VCreate":
            {
                startActivity(new Intent(this, ThirdActivity.class));
                this.finish();
                break;
            }
        }
    }
}