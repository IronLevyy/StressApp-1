package com.zemnuhov.stressapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zemnuhov.stressapp.MainResurce.MainFragment;
import com.zemnuhov.stressapp.ScanResurce.ScanFragment;

import static com.zemnuhov.stressapp.Settings.ParsingSPref.SP_INTERVAL_TAG;
import static com.zemnuhov.stressapp.Settings.ParsingSPref.SP_SOURCE_TAG;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstantAndHelp.setContext(getApplicationContext());
        ConstantAndHelp.setFragmentManager(getSupportFragmentManager());

        String sourcesSharedPref = ConstantAndHelp.SharedPreferenceLoad(SP_SOURCE_TAG);
        String intervalsSharedPref = ConstantAndHelp.SharedPreferenceLoad(SP_INTERVAL_TAG);

        if(sourcesSharedPref.equals("0")){
            ConstantAndHelp.SharedPreferenceSave(SP_SOURCE_TAG,ConstantAndHelp.getDefaultSources());
            sourcesSharedPref = ConstantAndHelp.SharedPreferenceLoad(SP_SOURCE_TAG);
        }
        if(intervalsSharedPref.equals("0")|| intervalsSharedPref.equals("")){
            ConstantAndHelp.SharedPreferenceSave(SP_INTERVAL_TAG,
                    ConstantAndHelp.getDefaultIntervals());
            intervalsSharedPref = ConstantAndHelp.SharedPreferenceLoad(SP_INTERVAL_TAG);
        }

        final BluetoothManager bluetoothManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }


    }

    private void startProgram(){
        if(ConstantAndHelp.loadDeviceAddress().equals("0")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, ScanFragment.newInstance()).
                    commit();
        }else {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container,
                            MainFragment.newInstance(ConstantAndHelp.loadDeviceAddress())).
                    commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem refreshButtonMenu = menu.findItem(R.id.menu_refresh);
        ConstantAndHelp.setMainMenu(refreshButtonMenu);
        startProgram();
        return super.onCreateOptionsMenu(menu);
    }
}