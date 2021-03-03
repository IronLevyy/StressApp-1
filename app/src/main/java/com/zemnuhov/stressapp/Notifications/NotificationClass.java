package com.zemnuhov.stressapp.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.Notifications.Receiver;
import com.zemnuhov.stressapp.R;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

public class NotificationClass {

    private final String FOREGROUND_CHANNEL_ID="FOREGROUND_NOTIFICATION";
    private final String FRONT_CHANNEL_ID="FRONT_NOTIFICATION";
    public final String ACTION_SNOOZE="ACTION_SNOOZE";
    final String LOG_TAG = "myLogs";


    public Notification getNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GlobalValues.getContext(), FOREGROUND_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                .setContentTitle("Мы следим за вашим состоянием")
                .setContentText("Соединение с устройством установлено");
        Notification notification=builder.setOngoing(true)
                .setPriority(PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        builder.setDefaults(Notification.DEFAULT_ALL);
        createNotificationChannel(FOREGROUND_CHANNEL_ID);
        return notification;
    }

    private void createNotificationChannel(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, id, importance);
            channel.setDescription(id);
            NotificationManager notificationManager = GlobalValues.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void discoveredStressNotifi(){
        Intent snoozeIntent = new Intent(GlobalValues.getContext(), Receiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(GlobalValues.getContext(),
                1,snoozeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(GlobalValues.getContext(), "FRONT_NOTIFI")
                .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(snoozePendingIntent)
                .addAction(R.drawable.ic_snooze,"TAP",snoozePendingIntent)
                .addAction(R.drawable.ic_snooze,"TAP",snoozePendingIntent)
                .addAction(R.drawable.ic_snooze,"TAP",snoozePendingIntent)
                .addAction(R.drawable.ic_snooze,"TAP",snoozePendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(GlobalValues.getContext());

        createNotificationChannel(FRONT_CHANNEL_ID);
        notificationManagerCompat.notify(101, builder.build());
    }



}




