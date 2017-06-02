package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static com.worksdelight.phonecure.GlobalConstant.technician_service_id;
import static com.worksdelight.phonecure.R.id.time_ed;

/**
 * Created by worksdelight on 15/04/17.
 */

public class TechniciansServices extends Activity {
    ListView service_list;
    //  int imgArray[] = {R.drawable.backcover, R.drawable.battey, R.drawable.camera, R.drawable.charger, R.drawable.home_btn, R.drawable.microphone, R.drawable.ios_txt};
    // String txtArray[] = {"Backcover", "Battery", "Front camera", "Dock charger", "Home Button", "Microphone", "Software"};
    ImageView search_img, back;
    ScrollView main_scrollView;
    TextView submit_btn, service_txtView;
    Dialog dialog2;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> color_list = new ArrayList<>();
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    String serviceID = "";
    Global global;
    public TourGuide mTutorialHandler, mTutorialHandler2;
SharedPreferences sp;
    SharedPreferences.Editor ed;
    int p=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iphone_service_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp=getSharedPreferences("register",Context.MODE_PRIVATE);
        ed=sp.edit();
        global = (Global) getApplicationContext();
        init();

    }

    public void init() {
        back = (ImageView) findViewById(R.id.back);
        submit_btn = (TextView) findViewById(R.id.submit_btn);
        service_txtView = (TextView) findViewById(R.id.service_txtView);
        main_scrollView = (ScrollView) findViewById(R.id.main_scrollView);
        service_list = (ListView) findViewById(R.id.service_list);
        submit_btn.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //  search_img = (ImageView) findViewById(R.id.search_img);
        dialogWindow();
        subcategoryMethod();
        service_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(TechniciansServices.this,"service",Toast.LENGTH_SHORT).show();
                if(sp.getString("techService","").equalsIgnoreCase("")) {
                    if (i == 0) {
                        mTutorialHandler.cleanUp();
                        Intent map = new Intent(TechniciansServices.this, TechniciansRegisterProduct.class);
                        map.putExtra(GlobalConstant.id, list.get(i).get(GlobalConstant.id));
                        map.putExtra(GlobalConstant.service_id, list.get(i).get(GlobalConstant.service_id));
                        map.putExtra("device_type", getIntent().getExtras().getString("device_type"));
                        map.putExtra("service name",list.get(i).get(GlobalConstant.name));
                        map.putExtra("pos", String.valueOf(i));

                        startActivityForResult(map, 0);
                    }
                }else{
                    Intent map = new Intent(TechniciansServices.this, TechniciansRegisterProduct.class);
                    map.putExtra(GlobalConstant.id, list.get(i).get(GlobalConstant.id));
                    map.putExtra(GlobalConstant.service_id, list.get(i).get(GlobalConstant.service_id));
                    map.putExtra("device_type", getIntent().getExtras().getString("device_type"));
                    map.putExtra("service name",list.get(i).get(GlobalConstant.name));
                    map.putExtra("pos", String.valueOf(i));

                    startActivityForResult(map, 0);
                }

            }
        });
        submit_btn.setVisibility(View.GONE);
        /*submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceID.equalsIgnoreCase("")){
                    Toast.makeText(TechniciansServices.this,"Please select services",Toast.LENGTH_SHORT).show();
                }else{

                }

            }
        });*/
        service_txtView.setText(getIntent().getExtras().getString("device_type"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        global.setRegisterTechType(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
          ed.putString("techService","1");
            ed.commit();
            list.clear();
            dialogWindow();
            subcategoryMethod();
        } else {

        }
    }

    //--------------------Category api method---------------------------------
    private void subcategoryMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.SUB_CATEGORY_URL + getIntent().getExtras().getString("id") + "&user_id=" + CommonUtils.UserID(this),
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
                                JSONArray data = obj.getJSONArray("data");
                                global.setAar(data);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject arryObj = data.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(GlobalConstant.id, arryObj.getString(GlobalConstant.id));

                                    map.put(GlobalConstant.device_model_id, arryObj.getString(GlobalConstant.device_model_id));
                                    map.put(GlobalConstant.technician_service_id, arryObj.getString(GlobalConstant.technician_service_id));
                                    map.put(GlobalConstant.enabled_status, arryObj.getString(GlobalConstant.enabled_status));


                                    map.put(GlobalConstant.service_id, arryObj.getString(GlobalConstant.service_id));
                                    map.put(GlobalConstant.name, arryObj.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.icon, arryObj.getString(GlobalConstant.icon));
                                    map.put(GlobalConstant.status, arryObj.getString(GlobalConstant.status));
                                    map.put(GlobalConstant.available_colors, arryObj.getString(GlobalConstant.available_colors));
                                    map.put(GlobalConstant.expected_time, arryObj.getString(GlobalConstant.expected_time));
                                    map.put(GlobalConstant.price, arryObj.getString(GlobalConstant.price));
                                   /* JSONArray color_images = arryObj.getJSONArray(GlobalConstant.device_model_colors);
                                    if (color_images.length() > 0) {
                                        for (int k = 0; k < color_images.length(); k++) {
                                            JSONObject colorObj = color_images.getJSONObject(k);
                                            HashMap<String, String> ColorMap = new HashMap<>();
                                            ColorMap.put(GlobalConstant.status, colorObj.getString(GlobalConstant.status));


                                            color_list.add(ColorMap);
                                        }
                                    }*/
                                    //map.put(GlobalConstant.color_images, color_list.toString());
                                    list.add(map);
                                    // color_list.clear();
                                }
                                if (list.size() != 0) {
                                    service_list.setAdapter(new DeviceAdapter(TechniciansServices.this, list));


                                    global.setServiceList(list);
                                }
                            } else {
                                Toast.makeText(TechniciansServices.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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


    //--------------------Adapter class-----------------
    class DeviceAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflator;
        Holder holder = null;
        String url = "";
        ArrayList<HashMap<String, String>> deviceList = new ArrayList<>();


        DeviceAdapter(Context c, ArrayList<HashMap<String, String>> deviceList) {
            this.c = c;
            this.deviceList = deviceList;
            inflator = LayoutInflater.from(c);
            imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)

                    .showStubImage(0)        //	Display Stub Image
                    .showImageForEmptyUri(0)    //	If Empty image found
                    .cacheInMemory()
                    .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
            initImageLoader();

        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return deviceList.size();
        }

        @Override
        public int getCount() {
            return deviceList.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            holder = new Holder();
            if (view == null) {

                view = inflator.inflate(R.layout.device_service_with_price_layout, null);
                holder.device_image = (ImageView) view.findViewById(R.id.device_icon);
                holder.device_name = (TextView) view.findViewById(R.id.device_name);
                holder.price_ed = (TextView) view.findViewById(R.id.price_ed);
                holder.time_ed = (TextView) view.findViewById(time_ed);
                holder.select_img = (ImageView) view.findViewById(R.id.select_img);
                holder.unselect_img = (ImageView) view.findViewById(R.id.unselect_img);
                holder.main_layout=(RelativeLayout)view.findViewById(R.id.main_layout);

                holder.select_img.setTag(holder);
                holder.unselect_img.setTag(holder);
                holder.device_name.setTag(holder);
                holder.main_layout.setTag(holder);
                view.setTag(holder);
                if(sp.getString("techService","").equalsIgnoreCase("")){
                if(i==0){
                    Animation enterAnimation = new AlphaAnimation(0f, 1f);
                    enterAnimation.setDuration(600);
                    enterAnimation.setFillAfter(true);

                    Animation exitAnimation = new AlphaAnimation(1f, 0f);
                    exitAnimation.setDuration(600);
                    exitAnimation.setFillAfter(true);


                    mTutorialHandler = TourGuide.init(TechniciansServices.this).with(TourGuide.Technique.Click)
                            .setPointer(new Pointer().setColor(getResources().getColor(R.color.main_color)))
                            .setToolTip(new ToolTip()

                                    .setDescription("SELECT THE ISSUE(S)")
                                    .setGravity(Gravity.BOTTOM).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            mTutorialHandler.cleanUp();
                                        }
                                    })
                            )
                            .setOverlay(new Overlay()
                                    .setEnterAnimation(enterAnimation).setStyle(Overlay.Style.Rectangle)
                                    .setExitAnimation(exitAnimation).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mTutorialHandler.cleanUp();
                                        }
                                    })
                            );
                    mTutorialHandler.playOn(holder.main_layout);

                }
                 }

            } else {
                holder = (Holder) view.getTag();

            }


            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sp.getString("techService","").equalsIgnoreCase("")) {
                        if (i == 0) {
                            mTutorialHandler.cleanUp();
                            Intent map = new Intent(TechniciansServices.this, TechniciansRegisterProduct.class);
                            map.putExtra(GlobalConstant.id, deviceList.get(i).get(GlobalConstant.id));
                            map.putExtra(GlobalConstant.service_id, deviceList.get(i).get(GlobalConstant.service_id));
                            map.putExtra("device_type", getIntent().getExtras().getString("device_type"));
                            map.putExtra("service name",list.get(i).get(GlobalConstant.name));
                            map.putExtra("pos", String.valueOf(i));
                            startActivityForResult(map, 0);
                        }
                    }else{
                        Intent map = new Intent(TechniciansServices.this, TechniciansRegisterProduct.class);
                        map.putExtra(GlobalConstant.id, deviceList.get(i).get(GlobalConstant.id));
                        map.putExtra(GlobalConstant.service_id, deviceList.get(i).get(GlobalConstant.service_id));
                        map.putExtra("device_type", getIntent().getExtras().getString("device_type"));
                        map.putExtra("service name",list.get(i).get(GlobalConstant.name));
                        map.putExtra("pos", String.valueOf(i));
                        startActivityForResult(map, 0);
                    }

                }
            });
            holder.price_ed.setText("€"+deviceList.get(i).get(GlobalConstant.price));
            holder.time_ed.setText("Estimate time: "+getDurationString(Integer.parseInt(deviceList.get(i).get(GlobalConstant.expected_time)))+" Hours");
            holder.select_img.setImageResource(R.drawable.toggle);
            holder.unselect_img.setImageResource(R.drawable.toogle2);
            if (deviceList.get(i).get(GlobalConstant.status).equalsIgnoreCase("1")) {
                holder.select_img.setVisibility(View.GONE);
                holder.unselect_img.setVisibility(View.VISIBLE);
                p=i;
            } else {

                holder.select_img.setVisibility(View.VISIBLE);
                holder.unselect_img.setVisibility(View.GONE);
                holder.select_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder = (Holder) view.getTag();
                        Intent map = new Intent(TechniciansServices.this, TechniciansRegisterProduct.class);
                        map.putExtra(GlobalConstant.id, deviceList.get(i).get(GlobalConstant.id));
                        map.putExtra(GlobalConstant.service_id, deviceList.get(i).get(GlobalConstant.service_id));
                        map.putExtra("device_type", getIntent().getExtras().getString("device_type"));
                        map.putExtra("service name",list.get(i).get(GlobalConstant.name));
                        map.putExtra("pos", String.valueOf(i));
                        startActivityForResult(map, 0);
                    }
                });
            }
            url = GlobalConstant.SUB_CAETGORY_IMAGE_URL + deviceList.get(i).get(GlobalConstant.icon);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, holder.device_image, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                holder.device_image.setImageResource(0);
            }

            holder.device_name.setText(deviceList.get(i).get(GlobalConstant.name));


                if (deviceList.get(i).get(GlobalConstant.enabled_status).equalsIgnoreCase("1")) {
                    holder.select_img.setVisibility(View.GONE);
                    holder.unselect_img.setVisibility(View.VISIBLE);

                } else {

                    holder.select_img.setVisibility(View.VISIBLE);
                    holder.unselect_img.setVisibility(View.GONE);


                }

            holder.unselect_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.select_img.setVisibility(View.VISIBLE);
                    holder.unselect_img.setVisibility(View.GONE);
                    deviceList.get(i).put(GlobalConstant.enabled_status,"0");
                    enableMethod(deviceList.get(i).get(GlobalConstant.technician_service_id), String.valueOf(i), "0");

                }
            });
            holder.select_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.select_img.setVisibility(View.GONE);
                    holder.unselect_img.setVisibility(View.VISIBLE);
                    deviceList.get(i).put(GlobalConstant.enabled_status,"1");
                    enableMethod(deviceList.get(i).get(GlobalConstant.technician_service_id), String.valueOf(i), "1");
                }
            });



            return view;
        }

        class Holder {
            ImageView device_image, select_img, unselect_img;
            TextView device_name,price_ed,time_ed;
            RelativeLayout main_layout;
        }
    }
    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + ":" + twoDigitString(minutes);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number % 10 == 0) {
            return ""+number;
        }

        return String.valueOf(number);
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
    //--------------------Enable api method---------------------------------

    //--------------------search api method---------------------------------
    private void enableMethod(final String id, final String pos, final String isValue) {
        //Map<String, String> params = new HashMap<String, String>();

        JSONObject params = new JSONObject();

        try {
            params.put(GlobalConstant.USERID, CommonUtils.UserID(TechniciansServices.this));
            params.put(technician_service_id, id);
            params.put("enabled_status", isValue);

            Log.e("params valye", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, GlobalConstant.SERVICE_ENABLE_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("respnse", response.toString());

                        try {


                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                              //  list.get(Integer.parseInt(pos)).put(GlobalConstant.enabled_status, isValue);
                               // service_list.setAdapter(new DeviceAdapter(TechniciansServices.this, list));
                                //service_list.setSelection(p);
                              /*  CommonUtils.getListViewSize(service_list);
                                main_scrollView.smoothScrollTo(0, 0);*/

                            } else {
                                Toast.makeText(TechniciansServices.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }


}
