package com.zemnuhov.stressapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.Settings.ParsingSPref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DataBaseClass {
    
    private CallbackRefreshStatistic callback;
    private DBHelper dbHelper;

    public DataBaseClass(){
        dbHelper = new DBHelper(ConstantAndHelp.getContext());
    }

    public interface CallbackRefreshStatistic{
        void refresh();
    }

    public void registerCallback(CallbackRefreshStatistic callback){
        this.callback=callback;
    }


    public void addPeak(Long time,Double max){
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

    public void addResultDayLine(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                SimpleDateFormat formatDate=new SimpleDateFormat("yyyy-MM-dd");
                String dateString=formatDate.format(date);
                Integer peaksToDay=readCountPeak(86450000L);
                Integer avgToDay=readAvgTonic(86450000L);
                cv.put("date", dateString);
                cv.put("avgTonic", avgToDay);
                cv.put("peaksCount", peaksToDay);
                db.insert("Result", null, cv);
                dbHelper.close();
            }
        });
        thread.start();
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

    public Integer readCountPeak(Long range){
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

    public Integer readResultDayLine(Long range){
        Integer count=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Double> valArray=new ArrayList<>();
        Cursor c = db.query("Result", null, null, null,
                null, null, null);
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

    public void clearPeaksDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Peaks", null, null);
        dbHelper.close();
    }

    public void clearTonicDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("Tonic", null, null);
        dbHelper.close();
    }

    public void clearResultDB(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Result", null, null);
        dbHelper.close();
    }

    public void clearSourcesStatistic(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("SourcesStatistic", null, null);
        dbHelper.close();
    }

    public Double avgList(ArrayList<Double> list){
        double result = 0;
        for(double item:list){
            result+=item;
        }
        return result/list.size();
    }

    public void addTenMinuteLine(Long time,Integer peaks){
        Integer tonic=readAvgTonic(600000L);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("time", time);
        cv.put("peaksCount", peaks);
        cv.put("tonic", tonic);
        long rowID = db.insert("TenMinuteRecordings", null, cv);
        dbHelper.close();

    }

    public ArrayList<TenMinuteObjectDB> readTenMinuteTable(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<TenMinuteObjectDB> array=new ArrayList<>();
        Cursor c = db.query("TenMinuteRecordings", null, null,
                null, null, null, null);
        if (c.moveToFirst()) {
            int timeColIndex = c.getColumnIndex("time");
            int peaksColIndex = c.getColumnIndex("peaksCount");
            int tonicColIndex = c.getColumnIndex("tonic");
            do {
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mm-yyyy");
                String stringDate=dateFormat.format(time);
                Calendar readingDate = Calendar.getInstance();
                try {
                    readingDate.setTime(dateFormat.parse(stringDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(readingDate.getTime().getTime()<c.getLong(timeColIndex)){
                    array.add(new TenMinuteObjectDB(c.getLong(timeColIndex),
                            c.getInt(peaksColIndex),c.getDouble(tonicColIndex)));
                }

            } while (c.moveToNext());
        }
        c.close();
        dbHelper.close();
        return array;
    }

    public void clearTenMinuteTable(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("TenMinuteRecordings", null, null);
        dbHelper.close();
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDBPeaks",
                    null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table Peaks ("
                    + "time INTEGER,"
                    + "max double" + ");");

            db.execSQL("create table Tonic ("
                    + "time INTEGER,"
                    + "value double" + ");");
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
