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
import com.payprint.pos.core.App;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeC2BFetcher {

    private static final String TAG = "NodeC2BFetcher";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean isFetching = false;

    public interface FetchCallback {
        void onComplete(boolean success, int count, String message);
    }

    public static synchronized void fetchC2BTransactions(Context context, @Nullable FetchCallback callback) {
        Context validContext = context != null ? context : App.getInstance();
        if (validContext == null) return;

        SharedPreferences prefs = MainActivity.SHAREDPREFERENCES;
        if (prefs == null) {
            prefs = validContext.getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        if (prefs == null) return;

        boolean isC2BEnabled = prefs.getBoolean("node_api_c2b_enabled", true);
        if (!isC2BEnabled) {
            if (callback != null) {
                callback.onComplete(false, 0, "C2B Transactions Fetch is disabled in settings");
            }
            return;
        }

        final Context appContext = validContext.getApplicationContext();
        executor.execute(() -> {
            synchronized (NodeC2BFetcher.class) {
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
                Log.e(TAG, "Error fetching C2B transactions", e);
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Error: " + e.getLocalizedMessage()));
                }
            } finally {
                synchronized (NodeC2BFetcher.class) {
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
        String endpoint = prefs.getString("node_api_transactions_endpoint", "transactions").trim();
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
            Log.d(TAG, "C2B Fetch response code: " + responseCode + " from URL: " + fullUrl);

            if (responseCode >= 200 && responseCode < 300) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }

                String rawJson = response.toString().trim();
                Log.d(TAG, "Raw C2B response: " + rawJson);

                JSONArray array = null;
                if (rawJson.startsWith("[")) {
                    array = new JSONArray(rawJson);
                } else if (rawJson.startsWith("{")) {
                    JSONObject root = new JSONObject(rawJson);
                    if (root.has("data") && root.get("data") instanceof JSONArray) {
                        array = root.getJSONArray("data");
                    } else if (root.has("transactions") && root.get("transactions") instanceof JSONArray) {
                        array = root.getJSONArray("transactions");
                    } else if (root.has("results") && root.get("results") instanceof JSONArray) {
                        array = root.getJSONArray("results");
                    } else if (root.has("c2b") && root.get("c2b") instanceof JSONArray) {
                        array = root.getJSONArray("c2b");
                    }
                }

                List<C2BTransactionModel> transactionList = new ArrayList<>();
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        C2BTransactionModel tx = new C2BTransactionModel();

                        tx.id = optCleanString(obj, "id");
                        tx.transId = optCleanString(obj, "trans_id");
                        tx.transTime = optCleanString(obj, "trans_time");

                        if (!obj.isNull("amount")) {
                            try {
                                tx.amount = obj.getDouble("amount");
                            } catch (Exception e) {
                                try {
                                    tx.amount = Double.parseDouble(obj.getString("amount").replace(",", ""));
                                } catch (Exception ignored) {
                                    tx.amount = 0.0;
                                }
                            }
                        } else {
                            tx.amount = 0.0;
                        }

                        tx.businessShortcode = optCleanString(obj, "business_shortcode");
                        if (tx.businessShortcode.isEmpty()) {
                            tx.businessShortcode = optCleanString(obj, "BusinessShortCode");
                        }

                        tx.billRefNumber = optCleanString(obj, "bill_ref_number");
                        if (tx.billRefNumber.isEmpty()) {
                            tx.billRefNumber = optCleanString(obj, "BillRefNumber");
                        }

                        tx.msisdn = optCleanString(obj, "msisdn");
                        if (tx.msisdn.isEmpty()) {
                            tx.msisdn = optCleanString(obj, "MSISDN");
                        }

                        tx.firstName = optCleanString(obj, "first_name");
                        if (tx.firstName.isEmpty()) {
                            tx.firstName = optCleanString(obj, "FirstName");
                        }

                        tx.middleName = optCleanString(obj, "middle_name");
                        if (tx.middleName.isEmpty()) {
                            tx.middleName = optCleanString(obj, "MiddleName");
                        }

                        tx.lastName = optCleanString(obj, "last_name");
                        if (tx.lastName.isEmpty()) {
                            tx.lastName = optCleanString(obj, "LastName");
                        }

                        tx.createdAt = optCleanString(obj, "created_at");
                        tx.transTimeEat = optCleanString(obj, "trans_time_eat");
                        tx.transTimeFormatted = optCleanString(obj, "trans_time_formatted");

                        // Extract Paybill Account Name
                        tx.accountName = optCleanString(obj, "account_name");
                        if (tx.accountName.isEmpty()) {
                            tx.accountName = optCleanString(obj, "AccountName");
                        }
                        if (tx.accountName.isEmpty()) {
                            tx.accountName = tx.billRefNumber;
                        }
                        if (tx.accountName.isEmpty()) {
                            tx.accountName = tx.getFullName();
                        }

                        if (tx.id.isEmpty()) {
                            tx.id = !tx.transId.isEmpty() ? tx.transId : UUID.randomUUID().toString();
                        }

                        transactionList.add(tx);
                    }
                }

                DBHelper db = new DBHelper(context);
                db.saveC2BTransactions(transactionList);

                Log.d(TAG, "Successfully fetched and saved " + transactionList.size() + " C2B transactions");

                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(true, transactionList.size(), "Fetched " + transactionList.size() + " transactions"));
                }
            } else {
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Server returned HTTP " + responseCode));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching C2B transactions: " + e.getLocalizedMessage());
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(false, 0, "Connection error: " + e.getLocalizedMessage()));
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String optCleanString(JSONObject obj, String key) {
        if (obj.isNull(key)) {
            return "";
        }
        String val = obj.optString(key, "").trim();
        if (val.equalsIgnoreCase("null")) {
            return "";
        }
        return val;
    }
}
