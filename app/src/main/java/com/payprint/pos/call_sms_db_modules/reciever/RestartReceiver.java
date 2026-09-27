package com.payprint.pos.call_sms_db_modules.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.payprint.pos.call_sms_db_modules.services.InfiniteService;

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
