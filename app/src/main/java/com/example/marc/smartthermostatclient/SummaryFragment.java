package com.example.marc.smartthermostatclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

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
    private Button buttonDELETE;
    private Button buttonPOST;
    private RequestQueue queue;
    private String serverURL;
    private SharedPreferences prefs;
    ScheduledFuture<?> sf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        buttonDELETE = (Button)rootView.findViewById(R.id.buttonPUT);
        buttonPOST = (Button)rootView.findViewById(R.id.buttonPOST);


        //load shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        serverURL=prefs.getString("URL_preference", "");

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                Toast.makeText(getActivity(),"New temperature added succesfully!",Toast.LENGTH_SHORT).show();
            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
            }
        };
        buttonDELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIRequestHandler.INSTANCE.makeDeleteRequest(serverURL + "/temp",listener,errorListener);
            }
        });
        buttonPOST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getActivity());
                new AlertDialog.Builder(getActivity())
                        .setTitle("Add thermometer")
                        .setMessage("Enter the name of the thermometer")
                        .setView(input)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                APIRequestHandler.INSTANCE.makePostRequest(serverURL + "/temp",input.getText().toString(),listener,errorListener);
                            }
                         }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();

            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        serverURL=prefs.getString("URL_preference", "");
    }

    @Override
    public void update(Observable observableSwitch, Object checked) {
        if (checked.equals(true)){
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String serverURL = prefs.getString("URL_preference", "");
            UpdatesScheduler updateScheduler = new UpdatesScheduler(this.getActivity(), getView(), serverURL);
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            int interval = Integer.parseInt(prefs.getString("refresh_interval", ""));
            sf = exec.scheduleAtFixedRate(updateScheduler, 0, interval, TimeUnit.MILLISECONDS);
        }
        else{
            sf.cancel(false);
        }
    }
}