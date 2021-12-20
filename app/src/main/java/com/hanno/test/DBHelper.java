package com.hanno.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "databaseVocab.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Vocab(word TEXT primary key, meaning TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Vocab");

    }

    public Boolean insertVocab(String word, String meaning)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word", word);
        contentValues.put("meaning", meaning);
        long result=DB.insert("Vocab", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }


    public Boolean updateVocab(String word, String meaning) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("meaning", meaning);
        Cursor cursor = DB.rawQuery("Select * from Vocab where word = ?", new String[]{word});
        if (cursor.getCount() > 0) {
            long result = DB.update("Vocab", contentValues, "word=?", new String[]{word});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }}


    public Boolean deleteVocab (String word)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Vocab where word = ?", new String[]{word});
        if (cursor.getCount() > 0) {
            long result = DB.delete("Vocab", "word=?", new String[]{word});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }


    public Boolean checkSolution (String word, String solution)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Vocab where word = ?",new String[]{word});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("meaning")).equalsIgnoreCase(solution);

    }
    public Cursor getContext ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Vocab", null);
        return cursor;

    }
    public Cursor getVocab ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Vocab ", null);
        return cursor;
    }
    public Cursor getRandom ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Vocab ORDER BY RANDOM()", null);
        return cursor;
    }
}
