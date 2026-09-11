package com.example.dotpesa_new_dec_2022.core;

import android.app.Application;
import android.content.Intent;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.SmsDetector;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SmsDetector.askForPermissions(this);
        Intent intent = new Intent(this, SmsService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);

//        PackageManager packageManager = getPackageManager();
//        ComponentName componentName = new ComponentName(this, DeviceAdminActivity.class);
//        packageManager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
