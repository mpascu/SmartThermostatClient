package com.example.marc.smartthermostatclient;

import android.app.Activity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Marc on 12/03/2015.
 */
public class ThermostatUpdateScheduler extends UpdatesScheduler{

    public ThermostatUpdateScheduler(Activity c, View rootView, String url){
        super(c,rootView,url);
    }

    @Override
    public void run() {
        final RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radioGroupMode);
        Response.Listener<String> thermoListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                ThermostatParser(res);
            }
        };

        //IMPLEMENTAR EL ERRRORLSITENER AL REQUESTHANDLER
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        APIRequestHandler.INSTANCE.makeGetRequest(url,thermoListener,errorListener);
    }

    private void ThermostatParser(String jsonString) {
        JSONParser jsonParser = new JSONParser();

        try {
            JSONObject temperatures = (JSONObject) jsonParser.parse(jsonString);
            TextView t = (TextView)rootView.findViewById(R.id.ThermostatText);
            t.setText(temperatures.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
