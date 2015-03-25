package com.example.marc.smartthermostatclient;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.marc.smartthermostatclient.DataStructure.Sensor;
import com.example.marc.smartthermostatclient.DataStructure.SensorManager;
import com.example.marc.smartthermostatclient.DataStructure.Thermostat;
import com.example.marc.smartthermostatclient.DataStructure.ThermostatManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;

/**
 * Created by Marc on 12/03/2015.
 */
public class ThermostatUpdateScheduler extends UpdatesScheduler{
    public Thermostat thermostat=null;
    private int id;

    public ThermostatUpdateScheduler(Activity c, View rootView, String url, int id){
        super(c,rootView,url);
        this.id=id;
    }

    @Override
    public void run() {

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

    public void ThermostatParser(String jsonString) {
        JSONParser jsonParser = new JSONParser();

        try {
            JSONObject temperatures = (JSONObject) jsonParser.parse(jsonString);
            JSONArray JSONSensors = (JSONArray) temperatures.get("sensors");
            HashSet<Sensor> sensors = getSensors(JSONSensors);

            this.thermostat = ThermostatManager.getInstance().getThermostat(id);
            this.thermostat.update((String)temperatures.get("name"),
                    Double.parseDouble((String)temperatures.get("temperature")),
                    Thermostat.modeOptions.valueOf((String)temperatures.get("mode")),
                    sensors);
            this.thermostat.updateView(rootView);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private HashSet<Sensor> getSensors(JSONArray jsonSensors) {
        HashSet<Sensor> sensors = new HashSet<>();
        for (int x=0; x<jsonSensors.size();x++){
            //System.out.println("IDDDD"+(int)(long)jsonSensors.get(x));
            //System.out.println("calue"+SensorManager.getInstance().getSensor((int)(long)jsonSensors.get(x)).getId());
            //System.out.println(SensorManager.getInstance().getSensor((int) (long) jsonSensors.get(x)).getName());
            ///System.out.println(SensorManager.getInstance().getSensor((int)(long)jsonSensors.get(x)).getTemperature());
            sensors.add(SensorManager.getInstance().getSensor((int)(long)jsonSensors.get(x)));
        }
        return sensors;
    }
}
