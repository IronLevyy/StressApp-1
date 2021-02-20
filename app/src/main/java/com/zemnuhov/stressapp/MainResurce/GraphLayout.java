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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.zemnuhov.stressapp.R;

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
        mainGraph.getViewport().setXAxisBoundsManual(true);
        mainGraph.getViewport().setMinY(-3);
        mainGraph.getViewport().setMaxY(3);
        mainGraph.getViewport().setScalable(true);
        mainGraph.getViewport().setScrollable(true);
        mainGraph.getViewport().setScalableY(false);
        mainGraph.getViewport().setScrollableY(false);
        mainGraph.setBackgroundColor(Color.WHITE);
        mainGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
    }

    public void addLineSeriesValue(Double value){
        System.out.println(value);
        seriesNormal.appendData(new DataPoint(i,value),true,1000000);
        i++;

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
