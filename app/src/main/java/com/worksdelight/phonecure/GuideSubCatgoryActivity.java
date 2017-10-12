package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by worksdelight on 31/05/17.
 */

public class GuideSubCatgoryActivity extends Activity {
    TextView device_name;
    ListView device_listView;
    ImageView back;
    Dialog dialog2;
    ArrayList<String> deviceList = new ArrayList<>();

    Global global;
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
        global=(Global)getApplicationContext();
        device_name=(TextView)findViewById(R.id.device_name);
        device_name.setText("Sub devices");
        //device_txtView = (TextView) findViewById(R.id.device_txtView);
        // types_txtView = (TextView) findViewById(R.id.types_txtView);
        device_listView = (ListView) findViewById(R.id.device_listView);
        //types_listView = (ListView) findViewById(R.id.types_listView);
        //  type_view_include = (RelativeLayout) findViewById(R.id.type_view_include);
        back = (ImageView) findViewById(R.id.back);
      /*  dialogWindow();
        categoryMethod();*/
        //device_txtView.setOnClickListener(this);
        //types_txtView.setOnClickListener(this);
        JSONObject obj=global.getObjArray().get(Integer.parseInt(getIntent().getExtras().getString("pos")));
        Log.e("obj",obj.toString());



            Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                String key = iter.next();

                    deviceList.add(key);



            }
            Log.e("device_list",deviceList.toString());



        device_listView.setAdapter(new DeviceAdapter(GuideSubCatgoryActivity.this));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        device_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent j=new Intent(GuideSubCatgoryActivity.this,GuideServiceListActivity.class);
                j.putExtra("name",deviceList.get(i));

                startActivity(j);

            }
        });


    }

   /* //--------------------Category api method---------------------------------
    private void categoryMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.DEVICE_GUIDE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj=new JSONObject(response);
                            JSONObject phone=obj.getJSONObject("Phone");
                            Iterator<String> iter = phone.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    deviceList.add(key);
                                    JSONObject value = phone.getJSONObject(key);
                                    objArray.add(value);

                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                            Log.e("device_list",deviceList.toString());
                            global.setObjArray(objArray);
                            device_listView.setAdapter(new GuidePhoneActivity.DeviceAdapter(GuidePhoneActivity.this));
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
    }*/
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


            } else {
                holder = (Holder) view.getTag();

            }


            holder.device_image.setVisibility(View.GONE);
            holder.device_count.setVisibility(View.GONE);


            holder.device_name.setText(deviceList.get(i));
            holder.select_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });
            holder.unselect_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            return view;
        }

        class Holder {
            ImageView device_image, select_img, unselect_img;
            TextView device_name, device_count;
            RelativeLayout device_view;
        }
    }



}
