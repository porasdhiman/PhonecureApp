package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.bruce.pickerview.LoopView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 15/04/17.
 */

public class TechniciansHomeFragment extends Fragment implements View.OnClickListener {

    private View parentView;

    TextView current_device_name_txt, done_txtView, cancel_txtView;
    Global global;
    RelativeLayout select_device_layout;
    MapView mapView;
    RelativeLayout select_color, bottome_layout;
    Dialog dialog2;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    LoopView loopView;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    int pos, measuredWidth, measuredHeight;
    Marker markers;
    TextView techniciansName_txtView, service_txt, booking_txt;
    RelativeLayout appointment_layout, service_layout, profile_layout, setting_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");

        parentView = inflater.inflate(R.layout.technicians_home_layout, container, false);

        sp = getActivity().getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
      /*  Login_TV = (LoginButton) parentView.findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email"));
        fbMethod();*/
        WindowManager w = getActivity().getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight();
        }
        TechniciansMainActivity parentActivity = (TechniciansMainActivity) getActivity();
        parentActivity.homevisibilityMethod();
        global = (Global) getActivity().getApplicationContext();
        init(parentView);
        mapView = (MapView) parentView.findViewById(R.id.mapView);
        service_txt = (TextView) parentView.findViewById(R.id.service_txt);
        booking_txt = (TextView) parentView.findViewById(R.id.booking_txt);
        techniciansName_txtView = (TextView) parentView.findViewById(R.id.techniciansName_txtView);

        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        IconFactory iconFactory = IconFactory.getInstance(getActivity());

        final Icon icon = iconFactory.fromResource(R.drawable.map_icon);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(global.getLat()), Double.parseDouble(global.getLong())))
                        .zoom(10)

                        .build());
                ;


                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(global.getLat()), Double.parseDouble(global.getLong()))).icon(icon));

                mapboxMap.setPadding(0, 0, 0, measuredHeight / 2 + 50);


            }
        });
        dialogWindow();
        showDeviceMethod();
        return parentView;
    }

    public void init(View v) {
        techniciansName_txtView = (TextView) v.findViewById(R.id.techniciansName_txtView);
        appointment_layout = (RelativeLayout) v.findViewById(R.id.appointment_layout);
        service_layout = (RelativeLayout) v.findViewById(R.id.service_layout);
        profile_layout = (RelativeLayout) v.findViewById(R.id.profile_layout);
        setting_layout = (RelativeLayout) v.findViewById(R.id.setting_layout);
        appointment_layout.setOnClickListener(this);
        service_layout.setOnClickListener(this);
        profile_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_layout:
                Intent i = new Intent(getActivity(), TechniciansHistory.class);
                startActivity(i);
                break;
            case R.id.service_layout:
                Intent j = new Intent(getActivity(), TechniciansDevice.class);
                startActivity(j);
                break;
            case R.id.profile_layout:
                Intent w = new Intent(getActivity(), WorkingHourActivity.class);
                startActivity(w);
                break;
            case R.id.setting_layout:
                Intent t = new Intent(getActivity(), TechniciansSetting.class);
                startActivity(t);
                break;

        }
    }

    public ArrayList<String> getList() {
        ArrayList<String> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list1.add(list.get(i).get(GlobalConstant.color));
        }
        return list1;
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
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
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();

    }

    //--------------------Category api method---------------------------------
    private void showDeviceMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.STARTING_URL + "user_id=" + CommonUtils.UserID(getActivity()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONObject data = obj.getJSONObject("data");
                                techniciansName_txtView.setText("Hi, " + data.getString("name"));
                                booking_txt.setText(data.getString("booking_count") + " Scheduled");

                                service_txt.setText(data.getString("services_count") + " Services");


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

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(getActivity());
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.progrees_login);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) dialog2.findViewById(R.id.loader_view);
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

}
