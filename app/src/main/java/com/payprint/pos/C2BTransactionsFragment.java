package com.payprint.pos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.payprint.pos.R;

public class C2BTransactionsFragment extends Fragment {

    public static C2BTransactionsFragment instance;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public C2BTransactionsFragment() {
    }

    public static C2BTransactionsFragment getInstance() {
        return instance;
    }

    @Override
    public void onStart() {
        super.onStart();
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_c2b_transactions, container, false);

        tabLayout = v.findViewById(R.id.c2b_tab_layout);
        viewPager = v.findViewById(R.id.c2b_view_pager);

        viewPager.setSaveEnabled(false);

        C2BPageAdapter adapter = new C2BPageAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("SMS Messages");
                    tab.setIcon(R.drawable.inbox_24);
                    break;
                case 1:
                    tab.setText("API Messages");
                    tab.setIcon(R.drawable.sync_24);
                    break;
                case 2:
                    tab.setText("Bluetooth Printer");
                    tab.setIcon(R.drawable.ic_printer_24);
                    break;
            }
        }).attach();

        return v;
    }
}
