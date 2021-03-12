package com.zemnuhov.stressapp.StatisticSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class StatisticSettingActivity extends AppCompatActivity implements
        SourceStressItem.DeletedCallBack,
        TimeRangeItem.DeleteIntervalCallBack,
        TimeRangeItem.RefreshCallBack {


    private final String DEFAULT_SOURCES ="Семья:Работа:Друзья:Здоровье:Артефакты";
    private final String DEFAULT_INTERVALS="Утро_8:00-11:59|День_12:00-17:59|Вечер_18:00-00:00";
    private String sourcesSharedPref;
    private String intervalsSharedPref;
    private ArrayList<String> sources;
    private HashMap<Integer,SourceStressItem> sourceItems;
    private HashMap<Integer,TimeRangeItem> timeRangeItemArrayList;
    private final String SP_SOURCE_TAG="SP_SOURCE_TAG";
    private final String SP_INTERVAL_TAG="SP_INTERVAL_TAG";
    private LinearLayout sourceStressLinear;
    private LinearLayout intervalsLinear;
    private EditText editSources;
    private EditText editTitleInterval;
    private ImageButton addSourcesButton;
    private ImageButton addIntervalButton;
    private ArrayList<String> intervals;
    private Bundle arguments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_statistic_activity);
        arguments = getIntent().getExtras();
        init();
        fillingLayoutSource();
        fillingLayoutIntervals();
        onClickListeners();

    }

    private void onClickListeners(){
        addSourcesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=editSources.getText().toString();
                if(sourceItems.size()<7) {
                    if (!title.equals("")) {
                        SourceStressItem sourceStressItem =
                                SourceStressItem.newInstance(title, keyReturnSource(sourceItems),arguments);
                        sourceItems.put(sourceItems.size(),sourceStressItem);
                        getSupportFragmentManager().beginTransaction()
                                .add(sourceStressLinear.getId(), sourceStressItem)
                                .commit();
                        sourceStressItem.registerCallBack(StatisticSettingActivity.this::deleted);
                        refreshSourceSPreference();
                        editSources.setText("");
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Вы не ввели название!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Вы не можете добавить больше 7 источников стресса!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        addIntervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitleInterval.getText().toString();
                if (!title.equals("")) {
                    TimeRangeItem timeRangeItem =
                            TimeRangeItem.newInstance(title + "_00:00-00:00", keyReturnTimeRange(timeRangeItemArrayList));
                    timeRangeItemArrayList.put(timeRangeItem.getIdItem(), timeRangeItem);
                    getSupportFragmentManager().beginTransaction()
                            .add(intervalsLinear.getId(), timeRangeItem)
                            .commit();
                    timeRangeItem.registerCallback(StatisticSettingActivity.this::deleteInterval);
                    timeRangeItem.registerCallback(StatisticSettingActivity.this::refresh);
                    refreshIntervalSPreference();
                    editTitleInterval.setText("");
                } else{
                    Toast.makeText(getApplicationContext(),
                            "Вы не ввели название!",
                            Toast.LENGTH_LONG)
                            .show();

                }
            }
        });
    }

    private Integer keyReturnTimeRange(HashMap<Integer,TimeRangeItem> map){
        Set<Integer> keys=map.keySet();
        for(Integer i=0;i<keys.size();i++){
            if(!keys.contains(i)){
                return i;
            }
        }
        return keys.size();
    }

    private Integer keyReturnSource(HashMap<Integer,SourceStressItem> map){
        Set<Integer> keys=map.keySet();
        for(Integer i=0;i<keys.size();i++){
            if(!keys.contains(i)){
                return i;
            }
        }
        return keys.size();
    }

    private void fillingLayoutSource(){
        Integer id=0;
        for(String item:sources){
            SourceStressItem sourceStressItem=SourceStressItem.newInstance(item,id,arguments);
            sourceItems.put(id,sourceStressItem);
            getSupportFragmentManager().beginTransaction()
                    .add(sourceStressLinear.getId(),sourceStressItem)
                    .commit();
            sourceStressItem.registerCallBack(this::deleted);
            id++;
        }
    }

    private void fillingLayoutIntervals(){
        Integer id=0;
        for(String item:intervals){
            TimeRangeItem timeRangeItem=TimeRangeItem.newInstance(item,id);
            timeRangeItemArrayList.put(id,timeRangeItem);
            getSupportFragmentManager().beginTransaction()
                    .add(intervalsLinear.getId(),timeRangeItem)
                    .commit();
            timeRangeItem.registerCallback(this::deleteInterval);
            timeRangeItem.registerCallback(this::refresh);
            id++;
        }
    }

    private void init(){
        sourceStressLinear=findViewById(R.id.source_stress_linear);
        intervalsLinear=findViewById(R.id.intervals_linear);
        editSources=findViewById(R.id.edit_source_title);
        addSourcesButton=findViewById(R.id.add_source_button);
        addIntervalButton=findViewById(R.id.add_interval_button);
        editTitleInterval=findViewById(R.id.edit_title_interval);

        sourceItems=new HashMap<>();
        timeRangeItemArrayList=new HashMap<>();
        sourcesSharedPref=GlobalValues.SharedPreferenceLoad(SP_SOURCE_TAG);
        intervalsSharedPref=GlobalValues.SharedPreferenceLoad(SP_INTERVAL_TAG);

        if(sourcesSharedPref.equals("0")){
            GlobalValues.SharedPreferenceSave(SP_SOURCE_TAG,DEFAULT_SOURCES);
            sourcesSharedPref=GlobalValues.SharedPreferenceLoad(SP_SOURCE_TAG);
        }
        sources = new ArrayList<>(Arrays.asList(sourcesSharedPref.split(":")));

        if(intervalsSharedPref.equals("0")||intervalsSharedPref.equals("")){
            GlobalValues.SharedPreferenceSave(SP_INTERVAL_TAG,DEFAULT_INTERVALS);
            intervalsSharedPref=GlobalValues.SharedPreferenceLoad(SP_INTERVAL_TAG);
        }
        intervals = new ArrayList<>(Arrays.asList(intervalsSharedPref.split("\\|")));

    }

    private void refreshSourceSPreference(){
        String temp="";
        Integer i = 0;
        for(Integer key:sourceItems.keySet()){
            if(i<sourceItems.size()-1){
                temp += sourceItems.get(key).getTitle() + ":";
            }else {
                temp += sourceItems.get(key).getTitle();
            }
            i++;
        }
        GlobalValues.SharedPreferenceSave(SP_SOURCE_TAG,temp);
        Log.i("asdsg",temp);
    }

    private void refreshIntervalSPreference(){
        String temp="";
        Integer i=0;
        for(Integer key:timeRangeItemArrayList.keySet()){
            if(i<timeRangeItemArrayList.size()-1){
                temp += timeRangeItemArrayList.get(key).getItemString()+"|";
            }else {
                temp +=  timeRangeItemArrayList.get(key).getItemString();
            }
            i++;
        }
        GlobalValues.SharedPreferenceSave(SP_INTERVAL_TAG,temp);
        Log.i("asdsg",temp);
    }

    @Override
    public void deleted(Integer id) {
        getSupportFragmentManager().beginTransaction()
                .remove(sourceItems.get(id)).commit();
        sourceItems.remove(id);
        refreshSourceSPreference();
    }

    @Override
    public void deleteInterval(Integer id) {
        Log.i("DEBUG_DELL",id.toString());
        Log.i("DEBUG_DELL",timeRangeItemArrayList.toString());
        getSupportFragmentManager().beginTransaction()
                .remove(timeRangeItemArrayList.get(id))
                .commit();
        timeRangeItemArrayList.remove(id);
        refreshIntervalSPreference();

    }

    @Override
    public void refresh() {
        refreshIntervalSPreference();
    }
}
