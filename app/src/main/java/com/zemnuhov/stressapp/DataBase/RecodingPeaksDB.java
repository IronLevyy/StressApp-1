package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zemnuhov.stressapp.GlobalValues;

import java.util.Calendar;
import java.util.Date;

public class RecodingPeaksDB {

    DBHelper dbHelper;
    public RecodingPeaksDB(){
        dbHelper = new DBHelper(GlobalValues.getContext());
    }

    public void addToDB(Long time,Double max){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("time", time);
                cv.put("max", max);
                long rowID = db.insert("Peaks", null, cv);
                dbHelper.close();
            }
        });
        thread.start();
    }

    public Integer readDB(Long range){
        Integer count=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Peaks", null, null, null, null, null, null);
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        if (c.moveToFirst()) {
            int timeColIndex = c.getColumnIndex("time");
            do {
                if(time.getTime()-range<c.getLong(timeColIndex)){
                    count++;
                }
            } while (c.moveToNext());
        } else
        c.close();
        dbHelper.close();
        return count;
    }

    public void clearDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Peaks", null, null);
        dbHelper.close();
    }




    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDBPeaks", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table Peaks ("
                    + "time INTEGER,"
                    + "max double" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
