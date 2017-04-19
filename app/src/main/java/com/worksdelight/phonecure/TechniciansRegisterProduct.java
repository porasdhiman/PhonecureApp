package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
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

/**
 * Created by worksdelight on 14/04/17.
 */

public class TechniciansRegisterProduct extends Activity {
    ImageView back;
    TextView service_txtView, done;
    ListView product_listView;
    EditText price_ed, time_ed;
    RelativeLayout all_select_layout;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    Dialog dialog2;
    Global global;
    ArrayList<HashMap<String, String>> color_name_list = new ArrayList<>();
  //  ArrayList<HashMap<String, String>> color_id_list = new ArrayList<>();
   // ArrayList<HashMap<String, String>> name_list = new ArrayList<>();
    ScrollView main_scroll;
    String serviceID = "";
JSONArray arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technicians_register_product_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        main_scroll = (ScrollView) findViewById(R.id.main_scroll);
        back = (ImageView) findViewById(R.id.back);
        service_txtView = (TextView) findViewById(R.id.service_txtView);
        product_listView = (ListView) findViewById(R.id.product_listView);
        price_ed = (EditText) findViewById(R.id.price_ed);
        time_ed = (EditText) findViewById(R.id.time_ed);
        done = (TextView) findViewById(R.id.done);
        /*all_select_layout = (RelativeLayout) findViewById(R.id.all_select_layout);
        all_select_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        int p=Integer.parseInt(getIntent().getExtras().getString("pos"));
        price_ed.setText(global.getServiceList().get(p).get(GlobalConstant.price));
        time_ed.setText(global.getServiceList().get(p).get(GlobalConstant.number_of_days));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (price_ed.getText().toString().length() == 0) {
                    Toast.makeText(TechniciansRegisterProduct.this, "Please enter price", Toast.LENGTH_SHORT).show();
                } else if (time_ed.getText().toString().length() == 0) {
                    Toast.makeText(TechniciansRegisterProduct.this, "Please estimated time", Toast.LENGTH_SHORT).show();


                } else if (serviceID.equalsIgnoreCase("")) {
                    Toast.makeText(TechniciansRegisterProduct.this, "Please select atleast one color", Toast.LENGTH_SHORT).show();

                } else {
                    dialogWindow();
                    addServiceMethod();

                }

            }
        });
        arr=global.getAar();


        JSONObject obj= null;
        try {
            obj = arr.getJSONObject(p);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JSONArray device= null;
        try {
            device = obj.getJSONArray(GlobalConstant.device_model_colors);
            for (int k=0;k<device.length();k++) {
                JSONObject deviceObj=device.getJSONObject(k);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.color_id,deviceObj.getString(GlobalConstant.color_id));
                map.put(GlobalConstant.color_name,deviceObj.getString(GlobalConstant.color_name));
                map.put(GlobalConstant.color_image,deviceObj.getString(GlobalConstant.color_image));
                map.put(GlobalConstant.model_image,deviceObj.getString(GlobalConstant.model_image));
                map.put(GlobalConstant.status,deviceObj.getString(GlobalConstant.status));
                color_name_list.add(map);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


Log.e("color_name_list",color_name_list.toString());


        //color_id_list = convertToHashMapForId(global.getAllDeviceList().get(global.getPostion()).get(GlobalConstant.color_images));

       // Log.e("list print", color_name_list.toString() + " " + color_id_list.toString());
        product_listView.setAdapter(new ProductAdapter(TechniciansRegisterProduct.this));
        CommonUtils.getListViewSize(product_listView);
        main_scroll.smoothScrollTo(0, 0);
    }

    public ArrayList<HashMap<String, String>> convertToHashMapIcon(String jsonString) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);
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

    public ArrayList<HashMap<String, String>> convertToHashMapForId(String jsonString) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);
            JSONObject jObject = null;
            String keyString = null;
            for (int i = 0; i < jArray.length(); i++) {
                HashMap<String, String> myHashMap = new HashMap<String, String>();
                jObject = jArray.getJSONObject(i);
                // beacuse you have only one key-value pair in each object so I have used index 0
                keyString = (String) jObject.names().get(2);
                myHashMap.put(keyString, jObject.getString(keyString));
                list.add(myHashMap);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;

    }
    public ArrayList<HashMap<String, String>> convertToHashMapForName(String jsonString) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(jsonString);
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


    //--------------------Adapter class-----------------
    class ProductAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflator;
        Holder holder = null;
        String url = "";


        ProductAdapter(Context c) {
            this.c = c;

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
            return color_name_list.size();
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

                view = inflator.inflate(R.layout.product_item, null);
                holder.color_img = (ImageView) view.findViewById(R.id.color_img);
                holder.device_name = (TextView) view.findViewById(R.id.color_name);
                holder.select_img = (ImageView) view.findViewById(R.id.select_img);
                holder.unselect_img = (ImageView) view.findViewById(R.id.unselect_img);
                view.setTag(holder);
                holder.color_img.setTag(holder);
                holder.select_img.setTag(holder);
                holder.unselect_img.setTag(holder);
                holder.device_name.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.device_name.setText(color_name_list.get(i).get(GlobalConstant.color_name));
            url = GlobalConstant.COLOR_IMAGE_URL + color_name_list.get(i).get(GlobalConstant.color_image);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, holder.color_img, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                holder.color_img.setImageResource(0);
            }
            if (color_name_list.get(i).get(GlobalConstant.status).equalsIgnoreCase("1")) {
                holder.select_img.setVisibility(View.GONE);
                holder.unselect_img.setVisibility(View.VISIBLE);
              /*  if (serviceID.equalsIgnoreCase("")) {
                    serviceID = color_name_list.get(i).get(GlobalConstant.color_id);
                } else {
                    if (!serviceID.contains(color_name_list.get(i).get(GlobalConstant.color_id))) {
                        serviceID = serviceID + "," + color_name_list.get(i).get(GlobalConstant.color_id);
                    }
                }
                Log.e("service id minus", serviceID);*/
            } else {

                holder.select_img.setVisibility(View.VISIBLE);
                holder.unselect_img.setVisibility(View.GONE);
              //  String id = String.valueOf(serviceID.charAt(0));

                /*if (serviceID.contains(",")) {
                    if (id.equalsIgnoreCase(color_name_list.get(i).get(GlobalConstant.color_id))) {
                        serviceID = serviceID.replace(color_name_list.get(i).get(GlobalConstant.color_id) + ",", "");

                    } else {
                        serviceID = serviceID.replace("," + color_name_list.get(i).get(GlobalConstant.color_id), "");

                    }
                } else {
                    serviceID = "";

                }
                Log.e("service id", serviceID);*/
            }
            holder.select_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (Holder) v.getTag();
                    holder.select_img.setVisibility(View.GONE);
                    holder.unselect_img.setVisibility(View.VISIBLE);
                    if (serviceID.equalsIgnoreCase("")) {
                        serviceID = color_name_list.get(i).get(GlobalConstant.color_id);
                    } else {
                        if (!serviceID.contains(color_name_list.get(i).get(GlobalConstant.color_id))) {
                            serviceID = serviceID + "," + color_name_list.get(i).get(GlobalConstant.color_id);
                        }
                    }
                    Log.e("service id minus", serviceID);

                }
            });
            holder.unselect_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (Holder) v.getTag();
                    holder.select_img.setVisibility(View.VISIBLE);
                    holder.unselect_img.setVisibility(View.GONE);
                    String id = String.valueOf(serviceID.charAt(0));

                    if (serviceID.contains(",")) {
                        if (id.equalsIgnoreCase(color_name_list.get(i).get(GlobalConstant.color_id))) {
                            serviceID = serviceID.replace(color_name_list.get(i).get(GlobalConstant.color_id) + ",", "");

                        } else {
                            serviceID = serviceID.replace("," + color_name_list.get(i).get(GlobalConstant.color_id), "");

                        }
                    } else {
                        serviceID = "";

                    }
                    Log.e("service id", serviceID);

                }
            });


            //holder.device_name.setText(deviceList.get(i).get(GlobalConstant.name));


            return view;
        }

        class Holder {
            ImageView color_img, select_img, unselect_img;
            TextView device_name;
        }
    }
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
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
    private void addServiceMethod() {
        //Map<String, String> params = new HashMap<String, String>();

        JSONObject params = new JSONObject();

        try {
            params.put(GlobalConstant.USERID, CommonUtils.UserID(TechniciansRegisterProduct.this));

            JSONArray installedList = new JSONArray();


            JSONObject installedPackage = new JSONObject();

            // HashMap<String,String> installedPackage = new HashMap<>();
            try {
                installedPackage.put(GlobalConstant.category_id, global.getAllDeviceList().get(global.getPostion()).get(GlobalConstant.category_id));
                installedPackage.put(GlobalConstant.sub_category_id, global.getAllDeviceList().get(global.getPostion()).get(GlobalConstant.sub_category_id));
                installedPackage.put(GlobalConstant.device_model_id, global.getAllDeviceList().get(global.getPostion()).get(GlobalConstant.id));
                installedPackage.put(GlobalConstant.service_id, getIntent().getExtras().getString(GlobalConstant.service_id));
                installedPackage.put("dm_service_id", getIntent().getExtras().getString(GlobalConstant.id));
                installedPackage.put("model_color", serviceID);
                installedPackage.put("price", price_ed.getText().toString());
                installedPackage.put("number_of_days", time_ed.getText().toString());


                installedList.put(installedPackage);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // String dataToSend = installedList.toString();
            params.put(GlobalConstant.services, installedList);
            Log.e("params valye", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, GlobalConstant.SERVICEADD_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString());
                        dialog2.dismiss();
                        try {


                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                finish();

                            } else {
                                Toast.makeText(TechniciansRegisterProduct.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog2.dismiss();
                Toast.makeText(TechniciansRegisterProduct.this, error.toString(), Toast.LENGTH_SHORT).show();


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
