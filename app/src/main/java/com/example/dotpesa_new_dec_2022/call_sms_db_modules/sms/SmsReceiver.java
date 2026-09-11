package com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.SmsDetector;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

/**
 *  by Fredrick.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsReceivedListener mListener;

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsDetector.startOutgoingSms(context);

        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        String format = data.getString("format");

        if (data != null) {
                /////
                SmsMessage[] currentMessage = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    currentMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                     currentMessage[i] = SmsMessage.createFromPdu((byte[]) pdu);
                }

                SmsMessage sms = currentMessage[0];
                String smsMessageContent = "";
                try {
                    if (currentMessage.length == 1 || sms.isReplace()) {
                        smsMessageContent = sms.getMessageBody();
                    } else {
                        StringBuilder bodyText = new StringBuilder();
                        for (int i = 0; i < currentMessage.length; i++) {
                            bodyText.append(currentMessage[i].getMessageBody());
                        }
                        smsMessageContent = bodyText.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ///
//                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String senderNum = currentMessage[0].getDisplayOriginatingAddress();

                String message = currentMessage[0].getDisplayMessageBody();
                LocalDateTime smsReceivedDate = LocalDateTime.now();

                long timestamp = currentMessage[0].getTimestampMillis();
                String senderSendTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(new Date(timestamp));

//                LocalDateTime smsSentDate = Instant.ofEpochMilli(currentMessage[0].getTimestampMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                String contactName = SmsDetector.retrieveContactName(context, senderNum);
                String serviceCenterAddress = currentMessage[0].getServiceCenterAddress();
                Boolean isStatusReportMessage = currentMessage[0].isStatusReportMessage();
                Integer protocolIdentifier = currentMessage[0].getProtocolIdentifier();
                Integer statusOnIcc = currentMessage[0].getStatusOnIcc();
                Boolean isCphsMwiMessage = currentMessage[0].isCphsMwiMessage();
                Boolean isMessageSynchronized = false;
                LocalDateTime messageSynchronisedDate = null;


                if (mListener != null){
                    mListener.onMessageReceived(senderNum, smsReceivedDate, senderSendTime, pdus, smsMessageContent, serviceCenterAddress, isStatusReportMessage, protocolIdentifier, statusOnIcc, isCphsMwiMessage, isMessageSynchronized, messageSynchronisedDate);
                }

        }
    }


    public static void setSmsReceivedListener(SmsReceivedListener smsListener) {
        mListener = smsListener;

    }
}
