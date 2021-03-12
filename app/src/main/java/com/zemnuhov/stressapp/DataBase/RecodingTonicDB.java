package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zemnuhov.stressapp.GlobalValues;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RecodingTonicDB {
    DBHelperTonic dbHelper;
    String LOG_TAG="DB_TONIC";

    public RecodingTonicDB(){
        dbHelper = new DBHelperTonic(GlobalValues.getContext());
    }

    public void addToDB(Long time,Double value){
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

    public Integer readDB(Long range){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Double> valArray=new ArrayList<>();
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
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
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        dbHelper.close();
        return avgList(valArray).intValue();
    }

    public Double avgList(ArrayList<Double> list){
        double result = 0;
        for(double item:list){
            result+=item;
        }
        return result/list.size();
    }

    public void clearDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Tonic", null, null);
        dbHelper.close();
    }




    class DBHelperTonic extends SQLiteOpenHelper {

        public DBHelperTonic(Context context) {
            super(context, "myDBTonic", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table Tonic ("
                    + "time INTEGER,"
                    + "value double" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
