package com.zemnuhov.stressapp.Settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class TimeRangeItem extends Fragment implements DialogSourcesInRanges.DialogCallBack {

    private TextView titleView;
    private EditText bHourView;
    private EditText bMinuteView;
    private EditText eHourView;
    private EditText eMinuteView;
    private ImageView deleteButton;
    private ImageView addSourcesOfRange;
    private String title;
    private ArrayList<String> time;
    private DeleteIntervalCallBack deleteIntervalCallBack;
    private RefreshCallBack refreshCallBack;
    private Integer id;
    private ArrayList<String> sourcesInRanges;
    private LinearLayout sources;

    public static TimeRangeItem newInstance(String data, Integer id) {
        TimeRangeItem fragment = new TimeRangeItem();
        ArrayList<String> dataArray=new ArrayList<>(Arrays.asList(data.split("_")));
        fragment.sourcesInRanges=new ArrayList<>();
        fragment.title=dataArray.get(0);
        fragment.id=id;
        fragment.time=new ArrayList<>();
        fragment.parseTimeRange(dataArray.get(1));
        if(dataArray.size()>2){
            fragment.sourcesInRanges.add(dataArray.get(2));
            fragment.sourcesInRanges.add(dataArray.get(3));
        }
        return fragment;
    }

    interface DeleteIntervalCallBack{

        void deleteInterval(Integer id);
    }

    public void registerCallback(DeleteIntervalCallBack callBack){
        this.deleteIntervalCallBack =callBack;
    }

    interface RefreshCallBack{

        void refresh();
    }

    public void registerCallback(RefreshCallBack callBack){
        this.refreshCallBack =callBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.settings_time_interval,container,false);
        init(view);
        fillingItem();
        clickListeners();
        return view;
    }

    private void init(View view){
        titleView =view.findViewById(R.id.time_range_title);
        bHourView =view.findViewById(R.id.edit_begin_hour);
        bMinuteView =view.findViewById(R.id.edit_begin_minute);
        eHourView =view.findViewById(R.id.edit_end_hour);
        eMinuteView =view.findViewById(R.id.edit_end_minute);
        deleteButton=view.findViewById(R.id.delete_interval);
        addSourcesOfRange=view.findViewById(R.id.add_sources_of_interval);
        sources=view.findViewById(R.id.sources_in_range);
        textChangedListeners();
    }

    private void clickListeners(){
        deleteButton.setOnClickListener(v -> deleteIntervalCallBack.deleteInterval(id));
        addSourcesOfRange.setOnClickListener(v -> {
            DialogSourcesInRanges dialog=DialogSourcesInRanges.newInstance(title);
            dialog.registerCallback(TimeRangeItem.this::dialogCallback);
            dialog.show(getFragmentManager(),"ADD_DIALOG");
        });
    }

    public Integer getIdItem(){
        return id;
    }

    private void fillingItem(){
        titleView.setText(title);
        bHourView.setText(time.get(0));
        bMinuteView.setText(time.get(1));
        eHourView.setText(time.get(2));
        eMinuteView.setText(time.get(3));
        if(sourcesInRanges.size()>0){
            addingSources(sourcesInRanges);
        }
    }

    private void textChangedListeners(){
        bHourView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                time.set(0,s.toString());
                refreshCallBack.refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bMinuteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                time.set(1,s.toString());
                refreshCallBack.refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        eHourView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                time.set(2,s.toString());
                refreshCallBack.refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        eMinuteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                time.set(3,s.toString());
                refreshCallBack.refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void parseTimeRange(String timeRange){
        ArrayList<String> firstSplit=new ArrayList<>(Arrays.asList(timeRange.split("-")));
        for(String item:firstSplit){
            ArrayList<String> secondSplit=new ArrayList<>(Arrays.asList(item.split(":")));
            time.add(secondSplit.get(0));
            time.add(secondSplit.get(1));
        }
    }

    private void addingSources(ArrayList<String> sourcesList){
        sources.removeAllViews();
        for(String source:sourcesList){
            TextView textView=new TextView(getContext());
            textView.setText(source);
            if(getResources().getDisplayMetrics().xdpi>=320&&getResources().getDisplayMetrics().xdpi<380){
                textView.setTextSize(8.4F);
            }
            sources.addView(textView);
        }
    }

    public String getItemString(){
        String item="";
        item+=title+"_"+time.get(0)+":"+time.get(1)+"-"+time.get(2)+":"+time.get(3);
        if(sourcesInRanges.size()>0){
            for(String source:sourcesInRanges){
                item+="_"+source;
            }
        }
        return item;
    }

    @Override
    public void dialogCallback(String firstSources, String secondSources) {
        if(sourcesInRanges.size()>0) {
            sourcesInRanges.set(0, firstSources);
            sourcesInRanges.set(1, secondSources);
        }else {
            sourcesInRanges.add(firstSources);
            sourcesInRanges.add(secondSources);
        }
        addingSources(sourcesInRanges);
        refreshCallBack.refresh();
    }
}
