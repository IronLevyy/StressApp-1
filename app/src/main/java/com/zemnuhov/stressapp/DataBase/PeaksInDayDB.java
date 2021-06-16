package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zemnuhov.stressapp.ConstantAndHelp;

import java.util.Calendar;
import java.util.Date;

public class PeaksInDayDB {

    private DBHelper dbHelper;
    private static boolean closed=false;

    public PeaksInDayDB(){
        dbHelper = new DBHelper(ConstantAndHelp.getContext());
    }


    public void addPeak(Long time,Double max){
        Thread thread=new Thread(()->{
                while (closed){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                closed=true;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("time", time);
                cv.put("max", max);
                long rowID = db.insert("Peaks", null, cv);
                dbHelper.close();
                closed=false;

        });
        thread.start();
    }

    public Integer readCountPeak(Long range){
        while (closed){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        closed=true;
        Integer count=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Peaks", null, null, null, null, null, null);
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        if (c.moveToFirst()) {
            int timeColIndex = c.getColumnIndex("time");
            do {
                if (time.getTime() - range < c.getLong(timeColIndex)) {
                    count++;
                }
            } while (c.moveToNext());
        } else
            c.close();
        dbHelper.close();
        closed=false;
        return count;
    }
    public void clearPeaksDB(Long time){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Peaks", "time < ?", new String[]{time.toString()});
        dbHelper.close();
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
