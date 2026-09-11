package com.example.dotpesa_new_dec_2022.utilities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.dotpesa_new_dec_2022.SynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.UnsynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.example.dotpesa_new_dec_2022.core.App;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmsInboxImporter {

    private static final String TAG = "SmsInboxImporter";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean isImporting = false;

    public interface ImportCallback {
        void onComplete(int count);
    }

    public static void importDeviceSmsMessagesAsync(Context context) {
        importDeviceSmsMessagesAsync(context, null);
    }

    public static void importDeviceSmsMessagesAsync(Context context, ImportCallback callback) {
        Context validContext = context != null ? context : App.getInstance();
        if (validContext == null) return;

        if (ContextCompat.checkSelfPermission(validContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "READ_SMS permission not granted. Skipping device SMS import.");
            if (callback != null) {
                callback.onComplete(0);
            }
            return;
        }

        final Context appContext = validContext.getApplicationContext();
        executor.execute(() -> {
            synchronized (SmsInboxImporter.class) {
                if (isImporting) return;
                isImporting = true;
            }

            int count = 0;
            try {
                count = performImport(appContext);
            } catch (Exception e) {
                Log.e(TAG, "Error importing device SMS messages: " + e.getLocalizedMessage(), e);
            } finally {
                synchronized (SmsInboxImporter.class) {
                    isImporting = false;
                }
                final int finalCount = count;
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            if (UnsynchedInboxFragment.instance() != null) {
                                UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
                            }
                            if (SynchedInboxFragment.instance() != null) {
                                SynchedInboxFragment.instance().refreshSmsMessagesInbox();
                            }
                        } catch (Exception ignored) {}
                    }
                    if (callback != null) {
                        callback.onComplete(finalCount);
                    }
                });
            }
        });
    }

    private static int performImport(Context context) {
        ContentResolver cr = context.getContentResolver();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"_id", "address", "body", "date", "service_center"};

        Cursor cursor = cr.query(inboxUri, projection, null, null, "date DESC LIMIT 500");
        if (cursor == null) return 0;

        DBHelper db = new DBHelper(context);
        int importedCount = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            while (cursor.moveToNext()) {
                long smsId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                String serviceCenter = cursor.getString(cursor.getColumnIndexOrThrow("service_center"));

                if (body == null || body.trim().isEmpty()) continue;

                if (!SmsFilterManager.isMessageAllowed(context, address, body)) {
                    continue;
                }

                SMSModelSMSdetails sms = new SMSModelSMSdetails();
                sms.smsMessageSerial = (int) (smsId & 0x7FFFFFFF);
                sms.originationAddress = address != null ? address : "Unknown";
                sms.messageBody = body;
                sms.timeStamp = sdf.format(new Date(dateMillis));
                sms.messageServiceCenter = serviceCenter != null ? serviceCenter : "";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sms.messageReadDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateMillis), ZoneId.systemDefault());
                }

                sms.isMessageSynchronised = false;
                sms.messageSynchronisedDate = null;
                sms.protocolIdentifier = 0;
                sms.messageStatusOnSim = 0;
                sms.isStatusReport = false;
                sms.isMWIMessage = false;
                sms.messagePDU = "";
                sms.messageUserData = "";

                long rowId = db.add(sms);
                if (rowId > 0) {
                    importedCount++;
                }
            }
        } finally {
            cursor.close();
        }

        Log.d(TAG, "Imported " + importedCount + " existing SMS messages into database.");
        return importedCount;
    }
}
