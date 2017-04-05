package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 27/02/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View parentView;

    TextView current_device_name_txt;
    Global global;
    LinearLayout select_device_layout;
    MapView mapView;
    RelativeLayout select_color;
    Dialog dialog2;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Mapbox.getInstance(getActivity(), "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");

        parentView = inflater.inflate(R.layout.home_layout_second, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.homevisibilityMethod();
        global = (Global) getActivity().getApplicationContext();
        init(parentView);
       /* mapView = (MapView) parentView.findViewById(R.id.mapView);
        // mapView.setStyle(Style.MAPBOX_STREETS);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

            }
        });
        mapView.onCreate(savedInstanceState);*/
        return parentView;
    }

    public void init(View v) {
        current_device_name_txt = (TextView) v.findViewById(R.id.current_device_name_txt);
        current_device_name_txt.setText(global.getDeviceName());
        select_device_layout = (LinearLayout) v.findViewById(R.id.select_device_layout);
        select_device_layout.setOnClickListener(this);
        select_color = (RelativeLayout) v.findViewById(R.id.select_color);
        select_color.setOnClickListener(this);
        colorMethod();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_device_layout:
                Intent i = new Intent(getActivity(), DeviceActivity.class);
                startActivity(i);

                break;
            case R.id.select_color:
                dialogWindow();

                break;
        }
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(getActivity());
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       /* dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);*/
        dialog2.setContentView(R.layout.color_list_dialog);
        ListView color_list = (ListView) dialog2.findViewById(R.id.color_list);
        color_list.setAdapter(new ColorAdapter(getActivity(), list));
        color_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent home=new Intent(getActivity(),iPhoneServiceActivity.class);
                startActivity(home);
            }
        });

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

    public class ColorAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        ArrayList<HashMap<String, String>> colorList = new ArrayList<>();

        ColorAdapter(Context c, ArrayList<HashMap<String, String>> colorList) {
            this.c = c;
            this.colorList = colorList;
            inflatore=LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return colorList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflatore.inflate(R.layout.color_item, null);

            TextView color_txt = (TextView) view.findViewById(R.id.color_txt);

            color_txt.setText(colorList.get(i).get(GlobalConstant.color));
            return view;
        }
    }

    //--------------------Category api method---------------------------------
    private void colorMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.COLORGET_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.


                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray data = obj.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject arryObj = data.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(GlobalConstant.id, arryObj.getString(GlobalConstant.id));
                                    map.put(GlobalConstant.color, arryObj.getString(GlobalConstant.color));
                                    map.put(GlobalConstant.icon, arryObj.getString(GlobalConstant.icon));
                                    list.add(map);
                                }


                            } else {
                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    /*@Override
    public void onResume() {
        super.onResume();

            mapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

            mapView.onPause();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

            mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

            mapView.onLowMemory();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

            mapView.onDestroy();

    }*/
}