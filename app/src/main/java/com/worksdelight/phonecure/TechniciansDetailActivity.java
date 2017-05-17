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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by worksdelight on 02/03/17.
 */

public class TechniciansDetailActivity extends Activity {
    CircleImageView user_view;
    ImageView favorite_img;
    TextView tech_name, rating_value, book_appointment;
    Global global;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    boolean isFavorite = false;
    Dialog dialog2;
    int pos;
    String favorite;
ImageView back;
    TextView repair_on_location,repair_at_shop,total_job,total_review;
    LinearLayout sun_layout,mon_layout,tue_layout,wed_layout,thu_layout,fri_layout,sat_layout,working_layout;
    TextView sun_txt,mon_txt,tue_txt,wed_txt,thu_txt,fri_txt,sat_txt;

    TextView sun_time,mon_time,tue_time,wed_time,thu_time,fri_time,sat_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.technicians_detail_layout_second);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        init();

    }

    public void init() {
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        repair_at_shop = (TextView) findViewById(R.id.repair_at_shop);
        repair_on_location = (TextView) findViewById(R.id.repair_on_location);
        total_job= (TextView) findViewById(R.id.total_job);
        total_review = (TextView) findViewById(R.id.total_review);
        user_view = (CircleImageView) findViewById(R.id.user_view);
        favorite_img = (ImageView) findViewById(R.id.favorite_img);
        tech_name = (TextView) findViewById(R.id.tech_name);
        rating_value = (TextView) findViewById(R.id.rating_value);
        book_appointment = (TextView) findViewById(R.id.book_appointment);
        pos = Integer.parseInt(getIntent().getExtras().getString("pos"));
        workingHoursShowMehod(pos);
        if(getIntent().getExtras().getString("type").equalsIgnoreCase("0")) {
            book_appointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TechniciansDetailActivity.this, BookAppoinmentActivity.class);
                    intent.putExtra("pos", String.valueOf(pos));
                  //  intent.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                    startActivity(intent);
                }
            });
            favorite_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isFavorite) {
                        favorite = "0";
                        dialogWindow();
                        favoriteMethod();
                    } else {
                        favorite = "1";
                        dialogWindow();
                        favoriteMethod();
                    }
                }
            });
        }else{
            book_appointment.setVisibility(View.GONE);
        }
        if(global.getDateList().get(pos).get(GlobalConstant.repair_at_shop).equalsIgnoreCase("1")){
            repair_at_shop.setText("Yes");
        }else{
            repair_at_shop.setText("No");
        }
        if(global.getDateList().get(pos).get(GlobalConstant.repair_on_location).equalsIgnoreCase("1")){
            repair_on_location.setText("Yes");
        }else{
            repair_on_location.setText("No");
        }
        total_job.setText(global.getDateList().get(pos).get(GlobalConstant.total_bookings));
        total_review.setText(global.getDateList().get(pos).get(GlobalConstant.reviews));

        tech_name.setText(cap(global.getDateList().get(pos).get(GlobalConstant.name)));
        rating_value.setText(global.getDateList().get(pos).get(GlobalConstant.average_rating));
        String url = GlobalConstant.TECHNICIANS_IMAGE_URL + global.getDateList().get(pos).get(GlobalConstant.image);
        if (url != null && !url.equalsIgnoreCase("null")
                && !url.equalsIgnoreCase("")) {
            imageLoader.displayImage(url, user_view, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);

                        }
                    });
        } else {
            user_view.setImageResource(R.drawable.user_back);
        }
        wishMethod();

    }
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
    public void wishMethod() {
        if (global.getDateList().get(pos).get(GlobalConstant.favorite).equalsIgnoreCase("0")) {
            favorite_img.setImageResource(R.drawable.heart_img);
            isFavorite = false;
        } else {
            favorite_img.setImageResource(R.drawable.heart_white);
            isFavorite = true;
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

    //--------------------search api method---------------------------------
    private void favoriteMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.WISHLIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("responsefff", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                global.getDateList().get(pos).put(GlobalConstant.favorite, favorite);
                                wishMethod();
                            } else {
                                Toast.makeText(TechniciansDetailActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.USERID, CommonUtils.UserID(TechniciansDetailActivity.this));
                params.put(GlobalConstant.favorite_user_id, global.getDateList().get(pos).get(GlobalConstant.id));

                params.put(GlobalConstant.favorite, favorite);


                Log.e("Parameter for wish list", params.toString());
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

    public void workingHoursShowMehod(int p){
        working_layout=(LinearLayout)findViewById(R.id.working_layout);
        sun_layout=(LinearLayout)findViewById(R.id.sun_layout);
        mon_layout=(LinearLayout)findViewById(R.id.mon_layout);
        tue_layout=(LinearLayout)findViewById(R.id.tue_layout);
        wed_layout=(LinearLayout)findViewById(R.id.wed_layout);
        thu_layout=(LinearLayout)findViewById(R.id.thu_layout);
        fri_layout=(LinearLayout)findViewById(R.id.fri_layout);
        sat_layout=(LinearLayout)findViewById(R.id.sat_layout);

        sun_txt=(TextView)findViewById(R.id.sun_txt);
        mon_txt=(TextView)findViewById(R.id.mon_txt);
        tue_txt=(TextView)findViewById(R.id.tue_txt);
        wed_txt=(TextView)findViewById(R.id.wed_txt);
        thu_txt=(TextView)findViewById(R.id.thu_txt);
        fri_txt=(TextView)findViewById(R.id.fri_txt);
        sat_txt=(TextView)findViewById(R.id.sat_txt);

        sun_time=(TextView)findViewById(R.id.sun_time);
        mon_time=(TextView)findViewById(R.id.mon_time);
        tue_time=(TextView)findViewById(R.id.tue_time);
        wed_time=(TextView)findViewById(R.id.wed_time);
        thu_time=(TextView)findViewById(R.id.thu_time);
        fri_time=(TextView)findViewById(R.id.fri_time);
        sat_time=(TextView)findViewById(R.id.sat_time);

        try {
            JSONObject obj=global.getCartData().getJSONObject(p);
            JSONArray avail_arr=obj.getJSONArray(GlobalConstant.availability);
            if(avail_arr.length()==0){
                working_layout.setVisibility(View.GONE);
            }else{
                JSONObject sun_obj=avail_arr.getJSONObject(0);
                if(sun_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    sun_txt.setText(cap(sun_obj.getString(GlobalConstant.day)));
                    sun_time.setText(sun_obj.getString(GlobalConstant.opening_time)+":"+sun_obj.getString(GlobalConstant.closing_time));
                }else{
                    sun_txt.setText(cap(sun_obj.getString(GlobalConstant.day)));
                    sun_time.setText(cap(sun_obj.getString(GlobalConstant.status)));
                }

                JSONObject mon_obj=avail_arr.getJSONObject(1);
                if(mon_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    mon_txt.setText(cap(mon_obj.getString(GlobalConstant.day)));
                    mon_time.setText(mon_obj.getString(GlobalConstant.opening_time)+":"+mon_obj.getString(GlobalConstant.closing_time));
                }else{
                    mon_txt.setText(cap(mon_obj.getString(GlobalConstant.day)));
                    mon_time.setText(cap(mon_obj.getString(GlobalConstant.status)));
                }

                JSONObject tue_obj=avail_arr.getJSONObject(2);
                if(tue_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    tue_txt.setText(cap(tue_obj.getString(GlobalConstant.day)));
                    tue_time.setText(tue_obj.getString(GlobalConstant.opening_time)+":"+tue_obj.getString(GlobalConstant.closing_time));
                }else{
                    tue_txt.setText(cap(tue_obj.getString(GlobalConstant.day)));
                    tue_time.setText(cap(tue_obj.getString(GlobalConstant.status)));
                }

                JSONObject wed_obj=avail_arr.getJSONObject(3);
                if(wed_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    wed_txt.setText(cap(wed_obj.getString(GlobalConstant.day)));
                    wed_time.setText(wed_obj.getString(GlobalConstant.opening_time)+":"+wed_obj.getString(GlobalConstant.closing_time));
                }else{
                    wed_txt.setText(cap(wed_obj.getString(GlobalConstant.day)));
                    wed_time.setText(cap(wed_obj.getString(GlobalConstant.status)));
                }

                JSONObject thu_obj=avail_arr.getJSONObject(4);
                if(thu_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    thu_txt.setText(cap(thu_obj.getString(GlobalConstant.day)));
                    thu_time.setText(thu_obj.getString(GlobalConstant.opening_time)+":"+thu_obj.getString(GlobalConstant.closing_time));
                }else{
                    thu_txt.setText(cap(thu_obj.getString(GlobalConstant.day)));
                    thu_time.setText(cap(thu_obj.getString(GlobalConstant.status)));
                }

                JSONObject fri_obj=avail_arr.getJSONObject(5);
                if(fri_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    fri_txt.setText(cap(fri_obj.getString(GlobalConstant.day)));
                    fri_time.setText(fri_obj.getString(GlobalConstant.opening_time)+":"+fri_obj.getString(GlobalConstant.closing_time));
                }else{
                    fri_txt.setText(cap(fri_obj.getString(GlobalConstant.day)));
                    fri_time.setText(cap(fri_obj.getString(GlobalConstant.status)));
                }

                JSONObject sat_obj=avail_arr.getJSONObject(6);
                if(sat_obj.getString(GlobalConstant.status).equalsIgnoreCase("open")){
                    sat_txt.setText(cap(sat_obj.getString(GlobalConstant.day)));
                    sat_time.setText(sat_obj.getString(GlobalConstant.opening_time)+":"+sat_obj.getString(GlobalConstant.closing_time));
                }else{
                    sat_txt.setText(cap(sat_obj.getString(GlobalConstant.day)));
                    sat_time.setText(cap(sat_obj.getString(GlobalConstant.status)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
