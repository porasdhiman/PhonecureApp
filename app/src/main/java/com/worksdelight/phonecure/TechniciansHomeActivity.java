package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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

import com.amulyakhare.textdrawable.TextDrawable;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 20/04/17.
 */

public class TechniciansHomeActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private View parentView;

    TextView current_device_name_txt, done_txtView, cancel_txtView;
    Global global;
    RelativeLayout select_device_layout;
    MapView mapView;
    RelativeLayout select_color, bottome_layout;
    Dialog dialog2,ratingDialog;
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

    //--------------Location lat long variable---------

    private Location mLastLocation, myLocation;
    LocationManager mLocationManager;
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */

    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    //-------------Guide text-----------
    RelativeLayout guide_layout;
    //------------rating variable --------
    String com_star = "1", time_star = "1", service_star = "1", skill_star = "1", user_id;
    String userName_mString = "", imageName_mString = "", id_mString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");

        setContentView(R.layout.technicians_home_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        buildGoogleApiClient();

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

        dialogWindow();
        showDeviceMethod();
        ratingPendingMethod();
    }

    public void init() {
        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        guide_layout = (RelativeLayout) findViewById(R.id.guide_layout);
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
        guide_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TechniciansHomeActivity.this, SearchServiceActivity.class);
                startActivity(i);
            }
        });

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
        mGoogleApiClient.connect();
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
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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

    //---------------------------Location lat long method-----------------
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Toast.makeText(SplashActivity.this, "" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            global.setLat(String.valueOf(mLastLocation.getLatitude()));
            global.setLong(String.valueOf(mLastLocation.getLongitude()));
            Log.e("lat long", global.getLat() + " " + global.getLong());

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
        } else {


        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

//-----------------------Check if rating panding-----------
private void ratingPendingMethod() {

// Request a string response from the provided URL.
    StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.IF_RATING_PENDING + CommonUtils.UserID(TechniciansHomeActivity.this),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.

                    Log.e("response", response);
                    try {
                        JSONObject obj = new JSONObject(response);

                        String status = obj.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONObject data = obj.getJSONObject("data");
                            JSONObject techDetails = data.getJSONObject("user_detail");
                            id_mString=techDetails.getString(GlobalConstant.id);
                            userName_mString=techDetails.getString(GlobalConstant.name);
                            imageName_mString=techDetails.getString(GlobalConstant.image);

                            ratingWindow();


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

        }
    });


    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    RequestQueue requestQueue = Volley.newRequestQueue(TechniciansHomeActivity.this);
    requestQueue.add(stringRequest);
}

    //-------------Rating method------------

    //---------------------------Progrees Dialog-----------------------
    public void ratingWindow() {
        ratingDialog = new Dialog(this);
        ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingDialog.setCanceledOnTouchOutside(false);
        ratingDialog.setCancelable(false);
        ratingDialog.setContentView(R.layout.rateing_dialog_layout);
        TextView submit_rating = (TextView) ratingDialog.findViewById(R.id.submit_rating);
        ratingStarinit(ratingDialog);
        submit_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWindow();
                ratingMethod();
            }
        });
        ImageView user_image = (ImageView) ratingDialog.findViewById(R.id.user_img);
        TextView user_txt = (TextView) ratingDialog.findViewById(R.id.user_txt);

        user_txt.setText(userName_mString);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(user_txt.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
        if (imageName_mString.equalsIgnoreCase("")) {

            user_image.setImageDrawable(drawable);
        } else {
            Picasso.with(this).load(GlobalConstant.TECHNICIANS_IMAGE_URL + imageName_mString).placeholder(drawable).transform(new CircleTransform()).into(user_image);


            //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
        }
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        ratingDialog.show();
    }

    public void ratingStarinit(Dialog d) {
        final ImageView com_star1 = (ImageView) d.findViewById(R.id.com_star1);
        final ImageView com_star2 = (ImageView) d.findViewById(R.id.com_star2);
        final ImageView com_star3 = (ImageView) d.findViewById(R.id.com_star3);
        final ImageView com_star4 = (ImageView) d.findViewById(R.id.com_star4);
        final ImageView com_star5 = (ImageView) d.findViewById(R.id.com_star5);

        final ImageView timing_star1 = (ImageView) d.findViewById(R.id.timing_star1);
        final ImageView timing_star2 = (ImageView) d.findViewById(R.id.timing_star2);
        final ImageView timing_star3 = (ImageView) d.findViewById(R.id.timing_star3);
        final ImageView timing_star4 = (ImageView) d.findViewById(R.id.timing_star4);
        final ImageView timing_star5 = (ImageView) d.findViewById(R.id.timing_star5);

        final ImageView service_star1 = (ImageView) d.findViewById(R.id.service_star1);
        final ImageView service_star2 = (ImageView) d.findViewById(R.id.service_star2);
        final ImageView service_star3 = (ImageView) d.findViewById(R.id.service_star3);
        final ImageView service_star4 = (ImageView) d.findViewById(R.id.service_star4);
        final ImageView service_star5 = (ImageView) d.findViewById(R.id.service_star5);


        final ImageView skill_star1 = (ImageView) d.findViewById(R.id.skill_star1);
        final ImageView skill_star2 = (ImageView) d.findViewById(R.id.skill_star2);
        final ImageView skill_star3 = (ImageView) d.findViewById(R.id.skill_star3);
        final ImageView skill_star4 = (ImageView) d.findViewById(R.id.skill_star4);
        final ImageView skill_star5 = (ImageView) d.findViewById(R.id.skill_star5);


        com_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "1";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.green_star);
                com_star3.setImageResource(R.drawable.green_star);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "2";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.green_star);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "3";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "4";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.star_fill);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "5";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.star_fill);
                com_star5.setImageResource(R.drawable.star_fill);
            }
        });
        timing_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "1";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.green_star);
                timing_star3.setImageResource(R.drawable.green_star);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "2";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.green_star);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "3";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "4";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.star_fill);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "5";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.star_fill);
                timing_star5.setImageResource(R.drawable.star_fill);
            }
        });

        service_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "1";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.green_star);
                service_star3.setImageResource(R.drawable.green_star);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "2";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.green_star);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "3";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "4";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.star_fill);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "5";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.star_fill);
                service_star5.setImageResource(R.drawable.star_fill);
            }
        });

        skill_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "1";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.green_star);
                skill_star3.setImageResource(R.drawable.green_star);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "2";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.green_star);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "3";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "4";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.star_fill);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "5";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.star_fill);
                skill_star5.setImageResource(R.drawable.star_fill);
            }
        });

    }

    //-------------------Rating method-------------------
    private void ratingMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.RATING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //JSONObject data=obj.getJSONObject("data");
                                ratingDialog.dismiss();

                            } else {
                                ratingDialog.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog2.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put(GlobalConstant.USERID, CommonUtils.UserID(TechniciansHomeActivity.this));
                params.put(GlobalConstant.favorite_user_id, id_mString);
                params.put("rating", com_star);
                params.put("rating1", service_star);
                params.put("rating2", time_star);
                params.put("rating3", skill_star);


                Log.e("Parameter for rating", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TechniciansHomeActivity.this);
        requestQueue.add(stringRequest);
    }

}
