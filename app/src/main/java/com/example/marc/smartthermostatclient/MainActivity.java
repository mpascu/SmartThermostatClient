package com.example.marc.smartthermostatclient;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.toolbox.Volley;

import java.io.Serializable;
import java.util.Locale;
import java.util.Observable;

public class MainActivity extends ActionBarActivity implements Serializable{

    private ObservableSwitchChangeListener listener;
    private SwipeTabsPagerAdapter pagerAdapter;
    private MenuItem toggleSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listener = new ObservableSwitchChangeListener();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        pagerAdapter = new SwipeTabsPagerAdapter(getFragmentManager(), listener);
        viewPager.setAdapter(pagerAdapter);
        APIRequestHandler.INSTANCE.setQueue(Volley.newRequestQueue(this));

        Locale locale = new Locale(PreferenceManager.getDefaultSharedPreferences(this).getString("lang", ""));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
    public void setPages(int pages){
        pagerAdapter.setCount(pages);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleSwitch.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Get the action view used in your toggleservice item
        toggleSwitch = menu.findItem(R.id.myswitch);
        Switch connectionSwitch = (Switch) toggleSwitch.getActionView().findViewById(R.id.connectionSwitch);
        connectionSwitch.setOnCheckedChangeListener(listener);

        return super.onCreateOptionsMenu(menu);
    }
    private class ObservableSwitchChangeListener extends Observable implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setChanged();
            notifyObservers(isChecked);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
