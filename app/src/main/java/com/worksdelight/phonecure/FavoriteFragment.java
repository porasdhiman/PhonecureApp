package com.worksdelight.phonecure;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by worksdelight on 28/02/17.
 */

public class FavoriteFragment extends Fragment {
    ListView repair_listView;



    Global global;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
RelativeLayout header_view;
    Dialog dialog2;
    ArrayList<HashMap<String,String>> list=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.iphone_repaire_layout, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();
global=(Global)getActivity().getApplicationContext();
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        header_view=(RelativeLayout)v.findViewById(R.id.header_view);
        header_view.setVisibility(View.GONE);
        repair_listView=(ListView)v.findViewById(R.id.repair_listView);
        dialogWindow();
        favoritesMethod();
        return v;
    }

    //--------------------Category api method---------------------------------
    private void favoritesMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.FAVORITE_URL +"user_id="+ CommonUtils.UserID(getActivity())+"&latitude="+global.getLat()+"&longitude="+global.getLong(),
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
                                JSONArray arr = obj.getJSONArray("data");
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject objArr = arr.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();

                                    map.put(GlobalConstant.id, objArr.getString(GlobalConstant.id));
                                    map.put(GlobalConstant.name, objArr.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.image, objArr.getString(GlobalConstant.image));

                                    map.put(GlobalConstant.availability, objArr.getString(GlobalConstant.availability));
                                    map.put(GlobalConstant.off_days, objArr.getString(GlobalConstant.off_days));
                                    map.put(GlobalConstant.distance, objArr.getString(GlobalConstant.distance));

                                    map.put(GlobalConstant.opening_time, objArr.getString(GlobalConstant.opening_time));
                                    map.put(GlobalConstant.closing_time, objArr.getString(GlobalConstant.closing_time));


                                    map.put(GlobalConstant.average_rating, objArr.getString(GlobalConstant.average_rating));
                                    map.put(GlobalConstant.latitude, objArr.getString(GlobalConstant.latitude));
                                    map.put(GlobalConstant.longitude, objArr.getString(GlobalConstant.longitude));
                                    list.add(map);
                                }
                                repair_listView.setAdapter(new RepairAdapter(getActivity(),list));
                            } else {
                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(getActivity());
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

             /*   holder.call = (ImageView) view.findViewById(R.id.call);
                holder.message = (ImageView) view.findViewById(R.id.message);

                holder.chat = (ImageView) view.findViewById(R.id.chat);

                holder.cancel = (ImageView) view.findViewById(R.id.cancel);*/

                holder.setting_layout = (LinearLayout) view.findViewById(R.id.setting_layout);
                //holder.setting_call_layout = (LinearLayout) view.findViewById(R.id.setting_call_layout);

                view.setTag(holder);
                /*holder.setting_layout.setTag(holder);
                holder.call.setTag(holder);
                holder.chat.setTag(holder);
                holder.message.setTag(holder);
                holder.cancel.setTag(holder);
                holder.setting_call_layout.setTag(holder);*/

            } else {
                holder = (Holder) view.getTag();
            }

            holder.setting_layout.setVisibility(View.GONE);
            holder.name.setText(list.get(i).get(GlobalConstant.name));
            holder.location.setText(list.get(i).get(GlobalConstant.distance) + "Km Away");
            holder.rating_value.setText(list.get(i).get(GlobalConstant.average_rating));
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

           /* holder.setting_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (RepairActivity.RepairAdapter.Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.VISIBLE);
                }
            });
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (RepairActivity.RepairAdapter.Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (RepairActivity.RepairAdapter.Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (RepairActivity.RepairAdapter.Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (RepairActivity.RepairAdapter.Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });*/
            return view;
        }

        public class Holder {
            ImageView cancel, chat, message, call;
            LinearLayout setting_layout, setting_call_layout;
            CircleImageView tech_view;
            TextView name, location, specilist, rating_value;

        }
    }
    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getActivity().getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).threadPoolSize(5)
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

