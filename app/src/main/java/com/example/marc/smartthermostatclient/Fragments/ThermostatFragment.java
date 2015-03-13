package com.example.marc.smartthermostatclient.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.marc.smartthermostatclient.R;
import com.example.marc.smartthermostatclient.ThermostatUpdateScheduler;
import com.example.marc.smartthermostatclient.UpdatesScheduler;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marc on 06/03/2015.
 */
public class ThermostatFragment extends android.support.v4.app.Fragment implements Observer{
    private int position;
    private String thermostatURL;
    private ScheduledFuture<?> sf;
    public static ThermostatFragment create(int position) {
        Bundle args = new Bundle();
        args.putInt("POS", position);
        ThermostatFragment fragment = new ThermostatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("POS");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thermostat, container, false);
        final LinearLayout programmerLayout = (LinearLayout) view.findViewById(R.id.programmer_layout);
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroupMode);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radioButtonAUTO){
                    programmerLayout.setVisibility(View.VISIBLE);
                    startUpdater();
                }else{
                    programmerLayout.setVisibility(View.GONE);
                }
            }
        });

        thermostatURL=loadUrlFromPreferences();
        System.out.println("AAAAAAAAAAAAAAAAAA" + thermostatURL);


        return view;
    }

    private String loadUrlFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getString("URL_preference", "")+"/thermo/"+this.position;
    }

    public void startUpdater(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int refreshInterval = Integer.parseInt(prefs.getString("refresh_interval", ""));
        UpdatesScheduler updateScheduler = new ThermostatUpdateScheduler(this.getActivity(), getView(), thermostatURL);
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        sf = exec.scheduleAtFixedRate(updateScheduler, 0, refreshInterval, TimeUnit.MILLISECONDS);

    }

    @Override
    public void update(Observable observableSwitch, Object checked) {
        if (checked.equals(true))
            startUpdater();
        else
            if(!sf.isCancelled())
                sf.cancel(false);
    }
}
