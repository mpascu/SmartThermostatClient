package com.example.marc.smartthermostatclient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.marc.smartthermostatclient.DataStructure.Thermostat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 03/03/2015.
 */
public class APIRequestHandler {
    public final static APIRequestHandler INSTANCE = new APIRequestHandler();
    private RequestQueue queue;

    private APIRequestHandler() {
    }

    public void setQueue(RequestQueue requestQueue) {
        this.queue = requestQueue;
    }

    public void makeGetRequest(String serverURL, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, serverURL,responseListener, errorListener);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makePostRequest(String serverURL, final String name, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, serverURL, responseListener, errorListener ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                return params;
            }
        };
        queue.add(postRequest);
    }
    public void makePutRequest(String serverURL, final Thermostat t, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest postRequest = new StringRequest(Request.Method.PUT, serverURL, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("temperature", Double.toString(t.getTemperature()));
                params.put("mode", t.getMode().toString());
                params.put("sensors", t.getSensorIds());
                return params;
            }
        };
        queue.add(postRequest);
    }
    public void makeDeleteRequest(String serverURL, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, serverURL, responseListener, errorListener);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
