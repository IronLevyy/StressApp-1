package com.zemnuhov.stressapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import static android.content.Context.MODE_PRIVATE;

public class GlobalValues {
     private static MenuItem mainMenu;
     private static Context context;
     private static FragmentManager fragmentManager;
     public static String deviceAddr;
     public static String dataSP;
     private static String SAVED_TAG= "deviceAddress";
     private static SharedPreferences sPref;

     public static FragmentManager getFragmentManager() {
          return fragmentManager;
     }

     public static void setFragmentManager(FragmentManager fragmentManager) {
          GlobalValues.fragmentManager = fragmentManager;
     }

     public static Context getContext() {
          return context;
     }

     public static void setContext(Context context) {
          GlobalValues.context = context;
     }

     public static MenuItem getMainMenu() {
          return mainMenu;
     }

     public static void setMainMenu(MenuItem mainMenu) {
          GlobalValues.mainMenu = mainMenu;
     }

     public static void saveDevice(String device) {
          sPref = context.getSharedPreferences(SAVED_TAG,MODE_PRIVATE);
          SharedPreferences.Editor ed = sPref.edit();
          ed.putString(SAVED_TAG, device);
          ed.commit();
     }

     public static String loadDeviceAddr() {
          sPref = context.getSharedPreferences(SAVED_TAG, MODE_PRIVATE);
          deviceAddr =  sPref.getString(SAVED_TAG, "0");
          return deviceAddr;
     }

     public static void SharedPreferenceSave(String TAG,String data){
          sPref = context.getSharedPreferences(TAG,MODE_PRIVATE);
          SharedPreferences.Editor ed = sPref.edit();
          ed.putString(TAG, data);
          ed.commit();
     }

     public static String SharedPreferenceLoad(String TAG){
          sPref = context.getSharedPreferences(TAG, MODE_PRIVATE);
          dataSP =  sPref.getString(TAG, "0");
          return dataSP;
     }
}
