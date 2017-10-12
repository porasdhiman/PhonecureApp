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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.worksdelight.phonecure.GlobalConstant.favorite;
import static com.worksdelight.phonecure.R.id.setting_layout;


/**
 * Created by worksdelight on 01/03/17.
 */

public class RepairActivity extends Activity {
    ListView repair_listView;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    TextView top_txt;
    Global global;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    ImageView back, map_img;
    Dialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iphone_repaire_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        back = (ImageView) findViewById(R.id.back);
        map_img = (ImageView) findViewById(R.id.map_img);
        repair_listView = (ListView) findViewById(R.id.repair_listView);

        top_txt = (TextView) findViewById(R.id.top_txt);
        top_txt.setVisibility(View.GONE);
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        repair_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                global.setEstimated_travel_time(global.getDateList().get(i).get(GlobalConstant.estimated_travel_time));

                Intent intent = new Intent(RepairActivity.this, TechniciansDetailActivity.class);
                intent.putExtra("pos", String.valueOf(i));
                //intent.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                intent.putExtra("type", "0");
                startActivity(intent);
            }
        });
        dialogWindow();
        SearchMethod();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        map_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (global.getDateList().size() > 0) {

                        Intent i = new Intent(RepairActivity.this, MapBoxActivity.class);

                        //i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                        startActivity(i);


                    } else {
                        Toast.makeText(RepairActivity.this, "Technicians not Available", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception c) {

                }
            }
        });
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
                                    map.put(GlobalConstant.average_rating_count, objArr.getString(GlobalConstant.average_rating_count));
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
                                if (list.size() > 0) {
                                    repair_listView.setAdapter(new RepairAdapter(RepairActivity.this, list));
                                }

                            } else {
                                Toast.makeText(RepairActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put(GlobalConstant.USERID, CommonUtils.UserID(RepairActivity.this));
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

    //---------------------Adapter class-----------------------
    class RepairAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        RepairAdapter(Context c, ArrayList<HashMap<String, String>> list) {
            this.c = c;
            this.list = list;
            inflatore = LayoutInflater.from(c);
        }

        @Override
        public int getViewTypeCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return list.size();
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
            if (view == null) {
                holder = new Holder();

                view = inflatore.inflate(R.layout.repair_list_item, null);
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.location = (TextView) view.findViewById(R.id.location);

                holder.specilist = (TextView) view.findViewById(R.id.specilist);

                holder.rating_value = (TextView) view.findViewById(R.id.rating_value);
                holder.tech_view = (CircleImageView) view.findViewById(R.id.tech_view);


                holder.status_time = (TextView) view.findViewById(R.id.status_time);
                holder.estimated_time = (TextView) view.findViewById(R.id.estimated_time);
                holder.star1=(ImageView)view.findViewById(R.id.star1);
                holder.star2=(ImageView)view.findViewById(R.id.star2);
                holder.star3=(ImageView)view.findViewById(R.id.star3);
                holder.star4=(ImageView)view.findViewById(R.id.star4);
                holder.star5=(ImageView)view.findViewById(R.id.star5);
                // holder.setting_layout = (LinearLayout) view.findViewById(setting_layout);
                // holder.setting_call_layout = (LinearLayout) view.findViewById(R.id.setting_call_layout);

                view.setTag(holder);
                // holder.setting_layout.setTag(holder);

                //  holder.setting_call_layout.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }
            holder.estimated_time.setText(getDurationString(Integer.parseInt(list.get(i).get(GlobalConstant.estimated_travel_time))));
            holder.status_time.setText("From "+list.get(i).get(GlobalConstant.opening_time));
            holder.name.setText(list.get(i).get(GlobalConstant.name));
            holder.location.setText(list.get(i).get(GlobalConstant.distance) + "Km");
            holder.rating_value.setText(list.get(i).get(GlobalConstant.average_rating_count));
            // holder.specilist.setText(list.get(i).get(GlobalConstant.average_rating));
            String url = GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(i).get(GlobalConstant.image);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, holder.tech_view, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                holder.tech_view.setImageResource(R.drawable.user_back);
            }

            if (list.get(i).get("average_rating_count").contains("1")) {
                holder.star1.setImageResource(R.drawable.star_fill);
                holder.star2.setImageResource(R.drawable.green_star);
                holder.star3.setImageResource(R.drawable.green_star);
                holder.star4.setImageResource(R.drawable.green_star);
                holder.star5.setImageResource(R.drawable.green_star);
            } else if (list.get(i).get("average_rating_count").contains("2")) {
                holder.star1.setImageResource(R.drawable.star_fill);
                holder.star2.setImageResource(R.drawable.star_fill);
                holder.star3.setImageResource(R.drawable.green_star);
                holder.star4.setImageResource(R.drawable.green_star);
                holder.star5.setImageResource(R.drawable.green_star);
            } else if (list.get(i).get("average_rating_count").contains("3")) {
                holder.star1.setImageResource(R.drawable.star_fill);
                holder.star2.setImageResource(R.drawable.star_fill);
                holder.star3.setImageResource(R.drawable.star_fill);
                holder.star4.setImageResource(R.drawable.green_star);
                holder.star5.setImageResource(R.drawable.green_star);
            } else if (list.get(i).get("average_rating_count").contains("4")) {
                holder.star1.setImageResource(R.drawable.star_fill);
                holder.star2.setImageResource(R.drawable.star_fill);
                holder.star3.setImageResource(R.drawable.star_fill);
                holder.star4.setImageResource(R.drawable.star_fill);
                holder.star5.setImageResource(R.drawable.green_star);
            } else if (list.get(i).get("average_rating_count").contains("5")) {
                holder.star1.setImageResource(R.drawable.star_fill);
                holder.star2.setImageResource(R.drawable.star_fill);
                holder.star3.setImageResource(R.drawable.star_fill);
                holder.star4.setImageResource(R.drawable.star_fill);
                holder.star5.setImageResource(R.drawable.star_fill);
            }else{
                holder.star1.setImageResource(R.drawable.green_star);
                holder.star2.setImageResource(R.drawable.green_star);
                holder.star3.setImageResource(R.drawable.green_star);
                holder.star4.setImageResource(R.drawable.green_star);
                holder.star5.setImageResource(R.drawable.green_star);
            }
            return view;
        }

        public class Holder {
            ImageView cancel, chat, message, call, star1, star2, star3, star4, star5;
            LinearLayout setting_layout, setting_call_layout;
            CircleImageView tech_view;
            TextView name, location, specilist, rating_value, status_time, estimated_time;

        }
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

    //--------------calculate estimate time--------------
    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number % 10 == 0) {
            return "" + number;
        }

        return String.valueOf(number);
    }
}
