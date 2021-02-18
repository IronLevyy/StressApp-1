package com.zemnuhov.stressapp;

import android.content.Context;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;

public class GlobalValues {
     private static MenuItem mainMenu;
     private static Context context;
     private static FragmentManager fragmentManager;

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
}
