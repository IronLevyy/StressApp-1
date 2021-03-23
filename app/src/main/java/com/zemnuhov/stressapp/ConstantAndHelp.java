package com.zemnuhov.stressapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;

import static android.content.Context.MODE_PRIVATE;

public class ConstantAndHelp {
     private static MenuItem mainMenu;
     private static Context context;
     private static FragmentManager fragmentManager;
     public static String deviceAddress;
     public static String dataSP;
     private static final String SAVED_TAG= "deviceAddress";
     private static SharedPreferences sPref;
     private static final String DEFAULT_SOURCES =
             "Семья:Работа:Друзья:Здоровье:Артефакты";
     private static final String DEFAULT_INTERVALS=
             "Утро_8:00-11:59|День_12:00-17:59|Вечер_18:00-00:00";
     private static final String SP_SOURCE_TAG="SP_SOURCE_TAG";
     private static final String SP_INTERVAL_TAG="SP_INTERVAL_TAG";

     public static FragmentManager getFragmentManager() {
          return fragmentManager;
     }

     public static void setFragmentManager(FragmentManager fragmentManager) {
          ConstantAndHelp.fragmentManager = fragmentManager;
     }

     public static String getDefaultSources() {
          return DEFAULT_SOURCES;
     }

     public static String getDefaultIntervals() {
          return DEFAULT_INTERVALS;
     }

     public static Context getContext() {
          return context;
     }

     public static void setContext(Context context) {
          ConstantAndHelp.context = context;
     }

     public static MenuItem getMainMenu() {
          return mainMenu;
     }

     public static void setMainMenu(MenuItem mainMenu) {
          ConstantAndHelp.mainMenu = mainMenu;
     }

     public static void saveDevice(String device) {
          sPref = context.getSharedPreferences(SAVED_TAG,MODE_PRIVATE);
          SharedPreferences.Editor ed = sPref.edit();
          ed.putString(SAVED_TAG, device);
          ed.apply();
     }

     public static String loadDeviceAddress() {
          sPref = context.getSharedPreferences(SAVED_TAG, MODE_PRIVATE);
          deviceAddress =  sPref.getString(SAVED_TAG, "0");
          return deviceAddress;
     }

     public static void SharedPreferenceSave(String TAG,String data){
          sPref = context.getSharedPreferences(TAG,MODE_PRIVATE);
          SharedPreferences.Editor ed = sPref.edit();
          ed.putString(TAG, data);
          ed.apply();
     }

     public static String SharedPreferenceLoad(String TAG){
          sPref = context.getSharedPreferences(TAG, MODE_PRIVATE);
          dataSP =  sPref.getString(TAG, "0");
          return dataSP;
     }
}
