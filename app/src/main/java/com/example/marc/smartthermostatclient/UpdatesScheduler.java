package com.example.marc.smartthermostatclient;

import android.app.Activity;
import android.view.View;

/**
 * Created by Marc on 12/03/2015.
 */
public abstract class UpdatesScheduler implements Runnable {

    protected MainActivity context;
    protected View rootView;
    protected String url;

    public UpdatesScheduler(Activity c, View rootView, String url){
        this.context=(MainActivity)c;
        this.rootView = rootView;
        this.url = url;
    }
}
