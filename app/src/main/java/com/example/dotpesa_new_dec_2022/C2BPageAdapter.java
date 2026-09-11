package com.example.dotpesa_new_dec_2022;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class C2BPageAdapter extends FragmentStateAdapter {

    public C2BPageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public C2BPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CloudC2BTabFragment();
            case 1:
                return new NodeSmsTabFragment();
            case 2:
                return new CompareTransactionsTabFragment();
            default:
                return new CloudC2BTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
