package com.example.marc.smartthermostatclient.DataStructure;

import java.util.HashMap;
import java.util.HashSet;

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

    public Thermostat getThermostat(int id){
        return sensorHashMap.get(id);
    }

    public void addThermostat(int id, Thermostat sensor){
        sensorHashMap.put(id, sensor);
    }

    public void updateThermostat(int id, String name, double temperature, Thermostat.modeOptions mode, HashSet<Sensor> sensors) {
        sensorHashMap.get(id).update( name,  temperature, mode, sensors);
    }
}
