package com.example.dotpesa_new_dec_2022;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ViewPagerAdapter_inboxmessages extends FragmentPagerAdapter {

    private final List<Fragment> firstFragment_unsynched_messages = new ArrayList<>();
    private final List<String> secondFragment_synched_messages = new ArrayList<String>();

    public ViewPagerAdapter_inboxmessages(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return firstFragment_unsynched_messages.get(position);
    }



    @Override
    public int getCount() {
        return secondFragment_synched_messages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (CharSequence) secondFragment_synched_messages.get(position);
    }

    public void AddFragment(Fragment fragment, String title){
        firstFragment_unsynched_messages.add(fragment);
        secondFragment_synched_messages.add(title);
    }
}
