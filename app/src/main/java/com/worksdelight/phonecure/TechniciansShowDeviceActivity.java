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
import android.widget.GridView;
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

import java.util.ArrayList;
import java.util.HashMap;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by worksdelight on 15/04/17.
 */

public class TechniciansShowDeviceActivity extends Activity {
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    Dialog dialog2;
    DisplayImageOptions options;
    GridView device_view;
    ImageView back;
    ArrayList<HashMap<String, String>> color_list = new ArrayList<>();
    public int lastPos;
    TextView next_txtView;
    int valueof_selected_item = 1, pos;
    ArrayList<String> value = new ArrayList<>();
    Global global;
  TourGuide mTutorialHandler, mTutorialHandler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_device_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        device_view = (GridView) findViewById(R.id.device);
        dialogWindow();
        showDeviceMethod();
        back = (ImageView) findViewById(R.id.back);
        next_txtView = (TextView) findViewById(R.id.next_txtView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        device_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent iPhone = new Intent(TechniciansShowDeviceActivity.this, TechniciansServices.class);
                iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                iPhone.putExtra("device_id", getIntent().getExtras().getString("id"));
                iPhone.putExtra("id", list.get(i).get(GlobalConstant.id));


                global.setPostion(i);

                startActivityForResult(iPhone, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            list.clear();
            dialogWindow();
            showDeviceMethod();
        } else {

        }
    }

