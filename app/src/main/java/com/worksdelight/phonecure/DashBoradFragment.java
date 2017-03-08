package com.worksdelight.phonecure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by worksdelight on 28/02/17.
 */

public class DashBoradFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dashboard_layout, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();


        return v;
    }

}

