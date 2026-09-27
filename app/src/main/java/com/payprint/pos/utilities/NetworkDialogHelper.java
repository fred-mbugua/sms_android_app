package com.payprint.pos.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import androidx.fragment.app.FragmentActivity;

import com.payprint.pos.FragmentSettingsAuth;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.payprint.pos.R;

public class NetworkDialogHelper {

    /**
     * Checks whether the device has an active Wi-Fi or Mobile Data connection.
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Displays a modern dialog when the device is offline with an action button to open network settings.
     */
    public static void showOfflineDialog(Context context, Runnable retryAction) {
        if (context == null) return;

        new MaterialAlertDialogBuilder(context)
                .setTitle("No Internet Connection")
                .setMessage("Your device is currently offline. PayPrint POS requires an active Wi-Fi or Mobile Data network connection to synchronize SMS and API transactions.\n\nPlease enable Wi-Fi or Mobile Data to continue.")
                .setIcon(R.drawable.network_config_24)
                .setPositiveButton("Open Network Settings", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (Exception ignored) {}
                    }
                })
                .setNegativeButton("Retry", (dialog, which) -> {
                    dialog.dismiss();
                    if (retryAction != null) {
                        retryAction.run();
                    }
                })
                .setNeutralButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Displays a modern dialog when the application cannot reach the Node.js server.
     */
    public static void showNodeServerUnreachableDialog(Context context, String serverUrl, String errorDetail, Runnable retryAction) {
        if (context == null) return;

        String targetUrlText = (serverUrl != null && !serverUrl.trim().isEmpty()) ? serverUrl : "Configured Node.js Server";
        String errorText = (errorDetail != null && !errorDetail.trim().isEmpty()) ? errorDetail : "Connection timeout / refused";

        String message = "PayPrint POS cannot establish a connection to your Node.js backend server.\n\n" +
                "Target Server:\n" + targetUrlText + "\n\n" +
                "Error Details:\n" + errorText + "\n\n" +
                "Troubleshooting Steps:\n" +
                "1. Ensure your Node.js server application is running.\n" +
                "2. Verify the Server Host IP address and Port in PayPrint POS Settings.\n" +
                "3. Confirm this device and the server are on the same Wi-Fi network.";

        new MaterialAlertDialogBuilder(context)
                .setTitle("Node.js Server Unreachable")
                .setMessage(message)
                .setIcon(R.drawable.sync_24)
                .setPositiveButton("Configure Settings", (dialog, which) -> {
                    dialog.dismiss();
                    openSettings(context);
                })
                .setNegativeButton("Retry", (dialog, which) -> {
                    dialog.dismiss();
                    if (retryAction != null) {
                        retryAction.run();
                    }
                })
                .setNeutralButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Displays a clean success dialog.
     */
    public static void showSuccessDialog(Context context, String title, String message) {
        if (context == null) return;

        new MaterialAlertDialogBuilder(context)
                .setTitle(title != null ? title : "Success")
                .setMessage(message != null ? message : "Operation completed successfully.")
                .setIcon(R.drawable.sync_24)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Displays a clean informational dialog.
     */
    public static void showInfoDialog(Context context, String title, String message) {
        if (context == null) return;

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.help_center_24)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private static void openSettings(Context context) {
        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new FragmentSettingsAuth())
                    .commit();
        }
    }
}
