package com.hanno.test;

import androidx.appcompat.app.AppCompatActivity;
import org.mariuszgromada.math.mxparser.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;

public class CalculatorActivity extends AppCompatActivity {

    private EditText display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        display = findViewById(R.id.textView2);

        display.setShowSoftInputOnFocus(false);

        display.setOnClickListener(v -> {
            if (getString(R.string.display).equals(display.getText().toString())) {
                display.setText("");
            }

        });
    }

    private void updateText(String strToAdd) {
        String leftStr = "";
        String rightStr = "";
        String oldStr = display.getText().toString();
        int cursorPos = display.getSelectionStart();

        if(oldStr.equals("") || oldStr.equals(getString(R.string.display))) {

        }
        else {
            leftStr = oldStr.substring(0, cursorPos);
            rightStr = oldStr.substring(cursorPos);
        }

        if (getString(R.string.display).equals(display.getText().toString()) || display.getText().toString().equals("")) {
            display.setText(strToAdd);
            display.setSelection(display.getText().toString().length());
        }
        else {
            display.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr));

            if(strToAdd.equals("sqrt(")){
                display.setSelection(cursorPos + 5);
            }
            else if(strToAdd.equals("cos(") || strToAdd.equals("sin(") || strToAdd.equals("tan(")){
                display.setSelection(cursorPos + 4);
            }
            else if(strToAdd.equals("ln(")){
                display.setSelection(cursorPos + 3);
            }
            else {
                display.setSelection(cursorPos + 1);
            }
        }
    }

    public void btnNull(View view) {
        updateText("0");
    }

    public void btnEins(View view) {
        updateText("1");
    }

    public void btnZwei(View view) {
        updateText("2");
    }

    public void btnDrei(View view) {
        updateText("3");
    }

    public void btnVier(View view) {
        updateText("4");
    }

    public void btnFuenf(View view) {
        updateText("5");
    }

    public void btnSechs(View view) {
        updateText("6");
    }

    public void btnSieben(View view) {
        updateText("7");
    }

    public void btnAcht(View view) {
        updateText("8");
    }

    public void btnNeun(View view) {
        updateText("9");
    }

    public void btnPlusminus(View view) {
        updateText("-");
    }

    public void btnKomma(View view) {
        updateText(".");
    }

    public void btnPlus(View view) {
        updateText("+");
    }

    public void btnMinus(View view) { updateText("-");}

    public void btnMal(View view) {
        updateText("×");
    }

    public void btnGeteilt(View view) {
        updateText("÷");
    }

    public void btnsin(View view) { updateText("sin(");}

    public void btncos(View view) { updateText("cos(");}

    public void btntan(View view) { updateText("tan(");}

    public void btnLn(View view) { updateText("ln(");}

    public void btnErgibt(View view) {
        String userExp = display.getText().toString();
        userExp = userExp.replaceAll("÷", "/");
        userExp = userExp.replaceAll("×", "*");

        Expression exp = new Expression(userExp);
        String result = String.valueOf(exp.calculate());
        display.setText(result);
        display.setSelection(result.length());
    }

    public void btnWurzel(View view) {
        updateText("sqrt(");
    }

    public void btnPotenz(View view) {
        updateText("^");
    }

    public void btnAzeichen(View view) {
        updateText("!");
    }

    public void btnC(View view) {
        display.setText("");
    }

    public void btnlKlammer(View view) {
        updateText("(");
    }

    public void btnKlammer(View view) {updateText(")"); }

    public void btnLoeschen(View view) {
        int cursorPos = display.getSelectionStart();
        int textLen = display.getText().length();

        if (cursorPos != 0 && textLen != 0) {
            SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
            selection.replace(cursorPos -1, cursorPos, "");
            display.setText(selection);
            display.setSelection(cursorPos -1);
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

    public void onBackPressed() {
        SharedPreferences preferences = getSharedPreferences("datacheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("datacheck", true);
        editor.apply();

        startActivity(new Intent(this, MenuActivity.class));
        this.finish();
    }
}