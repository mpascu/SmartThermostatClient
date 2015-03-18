package com.example.marc.smartthermostatclient.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.marc.smartthermostatclient.APIRequestHandler;
import com.example.marc.smartthermostatclient.DataStructure.Thermostat;
import com.example.marc.smartthermostatclient.DataStructure.ThermostatManager;
import com.example.marc.smartthermostatclient.R;
import com.example.marc.smartthermostatclient.ThermostatUpdateScheduler;

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
    private Thermostat thermo;
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
        final Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };
        final Response.ErrorListener errorRsponseListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
            }
        };
        final LinearLayout programmerLayout = (LinearLayout) view.findViewById(R.id.programmer_layout);
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroupMode);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Thermostat t = ThermostatManager.getInstance().getSensor(position);
                if(checkedId==R.id.radioButtonAUTO){
                    programmerLayout.setVisibility(View.VISIBLE);
                    APIRequestHandler.INSTANCE.makePutRequest(thermostatURL,Double.toString(t.getTemperature()),Thermostat.modeOptions.AUTO.toString(),t.getSensorIds(),responseListener,errorRsponseListener);
                }else{
                    programmerLayout.setVisibility(View.GONE);
                    if (checkedId==R.id.radioButtonON)
                        APIRequestHandler.INSTANCE.makePutRequest(thermostatURL,Double.toString(t.getTemperature()),Thermostat.modeOptions.ON.toString(),t.getSensorIds(),responseListener,errorRsponseListener);
                    if (checkedId==R.id.radioButtonOFF)
                        APIRequestHandler.INSTANCE.makePutRequest(thermostatURL,Double.toString(t.getTemperature()),Thermostat.modeOptions.OFF.toString(),t.getSensorIds(),responseListener,errorRsponseListener);
                }
            }
        });

        Button plusTemperature = (Button)view.findViewById(R.id.plusTemp);
        plusTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thermostat t = ThermostatManager.getInstance().getSensor(position);
                APIRequestHandler.INSTANCE.makePutRequest(thermostatURL,Double.toString(t.getTemperature()+0.5),t.getMode().toString(),t.getSensorIds(),responseListener,errorRsponseListener);
            }
        });
        Button minusTemperature = (Button)view.findViewById(R.id.minusTemp);
        minusTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thermostat t = ThermostatManager.getInstance().getSensor(position);

                double temp= t.getTemperature();
                APIRequestHandler.INSTANCE.makePutRequest(thermostatURL,Double.toString(temp-0.5),t.getMode().toString(),t.getSensorIds(),responseListener,errorRsponseListener);
            }
        });

        Button addSensor = (Button)view.findViewById(R.id.button_add_sensor);
        Button removeSensors = (Button)view.findViewById(R.id.button_remove_sensors);
        removeSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        thermostatURL=loadUrlFromPreferences();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdater();
    }

    private String loadUrlFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getString("URL_preference", "")+"/thermo/"+this.position;
    }

    private void startUpdater() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int refreshInterval = Integer.parseInt(prefs.getString("refresh_interval", ""));
        ThermostatUpdateScheduler updateScheduler = new ThermostatUpdateScheduler(this.getActivity(), getView(), thermostatURL, position);

        //System.out.println("Thermostat fragment"+t.getName());
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
