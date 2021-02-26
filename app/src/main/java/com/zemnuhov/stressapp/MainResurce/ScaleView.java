package com.zemnuhov.stressapp.MainResurce;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ScaleView extends Fragment {

    LinearLayoutCompat val1000g;
    LinearLayoutCompat val2000g;
    LinearLayoutCompat val3000g;
    LinearLayoutCompat val4000g;
    LinearLayoutCompat val4500y;
    LinearLayoutCompat val5500y;
    LinearLayoutCompat val6500y;
    LinearLayoutCompat val7500r;
    LinearLayoutCompat val8000r;
    HashMap<LinearLayoutCompat, ArrayList<Integer>> linearsStates;

    public static ScaleView newInstance() {
        ScaleView fragment = new ScaleView();
        return fragment;
    }

    private void init(View view){
        linearsStates=new HashMap<>();
        val1000g=view.findViewById(R.id.val_1000);
        val2000g=view.findViewById(R.id.val_2000);
        val3000g=view.findViewById(R.id.val_3000);
        val4000g=view.findViewById(R.id.val_4000);
        val4500y=view.findViewById(R.id.val_4500);
        val5500y=view.findViewById(R.id.val_5500);
        val6500y=view.findViewById(R.id.val_6500);
        val7500r=view.findViewById(R.id.val_7500);
        val8000r=view.findViewById(R.id.val_8000);
        hashMapInit();
    }

    private void hashMapInit(){
        linearsStates.put(val1000g,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.green_not_active),
                getResources().getColor(R.color.green_active))));
        linearsStates.put(val2000g,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.green_not_active),
                getResources().getColor(R.color.green_active))));
        linearsStates.put(val3000g,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.green_not_active),
                getResources().getColor(R.color.green_active))));
        linearsStates.put(val4000g,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.green_not_active),
                getResources().getColor(R.color.green_active))));
        linearsStates.put(val4500y,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.yellow_not_active),
                getResources().getColor(R.color.yellow_active))));
        linearsStates.put(val5500y,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.yellow_not_active),
                getResources().getColor(R.color.yellow_active))));
        linearsStates.put(val6500y,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.red_not_active),
                getResources().getColor(R.color.red_active))));
        linearsStates.put(val7500r,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.red_not_active),
                getResources().getColor(R.color.red_active))));
        linearsStates.put(val8000r,new ArrayList<>(Arrays.asList(
                getResources().getColor(R.color.red_not_active),
                getResources().getColor(R.color.red_active))));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.custom_scale,container,false);
        init(view);
        return view;

    }

    private void activated(LinearLayoutCompat linear){
        linear.setBackgroundColor(linearsStates.get(linear).get(1));
    }
    private void deactivated(LinearLayoutCompat linear) {
        linear.setBackgroundColor(linearsStates.get(linear).get(0));
    }
    private void conditions(Integer value, Integer border, LinearLayoutCompat linear){
        if(value>border){
            activated(linear);
        }else {
            deactivated(linear);
        }
    }

    public void setScale(Integer value) {
        conditions(value, 1000, val1000g);
        conditions(value, 2000, val2000g);
        conditions(value, 3000, val3000g);
        conditions(value, 4000, val4000g);
        conditions(value, 4500, val4500y);
        conditions(value, 5500, val5500y);
        conditions(value, 6500, val6500y);
        conditions(value, 7500, val7500r);
        conditions(value, 8000, val8000r);
    }

}
