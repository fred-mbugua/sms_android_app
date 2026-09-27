package com.payprint.pos.call_sms_db_modules.services;

import static androidx.core.app.NotificationCompat.VISIBILITY_SECRET;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.payprint.pos.R;

public class InfiniteService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    Notification issueNotification(PendingIntent pendingIntent) {
        Notification notify = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel("CHANNEL_1", "SMS Listener Channel", NotificationManager.IMPORTANCE_HIGH);
                channel.setShowBadge(false);
                notificationManager.createNotificationChannel(channel);

                NotificationCompat.Builder notification =
                        new NotificationCompat.Builder(this, "CHANNEL_1");

                notify = notification
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("SMS Listener service is running in the background")
                        .setVisibility(VISIBILITY_SECRET)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent).build();
            }
        } else {
            notify = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("SMS Listener service is running in the background")
                    .setVisibility(VISIBILITY_SECRET)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent).build();
        }

        return notify;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("InfiniteService");
        broadcastIntent.setPackage(getPackageName());
        sendBroadcast(broadcastIntent);
    }
}