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
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода


        Log.d(LOG_TAG, "--- Insert in mytable: ---");
        // подготовим данные для вставки в виде пар: наименование столбца - значение

        cv.put("time", time);
        cv.put("value", value);
        // вставляем запись и получаем ее ID
        long rowID = db.insert("Tonic", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);
        dbHelper.close();
    }

    public Integer readDB(Long range){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Double> valArray=new ArrayList<>();
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("Tonic", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
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
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "--- Clear mytable: ---");
        // удаляем все записи
        int clearCount = db.delete("Tonic", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
        dbHelper.close();
    }




    class DBHelperTonic extends SQLiteOpenHelper {

        public DBHelperTonic(Context context) {
            // конструктор суперкласса
            super(context, "myDBTonic", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table Tonic ("
                    + "time INTEGER,"
                    + "value double" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
