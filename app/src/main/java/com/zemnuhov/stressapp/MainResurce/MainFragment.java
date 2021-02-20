package com.zemnuhov.stressapp.MainResurce;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.zemnuhov.stressapp.BleServiceAdapter;
import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements BleServiceAdapter.CallBack {


    private String addressDevice;
    private BleServiceAdapter bleServiceAdapter;
    private GraphLayout graphLayout;
    private CurrentAndAvgLayout currentAndAvgLayout;
    private PeaksLayout peaksLayout;
    private StatisticLayout statisticLayout;

    public static MainFragment newInstance(String addressDevice) {
        MainFragment fragment = new MainFragment();
        fragment.addressDevice=addressDevice;
        return fragment;
    }

    private void initItem(){
        graphLayout=GraphLayout.newInstance();
        currentAndAvgLayout=CurrentAndAvgLayout.newInstance();
        peaksLayout=PeaksLayout.newInstance();
        statisticLayout=StatisticLayout.newInstance();
        getChildFragmentManager().beginTransaction().
                replace(R.id.graph_fragment_in_main, graphLayout).
                commit();
        getChildFragmentManager().beginTransaction().
                replace(R.id.current_and_avg_layout, currentAndAvgLayout).
                commit();
        getChildFragmentManager().beginTransaction().
                replace(R.id.peaks_layout, peaksLayout).
                commit();
        getChildFragmentManager().beginTransaction().
                replace(R.id.statistic_layout, statisticLayout).
                commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,container,false);
        GlobalValues.getMainMenu().setVisible(false);
        init();



        return view;
    }

    private void init(){
        initItem();
        bleServiceAdapter=new BleServiceAdapter(addressDevice);
        bleServiceAdapter.registerCallBack(this::callingBack);
    }


    @Override
    public void callingBack(Double valuePhasic, Double valueTonic) {
        graphLayout.addLineSeriesValue(valuePhasic);
        currentAndAvgLayout.setCurrentValue(valueTonic);
    }
}
