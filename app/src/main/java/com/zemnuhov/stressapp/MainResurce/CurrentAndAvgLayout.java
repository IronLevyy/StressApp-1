package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.DataBase.DataBaseClass;
import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CurrentAndAvgLayout extends Fragment {

    private TextView currentValue;
    private TextView avgValue;
    private TextView timeRange;
    private final ArrayList<String> timesRanges=
            new ArrayList<>(Arrays.asList("10M","1H","1D"));
    private final ArrayList<Long> timesRangesMillisecond=
            new ArrayList(Arrays.asList(600000L,3600000L,86400000L));
    private DataBaseClass dataBase;
    private ScaleView scale;

    public static CurrentAndAvgLayout newInstance() {
        return new CurrentAndAvgLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_avg_current_value,container,false);
        init(view);
        refreshAvg();

        timeRange.setOnClickListener(view1 -> {
            int thisRange = timesRanges.indexOf(timeRange.getText().toString());
            if(thisRange<timesRanges.size()-1){
                timeRange.setText(timesRanges.get(thisRange+1));
            }else {
                timeRange.setText(timesRanges.get(0));
            }
            refreshAvg();
        });
        return view;
    }

    private void init(View view){
        currentValue=view.findViewById(R.id.current_value);
        avgValue=view.findViewById(R.id.avg_value);
        timeRange=view.findViewById(R.id.time_range_tonic);
        timeRange.setText(timesRanges.get(0));
        dataBase=new DataBaseClass();
        scale=ScaleView.newInstance();
        ConstantAndHelp.getFragmentManager().beginTransaction().
                replace(R.id.scale_fragment,scale).
                commit();
    }

    public void refreshAvg(){
        int position=timesRanges.indexOf(timeRange.getText().toString());
        int avg= dataBase.readAvgTonic(timesRangesMillisecond.get(position));
        avgValue.setText(String.valueOf(avg));

    }

    public void setCurrentValue(Double value){
        currentValue.setText(String.valueOf(value.intValue()));
    }

    public void setScale(Integer value){
        scale.setScale(value);
    }
}
