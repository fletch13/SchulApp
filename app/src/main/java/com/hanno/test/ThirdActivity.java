package com.hanno.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.color.MaterialColors;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {

    EditText eTWord, eTMeaning;
    Button btnSave, btnGoBack, btnDelete, btnUpdate, btnView;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        eTWord = findViewById(R.id.eTWord);
        eTMeaning = findViewById(R.id.eTMeaning);
        btnSave = findViewById(R.id.btnSave);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnView = findViewById(R.id.btnView);
        DB = new DBHelper(this);

        btnSave.setOnClickListener(v -> {

            String wordTXT = eTWord.getText().toString();
            String meaningTXT = eTMeaning.getText().toString();

            Boolean insertvocab = DB.insertVocab(wordTXT, meaningTXT);
            if(insertvocab==true)
                Toast.makeText(ThirdActivity.this, "Gespeichert!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ThirdActivity.this, "Konnte nicht gespeichert werden!", Toast.LENGTH_SHORT).show();
            eTWord.setText("");
            eTMeaning.setText("");
        });

        btnDelete.setOnClickListener(v -> {

            String wordTXT = eTWord.getText().toString();
            Boolean checkdeletevocab = DB.deleteVocab(wordTXT);
            if(checkdeletevocab==true)
                Toast.makeText(ThirdActivity.this, "Eintrag gelöscht!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ThirdActivity.this, "Eintrag konnte nicht gelöscht werden!", Toast.LENGTH_SHORT).show();
            eTWord.setText("");
            eTMeaning.setText("");
        });

        btnUpdate.setOnClickListener(v -> {

            String wordTXT = eTWord.getText().toString();
            String meaningTXT = eTMeaning.getText().toString();

            Boolean checkupdatedata = DB.updateVocab(wordTXT, meaningTXT);
            if(checkupdatedata==true)
                Toast.makeText(ThirdActivity.this, "Änderung gespeichert!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ThirdActivity.this, "Änderung konnte nicht durchgeführt werden!", Toast.LENGTH_SHORT).show();
            eTWord.setText("");
            eTMeaning.setText("");
        });

        btnView.setOnClickListener(v -> {

            Cursor res = DB.getContext();
            if(res.getCount()==0){
                Toast.makeText(ThirdActivity.this, "Keine Einträge vorhanden!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                buffer.append("Originalwort :"+res.getString(0)+"\n");
                buffer.append("Wortbedeutung :"+res.getString(1)+"\n");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Einträge");
            builder.setMessage(buffer.toString());
            builder.show();
        });
        btnGoBack.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        CalendarUtils.addLastScreen("VCreate");
        Intent intent = new Intent (this, TranslatorActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onBackPressed()
    {
        CalendarUtils.goBack();
        Intent intent = new Intent (this, TranslatorActivity.class);
        startActivity(intent);
        this.finish();
    }
}