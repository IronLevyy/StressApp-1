package com.zemnuhov.stressapp.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static com.zemnuhov.stressapp.Settings.ParsingSPref.SP_INTERVAL_TAG;
import static com.zemnuhov.stressapp.Settings.ParsingSPref.SP_SOURCE_TAG;

public class StatisticSettingActivity extends AppCompatActivity implements
        SourceStressItem.DeletedCallBack,
        TimeRangeItem.DeleteIntervalCallBack,
        TimeRangeItem.RefreshCallBack {

    private String sourcesSharedPref;
    private String intervalsSharedPref;
    private ArrayList<String> sources;
    private HashMap<Integer,SourceStressItem> sourceItems;
    private HashMap<Integer,TimeRangeItem> timeRangeItemArrayList;
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
        addSourcesButton.setOnClickListener(v -> {
            String title=editSources.getText().toString();
            if(sourceItems.size()<7) {
                if (!title.equals("")) {
                    SourceStressItem sourceStressItem =
                            SourceStressItem.newInstance(title,
                                    keyReturnSource(sourceItems),arguments);
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
        });
        addIntervalButton.setOnClickListener(v -> {
            String title = editTitleInterval.getText().toString();
            if (!title.equals("")) {
                TimeRangeItem timeRangeItem =
                        TimeRangeItem.newInstance(title + "_00:00-00:00",
                                keyReturnTimeRange(timeRangeItemArrayList));
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
        sourcesSharedPref= ConstantAndHelp.SharedPreferenceLoad(SP_SOURCE_TAG);
        intervalsSharedPref= ConstantAndHelp.SharedPreferenceLoad(SP_INTERVAL_TAG);
        sources = new ArrayList<>(Arrays.asList(sourcesSharedPref.split(":")));
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
        ConstantAndHelp.SharedPreferenceSave(SP_SOURCE_TAG,temp);
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
        ConstantAndHelp.SharedPreferenceSave(SP_INTERVAL_TAG,temp);
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
