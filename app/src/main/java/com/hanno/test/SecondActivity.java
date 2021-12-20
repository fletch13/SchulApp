package com.hanno.test;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.color.MaterialColors;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {


    private AnimatorSet frontAnim;
    private AnimatorSet backAnim;

    DBHelper DB;
    int i, x;

    private boolean isFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView frontCard = findViewById(R.id.frontCard);
        TextView backCard = findViewById(R.id.backCard);
        TextView tVScore = findViewById(R.id.tVScore);
        Button flipBtn = findViewById(R.id.flipBtn);
        Button btnGoBack2 = findViewById(R.id.btnGoBack2);
        EditText eTSolution = findViewById(R.id.eTSolution);

        int color1 = MaterialColors.getColor(flipBtn, R.attr.colorOnPrimary);
        int color2 = MaterialColors.getColor(flipBtn, R.attr.colorPrimary);
        int color3 = MaterialColors.getColor(flipBtn, R.attr.colorPrimaryVariant);

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;

        frontCard.setCameraDistance(8000 * scale);
        backCard.setCameraDistance(8000 * scale);
        tVScore.setVisibility(View.GONE);

        frontCard.setBackgroundColor(color2);
        backCard.setBackgroundColor(color3);

        frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.front_animator);
        backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_animator);

        i=0;
        x=0;
        DB = new DBHelper(this);

        Cursor vocab = DB.getVocab();
        Cursor shuffle = DB.getRandom();

        int count = vocab.getCount();


        if (MenuActivity.isShuffle) {
            shuffle.moveToFirst();
        } else {
            vocab.moveToFirst();
        }

        if(count == 0) {
            Toast.makeText(SecondActivity.this, "Keine Einträge vorhanden!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (this, TranslatorActivity.class);
            startActivity(intent);
        } else{
            if (MenuActivity.isShuffle) {
                frontCard.setText(shuffle.getString(shuffle.getColumnIndex("word")));
            } else {
                frontCard.setText(vocab.getString(vocab.getColumnIndex("word")));
            }
            }


        flipBtn.setOnClickListener(view -> {

            if (MenuActivity.isShuffle) {

                if (isFront) {
                    if (shuffle.getCount() > i) {
                        i = i + 1;
                    }

                    backCard.setText(shuffle.getString(shuffle.getColumnIndex("meaning")));
                    frontAnim.setTarget(frontCard);
                    backAnim.setTarget(backCard);
                    frontAnim.start();
                    backAnim.start();

                    if (DB.checkSolution(shuffle.getString(shuffle.getColumnIndex("word")), eTSolution.getText().toString())) {
                        eTSolution.setTextColor(Color.GREEN);
                        x = x + 1;
                    } else {
                        eTSolution.setTextColor(Color.RED);
                    }


                    isFront = false;
                } else {
                    if (shuffle.getCount() > i) {
                        shuffle.moveToNext();
                    } else {
                        Toast.makeText(SecondActivity.this, "Alle Vokabeln gelernt! ", Toast.LENGTH_SHORT).show();
                        flipBtn.setVisibility(View.GONE);
                        eTSolution.setVisibility(View.GONE);
                        tVScore.setText(x + " richtig!");
                        tVScore.setVisibility(View.VISIBLE);
                    }

                    frontCard.setText(shuffle.getString(shuffle.getColumnIndex("word")));
                    eTSolution.setText("");
                    eTSolution.setTextColor(color1);
                    frontAnim.setTarget(backCard);
                    backAnim.setTarget(frontCard);
                    backAnim.start();
                    frontAnim.start();
                    isFront = true;
                }
            } else {
                if (isFront) {
                    if (vocab.getCount() > i) {
                        i = i + 1;
                    }

                    backCard.setText(vocab.getString(vocab.getColumnIndex("meaning")));
                    frontAnim.setTarget(frontCard);
                    backAnim.setTarget(backCard);
                    frontAnim.start();
                    backAnim.start();

                    if (DB.checkSolution(vocab.getString(vocab.getColumnIndex("word")), eTSolution.getText().toString())) {
                        eTSolution.setTextColor(Color.GREEN);
                        x = x + 1;
                    } else {
                        eTSolution.setTextColor(Color.RED);
                    }


                    isFront = false;
                } else {
                    if (vocab.getCount() > i) {
                        vocab.moveToNext();
                    } else {
                        Toast.makeText(SecondActivity.this, "Alle Vokabeln gelernt! ", Toast.LENGTH_SHORT).show();
                        flipBtn.setVisibility(View.GONE);
                        eTSolution.setVisibility(View.GONE);
                        tVScore.setText(x + " richtig!");
                        tVScore.setVisibility(View.VISIBLE);
                    }

                    frontCard.setText(vocab.getString(vocab.getColumnIndex("word")));
                    eTSolution.setText("");
                    eTSolution.setTextColor(color1);
                    frontAnim.setTarget(backCard);
                    backAnim.setTarget(frontCard);
                    backAnim.start();
                    frontAnim.start();
                    isFront = true;
                }

            }
        });
        btnGoBack2.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        CalendarUtils.addLastScreen("Lernen");
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