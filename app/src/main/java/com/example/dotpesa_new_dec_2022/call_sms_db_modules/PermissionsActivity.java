package com.example.dotpesa_new_dec_2022.call_sms_db_modules;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dotpesa_new_dec_2022.Appfunctionality;
import com.example.dotpesa_new_dec_2022.core.SmsService;
import com.example.dotpesa_new_dec_2022.utilities.PermissionManager;
import com.example.dotpesa_new_dec_2022.utilities.SmsInboxImporter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import dotpesa_new_dec_2022.R;

public class PermissionsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private TextView badgeSms;
    private TextView badgePhone;
    private TextView badgeContacts;
    private TextView badgeNotifications;
    private MaterialCardView cardNotifications;

    private MaterialButton btnGrantPermissions;
    private MaterialButton btnOpenSettings;
    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        badgeSms = findViewById(R.id.badgeSms);
        badgePhone = findViewById(R.id.badgePhone);
        badgeContacts = findViewById(R.id.badgeContacts);
        badgeNotifications = findViewById(R.id.badgeNotifications);
        cardNotifications = findViewById(R.id.cardNotifications);

        btnGrantPermissions = findViewById(R.id.btnGrantPermissions);
        btnOpenSettings = findViewById(R.id.btnOpenSettings);
        btnContinue = findViewById(R.id.btnContinue);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            cardNotifications.setVisibility(View.GONE);
        }

        btnGrantPermissions.setOnClickListener(v -> requestMissingPermissions());
        btnOpenSettings.setOnClickListener(v -> openAppSettings());
        btnContinue.setOnClickListener(v -> proceedToApp());

        updatePermissionUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionUI();
    }

    private void updatePermissionUI() {
        boolean smsGranted = isGranted(Manifest.permission.READ_SMS) && isGranted(Manifest.permission.RECEIVE_SMS);
        boolean phoneGranted = isGranted(Manifest.permission.READ_PHONE_STATE);
        boolean contactsGranted = isGranted(Manifest.permission.READ_CONTACTS);
        boolean notifGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || isGranted(Manifest.permission.POST_NOTIFICATIONS);

        updateBadge(badgeSms, smsGranted);
        updateBadge(badgePhone, phoneGranted);
        updateBadge(badgeContacts, contactsGranted);
        updateBadge(badgeNotifications, notifGranted);

        boolean allGranted = smsGranted && phoneGranted && contactsGranted && notifGranted;

        if (allGranted) {
            btnGrantPermissions.setVisibility(View.GONE);
            btnContinue.setVisibility(View.VISIBLE);

            // Import existing SMS messages from device inbox and start service
            SmsInboxImporter.importDeviceSmsMessagesAsync(this);
            startSmsListenerService();
            SmsDetector.startOutgoingSms(this);
        } else {
            btnGrantPermissions.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.GONE);
        }
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateBadge(TextView badge, boolean granted) {
        if (granted) {
            badge.setText("GRANTED");
            badge.setTextColor(Color.parseColor("#00C853"));
        } else {
            badge.setText("REQUIRED");
            badge.setTextColor(Color.parseColor("#EF4444"));
        }
    }

    private void requestMissingPermissions() {
        List<String> missing = PermissionManager.getMissingPermissions(this);
        if (!missing.isEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            proceedToApp();
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void startSmsListenerService() {
        try {
            Intent serviceIntent = new Intent(this, SmsService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedToApp() {
        SmsInboxImporter.importDeviceSmsMessagesAsync(this, count -> {
            Intent intent = new Intent(PermissionsActivity.this, Appfunctionality.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            updatePermissionUI();
            boolean allGranted = PermissionManager.hasAllPermissions(this);
            if (allGranted) {
                Toast.makeText(this, "All required permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some permissions were not granted. Please allow all for full functionality.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
