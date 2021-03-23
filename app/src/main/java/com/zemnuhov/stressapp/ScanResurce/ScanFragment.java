package com.zemnuhov.stressapp.ScanResurce;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ScanFragment extends Fragment {
    RecyclerView recyclerView;
    View view;
    LinearLayoutManager manager;
    SwipeRefreshLayout swipe;
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 10000;
    HashSet<Device> devices = new HashSet<>();
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if(result.getDevice().getName()!=null) {
                        if(isList(result.getDevice().getAddress())) {
                            devices.add(new Device(result.getDevice().getAddress(),
                                    result.getDevice().getName()));
                            List temp = new ArrayList(Arrays.asList(devices.toArray()));
                            RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(temp);
                            recyclerView.setAdapter(recycleViewAdapter);
                        }
                    }
                }
            };



    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.scan_fragment,container,false);
        ConstantAndHelp.getMainMenu().setVisible(true);
        init();
        setListeners();
        return view;
    }

    private Boolean isList(String adress){
        boolean flag=true;
        for(Device device:devices){
            if(adress.equals(device.MAC)){
                flag=false;
            }
        }
        return flag;
    }

    private void init(){
        recyclerView=view.findViewById(R.id.recycler_view_list);
        swipe=view.findViewById(R.id.refreshListDevice);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        List temp = new ArrayList(Arrays.asList(devices.toArray()));
        RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(temp);
        recyclerView.setAdapter(recycleViewAdapter);
    }

    private void setListeners(){
        swipe.setOnRefreshListener(() -> {
            devices.clear();
            List temp = new ArrayList(Arrays.asList(devices.toArray()));
            RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(temp);
            recyclerView.setAdapter(recycleViewAdapter);
            scanLeDevice();

        });
        ConstantAndHelp.getMainMenu().setOnMenuItemClickListener(item -> {
            devices.clear();
            List temp = new ArrayList(Arrays.asList(devices.toArray()));
            RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(temp);
            recyclerView.setAdapter(recycleViewAdapter);
            scanLeDevice();
            return false;
        });


    }

    private void scanLeDevice() {
        if (!mScanning) {
            handler.postDelayed(() -> {
                mScanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                swipe.setRefreshing(false);
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }
}
