package com.payprint.pos.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.payprint.pos.MainActivity;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.C2BTransactionModel;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.NodeSmsModel;
import com.payprint.pos.core.App;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeSmsFetcher {

    private static final String TAG = "NodeSmsFetcher";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean isFetching = false;

    public interface FetchCallback {
        void onComplete(boolean success, int count, String message);
    }

    public static synchronized void fetchSmsTransactions(Context context, @Nullable FetchCallback callback) {
        Context validContext = context != null ? context : App.getInstance();
        if (validContext == null) return;

        SharedPreferences prefs = MainActivity.SHAREDPREFERENCES;
        if (prefs == null) {
            prefs = validContext.getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        if (prefs == null) return;

        boolean isNodeSyncEnabled = prefs.getBoolean("node_api_enabled", true);
        if (!isNodeSyncEnabled) {
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Node.js SMS Sync is disabled in settings"));
            }
            return;
        }

        final Context appContext = validContext.getApplicationContext();
        executor.execute(() -> {
            synchronized (NodeSmsFetcher.class) {
                if (isFetching) {
                    if (callback != null) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Fetch already in progress"));
                    }
                    return;
                }
                isFetching = true;
            }

            try {
                performFetch(appContext, callback);
            } catch (Exception e) {
                Log.e(TAG, "Error fetching Node.js SMS messages", e);
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Error: " + e.getLocalizedMessage()));
                }
            } finally {
                synchronized (NodeSmsFetcher.class) {
                    isFetching = false;
                }
            }
        });
    }

    private static void performFetch(Context context, FetchCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String protocol = prefs.getString("node_api_protocol", "http").trim();
        String host = prefs.getString("node_api_host", "192.168.1.100").trim();
        String port = prefs.getString("node_api_port", "3000").trim();
        String endpoint = prefs.getString("node_api_sms_fetch_endpoint", "api/v1/sms/all").trim();
        if (endpoint.isEmpty()) {
            endpoint = prefs.getString("node_api_endpoint", "api/v1/sms/receive").trim();
        }
        String apiKey = prefs.getString("node_api_key", "").trim();

        if (host.isEmpty()) {
            host = "192.168.1.100";
        }

        if (host.startsWith("http://") || host.startsWith("https://")) {
            if (host.startsWith("https://")) {
                protocol = "https";
                host = host.substring(8);
            } else {
                protocol = "http";
                host = host.substring(7);
            }
        }

        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
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

        HttpURLConnection conn = null;
        try {
            URL url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (apiKey != null && !apiKey.trim().isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
                conn.setRequestProperty("x-api-key", apiKey.trim());
            }

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Node.js SMS Fetch response code: " + responseCode + " from " + fullUrl);

            if (responseCode >= 200 && responseCode < 300) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }

                List<NodeSmsModel> smsList = new ArrayList<>();
                String rawStr = response.toString().trim();

                JSONArray array = null;
                if (rawStr.startsWith("[")) {
                    array = new JSONArray(rawStr);
                } else if (rawStr.startsWith("{")) {
                    JSONObject root = new JSONObject(rawStr);
                    if (root.has("data")) {
                        Object dataObj = root.get("data");
                        if (dataObj instanceof JSONArray) {
                            array = (JSONArray) dataObj;
                        } else if (dataObj instanceof JSONObject) {
                            JSONObject dObj = (JSONObject) dataObj;
                            if (dObj.has("sms") && dObj.get("sms") instanceof JSONArray) {
                                array = dObj.getJSONArray("sms");
                            } else if (dObj.has("messages") && dObj.get("messages") instanceof JSONArray) {
                                array = dObj.getJSONArray("messages");
                            } else if (dObj.has("records") && dObj.get("records") instanceof JSONArray) {
                                array = dObj.getJSONArray("records");
                            }
                        }
                    }
                    if (array == null && root.has("messages") && root.get("messages") instanceof JSONArray) {
                        array = root.getJSONArray("messages");
                    }
                    if (array == null && root.has("records") && root.get("records") instanceof JSONArray) {
                        array = root.getJSONArray("records");
                    }
                    if (array == null && root.has("sms") && root.get("sms") instanceof JSONArray) {
                        array = root.getJSONArray("sms");
                    }
                    if (array == null && root.has("data_list") && root.get("data_list") instanceof JSONArray) {
                        array = root.getJSONArray("data_list");
                    }
                    if (array == null && root.has("rows") && root.get("rows") instanceof JSONArray) {
                        array = root.getJSONArray("rows");
                    }
                    if (array == null) {
                        smsList.add(parseSmsJson(root));
                    }
                }

                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        smsList.add(parseSmsJson(obj));
                    }
                }

                DBHelper db = new DBHelper(context);
                db.savePushedSmsTransactions(smsList);

                Log.d(TAG, "Successfully fetched and saved " + smsList.size() + " Node.js SMS records");

                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(true, smsList.size(), "Fetched " + smsList.size() + " SMS messages"));
                }
            } else {
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Server returned HTTP " + responseCode));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching Node.js SMS messages: " + e.getLocalizedMessage());
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Connection error: " + e.getLocalizedMessage()));
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static NodeSmsModel parseSmsJson(JSONObject obj) {
        NodeSmsModel sms = new NodeSmsModel();

        String rawId = optClean(obj, "id", optClean(obj, "_id", optClean(obj, "SMSID", optClean(obj, "smsMessageSerial", optClean(obj, "sms_serial", "")))));
        if (rawId.isEmpty() || rawId.equals("0")) {
            sms.id = UUID.randomUUID().toString();
        } else {
            sms.id = rawId;
        }

        sms.smsSerial = obj.optInt("SMSID", obj.optInt("smsMessageSerial", obj.optInt("sms_serial", obj.optInt("id", 0))));

        sms.sender = optClean(obj, "senderMobilePhoneNumber",
                optClean(obj, "sender",
                optClean(obj, "originationAddress",
                optClean(obj, "origination_address",
                optClean(obj, "phone",
                optClean(obj, "phone_number",
                optClean(obj, "address",
                optClean(obj, "from", ""))))))));

        sms.message = optClean(obj, "SMSMessage",
                optClean(obj, "message",
                optClean(obj, "messageBody",
                optClean(obj, "message_body",
                optClean(obj, "text",
                optClean(obj, "body",
                optClean(obj, "content", "")))))));

        sms.timestamp = optClean(obj, "SMSSentDate",
                optClean(obj, "timestamp",
                optClean(obj, "timeStamp",
                optClean(obj, "created_at",
                optClean(obj, "createdAt",
                optClean(obj, "date", ""))))));

        sms.serviceCenter = optClean(obj, "SMSCNumber", optClean(obj, "serviceCenter", optClean(obj, "service_center", "")));

        sms.createdAt = optClean(obj, "SMSReceivedDate", optClean(obj, "created_at", optClean(obj, "createdAt", sms.timestamp)));
        if (sms.createdAt.isEmpty()) {
            sms.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        }

        sms.messageStatus = optClean(obj, "sms_message_status", optClean(obj, "SMSMessageType", ""));
        sms.messageStatusOnSim = obj.optInt("sms_message_status_on_sim", 0);
        sms.messagePdu = optClean(obj, "PDUUserData", optClean(obj, "sms_message_pdu", ""));
        sms.protocolIdentifier = obj.optInt("sms_protocol_identifier", 0);
        sms.messageUserData = optClean(obj, "PDUUserHeader", optClean(obj, "sms_message_user_data", ""));
        sms.isStatusReport = obj.optBoolean("sms_is_status_report", false);
        sms.isMwiMessage = obj.optBoolean("sms_is_mwi_message", false);
        sms.messageReadDate = optClean(obj, "sms_message_read_date", sms.createdAt);
        sms.isMessageSynchronized = obj.optBoolean("sms_is_message_synchronized", true);
        sms.messageSynchronizedDate = optClean(obj, "sms_message_synchronized_date", "");
        sms.modemName = optClean(obj, "SMSModemName", optClean(obj, "modem_name", ""));

        // Extract Account Name from Paybill SMS or JSON payload
        sms.accountName = optClean(obj, "account_name", optClean(obj, "AccountName", optClean(obj, "bill_ref_number", optClean(obj, "BillRefNumber", C2BTransactionModel.extractAccountNameFromSms(sms.message)))));

        return sms;
    }

    private static String optClean(JSONObject obj, String key, String fallback) {
        if (obj.isNull(key)) return fallback;
        String val = obj.optString(key, fallback).trim();
        if (val.equalsIgnoreCase("null") || val.isEmpty()) return fallback;
        return val;
    }
}
