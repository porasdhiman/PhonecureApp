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

/**
 * Created by worksdelight on 07/03/17.
 */

public class DeviceActivity extends Activity implements View.OnClickListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        init();
    }

    public void init() {
        //device_txtView = (TextView) findViewById(R.id.device_txtView);
        // types_txtView = (TextView) findViewById(R.id.types_txtView);
        device_listView = (ListView) findViewById(R.id.device_listView);
        //types_listView = (ListView) findViewById(R.id.types_listView);
        type_view_include = (RelativeLayout) findViewById(R.id.type_view_include);
        back = (ImageView) findViewById(R.id.back);

        //device_txtView.setOnClickListener(this);
        //types_txtView.setOnClickListener(this);
        back.setOnClickListener(this);
        dialogWindow();
        categoryMethod();
        device_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent iPhone = new Intent(DeviceActivity.this, ShowDeviceActivity.class);
                iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                iPhone.putExtra("id", list.get(i).get(GlobalConstant.id));


                startActivity(iPhone);
            }
        });


    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.device_txtView:
                device_listView.setVisibility(View.VISIBLE);
                type_view_include.setVisibility(View.GONE);
                types_txtView.setBackgroundResource(R.drawable.white_btn_back);
                device_txtView.setBackgroundResource(R.drawable.green_btn);
                device_txtView.setTextColor(Color.parseColor("#ffffff"));
                types_txtView.setTextColor(Color.parseColor("#FFE6E4E4"));

                break;
            case R.id.types_txtView:
                device_listView.setVisibility(View.GONE);
                type_view_include.setVisibility(View.VISIBLE);
                device_txtView.setBackgroundResource(R.drawable.white_btn_back);
                types_txtView.setBackgroundResource(R.drawable.green_btn);
                types_txtView.setTextColor(Color.parseColor("#ffffff"));
                device_txtView.setTextColor(Color.parseColor("#FFE6E4E4"));
                break;
            case R.id.back:
                finish();
                break;
        }
*/
    }

    //--------------------Category api method---------------------------------
    private void categoryMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.CATEGORY_URL,
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
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject arryObj = data.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(GlobalConstant.id, arryObj.getString(GlobalConstant.id));
                                    map.put(GlobalConstant.name, arryObj.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.icon, arryObj.getString(GlobalConstant.icon));
                                    list.add(map);
                                }
                                device_listView.setAdapter(new DeviceAdapter(DeviceActivity.this, list));
                                CommonUtils.getListViewSize(device_listView);

                            } else {
                                Toast.makeText(DeviceActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            holder = new Holder();
            if (view == null) {

                view = inflator.inflate(R.layout.device_item_layout, null);
                holder.device_image = (ImageView) view.findViewById(R.id.device_icon);
                holder.device_name = (TextView) view.findViewById(R.id.device_name);

                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            url = GlobalConstant.IMAGE_URL + deviceList.get(i).get(GlobalConstant.icon);
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
            ImageView device_image;
            TextView device_name;
        }
    }

    //------------------------Type Adapter--------------------
    class TypeAdapter extends BaseAdapter {


        Context c;

        TypeAdapter(Context c) {
            this.c = c;

        }

        @Override
        public int getCount() {
            return 0;
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
            return null;
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
