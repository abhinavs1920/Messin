package com.appfree.messin;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

public class App extends Application{
    public static final String FCM_CHANNEL_ID = "FCM_CHANNEL_ID";
    @Override
    public void onCreate(){
        super.onCreate();
        startService(new Intent(this, MyReceiver.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel fcmChannel = new NotificationChannel(
                    FCM_CHANNEL_ID,"FCM_CHANNEL", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(fcmChannel);
        }
    }

}
