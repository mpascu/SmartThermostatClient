package com.example.marc.smartthermostatclient.DataStructure;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.marc.smartthermostatclient.MainActivity;
import com.example.marc.smartthermostatclient.R;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Marc on 13/03/2015.
 */
public class Thermostat {
    private MainActivity context;
    private String name;
    private double temperature;
    public enum modeOptions{ON,OFF,AUTO};
    private boolean hot;
    private modeOptions mode;
    private HashSet<Sensor> sensors;

    public Thermostat(MainActivity context) {
        this.context = context;
        this.hot=true;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getName() {
        return name;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public modeOptions getMode() {
        return mode;
    }

    public void setMode(modeOptions mode) {
        this.mode = mode;
    }

    public void updateView(View rootView) {
        TextView temp = (TextView) rootView.findViewById(R.id.thermo_temperature);
        temp.setText(Double.toString(this.temperature)+"ºC");

        RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radioGroupMode);
        switch (mode){
            case AUTO:
                rg.check(R.id.radioButtonAUTO);
                break;
            case ON:
                rg.check(R.id.radioButtonON);
                break;
            case OFF:
                rg.check(R.id.radioButtonOFF);
                break;
        }

        TableLayout temperaturesTable = (TableLayout) rootView.findViewById(R.id.thermo_temperatures_table);
        temperaturesTable.removeAllViews();
        int x = 0;
        for (Sensor s : sensors){
            //System.out.println(sensors.size());
            TableRow entry = new TableRow(context);
            entry.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
            TextView cell = new TextView(context);
            cell.setLayoutParams(layoutParams);
            cell.setText(s.getName());
            entry.addView(cell);
            TextView cell1 = new TextView(context);
            cell1.setLayoutParams(layoutParams);
            cell1.setText(s.getTemperature() + " ºC");
            entry.addView(cell1);
            temperaturesTable.addView(entry,x);
            x++;

        }
    }
    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }
    public String getSensorIds(){
        int[] a=new int[sensors.size()];
        int x=0;
        for (Sensor sensor : sensors){
            if(sensor==null)
                return "[]";
            a [x]=sensor.getId();
            x++;
        }
        return Arrays.toString(a);
    }

    public void removeSensors() {
        this.sensors=new HashSet<>();
    }
    public void addSensor(Sensor s){
        this.sensors.add(s);
    }
    public void update(String name, double temperature, modeOptions mode, HashSet<Sensor> sensors) {
        this.name=name;
        this.temperature=temperature;
        this.mode=mode;
        this.sensors=sensors;
    }

}
