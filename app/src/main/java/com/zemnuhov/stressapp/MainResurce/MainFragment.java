package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.BLE.BleServiceAdapter;
import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.R;
import com.zemnuhov.stressapp.ScanResurce.ScanFragment;

public class MainFragment extends Fragment implements BleServiceAdapter.CallBack {


    private BleServiceAdapter bleServiceAdapter;
    private PhasicGraphLayout phasicGraphLayout;
    private TonicGraphLayout tonicGraphLayout;
    private CurrentAndAvgLayout currentAndAvgLayout;
    private PeaksLayout peaksLayout;
    private StatisticLayout statisticLayout;
    private String addressDevice;
    private Handler handler = new Handler();

    public static MainFragment newInstance(String addressDevice) {
        MainFragment fragment = new MainFragment();
        fragment.addressDevice =addressDevice;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,container,false);
        ConstantAndHelp.getMainMenu().setVisible(false);
        init();

        return view;
    }

    private void initItem(){
        phasicGraphLayout = PhasicGraphLayout.newInstance();
        tonicGraphLayout = TonicGraphLayout.newInstance();
        phasicGraphLayout.setSwappingGraph(tonicGraphLayout);
        tonicGraphLayout.setSwappingGraph(phasicGraphLayout);
        currentAndAvgLayout=CurrentAndAvgLayout.newInstance();
        peaksLayout=PeaksLayout.newInstance();
        statisticLayout=StatisticLayout.newInstance();
        getChildFragmentManager().beginTransaction().
                replace(R.id.graph_fragment_in_main, phasicGraphLayout).
                commit();
        getChildFragmentManager().beginTransaction().
                replace(R.id.current_and_avg_layout, currentAndAvgLayout).
                commit();
        getChildFragmentManager().beginTransaction().
                replace(R.id.peaks_layout, peaksLayout).
                commit();
        getChildFragmentManager().beginTransaction().
                replace(R.id.statistic_layout, statisticLayout).
                commit();
    }

    private void init(){
        initItem();
        bleServiceAdapter=new BleServiceAdapter(addressDevice);
        bleServiceAdapter.registerCallBack(this::callingBack);
        Thread thread=new Thread(() -> {
            if(!bleServiceAdapter.getConnectedDevice()){
                ConstantAndHelp.getFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, ScanFragment.newInstance()).
                        commit();
                Toast.makeText(getContext(),
                        "Подключение не удалось.\nПроверьте включено ли устройство.",
                        Toast.LENGTH_LONG).show();
            }
        });
        handler.postDelayed(thread,5000);
    }

    @Override
    public void callingBack(Double valuePhasic, Double valueTonic,
                            Long time,Boolean isPeaks,Boolean isTonic) {
        if(valuePhasic!=-1000 && time!=0) {
            phasicGraphLayout.addLineSeriesValue(valuePhasic,time);
            tonicGraphLayout.addLineSeriesValue(valueTonic,time);
        }
        if(isPeaks){
            peaksLayout.refreshPeaks();
        }
        if(isTonic){
            currentAndAvgLayout.refreshAvg();
        }
        currentAndAvgLayout.setCurrentValue(valueTonic);
        currentAndAvgLayout.setScale(valueTonic.intValue());
    }

    @Override
    public void onStop() {
        super.onStop();
        bleServiceAdapter.disconnectedService();
    }

    @Override
    public void onStart() {
        super.onStart();
        bleServiceAdapter.connectedService();
    }
}
