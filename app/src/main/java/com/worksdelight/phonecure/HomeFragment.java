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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bruce.pickerview.LoopScrollListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 27/02/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View parentView;

    TextView current_device_name_txt, done_txtView, cancel_txtView;
    Global global;
    RelativeLayout select_device_layout;
    MapView mapView;
    RelativeLayout select_color, bottome_layout;
    Dialog dialog2,messageDialog;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    LoopView loopView;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    int pos, measuredWidth, measuredHeight;
    Marker markers;
    TextView select_color_txt;
    AdView mAdView;
    AdRequest adRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");

        parentView = inflater.inflate(R.layout.home_layout_second, container, false);

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
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.homevisibilityMethod();
        global = (Global) getActivity().getApplicationContext();
        init(parentView);
        mapView = (MapView) parentView.findViewById(R.id.mapView);

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

                mapboxMap.setPadding(0, 0, 0, measuredHeight / 2 + 40);


            }
        });

        return parentView;
    }

    public void init(View v) {
        mAdView = (AdView) v.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        select_color_txt = (TextView) v.findViewById(R.id.select_color_txt);

        current_device_name_txt = (TextView) v.findViewById(R.id.current_device_name_txt);
        cancel_txtView = (TextView) v.findViewById(R.id.cancel_txtView);
        done_txtView = (TextView) v.findViewById(R.id.done_txtView);
        current_device_name_txt.setText(global.getDeviceName() + " " + sp.getString("color name", ""));
        select_device_layout = (RelativeLayout) v.findViewById(R.id.select_device_layout);
        select_device_layout.setOnClickListener(this);
        select_color = (RelativeLayout) v.findViewById(R.id.select_color);
        bottome_layout = (RelativeLayout) v.findViewById(R.id.bottome_layout);
        select_color.setOnClickListener(this);
        loopView = (LoopView) v.findViewById(R.id.loop_view);
        loopView.setInitPosition(2);
        loopView.setCanLoop(false);

        loopView.setTextSize(25);
        colorMethod();
        cancel_txtView.setOnClickListener(this);
        done_txtView.setOnClickListener(this);
        select_color_txt.setOnClickListener(this);
    }

    /*//---------------------------facebook method------------------------------
    public void fbMethod() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //token = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        // Application code


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "picture.type(large),bio,id,name,link,gender,email, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_color_txt:
                if (list.size() > 0) {
                    bottome_layout.setVisibility(View.VISIBLE);
                    loopView.setDataList(getList());
                } else {
                    messageWindow();
                }
                break;
            case R.id.select_device_layout:
                Intent i = new Intent(getActivity(), DeviceActivity.class);
                startActivity(i);

                break;
            case R.id.select_color:
                // dialogWindow();
                if(list.size()>0) {
                    if (sp.getBoolean("pos", false) != true) {
                        bottome_layout.setVisibility(View.VISIBLE);
                        loopView.setDataList(getList());
                    } else {
                        global.setColorId(sp.getString("id", ""));
                        Intent home = new Intent(getActivity(), ServiceActivity.class);
                        startActivity(home);
                    }
                }else{
                    messageWindow();
                }
                break;
            case R.id.done_txtView:
                // dialogWindow();
                bottome_layout.setVisibility(View.GONE);
                loopView.setLoopListener(new LoopScrollListener() {
                    @Override
                    public void onItemSelect(int item) {
                        pos = item;
                    }
                });
                ed.putString("id", list.get(pos).get(GlobalConstant.id));
                ed.putString("color name", list.get(pos).get(GlobalConstant.color));
                ed.putBoolean("pos", true);
                ed.commit();
                current_device_name_txt.setText(global.getDeviceName() + " " + list.get(pos).get(GlobalConstant.color));
                global.setColorId(sp.getString("id", ""));
                bottome_layout.setVisibility(View.GONE);
                Intent home = new Intent(getActivity(), ServiceActivity.class);
                startActivity(home);
                break;
            case R.id.cancel_txtView:
                // dialogWindow();
                bottome_layout.setVisibility(View.GONE);

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


    /*//--------------------Category api method---------------------------------
    private void colorMethod() {
        String url = GlobalConstant.COLORGET_URL + global.getDeviceName();
        Log.e("url", url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
*/
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
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

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(getActivity());
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
    //---------------------------Progrees Dialog-----------------------
    public void messageWindow() {
        messageDialog = new Dialog(getActivity());
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.setCancelable(false);
        messageDialog.setContentView(R.layout.messagenot_support_layout);
TextView ok_txtView=(TextView)messageDialog.findViewById(R.id.ok_txtView);
        ok_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageDialog.dismiss();
            }
        });
TextView phonecure_txt=(TextView)messageDialog.findViewById(R.id.phonecure_txt);
        phonecure_txt.setText("We currently doesn’t offer rapair for \n"+global.getDeviceName()+"\n We have registered your device with \n" +
                "us and will contact you soon.");
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        messageDialog.show();
    }

    private void colorMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.COLORGET_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


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
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("slug", global.getDeviceName());


                Log.e("Parameter for color", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}