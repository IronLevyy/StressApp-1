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
import com.zemnuhov.stressapp.R;

import java.util.Calendar;
import java.util.Date;

public class GraphLayout extends Fragment {
    private GraphView mainGraph;
    private final LineGraphSeries<DataPoint> seriesNormal = new LineGraphSeries<>(new DataPoint[]{});
    private Integer i=0;

    public static GraphLayout newInstance() {

        Bundle args = new Bundle();

        GraphLayout fragment = new GraphLayout();
        fragment.setArguments(args);
        return fragment;
    }

    private void init(){
        mainGraph.addSeries(seriesNormal);
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

    }

    public void addLineSeriesValue(Double value){
        System.out.println(value);
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        seriesNormal.appendData(new DataPoint(time,value),true,1000000);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_graph_layout,container,false);
        mainGraph=view.findViewById(R.id.graph_main);
        init();
        return view;
    }
}
