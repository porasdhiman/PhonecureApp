package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

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
    TextView price_txt;
    float f = 0.0f;
    LinearLayout service_type_layout;
    ImageView repair_shop_image, repair_location_image, both_image;
    private Animation mEnterAnimation, mExitAnimation;
    public TourGuide mTutorialHandler, mTutorialHandler2;
    SharedPreferences sp, sp1;
    SharedPreferences.Editor ed, ed1;
ImageView cross_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicG9yYXMiLCJhIjoiY2owdWxrdThlMDR4ODJ3andqam94cm8xMCJ9.q7NNGKPgyZ-Vq1R80eJCxg");
        setContentView(R.layout.mapbox_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp = getSharedPreferences("tour", Context.MODE_PRIVATE);
        ed = sp.edit();
        sp1 = getSharedPreferences("register", Context.MODE_PRIVATE);
        ed1 = sp1.edit();
        global = (Global) getApplicationContext();
        technicians_name_txtView = (TextView) findViewById(R.id.technicians_name_txtView);
        price_txt = (TextView) findViewById(R.id.price_txt);
        average_rating_txt = (TextView) findViewById(R.id.average_rating_txt);
        book_appointment = (TextView) findViewById(R.id.book_appointment);
        tech_img = (CircleImageView) findViewById(R.id.tech_img);
        /*search_img = (ImageView) findViewById(R.id.search_img);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (list.size() > 0) {

                        Intent i = new Intent(MapBoxActivity.this, RepairActivity.class);

                        //i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                        startActivity(i);


                    } else {
                        Toast.makeText(MapBoxActivity.this, "Technicians not Available", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception c) {

                }

            }
        });*/
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
        cross_img=(ImageView)findViewById(R.id.cross_img);
        cross_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book_layout.setVisibility(View.GONE);
            }
        });
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


       /* dialogWindow();
        SearchMethod();*/
        service_type_layout = (LinearLayout) findViewById(R.id.service_type_layout);
        repair_shop_image = (ImageView) findViewById(R.id.repair_shop_image);
        repair_location_image = (ImageView) findViewById(R.id.repair_location_image);
        both_image = (ImageView) findViewById(R.id.both_image);
        if (sp1.getString("type", "").equalsIgnoreCase("register")) {
            service_type_layout.setVisibility(View.VISIBLE);
               /* setup enter and exit animation */
            Animation enterAnimation = new AlphaAnimation(0f, 1f);
            enterAnimation.setDuration(600);
            enterAnimation.setFillAfter(true);

            Animation exitAnimation = new AlphaAnimation(1f, 0f);
            exitAnimation.setDuration(600);
            exitAnimation.setFillAfter(true);

        /* initialize TourGuide without playOn() */
            mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer().setColor(getResources().getColor(R.color.main_color)))
                    .setToolTip(new ToolTip()

                            .setDescription("REPAIR AT TECHNICIAN / SHOP")
                            .setGravity(Gravity.TOP)
                    )
                    .setOverlay(new Overlay()
                            .setEnterAnimation(enterAnimation)
                            .setExitAnimation(exitAnimation)
                    );


            both_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTutorialHandler.cleanUp();
                    service_type_layout.setVisibility(View.GONE);
                    ed1.putString("type", "login");
                    ed1.commit();
                }
            });
            repair_shop_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler.setToolTip(new ToolTip().setDescription("REPAIR ON DESIRED LOCATION").setGravity(Gravity.TOP)).playOn(repair_location_image);
                }
            });
            repair_location_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTutorialHandler.cleanUp();
                    mTutorialHandler.setToolTip(new ToolTip().setDescription("IT’S UP TO YOU, BOTH ARE POSSIBLE").setGravity(Gravity.TOP)).playOn(both_image);
                }
            });
            mTutorialHandler.playOn(repair_shop_image);
        }
        eventLocOnMap();
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



                    if (global.getDateList().get(i).get(GlobalConstant.repair_at_shop).equalsIgnoreCase("1") && global.getDateList().get(i).get(GlobalConstant.repair_on_location).equalsIgnoreCase("1")) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(MapBoxActivity.this, R.drawable.sccoteerhome);
                        Bitmap originalBitmap = bitmapDrawable.getBitmap();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 90, 90, false);
                        IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);
                        Icon icon = iconFactory.fromBitmap(resizedBitmap);

                        markers.add(new MarkerOptions()
                                .position(new LatLng(lat, longt))
                                .icon(icon));
                    } else if (global.getDateList().get(i).get(GlobalConstant.repair_at_shop).equalsIgnoreCase("1")) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(MapBoxActivity.this, R.drawable.home_repair);
                        Bitmap originalBitmap = bitmapDrawable.getBitmap();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 90, 90, false);
                        IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);
                        Icon icon = iconFactory.fromBitmap(resizedBitmap);
                        markers.add(new MarkerOptions()
                                .position(new LatLng(lat, longt))
                                .icon(icon));
                    } else if (global.getDateList().get(i).get(GlobalConstant.repair_on_location).equalsIgnoreCase("1")) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(MapBoxActivity.this, R.drawable.scooter);
                        Bitmap originalBitmap = bitmapDrawable.getBitmap();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 90, 90, false);
                        IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);
                        Icon icon = iconFactory.fromBitmap(resizedBitmap);
                        markers.add(new MarkerOptions()
                                .position(new LatLng(lat, longt))
                                .icon(icon));
                    }


                   /* mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(-33.8500000, 18.4158234))
                            .title("Cape Town Harbour")
                            .snippet("One of the busiest ports in South Africa")
                            .icon(icon));*/
                    if (i == 0) {
                        IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(global.getLat()), Double.parseDouble(global.getLong())))
                                .zoom(10)
                                .build());
                        final Icon icon = iconFactory.fromResource(R.drawable.map_icon);
                        markers.add(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(global.getLat()), Double.parseDouble(global.getLong())))
                                .icon(icon));
                        map.put(String.valueOf(i), i);
                    } else {
                        map.put(String.valueOf(i), i);
                    }


                }
                mapboxMap.addMarkers(markers);
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {
                        Log.e("marker id", String.valueOf(marker.getId()));
                        if (marker.getId() != 0) {
                            long p = marker.getId() - 1;
                            pos = map.get(String.valueOf(p));


                            book_layout.setVisibility(View.VISIBLE);
                            book_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    global.setEstimated_travel_time(global.getDateList().get(pos).get(GlobalConstant.estimated_travel_time));
                                    Intent intent = new Intent(MapBoxActivity.this, TechniciansDetailActivity.class);
                                    intent.putExtra("pos", String.valueOf(pos));
                                    //intent.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                                    intent.putExtra("type", "0");
                                    startActivity(intent);
                                }
                            });
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
                                    global.setEstimated_travel_time(global.getDateList().get(pos).get(GlobalConstant.estimated_travel_time));
                                    Intent i = new Intent(MapBoxActivity.this, BookAppoinmentActivity.class);
                                    i.putExtra("pos", String.valueOf(pos));
                                    // i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));

                                    startActivity(i);


                                }
                            });

                            try {
                                JSONObject obj = global.getCartData().getJSONObject(pos);
                                JSONArray servicesArr = obj.getJSONArray("technician_services");
                                f = 0;
                                for (int j = 0; j < servicesArr.length(); j++) {
                                    JSONObject serviceObj = servicesArr.getJSONObject(j);

                                    f = f + Float.parseFloat(serviceObj.getString(GlobalConstant.price));


                                }
                                price_txt.setText("You will be charged €" + String.valueOf(f));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
                                            IconFactory iconFactory = IconFactory.getInstance(MapBoxActivity.this);

                                            final Icon icon = iconFactory.fromResource(R.drawable.map_icon);
                                            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                                    .target(new LatLng(Double.parseDouble(global.getLat()), Double.parseDouble(global.getLong())))
                                                    .zoom(10)

                                                    .build());


                                            mapboxMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Double.parseDouble(global.getLat()), Double.parseDouble(global.getLong()))).icon(icon));


                                        }
                                    });
                                } else {
                                    global.setCartData(arr);
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject objArr = arr.getJSONObject(i);
                                        HashMap<String, String> map = new HashMap<>();

                                        map.put(GlobalConstant.id, objArr.getString(GlobalConstant.id));
                                        map.put(GlobalConstant.name, objArr.getString(GlobalConstant.name));
                                        map.put(GlobalConstant.image, objArr.getString(GlobalConstant.image));

                                        map.put(GlobalConstant.availability, objArr.getString(GlobalConstant.availability));
                                        map.put(GlobalConstant.off_days, objArr.getString(GlobalConstant.off_days));
                                        map.put(GlobalConstant.distance, objArr.getString(GlobalConstant.distance));
                                        map.put(GlobalConstant.repair_at_shop, objArr.getString(GlobalConstant.repair_at_shop));

                                        map.put(GlobalConstant.repair_on_location, objArr.getString(GlobalConstant.repair_on_location));
                                        map.put(GlobalConstant.total_bookings, objArr.getString(GlobalConstant.total_bookings));
                                        map.put(GlobalConstant.reviews, objArr.getString(GlobalConstant.reviews));
                                        map.put(GlobalConstant.estimated_travel_time, objArr.getString(GlobalConstant.estimated_travel_time));
                                        map.put(GlobalConstant.opening_time, objArr.getString(GlobalConstant.opening_time));
                                        map.put(GlobalConstant.closing_time, objArr.getString(GlobalConstant.closing_time));
                                        map.put(GlobalConstant.other_charges, objArr.getString(GlobalConstant.other_charges));
                                        map.put(GlobalConstant.favorite_count, objArr.getString(GlobalConstant.favorite_count));


                                        map.put(favorite, objArr.getString(favorite));
                                        map.put(GlobalConstant.rating, objArr.getString(GlobalConstant.rating));
                                        map.put(GlobalConstant.average_rating, objArr.getString(GlobalConstant.average_rating));
                                        map.put(GlobalConstant.latitude, objArr.getString(GlobalConstant.address_latitude));
                                        map.put(GlobalConstant.longitude, objArr.getString(GlobalConstant.address_longitude));
                                        /*JSONArray servicesArr = objArr.getJSONArray("technician_services");
                                        for (int j = 0; j < servicesArr.length(); j++) {
                                            JSONObject serviceObj = servicesArr.getJSONObject(j);
                                            HashMap<String, String> serviceMap = new HashMap<>();
                                            serviceMap.put(GlobalConstant.dm_sub_category_id, serviceObj.getString(GlobalConstant.dm_sub_category_id));

                                                serviceMap.put(GlobalConstant.price, serviceObj.getString(GlobalConstant.price));


                                            serviceList.add(serviceMap);
                                        }


                                        map.put(GlobalConstant.service, serviceList.toString());


                                        serviceList.clear();*/
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
                params.put(GlobalConstant.USERID, CommonUtils.UserID(MapBoxActivity.this));
                params.put(GlobalConstant.category_id, getIntent().getExtras().getString("device_id"));
                params.put(GlobalConstant.device_model_id, getIntent().getExtras().getString("id"));

                params.put("dm_service_ids", getIntent().getExtras().getString("selected_id"));

                params.put("model_color", global.getColorId());

                params.put(GlobalConstant.sub_category_id, global.getSubCatId());

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
        loaderView.setIndicatorColor(getResources().getColor(R.color.main_color));
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
