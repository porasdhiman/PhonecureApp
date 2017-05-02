package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 13/04/17.
 */

public class OtherDeviceActivity extends Activity implements View.OnClickListener {
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
    ArrayList<HashMap<String, String>> iconList = new ArrayList<>();
    ArrayList<HashMap<String, String>> idList = new ArrayList<>();
    ArrayList<HashMap<String, String>> sub_categoryList = new ArrayList<>();
    ArrayList<HashMap<String, String>> category_idList = new ArrayList<>();
    TextView device_name;
ImageView back_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        device_name = (TextView) findViewById(R.id.device_name);
        back_img = (ImageView) findViewById(R.id.back_img);
        // types_txtView = (TextView) findViewById(R.id.types_txtView);
        device_listView = (ListView) findViewById(R.id.device_listView);
        //types_listView = (ListView) findViewById(R.id.types_listView);
        //type_view_include = (RelativeLayout) findViewById(R.id.type_view_include);
        back = (ImageView) findViewById(R.id.back);
        // int l = Integer.parseInt(getIntent().getExtras().getString("pos"));
        device_name.setText(getIntent().getExtras().getString("device_type"));

        try {
            JSONObject obj=global.getOtherData().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
            JSONArray arr=obj.getJSONArray(GlobalConstant.sub_categories);
            for (int i=0;i<arr.length();i++) {
                JSONObject arrObj=arr.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.id, arrObj.getString(GlobalConstant.id));
                map.put(GlobalConstant.sub_category, arrObj.getString(GlobalConstant.sub_category));
                map.put(GlobalConstant.sub_category_id, arrObj.getString(GlobalConstant.sub_category_id));
                map.put(GlobalConstant.icon, arrObj.getString(GlobalConstant.icon));


                list.add(map);
            }
            if(list.size()>0){
                device_listView.setAdapter(new DeviceAdapter(OtherDeviceActivity.this, list));
                CommonUtils.getListViewSize(device_listView);
                back_img.setVisibility(View.GONE);
            }else{
                back_img.setVisibility(View.VISIBLE);
                device_listView.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*dialogWindow();
        categoryMethod();*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        device_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent iPhone = new Intent(OtherDeviceActivity.this, ShowDeviceActivity.class);
                iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.sub_category));
                iPhone.putExtra("id", list.get(i).get(GlobalConstant.id));
                iPhone.putExtra(GlobalConstant.sub_category_id, list.get(i).get(GlobalConstant.sub_category_id));

                global.setDeviceId(list.get(i).get(GlobalConstant.id));

                startActivity(iPhone);

            }
        });


    }

    @Override
    public void onClick(View v) {

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
            url = GlobalConstant.IMAGE_URL +global.getDeviceId() + "/" + deviceList.get(i).get(GlobalConstant.sub_category_id) + "/" + deviceList.get(i).get(GlobalConstant.icon);
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

            holder.device_name.setText(deviceList.get(i).get(GlobalConstant.sub_category));
            return view;
        }

        class Holder {
            ImageView device_image;
            TextView device_name;
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