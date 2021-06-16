package com.zemnuhov.stressapp.Statistic;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.zemnuhov.stressapp.DataBase.TenMinuteInDayDB;
import com.zemnuhov.stressapp.DataBase.TenMinuteObjectDB;
import com.zemnuhov.stressapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatisticActivity extends AppCompatActivity {

    private GraphView barChart;
    private BarGraphSeries<DataPoint> barSeries=new BarGraphSeries<>();
    private LineGraphSeries<DataPoint> lineSeries=new LineGraphSeries<>();
    private ArrayList<TenMinuteObjectDB> tenMinuteObjectDB;
    private TenMinuteInDayDB dataBase;
    private LinearLayout scrollView;
    private Date dayBegin=new Date();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_activity);
        dataBase=new TenMinuteInDayDB();
        scrollView=findViewById(R.id.statistic_item_scroll);
        barChart=findViewById(R.id.statistic_bar_chart);
        tenMinuteObjectDB=dataBase.readTenMinuteTable();
        fillingBarAndLineChart();
        graphSetting(barChart);


        SimpleDateFormat formatDayBegin=new SimpleDateFormat("dd-MM-yyyy");
        String date=formatDayBegin.format(dayBegin);
        try {
            dayBegin=formatDayBegin.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(TenMinuteObjectDB item:tenMinuteObjectDB){
            if(item.getTime().after(dayBegin)) {
                StatisticItem statisticItem = StatisticItem.newInstance(item.getTime(), item.getTonic().intValue(), item.getPeaks());
                getSupportFragmentManager().beginTransaction().add(scrollView.getId(), statisticItem).commit();
            }
        }
    }

    private void fillingBarAndLineChart(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm");
        barChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Long date= (long) value;
                    return dateFormat.format(new Date(date));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        SimpleDateFormat formatDayBegin=new SimpleDateFormat("dd-MM-yyyy");
        String date=formatDayBegin.format(dayBegin);
        try {
            dayBegin=formatDayBegin.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (TenMinuteObjectDB item:tenMinuteObjectDB){
            if(item.getTime().after(dayBegin)) {
                barSeries.appendData(new DataPoint(item.getTime(),
                        item.getPeaks()),
                        false,
                        150);
                lineSeries.appendData(new DataPoint(item.getTime(),
                        valueMapper(item.getTonic(),0,10000,50,100)),
                        false,
                        150);
            }
        }
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
        barChart.addSeries(barSeries);
        barChart.addSeries(lineSeries);
        lineSeries.setColor(getResources().getColor(R.color.black));

    }

    void graphSetting(GraphView view){
        view.getViewport().setYAxisBoundsManual(true);
        view.getViewport().setXAxisBoundsManual(true);
        view.getViewport().setMinY(0);
        view.getViewport().setMaxY(100);
        view.getViewport().setMinX(barSeries.getLowestValueX());
        view.getViewport().setMaxX(barSeries.getHighestValueX());
        view.getViewport().setScalable(true);
        view.getViewport().setScrollable(true);
        view.getViewport().setScalableY(false);
        view.getViewport().setScrollableY(false);
        view.setBackgroundColor(Color.WHITE);
        view.getGridLabelRenderer().setGridColor(Color.WHITE);
        view.getGridLabelRenderer().setVerticalLabelsVisible(false);
    }

    private Double valueMapper(double value,double smin,double smax,double dmin, double dmax)
    {
        return ((value-smin) / (smax-smin)) * (dmax-dmin) + dmin;
    }
}
