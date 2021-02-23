package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.BLE.BleServiceAdapter;
import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;
import com.zemnuhov.stressapp.ScanResurce.ScanFragment;

public class MainFragment extends Fragment implements BleServiceAdapter.CallBack {


    private BleServiceAdapter bleServiceAdapter;
    private GraphLayout graphLayout;
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

    private void initItem(){
        graphLayout=GraphLayout.newInstance();
        currentAndAvgLayout=CurrentAndAvgLayout.newInstance();
        peaksLayout=PeaksLayout.newInstance();
        statisticLayout=StatisticLayout.newInstance();
        getChildFragmentManager().beginTransaction().
                replace(R.id.graph_fragment_in_main, graphLayout).
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,container,false);
        GlobalValues.getMainMenu().setVisible(false);
        init();

        return view;
    }

    private void init(){
        initItem();
        bleServiceAdapter=new BleServiceAdapter(addressDevice);
        bleServiceAdapter.registerCallBack(this::callingBack);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(!bleServiceAdapter.getConnectedDevice()){
                    GlobalValues.getFragmentManager().beginTransaction().
                            replace(R.id.fragment_container, ScanFragment.newInstance()).
                            commit();
                    Toast.makeText(getContext(),
                            "Подключение не удалось.\nПроверьте включено ли устройство.",
                            Toast.LENGTH_LONG).show();

                }
            }
        });
        handler.postDelayed(thread,5000);


    }


    @Override
    public void callingBack(Double valuePhasic, Double valueTonic, Long time) {
        if(valuePhasic!=-1000 && time!=0) {
            graphLayout.addLineSeriesValue(valuePhasic,time);
        }
        currentAndAvgLayout.setCurrentValue(valueTonic);
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
