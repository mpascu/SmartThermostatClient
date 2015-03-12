package com.example.marc.smartthermostatclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

/**
 * Created by Marc on 06/03/2015.
 */
public class ThermostatFragment extends android.support.v4.app.Fragment {
    private int position;

    public static ThermostatFragment create(int position) {
        Bundle args = new Bundle();
        args.putInt("POS", position);
        ThermostatFragment fragment = new ThermostatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("POS");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thermostat, container, false);
        final LinearLayout programmerLayout = (LinearLayout) view.findViewById(R.id.programmer_layout);
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroupMode);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radioButtonAUTO){
                    programmerLayout.setVisibility(View.VISIBLE);
                }else{
                    programmerLayout.setVisibility(View.GONE);
                }
            }
        });
        //rg.check();
        return view;
    }
}
