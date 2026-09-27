package com.payprint.pos;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> firstFragment_licensing = new ArrayList<>();
    private final List<String> secondFragment_networkConfig = new ArrayList<String>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return firstFragment_licensing.get(position);
    }



    @Override
    public int getCount() {
        return secondFragment_networkConfig.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (CharSequence) secondFragment_networkConfig.get(position);
    }

    public void AddFragment(Fragment fragment, String title){
        firstFragment_licensing.add(fragment);
        secondFragment_networkConfig.add(title);
    }
}
