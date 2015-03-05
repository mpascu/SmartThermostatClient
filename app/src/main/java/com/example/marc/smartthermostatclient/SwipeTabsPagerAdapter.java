package com.example.marc.smartthermostatclient;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Observable;

/**
* Created by Marc on 01/03/2015.
*/
public class SwipeTabsPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private Observable observable;
    private SummaryFragment summary;
    public SwipeTabsPagerAdapter(FragmentManager supportFragmentManager, Observable listener) {
        super(supportFragmentManager);
        this.observable = listener;
        summary = new SummaryFragment();
        this.observable.addObserver(summary);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Summary";
            case 1:
                return "Programmer";
            case 2:
                return "Options";
        }
        return "Default";

    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return summary;
        }
        return DefaultPageFragment.create(position + 1);
    }
}
