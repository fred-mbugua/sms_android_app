package com.example.dotpesa_new_dec_2022.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.dotpesa_new_dec_2022.Appfunctionality;
import com.example.dotpesa_new_dec_2022.MainActivity;
import com.example.dotpesa_new_dec_2022.UnsynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.MessagesModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.services.InfiniteService;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms.SmsReceivedListener;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms.SmsReceiver;
import com.example.dotpesa_new_dec_2022.utilities.SmsFilterManager;

import java.time.LocalDateTime;

import dotpesa_new_dec_2022.R;

public class SmsService extends InfiniteService {

    private static final String TAG = "SmsService";
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    public SmsService() {
    }

    public void safeStartForeground() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, Appfunctionality.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.dotpesalogo)
                .setContentTitle(getString(R.string.my_app_name))
                .setContentText("SMS Listener service is running in the background")
                .setContentIntent(pendingIntent)
                .build();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
            } else {
                startForeground(NOTIF_ID, notification);
            }
        } catch (Exception e) {
            Log.e(TAG, "Background startForeground restricted by Android OS: " + e.getLocalizedMessage());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SMS Listener Service";
            String description = "Service for SMS Listener background processing";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        safeStartForeground();

        final int ret = super.onStartCommand(intent, flags, startId);

        SmsReceiver.setSmsReceivedListener(new SmsReceivedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMessageReceived(String contactName, LocalDateTime smsReceivedDate, String senderSendTime, Object[] pdus,
                                          String smsMessageContent, String srviceCenterAddress, Boolean isStatusReportMessage,
                                          Integer protocolIdentifier, Integer statusOnIcc, Boolean isCphsMwiMessage, Boolean isMessageSynchronized, LocalDateTime messageSynchronisedDate) {

                Log.d(TAG, "SMS received from: " + contactName);

                if (!SmsFilterManager.isMessageAllowed(SmsService.this, contactName, smsMessageContent)) {
                    Log.d(TAG, "Message from " + contactName + " ignored based on SMS Filter Settings.");
                    return;
                }

                MessagesModel smsMessage = new MessagesModel(contactName, smsReceivedDate, senderSendTime, pdus, smsMessageContent, srviceCenterAddress, isStatusReportMessage, protocolIdentifier, statusOnIcc, isCphsMwiMessage, isMessageSynchronized, messageSynchronisedDate);
                MainActivity.saveSMSMessagesToDisk(smsMessage);

                MainActivity.listOfAllSMSMessagesInSQLiteDB();
                if (UnsynchedInboxFragment.instance() != null) {
                    UnsynchedInboxFragment.instance().loadSMSAndPushToPOS();
                }
            }
        });

        return ret;
    }
}