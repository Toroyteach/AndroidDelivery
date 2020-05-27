package com.example.frsystem.ui.orderslider;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.frsystem.ui.orderslider.activejobs.ActiveOrdersFragment;
import com.example.frsystem.ui.orderslider.availablejobs.JobsFragment;

public class PageAdapter extends FragmentStatePagerAdapter {

    int CountTab;

    public PageAdapter(FragmentManager fm, int CountTab) {
        super(fm);
        this.CountTab = CountTab;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {

            case 0:
                ActiveOrdersFragment Tab1 = new ActiveOrdersFragment();
                return Tab1;

            case 1:
                JobsFragment Tab2 = new JobsFragment();
                return Tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return CountTab;
    }
}

