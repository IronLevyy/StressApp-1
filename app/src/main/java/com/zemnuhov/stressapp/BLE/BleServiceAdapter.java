package com.zemnuhov.stressapp.BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.zemnuhov.stressapp.BLE.BluetoothLeService;
import com.zemnuhov.stressapp.GlobalValues;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BIND_AUTO_CREATE;

public class BleServiceAdapter {
    private String addressDevice;
    private Context context= GlobalValues.getContext();
    private BluetoothLeService bluetoothLeService;
    private Boolean connectedService = false;
    private Boolean connectedDevice = false;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private CallBack callback;

    private Intent gattServiceIntent;


    public interface CallBack{
        void callingBack(Double valuePhasic, Double valueTonic,Long time,
                         Boolean isPeaks,Boolean isTonic);

    }

    public void registerCallBack(CallBack callback){
        this.callback = callback;
    }

    public BleServiceAdapter(String addressDevice){
        this.addressDevice=addressDevice;


    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e("BluetoothLE", "Unable to initialize Bluetooth");
                //Отправить на поиск (Закрыть фрагмент)
            }
            bluetoothLeService.connect(addressDevice);
            connectedService=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
            connectedService=false;
        }
    };

    public Boolean getConnectedDevice(){
        return connectedDevice;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connectedDevice = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connectedDevice = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                getServices(bluetoothLeService.getSupportedGattServices());
                setCharacteristic();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                connectedDevice = true;
                Double value=intent.getDoubleExtra(BluetoothLeService.CLEAR_DATA,0);
                Double rezultPhasic=intent.getDoubleExtra(BluetoothLeService.PHASIC_DATA,-1000);
                Long time=intent.getLongExtra(BluetoothLeService.NOW_TIME,0);
                Boolean isPeaks=intent.getBooleanExtra(BluetoothLeService.IS_PEAKS,false);
                Boolean isTonic=intent.getBooleanExtra(BluetoothLeService.IS_TONIC,false);
                if(rezultPhasic!=null){
                    callback.callingBack(rezultPhasic,value,time,isPeaks,isTonic);
                }

            }
        }
    };

    void getServices(List<BluetoothGattService> gattServices){
        UUID uuidValue=UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
        for(BluetoothGattService service:gattServices){
            if(service.getUuid().equals(uuidValue)){
                characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            }
        }

    }

    private void setCharacteristic(){
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0 ) {
            if (notifyCharacteristic != null) {
                bluetoothLeService.setCharacteristicNotification(notifyCharacteristic, false);
                notifyCharacteristic = null;
            }
            bluetoothLeService.readCharacteristic(characteristic);
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            notifyCharacteristic = characteristic;
            bluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
        }
    }



    public void disconnectedService(){
        context.unbindService(serviceConnection);
        context.unregisterReceiver(gattUpdateReceiver);

    }

    public void connectedService(){
        gattServiceIntent = new Intent(context, BluetoothLeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(gattServiceIntent);
        }else {
            context.startService(gattServiceIntent);
        }
        context.bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
    }
}
