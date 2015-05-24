package com.example.marc.smartthermostatclient.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.marc.smartthermostatclient.APIRequestHandler;
import com.example.marc.smartthermostatclient.R;
import com.example.marc.smartthermostatclient.SummaryUpdatesScheduler;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
* Created by Marc on 01/03/2015.
*/
public class SummaryFragment extends Fragment implements Observer{

    private String serverURL;
    private SharedPreferences prefs;
    ScheduledFuture<?> sf;
    private int refreshInterval;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button buttonDeleteThermometers = (Button)rootView.findViewById(R.id.buttonPUT);
        Button buttonAddThermometer = (Button)rootView.findViewById(R.id.buttonPOST);
        Button addThermostat = (Button) rootView.findViewById(R.id.addThermostat);
        Button buttonDeleteThermostats = (Button)rootView.findViewById(R.id.deleteThermostats);

        //load shared preferences
        serverURL=loadPreferences();

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
            }
        };
        buttonDeleteThermometers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIRequestHandler.INSTANCE.makeDeleteRequest(serverURL + "/temp", listener, errorListener);
            }
        });
        buttonAddThermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getActivity());
                new AlertDialog.Builder(getActivity())
                        .setTitle("Afegir termometre")
                        .setMessage("Introdueix el nom del termometre")
                        .setView(input)
                        .setPositiveButton("Afegir", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                APIRequestHandler.INSTANCE.makePostRequest(serverURL + "/temp",input.getText().toString(),listener,errorListener);
                            }
                         }).setNegativeButton("Cancel.lar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();

            }
        });
        addThermostat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getActivity());
                new AlertDialog.Builder(getActivity())
                        .setTitle("Afegir zona")
                        .setMessage("Introdueix el nom de la zona")
                        .setView(input)
                        .setPositiveButton("Afegir", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                APIRequestHandler.INSTANCE.makePostRequest(serverURL + "/thermo", input.getText().toString(), listener, errorListener);
                            }
                        }).setNegativeButton("Cancel.lar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();

            }
        });
        buttonDeleteThermostats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIRequestHandler.INSTANCE.makeDeleteRequest(serverURL + "/thermo", listener, errorListener);
            }
        });

        return rootView;
    }

    private String loadPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(
                            SharedPreferences prefs, String key) {
                        serverURL=prefs.getString("URL_preference", "");

                    }
                });
        return prefs.getString("URL_preference", "");
    }
/*
    @Override
    public void onPause() {
        sf.cancel(false);
    }
*/
    @Override
    public void update(Observable observableSwitch, Object checked) {
        if (checked.equals(true)){
            refreshInterval = Integer.parseInt(prefs.getString("refresh_interval", ""));
            SummaryUpdatesScheduler updateScheduler = new SummaryUpdatesScheduler(this.getActivity(), getView(), serverURL);
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            sf = exec.scheduleAtFixedRate(updateScheduler, 0, refreshInterval, TimeUnit.MILLISECONDS);
        }
        else{
            sf.cancel(false);
        }
    }
}
