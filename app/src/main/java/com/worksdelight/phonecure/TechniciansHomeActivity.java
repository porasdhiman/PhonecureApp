package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bruce.pickerview.LoopView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
 * Created by worksdelight on 20/04/17.
 */

public class TechniciansHomeActivity extends Activity implements View.OnClickListener {
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
    TextView header_txt;
    ImageView message_img;
    AdView mAdView;
    AdRequest adRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");

        setContentView(R.layout.technicians_home_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }


        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
      /*  Login_TV = (LoginButton) parentView.findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email"));
        fbMethod();*/
        WindowManager w = getWindowManager();

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

        global = (Global) getApplicationContext();
        init();
        mapView = (MapView) findViewById(R.id.mapView);
        service_txt = (TextView) findViewById(R.id.service_txt);
        booking_txt = (TextView) findViewById(R.id.booking_txt);
        // techniciansName_txtView = (TextView)findViewById(R.id.techniciansName_txtView);

        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        IconFactory iconFactory = IconFactory.getInstance(this);

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

                mapboxMap.setPadding(0, 0, 0, measuredHeight / 2 + 20);


            }
        });
        dialogWindow();
        showDeviceMethod();
    }

    public void init() {
        mAdView = (AdView)findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        header_txt = (TextView) findViewById(R.id.header_txt);
        message_img = (ImageView) findViewById(R.id.message_img);
        // techniciansName_txtView = (TextView) findViewById(R.id.techniciansName_txtView);
        appointment_layout = (RelativeLayout) findViewById(R.id.appointment_layout);
        service_layout = (RelativeLayout) findViewById(R.id.service_layout);
        profile_layout = (RelativeLayout) findViewById(R.id.profile_layout);
        setting_layout = (RelativeLayout) findViewById(R.id.setting_layout);
        appointment_layout.setOnClickListener(this);
        service_layout.setOnClickListener(this);
        profile_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);
        message_img.setOnClickListener(this);
        String title = header_txt.getText().toString();
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1 = new SpannableString(title);
        str1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 5, str1.length(), str1.length() - 1);
        builder.append(str1);
        header_txt.setText(builder, TextView.BufferType.SPANNABLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            dialogWindow();
            showDeviceMethod();
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_layout:
                Intent i = new Intent(this, TechniciansHistory.class);
                startActivityForResult(i, 0);
                break;
            case R.id.service_layout:
                Intent j = new Intent(this, TechniciansDevice.class);
                j.putExtra("type", "1");
                startActivityForResult(j, 0);
                break;
            case R.id.profile_layout:
                Intent w = new Intent(this, TechniciansEditProfileActivity.class);
                startActivity(w);
                break;
            case R.id.setting_layout:
                Intent t = new Intent(this, TechniciansSetting.class);
                startActivity(t);
                break;
            case R.id.message_img:
                Intent m = new Intent(this, NotifyActivity.class);
                startActivity(m);
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
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
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
        if (mAdView != null) {
            mAdView.destroy();
        }

    }

    //--------------------Category api method---------------------------------
    private void showDeviceMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.STARTING_URL + "user_id=" + CommonUtils.UserID(TechniciansHomeActivity.this),
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
                                // techniciansName_txtView.setText("Hi, " + data.getString("name"));
                                booking_txt.setText(data.getString("booking_count") + " Scheduled");

                                service_txt.setText(data.getString("services_count") + " Services");


                            } else {
                                // Toast.makeText(TechniciansHomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog2.dismiss();
            }
        });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TechniciansHomeActivity.this);
        requestQueue.add(stringRequest);
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.progrees_login);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) dialog2.findViewById(R.id.loader_view);
        loaderView.setIndicatorColor(getResources().getColor(R.color.main_color));
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

}
