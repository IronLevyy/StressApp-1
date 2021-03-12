package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zemnuhov.stressapp.GlobalValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ResultDB {
    DBHelperResult dbHelper;
    String LOG_TAG="DB_TONIC";
    RecodingTonicDB recodingTonicDB;
    RecodingPeaksDB recodingPeaksDB;

    public ResultDB(){
        dbHelper = new DBHelperResult(GlobalValues.getContext());
        recodingPeaksDB=new RecodingPeaksDB();
        recodingTonicDB=new RecodingTonicDB();
    }

    public void addToDB(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                SimpleDateFormat formatDate=new SimpleDateFormat("yyyy-MM-dd");
                String dateString=formatDate.format(date);
                Integer peaksToDay=recodingPeaksDB.readDB(86450000L);
                Integer avgToDay=recodingTonicDB.readDB(86450000L);
                cv.put("date", dateString);
                cv.put("avgTonic", avgToDay);
                cv.put("peaksCount", peaksToDay);
                db.insert("Tonic", null, cv);
                dbHelper.close();
            }
        });
        thread.start();
    }

    public Integer readDB(Long range){
        Integer count=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Double> valArray=new ArrayList<>();
        Cursor c = db.query("Tonic", null, null, null, null, null, null);
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
        db.delete("Tonic", null, null);
        dbHelper.close();
    }




    class DBHelperResult extends SQLiteOpenHelper {

        public DBHelperResult(Context context) {
            super(context, "myDBResult", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table Result ("
                    + "date TEXT,"
                    + "avgTonic integer,"
                    + "peaksCount integer "+ ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
