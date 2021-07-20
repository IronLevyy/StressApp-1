package com.zemnuhov.stressapp.AuthorizationResources;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.R;

public class AuthorizationActivity extends Fragment {

    private EditText loginEntry;
    private EditText passwordEntry;
    private Button authButton;


    public static AuthorizationActivity newInstance() {
        AuthorizationActivity fragment = new AuthorizationActivity();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.authorization_fragment,container,false);
        loginEntry = view.findViewById(R.id.login_entry);
        passwordEntry = view.findViewById(R.id.password_entry);
        authButton = view.findViewById(R.id.auth_button);

        authButton.setOnClickListener(v->{
            AuthorizationController authorizationController = new AuthorizationController();
            if(loginEntry.getText().length()>0 && passwordEntry.getText().length()>3) {
                authorizationController.authorization(loginEntry.getText().toString(),
                        passwordEntry.getText().toString());
            }else {
                Toast.makeText(getContext(),"Вы не корекктно ввели логин или пароль!",
                        Toast.LENGTH_LONG).show();
            }
        });



        return view;
    }
}
