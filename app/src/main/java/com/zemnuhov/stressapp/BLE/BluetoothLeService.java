package com.zemnuhov.stressapp.BLE;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.zemnuhov.stressapp.DataBase.RecodingPeaksDB;
import com.zemnuhov.stressapp.DataBase.RecodingTonicDB;
import com.zemnuhov.stressapp.DataBase.ResultDB;
import com.zemnuhov.stressapp.Notifications.NotificationClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String bluetoothDeviceAddress;
    private BluetoothGatt bluetoothGatt;
    private DataTransform dataTransform;
    private int connectionState = STATE_DISCONNECTED;
    private Boolean peaksFlag=false;
    private HashMap<Long,Double> peaksArray;
    private RecodingPeaksDB recodingPeaksDB;
    private RecodingTonicDB recodingTonicDB;
    private Long lastRecodingTonic;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String CLEAR_DATA =
            "com.example.bluetooth.le.CLEAR_DATA";
    public final static String PHASIC_DATA =
            "com.example.bluetooth.le.PHASIC_DATA";
    public final static String NOW_TIME =
            "com.example.bluetooth.le.NOW_TIME";
    public final static String IS_PEAKS =
            "com.example.bluetooth.le.IS_PEAKS";
    public final static String IS_TONIC =
            "com.example.bluetooth.le.IS_TONIC";

    IBinder binder=new LocalBinder();

    BluetoothGattCallback gattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        bluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        dataTransform=new DataTransform();
        peaksArray=new HashMap<>();
        recodingPeaksDB =new RecodingPeaksDB();
        recodingTonicDB=new RecodingTonicDB();
        lastRecodingTonic=0L;
        NotificationClass notification=new NotificationClass();
        notification.discoveredStressNotifi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(100,notification.getNotification());
        }else {
            startForeground(100,notification.getNotification());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        if (data != null && data.length > 0) {
            String dataString=new String(data);
            trainingIntent(dataString,intent,time);
        }
        sendBroadcast(intent);
        cleaningDB(time);
    }

    private void trainingIntent(String dataString,Intent intent,Date time){
        Double value=(Double.parseDouble(dataString)/1023) * 10000;
        Double phasicValue=dataTransform.filterData(value);
        if(value>100) {
            if (phasicValue != null) {
                peaksCounter(intent, phasicValue, time.getTime());
            }
            if (time.getTime() - lastRecodingTonic > 30000) {
                lastRecodingTonic = time.getTime();
                recodingTonicDB.addToDB(time.getTime(), value);//Добавление тоники.
                intent.putExtra(BluetoothLeService.IS_TONIC, true);
            }
        }
        intent.putExtra(BluetoothLeService.CLEAR_DATA,value);
        intent.putExtra(BluetoothLeService.PHASIC_DATA,phasicValue);
        intent.putExtra(BluetoothLeService.NOW_TIME,time.getTime());
        Log.i("Value:",String.valueOf(value));
    }

    private void cleaningDB(Date time){
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("HH:mm");
        Log.i("Calendar",String.valueOf(formatForDateNow.format(time)));
        if(formatForDateNow.format(time).equals("23:59")){
            ResultDB resultDB=new ResultDB();
            resultDB.addToDB();
        }
        if(formatForDateNow.format(time).equals("00:00")){
            recodingTonicDB.clearDB();
            recodingPeaksDB.clearDB();
        }
    }

    private void peaksCounter(Intent intent,Double value,Long time){
        if(value>0.7){
            if(!peaksFlag){
                peaksFlag=true;
                peaksArray.clear();
            }else {
                peaksArray.put(time,value);
            }
        }else {
            if(peaksFlag) {
                Double max = Collections.max(peaksArray.values());//Амплитуда пика
                ArrayList<Long> keys=new ArrayList(Arrays.asList(peaksArray.keySet().toArray()));
                Long timePeaks=keys.get((int)(peaksArray.size()/2));
                recodingPeaksDB.addToDB(timePeaks,max);
                peaksFlag = false;
                intent.putExtra(BluetoothLeService.IS_PEAKS, true);
            }else {
                intent.putExtra(BluetoothLeService.IS_PEAKS, false);
            }
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }



    public boolean initialize() {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }


    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress)
                && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                connectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        bluetoothDeviceAddress = address;
        connectionState = STATE_CONNECTING;
        return true;
    }


    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }


    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }


    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.readCharacteristic(characteristic);
    }


    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    }


    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;

        return bluetoothGatt.getServices();
    }
}