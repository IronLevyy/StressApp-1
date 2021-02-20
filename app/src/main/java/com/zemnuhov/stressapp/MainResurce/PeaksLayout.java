package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.R;

public class PeaksLayout extends Fragment {

    public static PeaksLayout newInstance() {
        PeaksLayout fragment = new PeaksLayout();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.number_of_peaks,container,false);

        return view;
    }

}
