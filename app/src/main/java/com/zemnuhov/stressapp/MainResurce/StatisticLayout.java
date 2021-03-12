package com.zemnuhov.stressapp.MainResurce;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.zemnuhov.stressapp.DataBase.SourcesStatisticDB;
import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;
import com.zemnuhov.stressapp.StatisticSettings.StatisticSettingActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StatisticLayout extends Fragment implements SourcesStatisticDB.CallbackRefreshStatistic {

    private PieChart pieChart;
    private ImageView settingButton;
    private HashMap<String,Integer> countSources;
    private SourcesStatisticDB statisticDB;
    private LinearLayout sourceLayout;

    ArrayList<Integer> colors = new ArrayList<Integer>(
            Arrays.asList(ContextCompat.getColor(GlobalValues.getContext(), R.color.primary)
            ,ContextCompat.getColor(GlobalValues.getContext(), R.color.primary_dark)
            ,ContextCompat.getColor(GlobalValues.getContext(), R.color.primary_light)
            ,ContextCompat.getColor(GlobalValues.getContext(), R.color.secondary)
            ,ContextCompat.getColor(GlobalValues.getContext(), R.color.secondary_dark)
            ,ContextCompat.getColor(GlobalValues.getContext(), R.color.pie_chart_user1)
            ,ContextCompat.getColor(GlobalValues.getContext(), R.color.pie_chart_user2)));

    public static StatisticLayout newInstance() {
        StatisticLayout fragment = new StatisticLayout();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_statistic_lable,container,false);
        pieChart=view.findViewById(R.id.pieChart);
        settingButton=view.findViewById(R.id.setting_icon);
        sourceLayout=view.findViewById(R.id.source_layout_statistic);

        statisticDB=new SourcesStatisticDB();
        statisticDB.registerCallback(this::refresh);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GlobalValues.getContext(), StatisticSettingActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }



    private void statLayout(){
        List<PieEntry> entries = new ArrayList<>();
        countSources=statisticDB.readSourcesDB();
        ArrayList<Integer> pieChartColors=new ArrayList<>();
        Boolean isZero=false;
        Integer iter= 0;
        for(String source:countSources.keySet()){
            entries.add(new PieEntry(countSources.get(source), source));
            if(countSources.get(source)!=0){
                isZero=true;
            }
            pieChartColors.add(colors.get(iter));
            SourceItemMain sourceItemMain=SourceItemMain.newInstance(
                    colors.get(iter)
                    ,source
                    ,countSources.get(source));
            getChildFragmentManager().beginTransaction()
                    .add(sourceLayout.getId(),sourceItemMain)
                    .commit();
            iter++;
        }
        if(!isZero){
            entries.add(new PieEntry(1, ""));
        }
        PieDataSet set = new PieDataSet(entries, "Stat");


        set.setColors(pieChartColors);
        PieData data = new PieData(set);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDescription(null);
        pieChart.invalidate();
    }

    @Override
    public void onStart() {
        super.onStart();
        sourceLayout.removeAllViews();
        statLayout();
    }

    @Override
    public void refresh() {
        sourceLayout.removeAllViews();
        statLayout();
    }
}
