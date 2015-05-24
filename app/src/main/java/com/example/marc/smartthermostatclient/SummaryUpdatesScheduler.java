package com.example.marc.smartthermostatclient;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.marc.smartthermostatclient.DataStructure.Sensor;
import com.example.marc.smartthermostatclient.DataStructure.SensorManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Marc on 25/02/2015.
 */
public class SummaryUpdatesScheduler extends UpdatesScheduler {

    private int thermostatsNumber;
    private TableLayout temperaturesTable;
    private TableLayout thermostatsTable;
    private TableRow headerEntry;

    public SummaryUpdatesScheduler(Activity c, View rootView, String url) {
        super(c, rootView, url);
    }

    @Override
    public void run() {
        this.temperaturesTable = (TableLayout) rootView.findViewById(R.id.temperatures_table);
        this.thermostatsTable = (TableLayout) rootView.findViewById(R.id.thermostat_table);
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
        //IMPLEMENTAR EL ERRRORLSITENER AL REQUESTHANDLER
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
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

            temperaturesTable.addView(getTempsTableHeader(),0);
            for (int x=0; x<temperatures.size();x++){
                JSONObject sensor = (JSONObject)temperatures.get(x);
                JSONObject sensorValues = (JSONObject)sensor.get(Integer.toString(x+1));

                Sensor s = SensorManager.getInstance().updateSensor(x+1,(String)sensorValues.get("name"),(String)sensorValues.get("value"));
                TableRow entry = new TableRow(context);
                entry.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
                TextView cell = new TextView(context);
                cell.setLayoutParams(layoutParams);
                cell.setText("(" + (x+1) + ") " + s.getName());
                entry.addView(cell);
                TextView cell1 = new TextView(context);
                cell1.setLayoutParams(layoutParams);
                cell1.setText(s.getTemperature() + " ºC");
                entry.addView(cell1);
                temperaturesTable.addView(entry,x+1);
            }
            temperaturesTable.setBackgroundColor(Color.parseColor("#28ffffff"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private TableRow getTempsTableHeader(){
        if(headerEntry==null){
            TableRow entry = new TableRow(context);
            entry.setLayoutParams(new TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            TextView cell = new TextView(context);
            cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            cell.setTextColor(Color.BLACK);
            cell.setText("Sensor");
            entry.addView(cell);
            TextView cell1 = new TextView(context);
            cell1.setText("Temperatura");
            cell1.setTextColor(Color.BLACK);
            cell1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            entry.addView(cell1);
            headerEntry=entry;
        }
        return headerEntry;
    }
    private void ThermoJsonParser (String jsonString){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray thermostats = (JSONArray) jsonParser.parse(jsonString);
            if (thermostats.size()!=thermostatsNumber){
                thermostatsNumber=thermostats.size();
                context.setPages(thermostatsNumber);
            }
            thermostatsTable.removeAllViews();
            thermostatsTable.addView(getThermoTableHeader(),0);
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
                cell.setText(sensorValues.get("name").toString());
                entry.addView(cell);
                TextView cell1 = new TextView(context);
                cell1.setLayoutParams(layoutParams);
                cell1.setText(sensorValues.get("sensors").toString());
                entry.addView(cell1);
                TextView cell2 = new TextView(context);
                cell2.setLayoutParams(layoutParams);
                if(sensorValues.get("mode").toString().equals("AUTO")){
                    cell2.setText(sensorValues.get("temperature").toString() + " ºC");
                }
                else{
                    cell2.setText(sensorValues.get("mode").toString());
                }
                entry.addView(cell2);
                thermostatsTable.addView(entry,x+1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private TableRow getThermoTableHeader(){
        TableRow entry = new TableRow(context);
        entry.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        TextView cell = new TextView(context);
        cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        cell.setTextColor(Color.BLACK);
        cell.setText("Nom de la zona");
        entry.addView(cell);
        TextView cell1 = new TextView(context);
        cell1.setText("Sensors");
        cell1.setTextColor(Color.BLACK);
        cell1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        entry.addView(cell1);
        TextView cell2 = new TextView(context);
        cell2.setText("Mode");
        cell2.setTextColor(Color.BLACK);
        cell2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        entry.addView(cell2);
        return entry;
    }
}
