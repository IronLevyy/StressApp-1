package com.zemnuhov.stressapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zemnuhov.stressapp.DataBase.DataBaseClass;
import com.zemnuhov.stressapp.R;

public class DialogSelectedSource extends DialogFragment {

    private Long time;
    private String source;
    private Integer peaksCount;
    private Double tonicAvg;

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
        TextView selectedSource = view.findViewById(R.id.source_in_dialog);
        Button buttonNo = view.findViewById(R.id.button_no_dialog);
        Button buttonYes = view.findViewById(R.id.button_yes_dialog);
        selectedSource.setText(source);
        clickListeners(buttonNo,buttonYes);
        builder.setView(view);
        return builder.create();
    }

    private void clickListeners(Button buttonNo, Button buttonYes){
        buttonYes.setOnClickListener(v -> {
            DataBaseClass dataBase=new DataBaseClass();
            dataBase.addLineInStatistic(time,source,peaksCount,tonicAvg);
            dismiss();
        });
        buttonNo.setOnClickListener(v -> dismiss());
    }
}
