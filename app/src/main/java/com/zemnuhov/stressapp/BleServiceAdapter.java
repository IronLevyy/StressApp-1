package com.zemnuhov.stressapp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

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
    private ArrayList<Double> filterArray;
    private ArrayList<Double> helpPhasicArray;
    private ArrayList<Double> clearDataArray;
    private Intent gattServiceIntent;


    public interface CallBack{
        void callingBack(Double valuePhasic, Double valueTonic);

    }

    public void registerCallBack(CallBack callback){
        this.callback = callback;
    }

    public BleServiceAdapter(String addressDevice){
        this.addressDevice=addressDevice;
        filterArray=new ArrayList<>();
        helpPhasicArray=new ArrayList<>();
        clearDataArray=new ArrayList<>();

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
                Double value=intent.getDoubleExtra(BluetoothLeService.EXTRA_DATA,0);
                Double rezultPhasic=filterData(value);
                if(rezultPhasic!=null){
                    callback.callingBack(rezultPhasic,value);
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

    private Double filterData(Double value){
        if(clearDataArray.size()<20){
            clearDataArray.add(value);
        }
        else {
            if (helpPhasicArray.size() < 2) {
                helpPhasicArray.add(avgList(clearDataArray));
            } else {
                filterArray.add((helpPhasicArray.get(1) - helpPhasicArray.get(0)) / 4);
                helpPhasicArray.remove(0);
                if (filterArray.size() > 20) {
                    Double rezult = avgList(filterArray);
                    filterArray.remove(0);
                    return rezult;
                }
            }
            clearDataArray.remove(0);
        }
        return null;
    }

    public Double avgList(ArrayList<Double> list){
        double result = 0;
        for(double item:list){
            result+=item;
        }
        return result/list.size();
    }

    public void disconnectedService(){
        context.unbindService(serviceConnection);
        context.unregisterReceiver(gattUpdateReceiver);

    }

    public void connectedService(){
        gattServiceIntent = new Intent(context, BluetoothLeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(gattServiceIntent);
        }else{
            context.startService(gattServiceIntent);
        }
        context.bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
    }
}
