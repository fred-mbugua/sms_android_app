package com.example.dotpesa_new_dec_2022.core;

//import static com.example.dotpesa_new_dec_2022.sendingCallsDetailsTOERP.ProcessReceivedSMSMessage.DEVICEIMEI_LOCATION;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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

import java.time.LocalDateTime;

import dotpesa_new_dec_2022.R;

public class SmsService extends InfiniteService {

    public SmsService(){
        //no args constructer
    }

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */

//    public String getDeviceIMEI() {
//        String deviceUniqueIdentifier = null;
//        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        if (null != tm) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return null;
//            }
//            deviceUniqueIdentifier = tm.getDeviceId();
//        }
//        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
//            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        }
//        return deviceUniqueIdentifier;
//    }


    public void startForeground(){
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, Appfunctionality.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)//set notification channell
                .setOngoing(true)
                .setSmallIcon(R.drawable.dotpesalogo)
                .setContentTitle(getString(R.string.my_app_name))
                .setContentText("SMS Listener service is running in the background")
                .setContentIntent(pendingIntent)
                .build());

    }

    private void createNotificationChannel() {

        String channelName = "Channel for call center app";
        String channelDescription = "Channell for Call Center app notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES .O){
            CharSequence name = channelName;
            String description = channelDescription;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground();

        final int ret = super.onStartCommand(intent, flags, startId);
        // to detect received sms
        SmsReceiver.setSmsReceivedListener(new SmsReceivedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMessageReceived(String contactName, LocalDateTime smsReceivedDate, String senderSendTime, Object[] pdus,
                                          String smsMessageContent, String srviceCenterAddress, Boolean isStatusReportMessage,
                                          Integer protocolIdentifier, Integer statusOnIcc, Boolean isCphsMwiMessage, Boolean isMessageSynchronized, LocalDateTime messageSynchronisedDate) {

                Log.e("sms received from", contactName);

                MessagesModel smsMessage = new MessagesModel(contactName, smsReceivedDate, senderSendTime, pdus, smsMessageContent, srviceCenterAddress, isStatusReportMessage, protocolIdentifier, statusOnIcc, isCphsMwiMessage, isMessageSynchronized, messageSynchronisedDate);
                MainActivity.saveSMSMessagesToDisk(smsMessage);

                //updating the INBOX_MESSAGES_LIST
                MainActivity.listOfAllSMSMessagesInSQLiteDB();
                System.out.println(MainActivity.INBOX_MESSAGE_LIST.size());
                UnsynchedInboxFragment.instance().loadSMSAndPushToPOS();
                
            }
        });


        return ret;
    }
}
