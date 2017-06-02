package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 06/03/17.
 */

public class AppointmentActivity extends Activity {
    ImageView back_img;
    TextView name_txt, address_txt, date_txt, cancel_request_txt, total_price, close_date_txt;
    ListView service_list;
    Global global;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ScrollView main_scroll;
    Dialog dialog2, ratingDialog;
    String booking_id;
    String statusValue, filePath, invoice;
    ImageView user_view;
    File pdfFile;
    AlertDialog builder;
    ImageView navigation_img, service_img;
    //String sourceLatitude="30.7046",sourceLongitude="76.7179",destinationLatitude="30.7398339",destinationLongitude="76.78270199999997";
    String sourceLatitude = "30.7046", sourceLongitude = "76.7179", destinationLatitude = "", destinationLongitude = "";
    String com_star = "0", time_star = "0", service_star = "0", skill_star = "0", user_id;
    TextView service_name,device_name,total_est_time,othertxt,estimate_travel_txt;
String home_repair="",scoter_repair="";
    String device_model_name,total_expected_time,other_charges,estimated_travel_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_information);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        main_scroll = (ScrollView) findViewById(R.id.main_scroll);
        navigation_img = (ImageView) findViewById(R.id.navigation_img);
        service_img = (ImageView) findViewById(R.id.service_img);
        service_name = (TextView) findViewById(R.id.service_name);
        device_name= (TextView) findViewById(R.id.device_name);
        othertxt=(TextView) findViewById(R.id.other_price);
        total_est_time= (TextView) findViewById(R.id.total_est_time);
        estimate_travel_txt=(TextView) findViewById(R.id.estimate_travel_txt);
        back_img = (ImageView) findViewById(R.id.back_img);
        user_view = (ImageView) findViewById(R.id.user_view);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        total_price = (TextView) findViewById(R.id.total_price);
        // back_img.setColorFilter(back_img.getContext().getResources().getColor(R.color.main_color), PorterDuff.Mode.SRC_ATOP);
        cancel_request_txt = (TextView) findViewById(R.id.cancel_request_txt);
        name_txt = (TextView) findViewById(R.id.name_txt);
        address_txt = (TextView) findViewById(R.id.address_txt);
        date_txt = (TextView) findViewById(R.id.date_txt);
        // close_date_txt = (TextView) findViewById(R.id.close_date_txt);
        service_list = (ListView) findViewById(R.id.service_list);
        sourceLatitude=global.getLat();
        sourceLongitude=global.getLong();
        if (getIntent().getExtras().getString("type").equalsIgnoreCase("0")) {
            navigation_img.setVisibility(View.GONE);
            try {
                JSONObject obj = global.getCompletedaar().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
                booking_id = obj.getString(GlobalConstant.id);
                invoice = obj.getString(GlobalConstant.invoice);
                home_repair=obj.getString(GlobalConstant.repair_at_shop);
                scoter_repair=obj.getString(GlobalConstant.repair_on_location);
                device_model_name=obj.getString("device_model_name");
                total_expected_time=obj.getString("total_expected_time");
                other_charges=obj.getString("other_charges");
                estimated_travel_time=obj.getString("estimated_travel_time");
                JSONObject objUser = obj.getJSONObject(GlobalConstant.user_detail);
                destinationLatitude = objUser.getString(GlobalConstant.latitude);
                destinationLongitude = objUser.getString(GlobalConstant.longitude);
                name_txt.setText(cap(objUser.getString(GlobalConstant.name)));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(name_txt.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
                if (objUser.getString(GlobalConstant.image).equalsIgnoreCase("")) {

                    user_view.setImageDrawable(drawable);
                } else {
                    Picasso.with(this).load(GlobalConstant.TECHNICIANS_IMAGE_URL + objUser.getString(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into(user_view);


                    //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
                }


                JSONObject shipping_address = objUser.getJSONObject("shipping_address");
                address_txt.setText(shipping_address.getString(GlobalConstant.ship_address) + "," + shipping_address.getString(GlobalConstant.ship_city));
                JSONArray booking_item_arr = obj.getJSONArray(GlobalConstant.booking_items);
                for (int i = 0; i < booking_item_arr.length(); i++) {
                    JSONObject bookinObj = booking_item_arr.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(GlobalConstant.id, bookinObj.getString(GlobalConstant.id));
                    map.put(GlobalConstant.price, bookinObj.getString(GlobalConstant.price));
                    map.put(GlobalConstant.name, bookinObj.getString(GlobalConstant.name));
                    map.put(GlobalConstant.expected_time, bookinObj.getString(GlobalConstant.expected_time));

                    list.add(map);
                }


                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat d = new SimpleDateFormat("dd MMM yyyy");

                Date convertedDate = null;
                try {
                    convertedDate = inputFormat.parse(obj.getString(GlobalConstant.date));
                    String s = formatter.format(convertedDate);
                    date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                    // close_date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                total_price.setText("€" + String.valueOf(obj.getString(GlobalConstant.total_amount)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cancel_request_txt.setText("Download Invoice");
            cancel_request_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadProfileImage(GlobalConstant.PDF_DOWNLOAD_URL + invoice, AppointmentActivity.this);

                }
            });

        } else {
            //cancel_request_txt.setVisibility(View.VISIBLE);
            try {
                JSONObject obj = global.getPendingaar().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
                booking_id = obj.getString(GlobalConstant.id);
                statusValue = obj.getString(GlobalConstant.status);
                home_repair=obj.getString(GlobalConstant.repair_at_shop);
                scoter_repair=obj.getString(GlobalConstant.repair_on_location);
                device_model_name=obj.getString("device_model_name");
                total_expected_time=obj.getString("total_expected_time");
                other_charges=obj.getString("other_charges");
                estimated_travel_time=obj.getString("estimated_travel_time");
                JSONObject objUser = obj.getJSONObject(GlobalConstant.user_detail);
                user_id = objUser.getString(GlobalConstant.id);



                name_txt.setText(cap(objUser.getString(GlobalConstant.name)));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(name_txt.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
                if (objUser.getString(GlobalConstant.image).equalsIgnoreCase("")) {

                    user_view.setImageDrawable(drawable);
                } else {
                    Picasso.with(this).load(GlobalConstant.TECHNICIANS_IMAGE_URL + objUser.getString(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into(user_view);


                    //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
                }
                JSONObject shipping_address = objUser.getJSONObject("shipping_address");
                destinationLatitude = shipping_address.getString("ship_latitude");
                destinationLongitude = shipping_address.getString("ship_longitude");
                address_txt.setText(shipping_address.getString(GlobalConstant.ship_address) + "," + shipping_address.getString(GlobalConstant.ship_city));
                JSONArray booking_item_arr = obj.getJSONArray(GlobalConstant.booking_items);
                for (int i = 0; i < booking_item_arr.length(); i++) {
                    JSONObject bookinObj = booking_item_arr.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(GlobalConstant.id, bookinObj.getString(GlobalConstant.id));
                    map.put(GlobalConstant.price, bookinObj.getString(GlobalConstant.price));
                    map.put(GlobalConstant.name, bookinObj.getString(GlobalConstant.name));
                    map.put(GlobalConstant.expected_time, bookinObj.getString(GlobalConstant.expected_time));
                    list.add(map);
                }

                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat d = new SimpleDateFormat("dd MMM yyyy");

                Date convertedDate = null;
                try {
                    convertedDate = inputFormat.parse(obj.getString(GlobalConstant.date));
                    String s = formatter.format(convertedDate);
                    date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                    //  close_date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                total_price.setText("€" + String.valueOf(obj.getString(GlobalConstant.total_amount)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (statusValue.equalsIgnoreCase("pending")) {
                cancel_request_txt.setVisibility(View.VISIBLE);
                cancel_request_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder = new AlertDialog.Builder(AppointmentActivity.this).setMessage("Do You Want To Cancel?")
                                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialogWindow();
                                        ComAdnDelMethod();
                                        builder.dismiss();
                                    }

                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub

                                        builder.dismiss();
                                    }
                                }).show();
                    }
                });
            } else {
                navigation_img.setVisibility(View.GONE);
                cancel_request_txt.setText("Order " + statusValue);
            }
            if(home_repair.equalsIgnoreCase("1")){
                service_img.setImageResource(R.drawable.home_repair);
                service_name.setText("Repair at service point");
            }
            if(scoter_repair.equalsIgnoreCase("1")){
                service_img.setImageResource(R.drawable.scooter);
                service_name.setText("Repair at your location");
            }
            device_name.setText(device_model_name);
            total_est_time.setText(getDurationString(Integer.parseInt(total_expected_time))+" Hours");
            othertxt.setText("€"+String.valueOf(Float.parseFloat(other_charges)));
            estimate_travel_txt.setText(getDurationString(Integer.parseInt(estimated_travel_time))+" Hours");

        }

        service_list.setAdapter(new CompletedAdapter(this));
        CommonUtils.getListViewSize(service_list);
        main_scroll.smoothScrollBy(0, 0);

        navigation_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  WebView webview = (WebView) findViewById(R.id.webView1);
                webview.setWebViewClient(new WebViewClient());
                webview.getSettings().setJavaScriptEnabled(true);
                webview.loadUrl("http://maps.google.com/maps?" + "saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355");
            }*/

                String uri = "http://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
                Log.e("navi url", uri);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat d = new SimpleDateFormat("MMM dd,yyyy");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }

    class CompletedAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;


        CompletedAdapter(Context c/*, ArrayList<HashMap<String, String>> list*/) {
            this.c = c;
            //this.list = list;
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

                view = inflatore.inflate(R.layout.appointment_item_layout, null);
                holder.service_name = (TextView) view.findViewById(R.id.service_name);
                holder.service_time = (TextView) view.findViewById(R.id.service_time);
                holder.service_price = (TextView) view.findViewById(R.id.service_price);

                view.setTag(holder);


            } else {
                holder = (Holder) view.getTag();
            }
            holder.service_name.setText(list.get(i).get(GlobalConstant.name));
            holder.service_price.setText("€" + String.valueOf(Float.parseFloat(list.get(i).get(GlobalConstant.price))));
            holder.service_time.setText("Expected time "+getDurationString(Integer.parseInt(list.get(i).get(GlobalConstant.expected_time)))+" Hours");
            return view;
        }

        public class Holder {

            TextView service_name, service_time, service_price;

        }
    }
    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number % 10 == 0) {
            return "" + number;
        }

        return String.valueOf(number);
    }
   /* public void secondValue(String time){

        String timeSplit[] = time.split(":");
        int seconds = Integer.parseInt(timeSplit[0]) * 60 * 60 +  Integer.parseInt(timeSplit[1]) * 60;
        Toast.makeText()
        System.out.println(seconds);
    }*/

    //--------------------DEL And COMPLETED api method---------------------------------
    private void ComAdnDelMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.COM_AND_DEL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //JSONObject data=obj.getJSONObject("data");
                                ratingWindow();

                            } else {
                                Toast.makeText(AppointmentActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog2.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(GlobalConstant.id, booking_id);
                params.put(GlobalConstant.USERID, CommonUtils.UserID(AppointmentActivity.this));
                params.put(GlobalConstant.status, "cancelled");


                Log.e("Parameter for cancel", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(AppointmentActivity.this);
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

    //---------------------------Progrees Dialog-----------------------
    public void ratingWindow() {
        ratingDialog = new Dialog(this);
        ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingDialog.setCanceledOnTouchOutside(false);
        ratingDialog.setCancelable(false);
        ratingDialog.setContentView(R.layout.rateing_dialog_layout);
        TextView submit_rating = (TextView) ratingDialog.findViewById(R.id.submit_rating);
        ratingStarinit(ratingDialog);
        submit_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWindow();
                ratingMethod();
            }
        });
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        ratingDialog.show();
    }

    public void ratingStarinit(Dialog d) {
        final ImageView com_star1 = (ImageView) d.findViewById(R.id.com_star1);
        final ImageView com_star2 = (ImageView) d.findViewById(R.id.com_star2);
        final ImageView com_star3 = (ImageView) d.findViewById(R.id.com_star3);
        final ImageView com_star4 = (ImageView) d.findViewById(R.id.com_star4);
        final ImageView com_star5 = (ImageView) d.findViewById(R.id.com_star5);

        final ImageView timing_star1 = (ImageView) d.findViewById(R.id.timing_star1);
        final ImageView timing_star2 = (ImageView) d.findViewById(R.id.timing_star2);
        final ImageView timing_star3 = (ImageView) d.findViewById(R.id.timing_star3);
        final ImageView timing_star4 = (ImageView) d.findViewById(R.id.timing_star4);
        final ImageView timing_star5 = (ImageView) d.findViewById(R.id.timing_star5);

        final ImageView service_star1 = (ImageView) d.findViewById(R.id.service_star1);
        final ImageView service_star2 = (ImageView) d.findViewById(R.id.service_star2);
        final ImageView service_star3 = (ImageView) d.findViewById(R.id.service_star3);
        final ImageView service_star4 = (ImageView) d.findViewById(R.id.service_star4);
        final ImageView service_star5 = (ImageView) d.findViewById(R.id.service_star5);


        final ImageView skill_star1 = (ImageView) d.findViewById(R.id.skill_star1);
        final ImageView skill_star2 = (ImageView) d.findViewById(R.id.skill_star2);
        final ImageView skill_star3 = (ImageView) d.findViewById(R.id.skill_star3);
        final ImageView skill_star4 = (ImageView) d.findViewById(R.id.skill_star4);
        final ImageView skill_star5 = (ImageView) d.findViewById(R.id.skill_star5);


        com_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "1";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.green_star);
                com_star3.setImageResource(R.drawable.green_star);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "2";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.green_star);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "3";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "4";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.star_fill);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "5";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.star_fill);
                com_star5.setImageResource(R.drawable.star_fill);
            }
        });
        timing_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "1";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.green_star);
                timing_star3.setImageResource(R.drawable.green_star);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "2";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.green_star);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "3";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "4";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.star_fill);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "5";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.star_fill);
                timing_star5.setImageResource(R.drawable.star_fill);
            }
        });

        service_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "1";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.green_star);
                service_star3.setImageResource(R.drawable.green_star);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "2";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.green_star);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "3";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "4";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.star_fill);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "5";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.star_fill);
                service_star5.setImageResource(R.drawable.star_fill);
            }
        });

        skill_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "1";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.green_star);
                skill_star3.setImageResource(R.drawable.green_star);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "2";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.green_star);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "3";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "4";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.star_fill);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "5";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.star_fill);
                skill_star5.setImageResource(R.drawable.star_fill);
            }
        });

    }

    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    //-------------------------background proceess for pdf file------------------
    public void loadProfileImage(String file_url, Context c) {
        String splitPath[] = file_url.split("/");
        String targetFileName = splitPath[splitPath.length - 1];


        pdfFile = new File(Environment.getExternalStorageDirectory() + "/PhoneCure/" + targetFileName);
        if (pdfFile.exists()) {


            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(AppointmentActivity.this, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
            }
        } else {

            dialogWindow();
            new DownloadFileAsync().execute(file_url);

        }
    }


    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count = 0;
            int MEGABYTE = 1024;
            //Integer.parseInt(listing.get(p).get("file_size"));
            try {

                URL url = new URL(aurl[0]);
                String path = (String) aurl[0];
                String splitPath[] = path.split("/");

                String targetFileName = splitPath[splitPath.length - 1];
                File f = new File(Environment.getExternalStorageDirectory() + "/PhoneCure/");
                f.mkdir();

                pdfFile = new File(f, targetFileName);
                try {
                    pdfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filePath = pdfFile.toString();
//create storage directories, if they don't exist

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                // c.setDoOutput(true);
                c.connect();


                InputStream input = c.getInputStream();// new BufferedInputStream(url.openStream(), Integer.parseInt(listing.get(p).get("file_size")));


                FileOutputStream output = new FileOutputStream(pdfFile);
                int lenghtOfFile = c.getContentLength();


                byte data[] = new byte[MEGABYTE];

                long total = 0;

                while ((count = input.read(data)) != 0) {
                    total += count;

                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.e("ANDRO_ASYNC", progress[0]);

        }

        @Override
        protected void onPostExecute(String unused) {
            dialog2.dismiss();
            Log.e("file path", filePath);


            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(AppointmentActivity.this, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
            }
        }
    }

    //--------------------DEL And COMPLETED api method---------------------------------
    private void ratingMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.RATING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //JSONObject data=obj.getJSONObject("data");
                                ratingDialog.dismiss();
                                Intent i = new Intent(AppointmentActivity.this, TechniciansHistory.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(AppointmentActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog2.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put(GlobalConstant.USERID, CommonUtils.UserID(AppointmentActivity.this));
                params.put(GlobalConstant.favorite_user_id, user_id);
                params.put("rating", com_star);
                params.put("rating1", service_star);
                params.put("rating2", time_star);
                params.put("rating3", skill_star);


                Log.e("Parameter for rating", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(AppointmentActivity.this);
        requestQueue.add(stringRequest);
    }
}
