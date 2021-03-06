package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
 * Created by worksdelight on 14/04/17.
 */

public class TechniciansDevice extends Activity {
    TextView device_txtView, types_txtView;
    ListView device_listView/*, types_listView*/;
    // int imgArray[] = {R.drawable.apple_big_logo, R.drawable.android_logo, R.drawable.windows_logo, R.drawable.tablet_logo, R.drawable.portable_logo, R.drawable.game_logo};
    RelativeLayout type_view_include;
    //String txtArray[] = {"Apple Device", "Android Device", "Window Device", "Tablet Device", "Portable Device", "Game Console"};
    ImageView back;
    Dialog dialog2;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    ArrayList<HashMap<String, String>> nextList = new ArrayList<>();
    Global global;
    TextView done,device_name;
    public TourGuide mTutorialHandler, mTutorialHandler2;
SharedPreferences sp;
    SharedPreferences.Editor ed;
    AlertDialog builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);
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
        done = (TextView) findViewById(R.id.done);
        device_name=(TextView)findViewById(R.id.device_name);
        device_name.setText(getResources().getString(R.string.add_device));
        //device_txtView = (TextView) findViewById(R.id.device_txtView);
        // types_txtView = (TextView) findViewById(R.id.types_txtView);
        device_listView = (ListView) findViewById(R.id.device_listView);
        //types_listView = (ListView) findViewById(R.id.types_listView);
      //  type_view_include = (RelativeLayout) findViewById(R.id.type_view_include);
        back = (ImageView) findViewById(R.id.back);

        //device_txtView.setOnClickListener(this);
        //types_txtView.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(sp.getString("type","").equalsIgnoreCase("login")){
            back.setVisibility(View.VISIBLE);
            done.setVisibility(View.GONE);
        }else{
            back.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed.putString("type","login");
                ed.commit();
                Intent i=new Intent(TechniciansDevice.this,TechniciansHomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        dialogWindow();
        categoryMethod();
        device_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(sp.getString("techDevice","").equalsIgnoreCase("")) {
                    if (i == 0) {
                        mTutorialHandler.cleanUp();
                        if (list.get(i).get(GlobalConstant.value).equalsIgnoreCase("true")) {
                            Intent iPhone = new Intent(TechniciansDevice.this, TechniciansShowDeviceActivity.class);
                            iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                            iPhone.putExtra("id", list.get(i).get(GlobalConstant.id));
                            iPhone.putExtra(GlobalConstant.sub_category_id, "1");

                            global.setDeviceId(list.get(i).get(GlobalConstant.category_id));
                            startActivityForResult(iPhone, 0);
                        } else {
                            Intent iPhone = new Intent(TechniciansDevice.this, TechniciansOtherDeviceActivity.class);
                            iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                            iPhone.putExtra("id", list.get(i).get(GlobalConstant.category_id));
                            global.setDeviceId(list.get(i).get(GlobalConstant.category_id));

                            startActivityForResult(iPhone,0);
                        }
                    }
                }else{
                    if (list.get(i).get(GlobalConstant.value).equalsIgnoreCase("true")) {
                        Intent iPhone = new Intent(TechniciansDevice.this, TechniciansShowDeviceActivity.class);
                        iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                        iPhone.putExtra("id", list.get(i).get(GlobalConstant.id));
                        iPhone.putExtra(GlobalConstant.sub_category_id, "1");

                        global.setDeviceId(list.get(i).get(GlobalConstant.category_id));
                        startActivityForResult(iPhone, 0);
                    } else {
                        Intent iPhone = new Intent(TechniciansDevice.this, TechniciansOtherDeviceActivity.class);
                        iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                        iPhone.putExtra("id", list.get(i).get(GlobalConstant.category_id));
                        global.setDeviceId(list.get(i).get(GlobalConstant.category_id));

                        startActivityForResult(iPhone,0);
                    }
                }



            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            ed.putString("techDevice","1");
            ed.commit();

            list.clear();

            dialogWindow();
            categoryMethod();
        } else {

        }
    }

    //--------------------Category api method---------------------------------
    private void categoryMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.CATEGORY_URL + "?user_id=" + CommonUtils.UserID(TechniciansDevice.this),
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
                                global.setOtherData(data);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject arryObj = data.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(GlobalConstant.id, arryObj.getString(GlobalConstant.id));
                                    map.put(GlobalConstant.name, arryObj.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.icon, arryObj.getString(GlobalConstant.icon));
                                    map.put(GlobalConstant.category_id, arryObj.getString(GlobalConstant.category_id));
                                    map.put(GlobalConstant.status, arryObj.getString(GlobalConstant.status));
                                    map.put(GlobalConstant.services_count, arryObj.getString(GlobalConstant.services_count));

                                    if (arryObj.has(GlobalConstant.sub_category_id)) {
                                        map.put(GlobalConstant.value, "true");
                                    } else {
                                        map.put(GlobalConstant.value, "false");

                                    }
                                    /*JSONArray sub_categories = arryObj.getJSONArray(GlobalConstant.sub_categories);
                                    if (sub_categories.length() > 0) {
                                        for (int k = 0; k < sub_categories.length(); k++) {
                                            JSONObject sub_categoriesObj = sub_categories.getJSONObject(k);

                                            HashMap<String, String> sub_categoriesMap = new HashMap<>();
                                            sub_categoriesMap.put(GlobalConstant.id, sub_categoriesObj.getString(GlobalConstant.id));
                                            sub_categoriesMap.put(GlobalConstant.category_id, sub_categoriesObj.getString(GlobalConstant.category_id));
                                            sub_categoriesMap.put(GlobalConstant.sub_category, sub_categoriesObj.getString(GlobalConstant.sub_category));
                                            sub_categoriesMap.put(GlobalConstant.icon, sub_categoriesObj.getString(GlobalConstant.icon));
                                            nextList.add(sub_categoriesMap);
                                        }
                                        map.put(GlobalConstant.sub_categories, nextList.toString());
                                        nextList.clear();
                                    } else {
                                        map.put(GlobalConstant.sub_categories, nextList.toString());
                                    }
*/
                                    list.add(map);
                                }
                                global.setOtherDeviceList(list);
                                Log.e("device_list", list.toString());
                                device_listView.setAdapter(new DeviceAdapter(TechniciansDevice.this, list));
                                CommonUtils.getListViewSize(device_listView);

                            } else {
                                Toast.makeText(TechniciansDevice.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                view = inflator.inflate(R.layout.tecnicians_device_item, null);
                holder.device_image = (ImageView) view.findViewById(R.id.device_icon);
                holder.device_name = (TextView) view.findViewById(R.id.device_name);
                holder.device_count = (TextView) view.findViewById(R.id.device_count);
                holder.select_img = (ImageView) view.findViewById(R.id.select_img);
                holder.unselect_img = (ImageView) view.findViewById(R.id.unselect_img);
                holder.device_view=(RelativeLayout) view.findViewById(R.id.device_view);
                holder.select_img.setTag(holder);
                holder.unselect_img.setTag(holder);
                view.setTag(holder);

               if(sp.getString("techDevice","").equalsIgnoreCase("")){
                    if(i==0){
                        Animation enterAnimation = new AlphaAnimation(0f, 1f);
                        enterAnimation.setDuration(600);
                        enterAnimation.setFillAfter(true);

                        Animation exitAnimation = new AlphaAnimation(1f, 0f);
                        exitAnimation.setDuration(600);
                        exitAnimation.setFillAfter(true);


                        mTutorialHandler = TourGuide.init(TechniciansDevice.this).with(TourGuide.Technique.Click)
                                .setPointer(new Pointer().setColor(getResources().getColor(R.color.main_color)))
                                .setToolTip(new ToolTip()

                                        .setDescription(getResources().getString(R.string.add_all_device))
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
                        mTutorialHandler.playOn(holder.device_view);

                    }
               }
            } else {
                holder = (Holder) view.getTag();

            }


            url = GlobalConstant.IMAGE_URL + deviceList.get(i).get(GlobalConstant.id) + "/" + deviceList.get(i).get(GlobalConstant.icon);
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

            if (deviceList.get(i).get(GlobalConstant.status).equalsIgnoreCase("1")) {
                holder.select_img.setVisibility(View.GONE);
                holder.unselect_img.setVisibility(View.VISIBLE);
                holder.device_count.setText(deviceList.get(i).get(GlobalConstant.services_count) + " "+getResources().getString(R.string.device_added));
            } else {

                holder.select_img.setVisibility(View.VISIBLE);
                holder.unselect_img.setVisibility(View.GONE);
                holder.device_count.setText(getResources().getString(R.string.device_added));
                holder.device_count.setTextColor(Color.parseColor("#ff0000"));
            }
            holder.device_name.setText(deviceList.get(i).get(GlobalConstant.name));
            holder.select_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  /*  if (serviceID.equalsIgnoreCase("")) {
                        serviceID = deviceList.get(i).get(GlobalConstant.id);
                    } else {
                        if (!serviceID.contains(deviceList.get(i).get(GlobalConstant.id))) {
                            serviceID = serviceID + "," + deviceList.get(i).get(GlobalConstant.id);
                        }
                    }
                    Log.e("service id minus",serviceID);*/

                }
            });
            holder.unselect_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   /* String id = String.valueOf(serviceID.charAt(0));

                    if (serviceID.contains(",")) {
                        if (id.equalsIgnoreCase(deviceList.get(i).get(GlobalConstant.id))) {
                            serviceID = serviceID.replace(deviceList.get(i).get(GlobalConstant.id) + ",", "");

                        } else {
                            serviceID = serviceID.replace("," + deviceList.get(i).get(GlobalConstant.id), "");

                        }
                    } else {
                        serviceID = "";

                    }
                    Log.e("service id",serviceID);*/

                }
            });
            holder.device_name.setText(deviceList.get(i).get(GlobalConstant.name));
            return view;
        }

        class Holder {
            ImageView device_image, select_img, unselect_img;
            TextView device_name, device_count;
            RelativeLayout device_view;
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

}
