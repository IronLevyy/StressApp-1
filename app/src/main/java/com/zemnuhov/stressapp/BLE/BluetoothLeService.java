package com.zemnuhov.stressapp.BLE;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.zemnuhov.stressapp.DataBase.DataBaseClass;
import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.Notifications.NotificationClass;
import com.zemnuhov.stressapp.Settings.ParsingSPref;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String bluetoothDeviceAddress;
    private BluetoothGatt bluetoothGatt;
    private DataFilter dataFilter;
    private int connectionState = STATE_DISCONNECTED;
    private Boolean peaksFlag=false;
    private HashMap<Long,Double> peaksArray;
    private DataBaseClass dataBase;
    private Long lastRecodingTonic;
    private Long lastNotification;
    private ParsingSPref parsingSPref;
    private NotificationClass notification;
    private Handler handler=new Handler();
    private ArrayList<Double> dataArray;
    private boolean isThreadActive=false;


    BluetoothGattCharacteristic peaksCharacteristic;
    BluetoothGattCharacteristic tonicCharacteristic;
    BluetoothGattCharacteristic timeCharacteristic;


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
        dataFilter =new DataFilter();
        peaksArray=new HashMap<>();
        dataBase=new DataBaseClass();
        lastRecodingTonic=0L;
        lastNotification=0L;
        notification=new NotificationClass();
        dataArray =new ArrayList<>();

        startForeground(100,notification.getForegroundNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action
            , final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        Calendar calendar;
        Date time;

        for(int i=0;i<5;i++) {
            ByteBuffer bb = ByteBuffer.wrap(data);
            if (data != null && data.length > 0) {
                Integer dataString = bb.getInt();
                Double value = (dataString.doubleValue() / 1023) * 10000;
                calendar = Calendar.getInstance();
                time = calendar.getTime();
                trainingIntent(value, intent, time);
                peaksController(time, value);

            }
            sendBroadcast(intent);
        }
        cleaningDB(new Date());
    }

    private void initCharacteristic(){
        UUID uuidValue=UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
        for(BluetoothGattService s:bluetoothGatt.getServices()){
            if(s.getUuid().equals(uuidValue)){
                peaksCharacteristic = s.getCharacteristic(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"));
                tonicCharacteristic = s.getCharacteristic(UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb"));
                timeCharacteristic = s.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
            }
        }

    }

    private void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void applicationStart(){
        Thread sendDataThread=new Thread(()->{
            sleepThread(3000);
            DataBaseClass db=new DataBaseClass();
            int count=db.readCountPeak(600000L);
            int tonicAvg=db.readAvgTonic(600000L);

            peaksCharacteristic.setValue(count,BluetoothGattCharacteristic.FORMAT_UINT16,0);
            bluetoothGatt.writeCharacteristic(peaksCharacteristic);

            sleepThread(3000);

            tonicCharacteristic.setValue(tonicAvg,BluetoothGattCharacteristic.FORMAT_UINT16,0);
            bluetoothGatt.writeCharacteristic(tonicCharacteristic);

            sleepThread(10000);

            SimpleDateFormat nowTime = new SimpleDateFormat("HH:mm:ss");
            String timeString=nowTime.format(new Date().getTime()+10000);

            for(String a:timeString.split("")){

                byte[] f= new byte[0];
                try {
                    f = a.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                timeCharacteristic.setValue(f);
                timeCharacteristic.setWriteType(WRITE_TYPE_NO_RESPONSE);
                bluetoothGatt.writeCharacteristic(timeCharacteristic);
                sleepThread(1000);
            }
            Log.i("TimeCorrect","Complete");

        });
        sendDataThread.start();
    }

    private void peaksController(Date time,Double value){
        SimpleDateFormat minute = new SimpleDateFormat("mm");
        if(minute.format(time).substring(1).equals("0")){
            if(new Date().getTime()-lastNotification>500000) {
                int count=dataBase.readCountPeak(600000L);
                applicationStart();
                lastNotification = new Date().getTime();
                dataBase.addTenMinuteLine(time.getTime(),count);
                if (count > 30){
                        parsingSPref =
                                new ParsingSPref(ConstantAndHelp.SharedPreferenceLoad(
                                        ParsingSPref.SP_INTERVAL_TAG));
                    ArrayList<ArrayList<String>> timeAndSources = parsingSPref.getTimesAndSources();
                    boolean flagNotification=false;
                    for (ArrayList<String> item : timeAndSources) {
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("H:mm dd.MM.yyyy");
                        SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            Date begin = formatter.parse(item.get(0) + " " + formatterDate.format(date));
                            Date end = formatter.parse(item.get(1) + " " + formatterDate.format(date));
                            if (time.after(begin) && time.before(end)) {
                                if (item.size() > 2) {
                                    notification.discoveredStressNotification(count
                                            , value, time.getTime(), item.get(2), item.get(3));
                                    flagNotification=true;
                                } else {
                                    notification.discoveredStressNotification(count
                                            , value, time.getTime());
                                    flagNotification=true;
                                }
                            }
                            if(!flagNotification){
                                notification.discoveredStressNotification(count
                                        , value, time.getTime());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void trainingIntent(Double value,Intent intent,Date time){
        Double phasicValue= dataFilter.filterData(value);
        if(value>100) {
            if (phasicValue != null) {
                peaksCounter(intent, phasicValue, time.getTime());
            }
            if (time.getTime() - lastRecodingTonic > 30000) {
                lastRecodingTonic = time.getTime();
                dataBase.addTonic(time.getTime(), value);//Добавление тоники.
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
            dataBase.addResultDayLine();
        }
        if(formatForDateNow.format(time).equals("00:00")){
            dataBase.clearPeaksDB();
            dataBase.clearTonicDB();
            dataBase.clearTenMinuteTable();
        }
    }

    private void peaksCounter(Intent intent,double value,long time){
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
                dataBase.addPeak(timePeaks,max);
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
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean success = bluetoothGatt.writeDescriptor(descriptor);
        initCharacteristic();
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
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

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
