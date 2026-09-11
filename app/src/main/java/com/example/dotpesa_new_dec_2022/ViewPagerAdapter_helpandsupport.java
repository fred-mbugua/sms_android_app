package com.example.dotpesa_new_dec_2022;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ViewPagerAdapter_helpandsupport extends FragmentPagerAdapter {

    private final List<Fragment> firstFragment_helpandsupport = new ArrayList<>();
    private final List<String> secondFragment_aboutus = new ArrayList<String>();

    public ViewPagerAdapter_helpandsupport(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return firstFragment_helpandsupport.get(position);
    }



    @Override
    public int getCount() {
        return secondFragment_aboutus.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (CharSequence) secondFragment_aboutus.get(position);
    }

    public void AddFragment(Fragment fragment, String title){
        firstFragment_helpandsupport.add(fragment);
        secondFragment_aboutus.add(title);
    }
}
