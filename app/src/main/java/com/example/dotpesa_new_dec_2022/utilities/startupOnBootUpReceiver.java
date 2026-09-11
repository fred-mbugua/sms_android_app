package com.example.dotpesa_new_dec_2022.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dotpesa_new_dec_2022.core.SmsService;

public class startupOnBootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_REBOOT.equals(intent.getAction()) || Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Intent startupIntent = new Intent(context, SmsService.class);
            startupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(startupIntent);
        }
    }
}
