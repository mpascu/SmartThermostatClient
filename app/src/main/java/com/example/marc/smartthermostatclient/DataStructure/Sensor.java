package com.example.marc.smartthermostatclient.DataStructure;

/**
 * Created by Marc on 13/03/2015.
 */
public class Sensor {
    private int id;
    private String name;
    private float temperature;

    public Sensor(int id, String name,float temperature ) {
        this.temperature = temperature;
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
