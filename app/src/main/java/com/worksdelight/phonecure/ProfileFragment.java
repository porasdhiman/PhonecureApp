package com.worksdelight.phonecure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by worksdelight on 27/02/17.
 */

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.blank_layout, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();

        return v;
    }
}