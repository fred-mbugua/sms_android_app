package com.example.dotpesa_new_dec_2022;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import dotpesa_new_dec_2022.R;

public class Appfunctionality extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    InboxFragment inboxFragment = new InboxFragment();
    HelpCenterFragment helpCenterFragment = new HelpCenterFragment();
    FragmentSettingsAuth fragmentSettingsAuth = new FragmentSettingsAuth();
    LogoutPageFragment logoutPageFragment = new LogoutPageFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dotpesafunctionality);

        //creating views
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, inboxFragment).commit();

        //creating message counter
//        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.inbox);
//        badgeDrawable.setVisible(true);
//        badgeDrawable.setNumber(10);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()){
                    case R.id.calllogs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, inboxFragment).commit();
                        return true;
                    case R.id.help_center:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, helpCenterFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentSettingsAuth).commit();
                        return true;
                    case R.id.logout:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, logoutPageFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}