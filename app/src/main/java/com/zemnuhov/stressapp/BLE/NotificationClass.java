package com.zemnuhov.stressapp.BLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zemnuhov.stressapp.GlobalValues;
import com.zemnuhov.stressapp.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class NotificationClass {

    private final String CHANNEL_ID="FOREGROUND_NOTIFICATION";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification getNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GlobalValues.getContext(), "Stress")
                .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
                .setContentTitle("Мы следим за вашим состоянием")
                .setContentText("Соединение с устройством установлено");
        Notification notification=builder.setOngoing(true)

                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        builder.setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Hello";
            String description = "getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Stress", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = GlobalValues.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GlobalValues.getContext());
        //notificationManager.notify(1, builder.build());
        return notification;
    }
}




