package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class PeaksLayout extends Fragment {

    private TextView timeRange;
    private final ArrayList<String> timesRanges=new ArrayList<>(Arrays.asList("10M","1H","1D"));

    public static PeaksLayout newInstance() {
        PeaksLayout fragment = new PeaksLayout();
        return fragment;
    }

    private void init(View view){
        timeRange=view.findViewById(R.id.time_range);
        timeRange.setText(timesRanges.get(0));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.number_of_peaks,container,false);
        init(view);

        timeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer thisRange = timesRanges.indexOf(timeRange.getText().toString());
                if(thisRange<timesRanges.size()-1){
                    timeRange.setText(timesRanges.get(thisRange+1));
                }else {
                    timeRange.setText(timesRanges.get(0));
                }
            }
        });
        return view;
    }

}
