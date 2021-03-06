package com.example.marc.smartthermostatclient;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.example.marc.smartthermostatclient.Fragments.SummaryFragment;
import com.example.marc.smartthermostatclient.Fragments.ThermostatFragment;

import java.util.Observable;

/**
* Created by Marc on 01/03/2015.
*/
public class SwipeTabsPagerAdapter extends FragmentPagerAdapter {
    private int PAGE_COUNT = 1;
    private Observable observable;
    private SummaryFragment summary;
    public SwipeTabsPagerAdapter(FragmentManager FragmentManager, Observable listener) {
        super(FragmentManager);
        this.observable = listener;
        summary = new SummaryFragment();
        this.observable.addObserver(summary);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public void setCount(int c){
        this.PAGE_COUNT=c+1;
        notifyDataSetChanged();
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Resum";

        }
        return "Zona "+position;

    }
    @Override
    public Fragment getItem(int position) {
        if (position==0)
                return summary;
        /*else if (position==PAGE_COUNT-1){
            return new ProgrammerFragment();
        }*/
        else{
            ThermostatFragment frag = ThermostatFragment.create(position);
            this.observable.addObserver(frag);
            return frag;
        }
    }
}
