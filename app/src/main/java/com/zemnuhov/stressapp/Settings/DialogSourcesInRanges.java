package com.zemnuhov.stressapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;

public class DialogSourcesInRanges extends DialogFragment {

    private String title;
    private TextView titleView;
    private Spinner firstSpinner;
    private Spinner secondSpinner;
    private final String SP_SOURCE_TAG="SP_SOURCE_TAG";
    private Button button;
    private String firstSpinnerResult;
    private String secondSpinnerResult;
    private DialogCallBack callBack;

    public static DialogSourcesInRanges newInstance(String title) {
        DialogSourcesInRanges fragment = new DialogSourcesInRanges();
        fragment.title=title;
        return fragment;
    }

    interface DialogCallBack{
        public void dialogCallback(String firstSources,String secondSources);
    }

    public void registerCallback(DialogCallBack callBack){
        this.callBack=callBack;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.setting_dialog_source_of_range,null);
        titleView=view.findViewById(R.id.title_dialog);
        firstSpinner=view.findViewById(R.id.first_spinner);
        secondSpinner=view.findViewById(R.id.second_spinner);
        button=view.findViewById(R.id.dialog_button);

        String[] sources=GlobalValues.SharedPreferenceLoad(SP_SOURCE_TAG).split(":");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,sources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        titleView.setText(title);
        firstSpinner.setAdapter(adapter);
        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSpinnerResult=(String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondSpinner.setAdapter(adapter);
        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSpinnerResult=(String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.dialogCallback(firstSpinnerResult,secondSpinnerResult);
                dismiss();
            }
        });


        builder.setView(view);
        return builder.create();
    }
}
