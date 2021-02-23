package com.zemnuhov.stressapp.BLE;

import java.util.ArrayList;

public class DataTransform {
    private ArrayList<Double> filterArray;
    private ArrayList<Double> helpPhasicArray;
    private ArrayList<Double> clearDataArray;

    DataTransform(){
        filterArray=new ArrayList<>();
        helpPhasicArray=new ArrayList<>();
        clearDataArray=new ArrayList<>();
    }

    public Double filterData(Double value){
        if(clearDataArray.size()<20){
            clearDataArray.add(value);
        }
        else {
            if (helpPhasicArray.size() < 2) {
                helpPhasicArray.add(avgList(clearDataArray));
            } else {
                filterArray.add((helpPhasicArray.get(1) - helpPhasicArray.get(0)) / 4);
                helpPhasicArray.remove(0);
                if (filterArray.size() > 20) {
                    Double rezult = avgList(filterArray);
                    filterArray.remove(0);
                    return rezult;
                }
            }
            clearDataArray.remove(0);
        }
        return null;
    }

    public Double avgList(ArrayList<Double> list){
        double result = 0;
        for(double item:list){
            result+=item;
        }
        return result/list.size();
    }
}
