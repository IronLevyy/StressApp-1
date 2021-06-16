package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.Settings.ParsingSPref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SourcesDB {

    private DBHelper dbHelper;
    private CallbackRefreshStatistic callback;

    public SourcesDB(){
        dbHelper = new DBHelper(ConstantAndHelp.getContext());
    }

    public interface CallbackRefreshStatistic{
        void refresh();
    }

    public void registerCallback(CallbackRefreshStatistic callback){
        this.callback=callback;
    }


    public void addLineInStatistic(Long time,String source,Integer peaksCount,Double tonicAvg){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("time", time);
                cv.put("source", source);
                cv.put("peaksCount", peaksCount);
                cv.put("tonic", tonicAvg);
                long rowID = db.insert("SourcesStatistic", null, cv);
                dbHelper.close();
                if(callback!=null) {
                    callback.refresh();
                }
            }
        });
        thread.start();
    }

    public HashMap<String,Integer> readSourcesDB(){
        HashMap<String,Integer> sourcesCount=new HashMap<>();
        ArrayList<String> sourcesActive=new ArrayList<>(Arrays.asList(
                ConstantAndHelp.SharedPreferenceLoad(ParsingSPref.SP_SOURCE_TAG)
                        .split(":")));
        for(String item:sourcesActive){
            sourcesCount.put(item,0);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("SourcesStatistic", null, null,
                null, null, null, null);
        if (c.moveToFirst()) {
            int sourceColIndex = c.getColumnIndex("source");
            do {
                String source=c.getString(sourceColIndex);
                if(sourcesCount.keySet().contains(source)){
                    sourcesCount.put(source,sourcesCount.get(source)+1);
                }
            } while (c.moveToNext());
        } else
            c.close();
        dbHelper.close();
        return sourcesCount;
    }

    public void clearSourcesStatistic(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("SourcesStatistic", null, null);
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
