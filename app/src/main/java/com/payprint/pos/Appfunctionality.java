package com.payprint.pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.payprint.pos.core.SmsService;
import com.payprint.pos.utilities.PermissionManager;
import com.payprint.pos.utilities.SmsInboxImporter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.payprint.pos.R;

public class Appfunctionality extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dotpesafunctionality);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (!PermissionManager.hasAllPermissions(this)) {
            PermissionManager.askForPermissions(this);
        } else {
            SmsInboxImporter.importDeviceSmsMessagesAsync(this);
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

        updateBottomNavVisibility();

        if (savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            boolean showInbox = prefs.getBoolean("show_sms_inbox_tab", true);
            if (showInbox) {
                bottomNavigationView.setSelectedItemId(R.id.calllogs);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new InboxFragment()).commit();
            } else {
                bottomNavigationView.setSelectedItemId(R.id.c2b_transactions);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new C2BTransactionsFragment()).commit();
            }
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.calllogs) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new InboxFragment()).commit();
                    return true;
                } else if (itemId == R.id.c2b_transactions) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new C2BTransactionsFragment()).commit();
                    return true;
                } else if (itemId == R.id.help_center) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new HelpCenterFragment()).commit();
                    return true;
                } else if (itemId == R.id.settings) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentSettingsAuth()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    private void updateBottomNavVisibility() {
        if (bottomNavigationView == null) return;
        SharedPreferences prefs = getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean showInbox = prefs.getBoolean("show_sms_inbox_tab", true);
        MenuItem inboxItem = bottomNavigationView.getMenu().findItem(R.id.calllogs);
        if (inboxItem != null) {
            inboxItem.setVisible(showInbox);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomNavVisibility();
        if (PermissionManager.hasAllPermissions(this)) {
            SmsInboxImporter.importDeviceSmsMessagesAsync(this);
        }
    }
}
