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

public class InboxFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) getView().findViewById(R.id.tablayout_incoming_messages_inbox);
        viewPager = (ViewPager) getView().findViewById(R.id.inbox_fragment_viewpager);
//        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        //add fragment here

        adapter.AddFragment(new UnsynchedInboxFragment(), "Un-synced Messages");
        adapter.AddFragment(new SynchedInboxFragment(), "Synced Messages");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.not_synced);
        tabLayout.getTabAt(1).setIcon(R.drawable.sync_24);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00c42d"));

//        PagerTabStrip pagerTabStrip = (PagerTabStrip) getView().findViewById(R.id.viewpagerstrip);
////        pagerTabStrip.setDrawFullUnderline(true);
//        pagerTabStrip.setTabIndicatorColor(Color.RED);
    }
}