package com.zemnuhov.stressapp.BLE;

import java.util.ArrayList;

public class DataFilter {
    private ArrayList<Double> filterArray;
    private ArrayList<Double> helpPhasicArray;
    private ArrayList<Double> clearDataArray;

    private double[] resultArray;

    DataFilter(){
        filterArray=new ArrayList<>();
        helpPhasicArray=new ArrayList<>();
        clearDataArray=new ArrayList<>();
        resultArray=new double[2];
    }

    public double[] filterData(Double value){
        if(clearDataArray.size()<100){
            clearDataArray.add(value);
        }
        else {
            if (helpPhasicArray.size() < 2) {
                Double tonic=avgList(clearDataArray);
                helpPhasicArray.add(tonic);
                resultArray[0]=tonic;
            } else {
                filterArray.add((helpPhasicArray.get(1) - helpPhasicArray.get(0)) /1);
                helpPhasicArray.remove(0);
                if (filterArray.size() > 30) {
                    resultArray[1]=avgList(filterArray);
                    filterArray.remove(0);
                    return resultArray;
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
