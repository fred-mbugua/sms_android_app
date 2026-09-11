package com.example.dotpesa_new_dec_2022;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dotpesa_new_dec_2022.core.SmsService;
import com.example.dotpesa_new_dec_2022.utilities.PermissionManager;
import com.example.dotpesa_new_dec_2022.utilities.SmsInboxImporter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import dotpesa_new_dec_2022.R;

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new InboxFragment()).commit();
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
                } else if (itemId == R.id.logout) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new LogoutPageFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionManager.hasAllPermissions(this)) {
            SmsInboxImporter.importDeviceSmsMessagesAsync(this);
        }
    }
}