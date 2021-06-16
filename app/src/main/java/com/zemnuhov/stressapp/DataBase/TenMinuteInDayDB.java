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

public class TenMinuteInDayDB {
    private DBHelper dbHelper;


    public TenMinuteInDayDB(){
        dbHelper = new DBHelper(ConstantAndHelp.getContext());
    }


    public void addTenMinuteLine(Long time,Integer peaks){
        TonicInDayDB tonicInDayDB=new TonicInDayDB();
        Integer tonic=tonicInDayDB.readAvgTonic(600000L);
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

    public void clearTenMinuteTable(Long time){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("TenMinuteRecordings", "time < ?", new String[]{time.toString()});
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
