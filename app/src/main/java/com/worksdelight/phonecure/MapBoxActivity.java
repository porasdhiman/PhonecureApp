package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.worksdelight.phonecure.GlobalConstant.favorite;

/**
 * Created by worksdelight on 30/03/17.
 */

public class MapBoxActivity extends Activity {
    MapView mapView;
    ImageView search_img, back;
    Dialog dialog2;
    LinearLayout book_layout;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    final HashMap<String, Integer> map = new HashMap<>();
    TextView average_rating_txt, technicians_name_txtView, book_appointment;
    int pos;
    Global global;
    CircleImageView tech_img;
    MapboxMap mapboxMapnew;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    ArrayList<HashMap<String, String>> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");
        setContentView(R.layout.mapbox_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        technicians_name_txtView = (TextView) findViewById(R.id.technicians_name_txtView);
        average_rating_txt = (TextView) findViewById(R.id.average_rating_txt);
        book_appointment = (TextView) findViewById(R.id.book_appointment);
        tech_img = (CircleImageView) findViewById(R.id.tech_img);
        search_img = (ImageView) findViewById(R.id.search_img);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapBoxActivity.this, RepairActivity.class);

                i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                startActivity(i);
            }
        });
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.setDateList(null);
                finish();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

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
        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        MarkerViewOptions opt = new MarkerViewOptions();

        dialogWindow();
        SearchMethod();

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
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
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void eventLocOnMap() {


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.removeAnnotations();
                List<BaseMarkerOptions> markers = new ArrayList<>();

                for (int i = 0; i < global.getDateList().size(); i++) {
                    Double lat = Double.parseDouble(global.getDateList().get(i).get(GlobalConstant.latitude));
                    Double longt = Double.parseDouble(global.getDateList().get(i).get(GlobalConstant.longitude));
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(lat, longt))
                            .zoom(10)
                            .build());
                    markers.add(new MarkerOptions()
                            .position(new LatLng(lat, longt))
                            .title(global.getDateList().get(i).get(GlobalConstant.name)));


                    map.put(String.valueOf(i), i);

                }
                mapboxMap.addMarkers(markers);
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {
                        Log.e("marker id", String.valueOf(marker.getId()));
                        pos = map.get(String.valueOf(marker.getId()));

                        book_layout.setVisibility(View.VISIBLE);

                        technicians_name_txtView.setText(cap(global.getDateList().get(pos).get(GlobalConstant.name)));


                        average_rating_txt.setText(global.getDateList().get(pos).get(GlobalConstant.average_rating));

                        String url = GlobalConstant.TECHNICIANS_IMAGE_URL + global.getDateList().get(pos).get(GlobalConstant.image);
                        if (url != null && !url.equalsIgnoreCase("null")
                                && !url.equalsIgnoreCase("")) {
                            imageLoader.displayImage(url, tech_img, options,
                                    new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage) {
                                            super.onLoadingComplete(imageUri, view,
                                                    loadedImage);

                                        }
                                    });
                        } else {
                            tech_img.setImageResource(R.drawable.user_back);
                        }
                        book_appointment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(MapBoxActivity.this, BookAppoinmentActivity.class);
                                i.putExtra("pos", String.valueOf(pos));
                                i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                                startActivity(i);

                            }
                        });
                        return false;
                    }
                });
            }
        });
        Log.e("hash map value", map.toString());
        //  openMarkerView();


    }


    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
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
                                if (arr.length() == 0) {
                                    mapView.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(MapboxMap mapboxMap) {
                                            Double lat = Double.parseDouble(global.getLat());
                                            Double longt = Double.parseDouble(global.getLong());
                                            mapboxMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, longt)));


                                        }
                                    });
                                } else {
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject objArr = arr.getJSONObject(i);
                                        HashMap<String, String> map = new HashMap<>();

                                        map.put(GlobalConstant.id, objArr.getString(GlobalConstant.id));
                                        map.put(GlobalConstant.name, objArr.getString(GlobalConstant.name));
                                        map.put(GlobalConstant.image, objArr.getString(GlobalConstant.image));

                                        map.put(GlobalConstant.availability, objArr.getString(GlobalConstant.availability));
                                        map.put(GlobalConstant.off_days, objArr.getString(GlobalConstant.off_days));
                                        map.put(GlobalConstant.distance, objArr.getString(GlobalConstant.distance));

                                        map.put(GlobalConstant.opening_time, objArr.getString(GlobalConstant.opening_time));
                                        map.put(GlobalConstant.closing_time, objArr.getString(GlobalConstant.closing_time));

                                        map.put(favorite, objArr.getString(favorite));
                                        map.put(GlobalConstant.rating, objArr.getString(GlobalConstant.rating));
                                        map.put(GlobalConstant.average_rating, objArr.getString(GlobalConstant.average_rating));
                                        map.put(GlobalConstant.latitude, objArr.getString(GlobalConstant.latitude));
                                        map.put(GlobalConstant.longitude, objArr.getString(GlobalConstant.longitude));
                                        JSONArray servicesArr = objArr.getJSONArray("services");
                                        for (int j = 0; j < servicesArr.length(); j++) {
                                            JSONObject serviceObj = servicesArr.getJSONObject(j);
                                            HashMap<String, String> serviceMap = new HashMap<>();
                                            serviceMap.put(GlobalConstant.dm_sub_category_id, serviceObj.getString(GlobalConstant.dm_sub_category_id));
                                            if (serviceObj.getString(GlobalConstant.price).contains(".")) {

                                                serviceMap.put(GlobalConstant.price, String.valueOf(Double.valueOf(serviceObj.getString(GlobalConstant.price)).intValue()));

                                            } else {
                                                serviceMap.put(GlobalConstant.price, serviceObj.getString(GlobalConstant.price));

                                            }
                                            serviceList.add(serviceMap);
                                        }


                                        map.put(GlobalConstant.service, serviceList.toString());


                                        serviceList.clear();
                                        list.add(map);
                                    }
                                    global.setDateList(list);
                                    Log.e("tech array", list.toString());
                                    eventLocOnMap();
                                }
                            } else {
                                Toast.makeText(MapBoxActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());


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
