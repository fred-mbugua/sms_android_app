package com.payprint.pos.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.payprint.pos.MainActivity;

public class startupOnBootUp extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}
