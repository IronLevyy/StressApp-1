package com.zemnuhov.stressapp.MainResurce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.R;

public class SourceItemMain extends Fragment {
    private Integer color;
    private String source;
    private Integer count;
    private ImageView colorView;
    private TextView sourceView;
    private TextView countView;

    public static SourceItemMain newInstance(Integer color, String source, Integer count) {
        SourceItemMain fragment = new SourceItemMain();
        fragment.color=color;
        fragment.source=source;
        fragment.count=count;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_statistic_lable_sources,container,
                false);
        colorView=view.findViewById(R.id.image_color_sources);
        sourceView=view.findViewById(R.id.name_sources);
        countView=view.findViewById(R.id.count_sources_statistic);
        colorView.setColorFilter(color);
        sourceView.setText(source);
        countView.setText(count.toString());
        return view;
    }
}
