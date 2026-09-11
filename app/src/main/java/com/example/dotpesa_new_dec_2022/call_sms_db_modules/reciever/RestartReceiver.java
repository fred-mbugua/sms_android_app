package com.example.dotpesa_new_dec_2022.call_sms_db_modules.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.services.InfiniteService;

/**
 *  by Fred.
 */

public class RestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e("RestartReceiver", "broadcast received");
        context.startService(new Intent(context.getApplicationContext(), InfiniteService.class));
    }
}
