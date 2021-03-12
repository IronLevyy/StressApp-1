package com.zemnuhov.stressapp.StatisticSettings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zemnuhov.stressapp.DataBase.SourcesStatisticDB;
import com.zemnuhov.stressapp.R;

public class DialogSelectedSource extends DialogFragment {

    private Long time;
    private String source;
    private Integer peaksCount;
    private Double tonicAvg;
    private TextView selectedSource;
    private Button buttonNo;
    private Button buttonYes;

    public static DialogSelectedSource newInstance(Long time,String source
            ,Integer peaksCount, Double tonicAvg) {
        DialogSelectedSource fragment = new DialogSelectedSource();
        fragment.time=time;
        fragment.source=source;
        fragment.peaksCount=peaksCount;
        fragment.tonicAvg=tonicAvg;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sources_activity,null);
        selectedSource=view.findViewById(R.id.source_in_dialog);
        buttonNo=view.findViewById(R.id.button_no_dialog);
        buttonYes=view.findViewById(R.id.button_yes_dialog);
        selectedSource.setText(source);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourcesStatisticDB statisticDB=new SourcesStatisticDB();
                statisticDB.addToDB(time,source,peaksCount,tonicAvg);
                dismiss();
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        builder.setView(view);
        return builder.create();
    }
}
