package com.worksdelight.phonecure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by worksdelight on 27/02/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{

    private View parentView;

    TextView current_device_name_txt;
Global global;
    LinearLayout select_device_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home_layout_second, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.homevisibilityMethod();
        global=(Global)getActivity().getApplicationContext();
        init(parentView);
        return parentView;
    }

    public void init(View v) {
        current_device_name_txt = (TextView) v.findViewById(R.id.current_device_name_txt);
        current_device_name_txt.setText(global.getDeviceName());
        select_device_layout=(LinearLayout)v.findViewById(R.id.select_device_layout);
        select_device_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_device_layout:
                Intent i=new Intent(getActivity(),DeviceActivity.class);
                startActivity(i);

                break;
        }
    }
}