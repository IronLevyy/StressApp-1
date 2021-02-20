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

public class CurrentAndAvgLayout extends Fragment {

    private TextView currentValue;

    public static CurrentAndAvgLayout newInstance() {
        CurrentAndAvgLayout fragment = new CurrentAndAvgLayout();
        return fragment;
    }

    private void init(View view){
        currentValue=view.findViewById(R.id.current_value);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.avg_current_value,container,false);
        init(view);
        return view;
    }

    public void setCurrentValue(Double value){
        currentValue.setText(String.valueOf(value.intValue()));
    }
}
