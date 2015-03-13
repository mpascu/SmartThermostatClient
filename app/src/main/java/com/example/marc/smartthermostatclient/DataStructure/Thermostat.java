package com.example.marc.smartthermostatclient.DataStructure;

import java.util.Set;

/**
 * Created by Marc on 13/03/2015.
 */
public class Thermostat {
    private String name;
    private float temperature;
    private String mode;
    private Set<Sensor> sensors;

    public Thermostat(String name, float temperature, String mode, Set<Sensor> sensors) {
        this.name = name;
        this.temperature = temperature;
        this.mode = mode;
        this.sensors = sensors;
    }

    public float getTemperature() {
        return temperature;
    }

    public String getName() {
        return name;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
