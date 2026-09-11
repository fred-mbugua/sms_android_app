package com.example.dotpesa_new_dec_2022;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import dotpesa_new_dec_2022.R;

public class SettingsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tablayout_settings);
        viewPager = view.findViewById(R.id.settings_viewpager);
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add setting configuration fragments
        adapter.AddFragment(new FragmentGeneralSettings(), "General & SMS Filter");
        adapter.AddFragment(new FragmentLicenseSettings(), "Device Licensing");
        adapter.AddFragment(new FragmentNeConfig(), "Network Configuration");
        adapter.AddFragment(new FragmentNodeConfig(), "Node.js API");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout.getTabAt(0) != null) {
            tabLayout.getTabAt(0).setIcon(R.drawable.inbox_24);
        }
        if (tabLayout.getTabAt(1) != null) {
            tabLayout.getTabAt(1).setIcon(R.drawable.licensing_24);
        }
        if (tabLayout.getTabAt(2) != null) {
            tabLayout.getTabAt(2).setIcon(R.drawable.network_config_24);
        }
        if (tabLayout.getTabAt(3) != null) {
            tabLayout.getTabAt(3).setIcon(R.drawable.sync_24);
        }

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00C853"));
    }
}
