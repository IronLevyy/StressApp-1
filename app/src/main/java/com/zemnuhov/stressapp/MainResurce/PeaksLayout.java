package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.DataBase.RecodingPeaksDB;
import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class PeaksLayout extends Fragment {

    private TextView timeRange;
    private final ArrayList<String> timesRanges=new ArrayList<>(Arrays.asList("10M","1H","1D"));
    private final ArrayList<Long> timesRangesMillisecond=new ArrayList(Arrays.asList(600000L,3600000L,86400000L));
    private TextView peaksCounter;
    private RecodingPeaksDB recodingPeaksDB;

    public static PeaksLayout newInstance() {
        PeaksLayout fragment = new PeaksLayout();
        return fragment;
    }

    private void init(View view){
        timeRange=view.findViewById(R.id.time_range);
        timeRange.setText(timesRanges.get(0));

        peaksCounter=view.findViewById(R.id.peaks_counter);

        recodingPeaksDB =new RecodingPeaksDB();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_number_of_peaks,container,false);
        init(view);
        refreshPeaks();

        timeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer thisRange = timesRanges.indexOf(timeRange.getText().toString());
                if(thisRange<timesRanges.size()-1){
                    timeRange.setText(timesRanges.get(thisRange+1));
                }else {
                    timeRange.setText(timesRanges.get(0));
                }
                refreshPeaks();
            }
        });
        return view;
    }

    public void refreshPeaks(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Integer position=timesRanges.indexOf(timeRange.getText().toString());
                Integer peaks= recodingPeaksDB.readDB(timesRangesMillisecond.get(position));
                peaksCounter.setText(peaks.toString());
            }
        });
        thread.start();

    }



}
