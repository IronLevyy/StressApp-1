package com.zemnuhov.stressapp.MainResurce;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;
import com.zemnuhov.stressapp.StatisticSettings.StatisticSettingActivity;

import java.util.ArrayList;
import java.util.List;

public class StatisticLayout extends Fragment {

    private PieChart pieChart;
    private ImageView settingButton;

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
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GlobalValues.getContext(), StatisticSettingActivity.class);
                startActivity(intent);
            }
        });
        statLayout();
        return view;
    }
    private void statLayout(){
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(34.0f, "Семья"));
        entries.add(new PieEntry(11.0f, "Работа"));
        entries.add(new PieEntry(40.0f, "Друзья"));
        entries.add(new PieEntry(10.0f, "Здоровье"));
        entries.add(new PieEntry(5.0f, "Артефакты"));
        PieDataSet set = new PieDataSet(entries, "Stat");

        ArrayList<Integer> cores = new ArrayList<Integer>();
        cores.add(ContextCompat.getColor(getContext(), R.color.primary));
        cores.add(ContextCompat.getColor(getContext(), R.color.primary_dark));
        cores.add(ContextCompat.getColor(getContext(), R.color.primary_light));
        cores.add(ContextCompat.getColor(getContext(), R.color.secondary));
        cores.add(ContextCompat.getColor(getContext(), R.color.secondary_dark));
        set.setColors(cores);
        PieData data = new PieData(set);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDescription(null);
        pieChart.invalidate();
    }
}
