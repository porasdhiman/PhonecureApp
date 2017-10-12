package com.worksdelight.phonecure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by worksdelight on 28/02/17.
 */

public class TechniciansFragment extends Fragment {
    ListView repair_listView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.technicians_layout, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();

        repair_listView = (ListView)v.findViewById(R.id.repair_listView);

        repair_listView.setAdapter(new ListViewAdapter(getActivity()) {
        });
        repair_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent j=new Intent(getActivity(),TechniciansDetailActivity.class);
                startActivity(j);
            }
        });
        return v;
    }






}




