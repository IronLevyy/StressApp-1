package com.zemnuhov.stressapp.MainResurce;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.zemnuhov.stressapp.DataBase.DataBaseClass;
import com.zemnuhov.stressapp.DataBase.TenMinuteObjectDB;
import com.zemnuhov.stressapp.R;
import com.zemnuhov.stressapp.Statistic.StatisticActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.PrimitiveIterator;

public class PeaksLayout extends Fragment {

    private TextView timeRange;
    private LinearLayout peaksLayout;
    private final ArrayList<String> timesRanges=new ArrayList<>(Arrays.asList("10M","1H","1D"));
    private final ArrayList<Long> timesRangesMillisecond=
            new ArrayList(Arrays.asList(600000L,3600000L,86400000L));
    private TextView peaksCounter;
    private DataBaseClass dataBase;
    private GraphView barGraph;
    private ArrayList<TenMinuteObjectDB> tenMinuteObjectDB;
    private BarGraphSeries<DataPoint> barSeries=new BarGraphSeries<>();
    private boolean isFirstFeeling=true;
    private long lastFillingGraph;
    private Thread thread;
    private boolean refreshFlag;

    public static PeaksLayout newInstance() {
        return new PeaksLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_number_of_peaks,container,false);
        init(view);
        refreshPeaks();
        feelingGraph();

        timeRange.setOnClickListener(view1 -> {
            Integer thisRange = timesRanges.indexOf(timeRange.getText().toString());
            if(thisRange<timesRanges.size()-1){
                timeRange.setText(timesRanges.get(thisRange+1));
            }else {
                timeRange.setText(timesRanges.get(0));
            }
            refreshPeaks();
        });
        return view;
    }

    private void feelingGraph(){
        thread=new Thread(() -> {
            while (refreshFlag) {
                Date time = new Date();
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm");
                if ((formatForDateNow.format(time).substring(1).equals("0") &&
                        time.getTime()-lastFillingGraph>70000) || isFirstFeeling) {
                    if(!isFirstFeeling){
                        lastFillingGraph=time.getTime();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    barGraph.removeAllSeries();
                    tenMinuteObjectDB = dataBase.readTenMinuteTable();
                    barSeries = new BarGraphSeries<>();
                    for (TenMinuteObjectDB item : tenMinuteObjectDB) {
                        if (new Date().getTime() - item.getTime().getTime() < 3600000) {
                            barSeries.appendData(new DataPoint(item.getTime(),
                                            item.getPeaks()),
                                    true,
                                    10);
                        }
                    }
                    getActivity().runOnUiThread(() ->{
                        graphSetting(barGraph);
                    });
                    isFirstFeeling=false;
                    barGraph.invalidate();
                }
            }
        });
        thread.start();
    }

    void graphSetting(GraphView view){
        view.getViewport().setXAxisBoundsManual(true);
        view.getViewport().setYAxisBoundsManual(true);
        view.getViewport().setMinX(barSeries.getLowestValueX());
        view.getViewport().setMaxX(barSeries.getLowestValueX()+3600000);
        view.getViewport().setMinY(0);
        view.getViewport().setMaxY(barSeries.getHighestValueY()+2);
        view.getViewport().setScalable(false);
        view.getViewport().setScrollable(true);
        view.getViewport().setScalableY(false);
        view.getViewport().setScrollableY(false);
        view.setBackgroundColor(Color.WHITE);
        view.getGridLabelRenderer().setGridColor(Color.GRAY);
        view.getGridLabelRenderer().setVerticalLabelsVisible(false);
        view.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        barSeries.setSpacing(1);
        barSeries.setDataWidth(500000);
        barSeries.setValueDependentColor(data -> {
            if(data.getY()<23){
                return getResources().getColor(R.color.green_active);
            }
            if(data.getY()>=23 && data.getY()<=30){
                return getResources().getColor(R.color.yellow_active);
            }
            return getResources().getColor(R.color.red_active);
        });
        barGraph.addSeries(barSeries);
    }

    private void init(View view){
        refreshFlag=true;
        dataBase=new DataBaseClass();
        timeRange=view.findViewById(R.id.time_range);
        timeRange.setText(timesRanges.get(0));

        peaksCounter=view.findViewById(R.id.peaks_counter);
        barGraph=view.findViewById(R.id.peaks_counter_graph);

        peaksLayout=view.findViewById(R.id.peaks_layout);
        peaksLayout.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), StatisticActivity.class);
            startActivity(intent);
        });
    }

    public void refreshPeaks(){
        Thread thread=new Thread(() -> {
            int position=timesRanges.indexOf(timeRange.getText().toString());
            int peaks= dataBase.readCountPeak(timesRangesMillisecond.get(position));
            peaksCounter.setText(String.valueOf(peaks));
        });
        thread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshFlag=false;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshFlag=true;
        if(!isFirstFeeling) {
            isFirstFeeling = true;
        }
    }
}