    //--------------------Category api method---------------------------------
    private void showDeviceMethod() {
        Log.e("davice_id", String.valueOf(getIntent().getExtras().get("id")));
// Request a string response from the provided URL.
        final String appUrl = GlobalConstant.DEVICE_URL + "category_id=" + global.getDeviceId() + "&" + GlobalConstant.sub_category_id + "=" + getIntent().getExtras().getString(GlobalConstant.sub_category_id) + "&user_id=" + CommonUtils.UserID(TechniciansShowDeviceActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, appUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();
                        Log.e("complete url", appUrl);
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
                                    map.put(GlobalConstant.category_id, arryObj.getString(GlobalConstant.category_id));
                                    map.put(GlobalConstant.sub_category_id, arryObj.getString(GlobalConstant.sub_category_id));
                                    map.put(GlobalConstant.status, arryObj.getString(GlobalConstant.status));

                                    map.put(GlobalConstant.services_count, arryObj.getString(GlobalConstant.services_count));
                                    map.put(GlobalConstant.name, arryObj.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.icon, arryObj.getString(GlobalConstant.icon));

                                    JSONArray color_images = arryObj.getJSONArray(GlobalConstant.color_images);

                                    if (color_images.length() > 0) {
                                        for (int k = 0; k < color_images.length(); k++) {
                                            JSONObject colorObj = color_images.getJSONObject(k);

                                            Log.e("value color", colorObj.getString("color_name"));

                                            HashMap<String, String> ColorMap = new HashMap<>();

                                            ColorMap.put(GlobalConstant.color_id, colorObj.getString(GlobalConstant.color_id));
                                            ColorMap.put(GlobalConstant.model_image, colorObj.getString(GlobalConstant.model_image));
                                            ColorMap.put(GlobalConstant.color_image, colorObj.getString(GlobalConstant.color_image));
                                            // ColorMap.put(GlobalConstant.color_name, colorObj.getString("color_name"));

                                            color_list.add(ColorMap);
                                        }
                                    }
                                    map.put(GlobalConstant.color_images, color_list.toString());
                                    list.add(map);
                                    color_list.clear();
                                }
                                if (list.size() > 0) {
                                    global.setAllDeviceList(list);
                                    Log.e("listing", list.toString());
                                    device_view.setAdapter(new DeviceAdapter(TechniciansShowDeviceActivity.this, list));
                                    Log.e("image name", String.valueOf(convertToHashMap(list.get(0).get(GlobalConstant.color_images))));
                                }
                            } else {
                                Toast.makeText(TechniciansShowDeviceActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
    //------------------------Device adapter--------------------------------

    class DeviceAdapter extends BaseAdapter {
        int j, l = -1;
        Context c;
        LayoutInflater inflator;
        Holder holder = null;
        String url = "";
        String durl = "";

        ArrayList<HashMap<String, String>> deviceList = new ArrayList<>();
        //ArrayList<HashMap<String, String>> color = new ArrayList<>();
        //ArrayList<HashMap<String, String>> model_image = new ArrayList<>();

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
            for (int p = 0; p < deviceList.size(); p++) {
                value.add("false");
            }
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

                view = inflator.inflate(R.layout.show_device_item_layout, null);
                holder.device_image = (ImageView) view.findViewById(R.id.device_item);
                holder.device_name = (TextView) view.findViewById(R.id.device_name);
                holder.color_layout = (LinearLayout) view.findViewById(R.id.color_layout);
                holder.main_layout = (LinearLayout) view.findViewById(R.id.main_layout);
                holder.status = (TextView) view.findViewById(R.id.status);

                holder.device_image.setTag(holder);
                view.setTag(holder);
                if(global.getRegisterTechType().equalsIgnoreCase("0")){
                    if(i==0){
                        Animation enterAnimation = new AlphaAnimation(0f, 1f);
                        enterAnimation.setDuration(600);
                        enterAnimation.setFillAfter(true);

                        Animation exitAnimation = new AlphaAnimation(1f, 0f);
                        exitAnimation.setDuration(600);
                        exitAnimation.setFillAfter(true);


                        mTutorialHandler2 = TourGuide.init(TechniciansShowDeviceActivity.this);
                        mTutorialHandler2.with(TourGuide.Technique.Click)
                                .setPointer(new Pointer().setColor(getResources().getColor(R.color.main_color)))
                                .setToolTip(new ToolTip()

                                        .setDescription("Select device model")
                                        .setGravity(Gravity.BOTTOM).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    mTutorialHandler2.cleanUp();
                                                }catch (Exception c){
                                                    Log.e("exception",c.toString());
                                                }
                                            }
                                        })
                                )
                                .setOverlay(new Overlay()
                                        .setEnterAnimation(enterAnimation).setStyle(Overlay.Style.Rectangle)
                                        .setExitAnimation(exitAnimation).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    mTutorialHandler2.cleanUp();
                                                }catch (Exception c){
                                                    Log.e("exception",c.toString());
                                                }
                                            }
                                        })
                                );
                        mTutorialHandler2.playOn(holder.device_image);

                    }
               }
            } else {
                holder = (Holder) view.getTag();
            }


            holder.color_layout.setVisibility(View.GONE);
            if (deviceList.get(i).get(GlobalConstant.status).equalsIgnoreCase("1")) {
                holder.status.setText(deviceList.get(i).get(GlobalConstant.services_count) + " Services added");
                holder.status.setTextColor(Color.parseColor("#47c63d"));
            } else {
                holder.status.setText("No service added yet");
                holder.status.setTextColor(Color.parseColor("#ff0000"));
            }

            url = GlobalConstant.IMAGE_URL + deviceList.get(i).get(GlobalConstant.category_id) + "/" + deviceList.get(i).get(GlobalConstant.sub_category_id) + "/" + deviceList.get(i).get(GlobalConstant.id) + "/" + deviceList.get(i).get(GlobalConstant.icon);
            Log.e("url", url);
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
            return view;
        }

        class Holder {
            ImageView device_image, v, img1, img2, img3, img4, img5, img6;
            TextView device_name, status;
            LinearLayout color_layout, main_layout;

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

    public ArrayList<HashMap<String, String>> convertToHashMap(String jsonString) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        Log.e("array value", jsonString);
        try {
            JSONArray jArray = new JSONArray(jsonString);
            Log.e("array value", jArray.toString());
            JSONObject jObject = null;
            String keyString = null;
            for (int i = 0; i < jArray.length(); i++) {
                HashMap<String, String> myHashMap = new HashMap<String, String>();
                jObject = jArray.getJSONObject(i);
                // beacuse you have only one key-value pair in each object so I have used index 0
                keyString = (String) jObject.names().get(0);
                myHashMap.put(keyString, jObject.getString(keyString));
                list.add(myHashMap);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<HashMap<String, String>> convertToHashMapForModelImage(String jsonString) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);
            JSONObject jObject = null;
            String keyString = null;
            for (int i = 0; i < jArray.length(); i++) {
                HashMap<String, String> myHashMap = new HashMap<String, String>();
                jObject = jArray.getJSONObject(i);
                // beacuse you have only one key-value pair in each object so I have used index 0
                keyString = (String) jObject.names().get(3);
                myHashMap.put(keyString, jObject.getString(keyString));
                list.add(myHashMap);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
}

