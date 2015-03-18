package com.example.marc.smartthermostatclient.DataStructure;

import java.util.HashMap;

/**
 * Created by Marc on 13/03/2015.
 */
public class ThermostatManager {
    private static ThermostatManager ourInstance = new ThermostatManager();
    private HashMap<Integer,Thermostat> sensorHashMap;

    private ThermostatManager(){
        sensorHashMap=new HashMap<>();
    }
    public static ThermostatManager getInstance() {
        return ourInstance;
    }

    public Thermostat getSensor(int id){
        return sensorHashMap.get(id);
    }

    public void addSensor(int id, Thermostat sensor){
        sensorHashMap.put(id, sensor);
    }
/*
    public Sensor updateSensor(int x, String name, String value) {
        if (sensorHashMap.containsKey(x)){
            sensorHashMap.get(x).setTemperature(Float.parseFloat(value));
        }
        else{
            sensorHashMap.put(x,new Sensor(x,name,Float.parseFloat(value)));
        }
        return sensorHashMap.get(x);
    }
*/}
