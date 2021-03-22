package com.zemnuhov.stressapp.Settings;

import java.util.ArrayList;
import java.util.Arrays;

public class ParsingSPref {

    public static final String SP_SOURCE_TAG="SP_SOURCE_TAG";
    public static final String SP_INTERVAL_TAG="SP_INTERVAL_TAG";
    private String data;
    private String TAG;

    public ParsingSPref(String data){
        this.data=data;
        this.TAG=TAG;
    }


    public ArrayList<ArrayList<String>> getTimesAndSources(){
        ArrayList<ArrayList<String>> resultTimes = new ArrayList<>();
        ArrayList<String> items=new ArrayList<>(Arrays.asList(data.split("\\|")));
        for(String item:items){
            ArrayList<String> dataInItems=new ArrayList<>(Arrays.asList(item.split("_")));
            ArrayList<String> itemTime=new ArrayList<>(Arrays.asList(dataInItems.get(1).split("-")));
            if(dataInItems.size()>2){
                itemTime.add(dataInItems.get(2));
                itemTime.add(dataInItems.get(3));
            }
            resultTimes.add(itemTime);

        }
        return resultTimes;//[[00:00,8:00,Работа,Учёба],[8:00,12:00]....]
    }

}
