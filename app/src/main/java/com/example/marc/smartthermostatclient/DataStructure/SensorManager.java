package com.example.marc.smartthermostatclient.DataStructure;

import java.util.HashMap;

/**
 * Created by Marc on 13/03/2015.
 */
public class SensorManager {
    private static SensorManager ourInstance = new SensorManager();
    private HashMap<Integer,Sensor> sensorHashMap;

    private SensorManager(){
        sensorHashMap=new HashMap<>();
    }
    public static SensorManager getInstance() {
        return ourInstance;
    }

    public Sensor getSensor(int id){
        return sensorHashMap.get(id);
    }

    public void addSensor(int id, Sensor sensor){
        sensorHashMap.put(id, sensor);
    }

    public Sensor updateSensor(int x, String name, String value) {
        if (sensorHashMap.containsKey(x)){
            sensorHashMap.get(x).setTemperature(Float.parseFloat(value));
        }
        else{
            sensorHashMap.put(x,new Sensor(x,name,Float.parseFloat(value)));
        }
        return sensorHashMap.get(x);
    }
}
