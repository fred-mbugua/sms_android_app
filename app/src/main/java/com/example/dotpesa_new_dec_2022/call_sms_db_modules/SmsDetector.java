package com.example.dotpesa_new_dec_2022.call_sms_db_modules;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms.SmsObserver;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms.SmsReceivedListener;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms.SmsReceiver;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms.SmsSentListener;

import java.io.File;

/**
 * edited by Fred.
 *
 * How to use this class?
 *
 * First ask for permissions using {@link #askForPermissions(Context)}
 * Then set listeners using {@link SmsReceiver#setSmsReceivedListener(SmsReceivedListener)} to detect received sms,
 * {@link SmsObserver#setSmsSentListener(SmsSentListener)} to detect sent sms,

 */
public class SmsDetector {
    private static String savedNumber;
    private static MediaRecorder recorder;
    private static boolean recordstarted = false;
    private static File outputFile;

    /**
     * Call this method to start detecting outgoing sms
     *
     * @param context Context of application
     */
    public static void startOutgoingSms(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"),true, new SmsObserver(new Handler(), context));
    }

    /**
     * Returns contact name for the provided phone number
     *
     * @param context Context of application
     * @param number phone number received to get contact name
     * @return contactName
     */
    @SuppressLint("Range")
    public static String retrieveContactName(Context context, String number) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        String contactName = "";
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            if(!cursor.isClosed()) {
                cursor.close();
            }

            //Log.e(TAG, "Contact Name: " + contactName);
        }
        return contactName;
    }

    /**
     * Check and asks permissions required for this library
     *
     * @param context Context of application
     */
    public static void askForPermissions(Context context) {
        Intent intent = new Intent(context, PermissionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Checks whether provided service is running or not
     *
     * @param context Context of application
     * @param aClass any {@link android.app.Service} class
     * @return {@literal true|false}
     */
    public static boolean isMyServiceRunning(Context context, Class<?> aClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                //Log.e("isMyServiceRunning", "yes");
                return true;
            }
        }
        //Log.e("isMyServiceRunning", "no");
        return false;
    }


}
