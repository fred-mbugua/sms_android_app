package com.example.dotpesa_new_dec_2022;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tablayout_incoming_messages_inbox);
        viewPager = view.findViewById(R.id.inbox_fragment_viewpager);
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.AddFragment(new UnsynchedInboxFragment(), "Un-synced Messages");
        adapter.AddFragment(new SynchedInboxFragment(), "Synced Messages");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout.getTabAt(0) != null) {
            tabLayout.getTabAt(0).setIcon(R.drawable.not_synced);
        }
        if (tabLayout.getTabAt(1) != null) {
            tabLayout.getTabAt(1).setIcon(R.drawable.sync_24);
        }

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00C853"));

        EditText searchEditText = view.findViewById(R.id.sms_search_edit_text);
        ImageView clearBtn = view.findViewById(R.id.sms_search_clear_btn);

        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();
                    if (clearBtn != null) {
                        clearBtn.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (UnsynchedInboxFragment.instance() != null) {
                            UnsynchedInboxFragment.instance().filterMessages(query);
                        }
                        if (SynchedInboxFragment.instance() != null) {
                            SynchedInboxFragment.instance().filterMessages(query);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        if (clearBtn != null && searchEditText != null) {
            clearBtn.setOnClickListener(v -> searchEditText.setText(""));
        }
    }
}