package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zemnuhov.stressapp.ConstantAndHelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

public class TonicInDayDB {

    private DBHelper dbHelper;

    public TonicInDayDB(){
        dbHelper = new DBHelper(ConstantAndHelp.getContext());
    }


    public void addTonic(Long time,Double value){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("time", time);
                cv.put("value", value);
                db.insert("Tonic", null, cv);
                dbHelper.close();
            }
        });
        thread.start();
    }

    public Integer readAvgTonic(Long range){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Double> valArray=new ArrayList<>();
        Cursor c = db.query("Tonic", null, null,
                null, null, null, null);
        if (c.moveToFirst()) {
            int timeColIndex = c.getColumnIndex("time");
            int valColIndex = c.getColumnIndex("value");
            do {
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                if(time.getTime()-range<c.getLong(timeColIndex)){
                    valArray.add(c.getDouble(valColIndex));
                }

            } while (c.moveToNext());
        }
        c.close();
        dbHelper.close();
        return avgList(valArray).intValue();
    }

    public void clearTonicDB(Long time){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Tonic", "time < ?", new String[]{time.toString()});
        dbHelper.close();
    }

    public Double avgList(ArrayList<Double> list){
        double result = 0;
        for(double item:list){
            result+=item;
        }
        return result/list.size();
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "StressDataBase",
                    null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table Peaks ("
                    + "time INTEGER,"
                    + "max double" + ");");
            db.execSQL("create table Result ("
                    + "date TEXT,"
                    + "avgTonic integer,"
                    + "peaksCount integer "+ ");");
            db.execSQL("create table SourcesStatistic ("
                    + "time INTEGER,"
                    + "source TEXT,"
                    + "peaksCount INTEGER,"
                    + "tonic INTEGER" + ");");
            db.execSQL("create table TenMinuteRecordings ("
                    + "time INTEGER,"
                    + "peaksCount INTEGER,"
                    + "tonic INTEGER" + ");");
            db.execSQL("create table Tonic ("
                    + "time INTEGER,"
                    + "value double" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
