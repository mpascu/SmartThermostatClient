package com.example.marc.smartthermostatclient;

import android.app.Activity;
import android.view.View;

/**
 * Created by Marc on 12/03/2015.
 */
public class ThermostatUpdateScheduler extends UpdatesScheduler{

    public ThermostatUpdateScheduler(Activity c, View rootView, String url){
        super(c,rootView,url);
    }

    @Override
    public void run() {

    }
}
