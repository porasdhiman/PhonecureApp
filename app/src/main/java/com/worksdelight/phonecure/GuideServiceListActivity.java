package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 31/05/17.
 */

public class GuideServiceListActivity extends Activity {
    ListView service_list;
    Dialog dialog2;
    ImageView back;
    ArrayList<HashMap<String, String>> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_list_service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        service_list = (ListView) findViewById(R.id.service_list);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialogWindow();
        categoryMethod();
        service_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent j = new Intent(GuideServiceListActivity.this, ServiceWebViewActivity.class);
                j.putExtra("url", deviceList.get(i).get("main_url"));
                j.putExtra("name", deviceList.get(i).get("name"));
                startActivity(j);
            }
        });
    }


    //--------------------Category api method---------------------------------
    private void categoryMethod() {
        String url = GlobalConstant.SERVICE_GUIDE_URL + getIntent().getExtras().getString("name");
        url = url.replace(" ", "%20");
        Log.e("url prine", url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray guides = obj.getJSONArray("guides");
                            for (int i = 0; i < guides.length(); i++) {
                                JSONObject objArr = guides.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();
                                JSONObject imageObj = objArr.getJSONObject("image");
                                map.put("main_url", objArr.getString("url").replace("\"", ""));
                                map.put("image", imageObj.getString("medium").replace("\"", ""));
                                if (objArr.getString("subject").length() == 0) {
                                    map.put("name", objArr.getString("title"));
                                } else {
                                    map.put("name", objArr.getString("subject"));
                                }

                                deviceList.add(map);
                            }
                            if(deviceList.size()>0) {
                                service_list.setAdapter(new DeviceAdapter(GuideServiceListActivity.this));
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


        DeviceAdapter(Context c) {
            this.c = c;

            inflator = LayoutInflater.from(c);

        }

        @Override
        public int getViewTypeCount() {
            return deviceList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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

                view = inflator.inflate(R.layout.guide_service_list_item, null);
                holder.service_img = (ImageView) view.findViewById(R.id.service_img);
                holder.service_name = (TextView) view.findViewById(R.id.service_name);

                view.setTag(holder);


            } else {
                holder = (Holder) view.getTag();

            }


            holder.service_name.setText(deviceList.get(i).get("name"));
            Picasso.with(c).load(deviceList.get(i).get("image")).into(holder.service_img);


            return view;
        }

        class Holder {
            ImageView service_img;
            TextView service_name;
            RelativeLayout device_view;
        }
    }


}
