package com.zemnuhov.stressapp.MainResurce;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.zemnuhov.stressapp.R;

import java.util.Calendar;
import java.util.Date;

public class GraphLayout extends Fragment {
    private GraphView mainGraph;
    private final LineGraphSeries<DataPoint> seriesNormal = new LineGraphSeries<>(new DataPoint[]{});
    private final PointsGraphSeries<DataPoint> seriesPeaks = new PointsGraphSeries<>(new DataPoint[]{});
    private Integer i=0;

    public static GraphLayout newInstance() {

        Bundle args = new Bundle();

        GraphLayout fragment = new GraphLayout();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_graph_layout,container,false);
        mainGraph=view.findViewById(R.id.graph_main);
        init();
        return view;
    }

    private void init(){
        mainGraph.addSeries(seriesNormal);
        mainGraph.addSeries(seriesPeaks);

        mainGraph.getViewport().setYAxisBoundsManual(true);
        mainGraph.getViewport().setXAxisBoundsManual(false);
        mainGraph.getViewport().setMinY(-3);
        mainGraph.getViewport().setMaxY(3);

        mainGraph.getViewport().setMinX(0);
        mainGraph.getViewport().setMaxX(15000);
        mainGraph.getViewport().setScalable(true);
        mainGraph.getViewport().setScrollable(true);
        mainGraph.getViewport().setScalableY(false);
        mainGraph.getViewport().setScrollableY(false);
        mainGraph.setBackgroundColor(Color.WHITE);
        mainGraph.getGridLabelRenderer().setGridColor(Color.WHITE);

        mainGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        mainGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        mainGraph.getGridLabelRenderer().setHumanRounding(false);
        mainGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        mainGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        seriesPeaks.setColor(Color.RED);
        seriesPeaks.setSize(3);

        seriesNormal.setColor(Color.BLACK);

    }

    public void addLineSeriesValue(Double value,Long time){
        System.out.println(value);
        if(value>0.7){
            seriesPeaks.appendData(new DataPoint(time,value),true,1000000);
        }
        seriesNormal.appendData(new DataPoint(time,value),true,1000000);

    }
}
