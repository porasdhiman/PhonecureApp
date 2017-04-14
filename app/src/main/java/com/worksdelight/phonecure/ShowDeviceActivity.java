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
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 21/03/17.
 */

public class ShowDeviceActivity extends Activity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_device_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
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
        next_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iPhone = new Intent(ShowDeviceActivity.this, iPhoneServiceActivity.class);
                iPhone.putExtra("device_type", list.get(valueof_selected_item).get(GlobalConstant.name));
                iPhone.putExtra("device_id", getIntent().getExtras().getString("id"));
                iPhone.putExtra("id", list.get(valueof_selected_item).get(GlobalConstant.id));


                startActivity(iPhone);
            }
        });
        device_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               /* Intent iPhone = new Intent(ShowDeviceActivity.this, iPhoneServiceActivity.class);
                iPhone.putExtra("device_type", list.get(i).get(GlobalConstant.name));
                iPhone.putExtra("device_id", getIntent().getExtras().getString("id"));
                iPhone.putExtra("id", list.get(i).get(GlobalConstant.id));


                startActivity(iPhone);*/
            }
        });
    }

    //--------------------Category api method---------------------------------
    private void showDeviceMethod() {
        Log.e("davice_id", String.valueOf(getIntent().getExtras().get("id")));
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.DEVICE_URL + "category_id=" + getIntent().getExtras().getString("id"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();
                        Log.e("complete url", GlobalConstant.DEVICE_URL + "category_id=" + getIntent().getExtras().getString("id"));
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

                                    map.put(GlobalConstant.name, arryObj.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.icon, arryObj.getString(GlobalConstant.icon));

                                    JSONArray color_images = arryObj.getJSONArray(GlobalConstant.color_images);
                                    if (color_images.length() > 0) {
                                        for (int k = 0; k < color_images.length(); k++) {
                                            JSONObject colorObj = color_images.getJSONObject(k);
                                            HashMap<String, String> ColorMap = new HashMap<>();
                                            ColorMap.put(GlobalConstant.color_id, colorObj.getString(GlobalConstant.color_id));
                                            ColorMap.put(GlobalConstant.model_image, colorObj.getString(GlobalConstant.model_image));
                                            ColorMap.put(GlobalConstant.color_image, colorObj.getString(GlobalConstant.color_image));
                                            color_list.add(ColorMap);
                                        }
                                    }
                                    map.put(GlobalConstant.color_images, color_list.toString());
                                    list.add(map);
                                    color_list.clear();
                                }
                                if (list.size() > 0) {
                                    Log.e("listing", list.toString());
                                    device_view.setAdapter(new DeviceAdapter(ShowDeviceActivity.this, list));
                                }
                            } else {
                                Toast.makeText(ShowDeviceActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(60, 60);
                vp.setMargins(0, 15, 0, 0);
                holder.img1 = new ImageView(c);
                holder.img1.setLayoutParams(vp);

                holder.img1.setMaxHeight(60);
                holder.img1.setMaxWidth(60);
                holder.img2= new ImageView(c);
                holder.img2.setLayoutParams(vp);

                holder.img2.setMaxHeight(60);
                holder.img2.setMaxWidth(60);
                holder.img3= new ImageView(c);
                holder.img3.setLayoutParams(vp);

                holder.img3.setMaxHeight(60);
                holder.img3.setMaxWidth(60);
                holder.img4= new ImageView(c);
                holder.img4.setLayoutParams(vp);

                holder.img4.setMaxHeight(60);
                holder.img4.setMaxWidth(60);
                holder.img5= new ImageView(c);
                holder.img5.setLayoutParams(vp);

                holder.img5.setMaxHeight(60);
                holder.img5.setMaxWidth(60);
                holder.img6 = new ImageView(c);
                holder.img6.setLayoutParams(vp);

                holder.img6.setMaxHeight(60);
                holder.img6.setMaxWidth(60);
                holder.main_layout.setTag(holder);
                holder.img1.setTag(holder);
                holder.img2.setTag(holder);
                holder.img3.setTag(holder);
                holder.img4.setTag(holder);
                holder.img5.setTag(holder);
                holder.img6.setTag(holder);
                holder.color_layout.addView(holder.img1);
                holder.color_layout.addView(holder.img2);
                holder.color_layout.addView(holder.img3);
                holder.color_layout.addView(holder.img4);
                holder.color_layout.addView(holder.img5);
                holder.color_layout.addView(holder.img6);
                /*if (!deviceList.get(i).get(GlobalConstant.color_images).equalsIgnoreCase("[]")) {
                    Log.e("postion", String.valueOf(i));
                    for (j = 0; j < color.size(); j++) {
                        holder.v = new ImageView(c);

                        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(50, 50);
                        vp.setMargins(0, 10, 0, 0);
                        holder.v.setLayoutParams(vp);

                        holder.v.setMaxHeight(50);
                        holder.v.setMaxWidth(50);
                        holder.v.setId(j);
                        holder.v.setTag(holder);
                        holder.color_layout.addView(holder.v);
                        holder.color_layout.setTag(holder);
                        holder.v.setBackgroundResource(R.drawable.default_circle_background);
                        Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(j).get(GlobalConstant.color_image)).into(holder.v);

                        Log.e("urljjjj", GlobalConstant.COLOR_IMAGE_URL + color.get(j).get(GlobalConstant.color_image));
                        holder.v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                holder = (Holder) view.getTag();
                                final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));

                                for (int k = 0; k < model_image.size(); k++) {

                                    if (view.getId() == k) {

                                        valueof_selected_item=i;
                                        next_txtView.setVisibility(View.VISIBLE);
                                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(k).get(GlobalConstant.model_image));
                                        url = GlobalConstant.IMAGE_URL + deviceList.get(i).get(GlobalConstant.category_id) + "/" + deviceList.get(i).get(GlobalConstant.sub_category_id) + "/" + deviceList.get(i).get(GlobalConstant.id) + "/" + deviceList.get(i).get(GlobalConstant.icon);
                                        ;
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
                                        for (int p = 0; p < value.size(); p++) {
                                            if (p == i) {
                                                value.set(i, "true");
                                            } else {
                                                value.set(p, "false");

                                            }
                                        }

                                    }else{

                                    }
                                }

                                notifyDataSetChanged();
                            }
                        });

                    }
                    color.clear();
                    view.setTag(holder);
                }*/
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            if (value.get(i).equalsIgnoreCase("true")) {
                holder.main_layout.setBackgroundResource(R.drawable.list_selectore);
            } else {
                holder.main_layout.setBackgroundResource(R.drawable.calender_back);
            }
            ArrayList<HashMap<String, String>> color = convertToHashMap(deviceList.get(i).get(GlobalConstant.color_images));
            if (!deviceList.get(i).get(GlobalConstant.color_images).equalsIgnoreCase("[]")) {
                if (color.size() >= 6) {

                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(0).get(GlobalConstant.color_image)).into(holder.img1);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(1).get(GlobalConstant.color_image)).into(holder.img2);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(2).get(GlobalConstant.color_image)).into(holder.img3);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(3).get(GlobalConstant.color_image)).into(holder.img4);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(4).get(GlobalConstant.color_image)).into(holder.img5);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(5).get(GlobalConstant.color_image)).into(holder.img6);
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.VISIBLE);
                    holder.img5.setVisibility(View.VISIBLE);
                    holder.img6.setVisibility(View.VISIBLE);
                } else if (color.size() == 5) {
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(0).get(GlobalConstant.color_image)).into(holder.img1);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(1).get(GlobalConstant.color_image)).into(holder.img2);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(2).get(GlobalConstant.color_image)).into(holder.img3);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(3).get(GlobalConstant.color_image)).into(holder.img4);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(4).get(GlobalConstant.color_image)).into(holder.img5);
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.VISIBLE);
                    holder.img5.setVisibility(View.VISIBLE);
                    holder.img6.setVisibility(View.GONE);

                } else if (color.size() == 4) {
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(0).get(GlobalConstant.color_image)).into(holder.img1);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(1).get(GlobalConstant.color_image)).into(holder.img2);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(2).get(GlobalConstant.color_image)).into(holder.img3);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(3).get(GlobalConstant.color_image)).into(holder.img4);
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.VISIBLE);
                    holder.img5.setVisibility(View.GONE);
                    holder.img6.setVisibility(View.GONE);

                } else if (color.size() == 3) {
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(0).get(GlobalConstant.color_image)).into(holder.img1);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(1).get(GlobalConstant.color_image)).into(holder.img2);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(2).get(GlobalConstant.color_image)).into(holder.img3);
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.GONE);
                    holder.img5.setVisibility(View.GONE);
                    holder.img6.setVisibility(View.GONE);


                } else if (color.size() == 2) {
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(0).get(GlobalConstant.color_image)).into(holder.img1);
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(1).get(GlobalConstant.color_image)).into(holder.img2);
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.GONE);
                    holder.img4.setVisibility(View.GONE);
                    holder.img5.setVisibility(View.GONE);
                    holder.img6.setVisibility(View.GONE);

                } else if (color.size() == 1) {
                    Picasso.with(c).load(GlobalConstant.COLOR_IMAGE_URL + color.get(0).get(GlobalConstant.color_image)).into(holder.img1);
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.img2.setVisibility(View.GONE);
                    holder.img3.setVisibility(View.GONE);
                    holder.img4.setVisibility(View.GONE);
                    holder.img5.setVisibility(View.GONE);
                    holder.img6.setVisibility(View.GONE);

                }
                color.clear();
            }
            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    j = 0;
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(75, 75);
                    LinearLayout.LayoutParams vp1 = new LinearLayout.LayoutParams(60, 60);
                    vp.gravity = Gravity.CENTER_HORIZONTAL;
                    vp1.gravity = Gravity.CENTER_HORIZONTAL;
                    vp.setMargins(0, 15, 0, 0);
                    vp1.setMargins(0, 15, 0, 0);

                    holder.img1.setLayoutParams(vp);
                    holder.img2.setLayoutParams(vp1);
                    holder.img3.setLayoutParams(vp1);
                    holder.img4.setLayoutParams(vp1);
                    holder.img5.setLayoutParams(vp1);
                    holder.img6.setLayoutParams(vp1);

                    final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));
                    if (model_image.size() >= 6) {

                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    } else {
                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    }

                    notifyDataSetChanged();
                }
            });

            holder.img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    j = 1;
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(75, 75);
                    LinearLayout.LayoutParams vp1 = new LinearLayout.LayoutParams(60, 60);
                    vp.gravity = Gravity.CENTER_HORIZONTAL;
                    vp1.gravity = Gravity.CENTER_HORIZONTAL;
                    vp.setMargins(0, 15, 0, 0);
                    vp1.setMargins(0, 15, 0, 0);
                    holder.img1.setLayoutParams(vp1);
                    holder.img2.setLayoutParams(vp);
                    holder.img3.setLayoutParams(vp1);
                    holder.img4.setLayoutParams(vp1);
                    holder.img5.setLayoutParams(vp1);
                    holder.img6.setLayoutParams(vp1);
                    final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));
                    if (model_image.size() >= 6) {

                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
                        url = GlobalConstant.IMAGE_URL + deviceList.get(i).get(GlobalConstant.category_id) + "/" + deviceList.get(i).get(GlobalConstant.sub_category_id) + "/" + deviceList.get(i).get(GlobalConstant.id) + "/" + deviceList.get(i).get(GlobalConstant.icon);
                        ;
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    } else {
                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            holder.img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    j = 2;
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(75, 75);
                    LinearLayout.LayoutParams vp1 = new LinearLayout.LayoutParams(60, 60);
                    vp.gravity = Gravity.CENTER_HORIZONTAL;
                    vp1.gravity = Gravity.CENTER_HORIZONTAL;
                    vp.setMargins(0, 15, 0, 0);
                    vp1.setMargins(0, 15, 0, 0);
                    holder.img1.setLayoutParams(vp1);
                    holder.img2.setLayoutParams(vp1);
                    holder.img3.setLayoutParams(vp);
                    holder.img4.setLayoutParams(vp1);
                    holder.img5.setLayoutParams(vp1);
                    holder.img6.setLayoutParams(vp1);
                    final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));
                    if (model_image.size() >= 6) {

                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
                        url = GlobalConstant.IMAGE_URL + deviceList.get(i).get(GlobalConstant.category_id) + "/" + deviceList.get(i).get(GlobalConstant.sub_category_id) + "/" + deviceList.get(i).get(GlobalConstant.id) + "/" + deviceList.get(i).get(GlobalConstant.icon);
                        ;
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    } else {
                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            holder.img4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    j = 3;
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(75, 75);
                    LinearLayout.LayoutParams vp1 = new LinearLayout.LayoutParams(60, 60);
                    vp.gravity = Gravity.CENTER_HORIZONTAL;
                    vp1.gravity = Gravity.CENTER_HORIZONTAL;
                    vp.setMargins(0, 15, 0, 0);
                    vp1.setMargins(0, 15, 0, 0);
                    holder.img1.setLayoutParams(vp1);
                    holder.img2.setLayoutParams(vp1);
                    holder.img3.setLayoutParams(vp1);
                    holder.img4.setLayoutParams(vp);
                    holder.img5.setLayoutParams(vp1);
                    holder.img6.setLayoutParams(vp1);
                    final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));
                    if (model_image.size() >= 6) {

                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    } else {
                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    }
                    notifyDataSetChanged();
                }

            });
            holder.img5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    j = 4;
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(75, 75);
                    LinearLayout.LayoutParams vp1 = new LinearLayout.LayoutParams(60, 60);
                    vp.gravity = Gravity.CENTER_HORIZONTAL;
                    vp1.gravity = Gravity.CENTER_HORIZONTAL;
                    vp.setMargins(0, 15, 0, 0);
                    vp1.setMargins(0, 15, 0, 0);
                    holder.img1.setLayoutParams(vp1);
                    holder.img2.setLayoutParams(vp1);
                    holder.img3.setLayoutParams(vp1);
                    holder.img4.setLayoutParams(vp1);
                    holder.img5.setLayoutParams(vp);
                    holder.img6.setLayoutParams(vp1);
                    final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));
                    if (model_image.size() >= 6) {

                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    } else {
                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            holder.img6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    j = 5;
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(75, 75);
                    LinearLayout.LayoutParams vp1 = new LinearLayout.LayoutParams(60, 60);
                    vp.gravity = Gravity.CENTER_HORIZONTAL;
                    vp1.gravity = Gravity.CENTER_HORIZONTAL;
                    vp.setMargins(0, 15, 0, 0);
                    vp1.setMargins(0, 15, 0, 0);
                    holder.img1.setLayoutParams(vp1);
                    holder.img2.setLayoutParams(vp1);
                    holder.img3.setLayoutParams(vp1);
                    holder.img4.setLayoutParams(vp1);
                    holder.img5.setLayoutParams(vp1);
                    holder.img6.setLayoutParams(vp);
                    final ArrayList<HashMap<String, String>> model_image = convertToHashMapForModelImage(deviceList.get(i).get(GlobalConstant.color_images));
                    if (model_image.size() >= 6) {

                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    } else {
                        next_txtView.setVisibility(View.VISIBLE);
                        deviceList.get(i).put(GlobalConstant.icon, model_image.get(j).get(GlobalConstant.model_image));
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
                        for (int p = 0; p < value.size(); p++) {
                            if (p == i) {
                                value.set(i, "true");
                            } else {
                                value.set(p, "false");

                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });

            holder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    valueof_selected_item=i;
                    next_txtView.setVisibility(View.GONE);
                    for (int p = 0; p < value.size(); p++) {
                        if (p == i) {
                            value.set(i, "true");
                        } else {
                            value.set(p, "false");

                        }
                    }

                    device_view.setAdapter(new DeviceAdapter(ShowDeviceActivity.this, list));
                    device_view.smoothScrollToPosition(valueof_selected_item);

                }
            });
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
            TextView device_name;
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
                keyString = (String) jObject.names().get(1);
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
