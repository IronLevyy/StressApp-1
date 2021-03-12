package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.StatisticSettings.ParsingSPref;
import com.zemnuhov.stressapp.StatisticSettings.SourceStressItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SourcesStatisticDB {
    SourcesStatisticDB.DBHelper dbHelper;
    CallbackRefreshStatistic callback;


    public interface CallbackRefreshStatistic{
        void refresh();
    }

    public void registerCallback(CallbackRefreshStatistic callback){
        this.callback=callback;
    }

    public SourcesStatisticDB(){
        dbHelper = new SourcesStatisticDB.DBHelper(GlobalValues.getContext());
    }

    public void addToDB(Long time,String source,Integer peaksCount,Double tonicAvg){
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
                GlobalValues.SharedPreferenceLoad(ParsingSPref.SP_SOURCE_TAG)
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

    public void clearDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("SourcesStatistic", null, null);
        dbHelper.close();
    }




    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDBSourceStatistic", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table SourcesStatistic ("
                    + "time INTEGER,"
                    + "source TEXT,"
                    + "peaksCount INTEGER,"
                    + "tonic INTEGER" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
