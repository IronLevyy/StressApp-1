package com.zemnuhov.stressapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment {

    PieChart pieChart;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,container,false);
        GlobalValues.getMainMenu().setVisible(false);
        pieChart=view.findViewById(R.id.pieChart);
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
        pieChart.invalidate();


        return view;
    }
}
