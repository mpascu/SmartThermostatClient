package com.example.marc.smartthermostatclient;

import android.app.Activity;
import android.os.Bundle;

import com.example.marc.smartthermostatclient.Fragments.PrefsFragment;

/**
 * Created by Marc on 25/02/2015.
 */
public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}
