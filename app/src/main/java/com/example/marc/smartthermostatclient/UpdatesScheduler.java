package com.example.marc.smartthermostatclient;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Marc on 25/02/2015.
 */
public class UpdatesScheduler implements Runnable {

    private Context context;
    private TableLayout temperaturesTable;
    private TableLayout thermostatsTable;
    private View rootView;
    private String url;

    public UpdatesScheduler(Context c,View rootView, String url) {
        this.context=c;
        this.rootView = rootView;
        this.temperaturesTable = (TableLayout) rootView.findViewById(R.id.temperatures_table);
        this.thermostatsTable = (TableLayout) rootView.findViewById(R.id.thermostat_table);
        this.url = url;
    }

    @Override
    public void run() {
        // Request a string response from the provided URL.
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                TempJsonParser(res);
            }
        };
        Response.Listener<String> thermoListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                ThermoJsonParser(res);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
            }
        };
        APIRequestHandler.INSTANCE.makeGetRequest(url + "/temp", listener, errorListener);
        APIRequestHandler.INSTANCE.makeGetRequest(url + "/thermo",thermoListener,errorListener);
    }

    private void TempJsonParser (String jsonString){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray temperatures = (JSONArray) jsonParser.parse(jsonString);
            temperaturesTable.removeAllViews();
            for (int x=0; x<temperatures.size();x++){
                JSONObject sensor = (JSONObject)temperatures.get(x);
                JSONObject sensorValues = (JSONObject)sensor.get(Integer.toString(x+1));

                TableRow entry = new TableRow(context);
                entry.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
                TextView cell = new TextView(context);
                cell.setLayoutParams(layoutParams);
                cell.setText(sensorValues.get("name").toString());
                entry.addView(cell);
                TextView cell1 = new TextView(context);
                cell1.setLayoutParams(layoutParams);
                cell1.setText(sensorValues.get("value").toString()+" ºC");
                cell1.setGravity(Gravity.LEFT);
                entry.addView(cell1);
                temperaturesTable.addView(entry,x);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void ThermoJsonParser (String jsonString){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray thermostats = (JSONArray) jsonParser.parse(jsonString);
            temperaturesTable.removeAllViews();
            for (int x=0; x<thermostats.size();x++){
                JSONObject sensor = (JSONObject)thermostats.get(x);
                JSONObject sensorValues = (JSONObject)sensor.get(Integer.toString(x+1));

                TableRow entry = new TableRow(context);
                entry.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
                TextView cell = new TextView(context);
                cell.setLayoutParams(layoutParams);
                cell.setText(sensorValues.get("sensors").toString());
                entry.addView(cell);
                TextView cell1 = new TextView(context);
                cell1.setLayoutParams(layoutParams);
                cell1.setText(sensorValues.get("temperature").toString()+" ºC");
                cell1.setGravity(Gravity.LEFT);
                entry.addView(cell1);
                thermostatsTable.addView(entry,x);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
