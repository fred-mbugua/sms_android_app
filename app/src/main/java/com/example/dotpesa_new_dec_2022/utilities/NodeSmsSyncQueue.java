package com.example.dotpesa_new_dec_2022.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.dotpesa_new_dec_2022.MainActivity;
import com.example.dotpesa_new_dec_2022.SynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.UnsynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.Constants;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.example.dotpesa_new_dec_2022.core.App;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeSmsSyncQueue {

    private static final String TAG = "NodeSmsSyncQueue";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean isProcessing = false;

    public interface TestCallback {
        void onResult(boolean success, String message);
    }

    public static synchronized void dispatchUnsyncedMessages(Context context) {
        Context validContext = context != null ? context : App.getInstance();
        if (validContext == null) return;

        SharedPreferences prefs = MainActivity.SHAREDPREFERENCES;
        if (prefs == null) {
            prefs = validContext.getSharedPreferences(MainActivity.DOTPESA_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        if (prefs == null) return;

        boolean isNodeSyncEnabled = prefs.getBoolean("node_api_enabled", false);
        if (!isNodeSyncEnabled) {
            return;
        }

        final Context appContext = validContext.getApplicationContext();
        executor.execute(() -> {
            synchronized (NodeSmsSyncQueue.class) {
                if (isProcessing) return;
                isProcessing = true;
            }

            try {
                processQueue(appContext);
            } catch (Exception e) {
                Log.e(TAG, "Error processing Node.js sync queue", e);
            } finally {
                synchronized (NodeSmsSyncQueue.class) {
                    isProcessing = false;
                }
            }
        });
    }

    private static void processQueue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.DOTPESA_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean("node_api_enabled", false);
        if (!isEnabled) return;

        String protocol = prefs.getString("node_api_protocol", "http").trim();
        String host = prefs.getString("node_api_host", "").trim();
        String port = prefs.getString("node_api_port", "").trim();
        String endpoint = prefs.getString("node_api_endpoint", "").trim();
        String apiKey = prefs.getString("node_api_key", "").trim();

        if (host.isEmpty()) {
            Log.w(TAG, "Node.js API host is not configured");
            return;
        }

        if (endpoint.startsWith("/")) {
            endpoint = endpoint.substring(1);
        }

        String fullUrl;
        if (!port.isEmpty()) {
            fullUrl = protocol + "://" + host + ":" + port + "/" + endpoint;
        } else {
            fullUrl = protocol + "://" + host + "/" + endpoint;
        }

        DBHelper db = new DBHelper(context);
        db.openDB();
        Cursor cursor = db.getAllMessagesNotSynched();
        List<SMSModelSMSdetails> unsyncedList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                SMSModelSMSdetails sms = new SMSModelSMSdetails();
                sms.smsMessageSerial = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.SMS_MESSAGE_SERIAL));
                sms.timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_TIMESTAMP));
                sms.messageBody = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_MESSAGE_BODY));
                sms.originationAddress = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_ORIGINATING_ADDRESS));
                sms.messageStatusOnSim = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.SMS_MESSAGE_STATUS_ON_SIM));
                sms.messagePDU = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_MESSAGE_PDU));
                sms.protocolIdentifier = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.SMS_PROTOCOL_IDENTIFIER));
                sms.messageServiceCenter = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_MESSAGE_SERVICE_CENTER));
                sms.messageUserData = cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_MESSAGE_USER_DATA));
                sms.isStatusReport = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_IS_STATUS_REPORT)));
                sms.isMWIMessage = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Constants.SMS_IS_MWI_MESSAGE)));

                unsyncedList.add(sms);
            }
            cursor.close();
        }
        db.closeDB();

        if (unsyncedList.isEmpty()) {
            return;
        }

        Log.d(TAG, "Found " + unsyncedList.size() + " unsynced SMS messages to dispatch to Node.js backend in FIFO order");

        for (SMSModelSMSdetails sms : unsyncedList) {
            boolean success = sendSmsToNodeApi(fullUrl, apiKey, sms);
            if (success) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    db.updateIncomingSMSisSynchronized(sms);
                }
                Log.d(TAG, "Successfully synced SMS serial " + sms.smsMessageSerial + " to Node.js backend");

                // Refresh UI fragments on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (UnsynchedInboxFragment.instance() != null) {
                                UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
                            }
                            if (SynchedInboxFragment.instance() != null) {
                                SynchedInboxFragment.instance().refreshSmsMessagesInbox();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Error refreshing inbox UI", ex);
                    }
                });
            } else {
                Log.w(TAG, "Failed to send SMS serial " + sms.smsMessageSerial + " to Node.js API. Stopping queue processing to preserve FIFO order and prevent data loss.");
                break; // Stop processing to guarantee FIFO ordering and retry later on network restore
            }
        }
    }

    private static boolean sendSmsToNodeApi(String urlString, String apiKey, SMSModelSMSdetails sms) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            if (apiKey != null && !apiKey.trim().isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
                conn.setRequestProperty("x-api-key", apiKey.trim());
            }

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("smsMessageSerial", sms.smsMessageSerial);
            jsonParam.put("sender", sms.originationAddress != null ? sms.originationAddress : "");
            jsonParam.put("message", sms.messageBody != null ? sms.messageBody : "");
            jsonParam.put("timestamp", sms.timeStamp != null ? sms.timeStamp : "");
            jsonParam.put("serviceCenter", sms.messageServiceCenter != null ? sms.messageServiceCenter : "");

            byte[] postData = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Node.js API response code: " + responseCode + " for SMS " + sms.smsMessageSerial);

            return responseCode >= 200 && responseCode < 300;
        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS to Node.js API: " + e.getLocalizedMessage());
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static void testConnection(Context context, String protocol, String host, String port, String endpoint, String apiKey, TestCallback callback) {
        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                String cleanEndpoint = endpoint.trim();
                if (cleanEndpoint.startsWith("/")) {
                    cleanEndpoint = cleanEndpoint.substring(1);
                }

                String fullUrl;
                if (!port.trim().isEmpty()) {
                    fullUrl = protocol.trim() + "://" + host.trim() + ":" + port.trim() + "/" + cleanEndpoint;
                } else {
                    fullUrl = protocol.trim() + "://" + host.trim() + "/" + cleanEndpoint;
                }

                URL url = new URL(fullUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoOutput(true);

                if (apiKey != null && !apiKey.trim().isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
                    conn.setRequestProperty("x-api-key", apiKey.trim());
                }

                JSONObject testPayload = new JSONObject();
                testPayload.put("test", true);
                testPayload.put("message", "Test connection from SMS Listener");

                byte[] postData = testPayload.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData);
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (responseCode >= 200 && responseCode < 300) {
                        callback.onResult(true, "Connected successfully! HTTP " + responseCode);
                    } else {
                        callback.onResult(false, "Server responded with HTTP " + responseCode);
                    }
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(false, "Connection failed: " + e.getLocalizedMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }
}