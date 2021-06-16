package com.zemnuhov.stressapp.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.DataBase.SourcesDB;
import com.zemnuhov.stressapp.R;
import com.zemnuhov.stressapp.Settings.StatisticSettingActivity;

import static android.app.Notification.CATEGORY_SERVICE;
import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class NotificationClass {

    private final String FOREGROUND_CHANNEL_ID="FOREGROUND_NOTIFICATION";
    private final String FRONT_CHANNEL_ID="FRONT_NOTIFICATION";
    public final String ACTION_SNOOZE="ACTION_SNOOZE";
    final String LOG_TAG = "myLogs";


    public Notification getForegroundNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ConstantAndHelp.getContext(), FOREGROUND_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                .setContentTitle("Мы следим за вашим состоянием")
                .setContentText("Соединение с устройством установлено");
        Notification notification=builder.setOngoing(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setCategory(CATEGORY_SERVICE)
                .build();
        builder.setDefaults(Notification.DEFAULT_ALL);
        createNotificationChannel(FOREGROUND_CHANNEL_ID);
        return notification;
    }

    public void getDisconnectNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                ConstantAndHelp.getContext(), FRONT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                .setContentTitle("Произошёл разрыв с устройством")
                .setContentText("Потеряно соединение с устройством, " +
                        "зайдите в приложение, что бы переподключиться")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(ConstantAndHelp.getContext());

        createNotificationChannel(FRONT_CHANNEL_ID);
        notificationManagerCompat.notify(102, builder.build());
    }

    private void createNotificationChannel(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, id, importance);
            channel.setDescription(id);
            NotificationManager notificationManager = ConstantAndHelp.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void discoveredStressNotification(Integer peaksCount,Double tonicAvg, Long time){

        Receiver.peaksCount=peaksCount;
        Receiver.tonicAvg=tonicAvg;
        Receiver.time=time;

        Intent snoozeIntent = new Intent(ConstantAndHelp.getContext(), Receiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, "default");
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(ConstantAndHelp.getContext(),
                1,snoozeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                ConstantAndHelp.getContext(), FRONT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                .setContentTitle("Обнаружен повышенный стресс")
                .setContentText("Количество пиков привысило норму на "
                        +(peaksCount-20)
                        +" расскажите что стало источником стресса.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(snoozePendingIntent)
                .addAction(R.drawable.ic_snooze
                        ,"Указать источник стресса"
                        ,snoozePendingIntent);
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(ConstantAndHelp.getContext());

        createNotificationChannel(FRONT_CHANNEL_ID);
        notificationManagerCompat.notify(101, builder.build());
    }

    public void discoveredStressNotification(Integer peaksCount,Double tonicAvg,
                                             Long time, String firstSource, String secondSource){
        Receiver.peaksCount=peaksCount;
        Receiver.tonicAvg=tonicAvg;
        Receiver.time=time;
        Intent firstSourceIntent = new Intent(ConstantAndHelp.getContext(), Receiver.class);
        Intent secondSourceIntent = new Intent(ConstantAndHelp.getContext(), Receiver.class);
        Intent defaultIntent = new Intent(ConstantAndHelp.getContext(), Receiver.class);


        firstSourceIntent.setAction(ACTION_SNOOZE);
        secondSourceIntent.setAction(ACTION_SNOOZE);
        defaultIntent.setAction(ACTION_SNOOZE);

        String def="default";

        firstSourceIntent.putExtra(EXTRA_NOTIFICATION_ID, firstSource);
        secondSourceIntent.putExtra(EXTRA_NOTIFICATION_ID, secondSource);
        defaultIntent.putExtra(EXTRA_NOTIFICATION_ID, def);


        PendingIntent firstSourcePendingIntent = PendingIntent.getBroadcast(
                ConstantAndHelp.getContext()
                , 1
                ,firstSourceIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent secondSourcePendingIntent = PendingIntent.getBroadcast(
                ConstantAndHelp.getContext()
                , 2
                ,secondSourceIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent defaultPendingIntent = PendingIntent.getBroadcast(
                ConstantAndHelp.getContext()
                , 3
                ,defaultIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ConstantAndHelp.getContext(), FRONT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                        .setContentTitle("Обнаружен повышенный стресс")
                        .setContentText("Количество пиков привысило норму на "
                                +(peaksCount-20)
                                +" расскажите что стало источником стресса.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(defaultPendingIntent)
                        .setContentIntent(firstSourcePendingIntent)
                        .setContentIntent(secondSourcePendingIntent)
                        .addAction(R.drawable.ic_snooze, firstSource,firstSourcePendingIntent)
                        .addAction(R.drawable.ic_snooze, secondSource,secondSourcePendingIntent)
                        .addAction(R.drawable.ic_snooze, "Другое",defaultPendingIntent);


        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(ConstantAndHelp.getContext());

        createNotificationChannel(FRONT_CHANNEL_ID);
        notificationManagerCompat.notify(101, builder.build());
    }

    public static class Receiver extends BroadcastReceiver {
        final String LOG_TAG = "myLogs";
        public static Integer peaksCount=0;
        public static Double tonicAvg=0D;
        public static Long time=0L;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive");
            Log.d(LOG_TAG, "action = " + intent.getAction());
            Log.d(LOG_TAG, "extra = " + intent.getStringExtra(EXTRA_NOTIFICATION_ID));
            Log.d(LOG_TAG, "peaksCount = " + peaksCount);
            Log.d(LOG_TAG, "tonicAvg = " + tonicAvg);
            Log.d(LOG_TAG, "time = " + time);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(101);
            if(!intent.getStringExtra(EXTRA_NOTIFICATION_ID).equals("default")){
                SourcesDB dataBase=new SourcesDB();
                dataBase.addLineInStatistic(time,intent.getStringExtra(EXTRA_NOTIFICATION_ID)
                        ,peaksCount
                        ,tonicAvg);
            }else {
                Intent intentDefault=new Intent(ConstantAndHelp.getContext()
                        , StatisticSettingActivity.class);
                intentDefault.putExtra("TIME_IN_INTENT",time);
                intentDefault.putExtra("PEAKS_COUNT_IN_INTENT",peaksCount);
                intentDefault.putExtra("TONIC_AVG_IN_INTENT",tonicAvg);
                intentDefault.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                ConstantAndHelp.getContext().startActivity(intentDefault);
            }

        }

    }

}




