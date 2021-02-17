package com.zemnuhov.stressapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScanFragment extends Fragment {
    RecyclerView recyclerView;
    View view;
    LinearLayoutManager manager;

    List<Device> devices=new ArrayList<>(Arrays.asList(new Device("15:56:AC15:1568","NoStress"),
            new Device("64:56:AC15:1568","NoStressKGR1")));

    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        return fragment;
    }

    private void init(){
        recyclerView=view.findViewById(R.id.recycler_view_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        RecycleViewAdapter recycleViewAdapter=new RecycleViewAdapter(devices);
        recyclerView.setAdapter(recycleViewAdapter);
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
        devices.add(new Device("15:56:AC15:1568","NoStress"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.scan_fragment,container,false);
        init();
        return view;
    }
}
