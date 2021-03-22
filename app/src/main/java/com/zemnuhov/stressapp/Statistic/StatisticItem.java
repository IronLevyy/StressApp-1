package com.zemnuhov.stressapp.Statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticItem extends Fragment {

    private TextView dateView;
    private TextView tonicView;
    private TextView peaksView;
    private Date date;
    private Integer tonic;
    private Integer peaks;

    public static StatisticItem newInstance(Date date, Integer tonic, Integer peaks) {
        StatisticItem fragment = new StatisticItem();
        fragment.date=date;
        fragment.tonic=tonic;
        fragment.peaks=peaks;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.statistic_item,container,false);
        dateView=view.findViewById(R.id.statistic_date_in_item);
        tonicView=view.findViewById(R.id.statistic_tonic_value);
        peaksView=view.findViewById(R.id.statistic_peaks_value);
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy\nHH:mm");
        dateView.setText(dateFormat.format(date));
        tonicView.setText(tonic.toString());
        peaksView.setText(peaks.toString());
        return view;
    }
}
