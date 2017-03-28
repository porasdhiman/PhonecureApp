package com.worksdelight.phonecure;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Double.parseDouble;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {
    Marker marker;
    private GoogleMap mMap;
    private Hashtable<String, String> markers;

    private Location mLastLocation;
    ImageView previous_btn;
    //--------------Google search api variable------------
    protected GoogleApiClient mGoogleApiClient;


    int i;
    Marker mark;
    String lat, lng;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    TwoWayView horizontal_view_list;
    ImageView search_img;
    Dialog dialog2;
    LinearLayout book_layout;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    final HashMap<String, Integer> map = new HashMap<>();
    TextView average_rating_txt, technicians_name_txtView, book_appointment;
    int pos;
    Global global;
    CircleImageView tech_img;

    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    ArrayList<HashMap<String, String>> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        global = (Global) getApplicationContext();
        technicians_name_txtView = (TextView) findViewById(R.id.technicians_name_txtView);
        average_rating_txt = (TextView) findViewById(R.id.average_rating_txt);
        book_appointment = (TextView) findViewById(R.id.book_appointment);
        tech_img = (CircleImageView) findViewById(R.id.tech_img);
       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();*/
        buildGoogleApiClient();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        search_img = (ImageView) findViewById(R.id.search_img);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, RepairActivity.class);
                startActivity(i);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
        book_layout = (LinearLayout) findViewById(R.id.book_layout);
       /* horizontal_view_list = (TwoWayView) findViewById(R.id.horizontal_view_list);
        horizontal_view_list.setAdapter(new HorizontalViewAdapter(this));
        horizontal_view_list.setItemMargin(20);*/
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        Log.e("device_d", getIntent().getExtras().getString("device_id") + " " + getIntent().getExtras().getString("id") + " " + getIntent().getExtras().getString("selected_id"));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state
        // and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            lat = String.valueOf(mLastLocation.getLatitude());
            lng = String.valueOf(mLastLocation.getLongitude());
            dialogWindow();
            SearchMethod();

        } else {
            // Toast.makeText(this, "Please check GPS and restart App", Toast.LENGTH_LONG).show();
        }
        Log.i("search", "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e("search", "Google Places API connection suspended.");
    }


    //------------------------Current lat long------------------------
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)

                .build();
    }

    public void openMarkerView() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                pos = map.get(marker.getId());

                book_layout.setVisibility(View.VISIBLE);

                technicians_name_txtView.setText(cap(list.get(pos).get(GlobalConstant.name)));


                average_rating_txt.setText(list.get(pos).get(GlobalConstant.average_rating));


                book_appointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MapsActivity.this, BookAppoinmentActivity.class);
                        i.putExtra("pos", String.valueOf(pos));
                        i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                        startActivity(i);

                    }
                });
                return false;
            }
        });

    }

    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public void eventLocOnMap() {


        //mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        // Add a marker in Sydney and move the camera
        Log.e("event list value ", list.toString());
        for (i = 0; i < list.size(); i++) {
            Double lat = Double.parseDouble(list.get(i).get(GlobalConstant.latitude));
            Double longt = Double.parseDouble(list.get(i).get(GlobalConstant.longitude));
            LatLng postion = new LatLng(lat, longt);
            mark = mMap.addMarker(new MarkerOptions().position(postion).title(list.get(i).get(GlobalConstant.name)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));

            // markers.put(mark.getId(), "http://envagoapp.com/uploads/" + global.getEvent_list().get(i).get(GlobalConstants.EVENT_IMAGES));
            map.put(mark.getId(), i);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
        LatLng postion = new LatLng(parseDouble(lat), parseDouble(lng));
        // mark = mMap.addMarker(new MarkerOptions().position(postion).icon(BitmapDescriptorFactory.fromResource(R.drawable.oval)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(postion, 3));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(3), 2000, null);
        openMarkerView();


    }

    //--------------------search api method---------------------------------
    private void SearchMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.SEARCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("responsefff", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray arr = obj.getJSONArray("data");
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject objArr = arr.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();

                                    map.put(GlobalConstant.id, objArr.getString(GlobalConstant.id));
                                    map.put(GlobalConstant.name, objArr.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.image, objArr.getString(GlobalConstant.image));

                                    map.put(GlobalConstant.availability, objArr.getString(GlobalConstant.availability));
                                    map.put(GlobalConstant.off_days, objArr.getString(GlobalConstant.off_days));
                                    map.put(GlobalConstant.distance, objArr.getString(GlobalConstant.distance));
                                    map.put(GlobalConstant.favorite, objArr.getString(GlobalConstant.favorite));
                                    map.put(GlobalConstant.rating, objArr.getString(GlobalConstant.rating));
                                    map.put(GlobalConstant.average_rating, objArr.getString(GlobalConstant.average_rating));
                                    map.put(GlobalConstant.latitude, objArr.getString(GlobalConstant.latitude));
                                    map.put(GlobalConstant.longitude, objArr.getString(GlobalConstant.longitude));
                                    JSONArray servicesArr = objArr.getJSONArray("services");
                                    for (int j = 0; j < servicesArr.length(); j++) {
                                        JSONObject serviceObj = servicesArr.getJSONObject(j);
                                        HashMap<String, String> serviceMap = new HashMap<>();
                                        serviceMap.put(GlobalConstant.dm_sub_category_id, serviceObj.getString(GlobalConstant.dm_sub_category_id));
                                        serviceMap.put(GlobalConstant.price, serviceObj.getString(GlobalConstant.price));
                                        serviceList.add(serviceMap);
                                    }


                                    map.put(GlobalConstant.service, serviceList.toString());
                                    serviceList.clear();
                                    list.add(map);
                                }
                                global.setDateList(list);
                                Log.e("tech array", list.toString());
                                eventLocOnMap();
                            } else {
                                Toast.makeText(MapsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.category_id, getIntent().getExtras().getString("device_id"));
                params.put(GlobalConstant.device_model_id, getIntent().getExtras().getString("id"));

                params.put(GlobalConstant.dm_sub_category_ids, getIntent().getExtras().getString("selected_id"));

                params.put(GlobalConstant.latitude, lat);
                params.put(GlobalConstant.longitude, lng);


                Log.e("Parameter for search", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

}
