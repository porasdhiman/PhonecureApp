package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by worksdelight on 26/04/17.
 */

public class WorkingHoursUpdateActivity extends Activity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    ImageView back;
    LinearLayout sun_layout, mon_layout, tue_layout, wed_layout, thu_layout, fri_layout, sat_layout;
    TextView sun_closs_txt, mon_closs_txt, tue_closs_txt, wed_closs_txt, thu_closs_txt, fri_closs_txt, sat_closs_txt;
    ImageView sun_toggle_img, mon_toggle_img, tue_toggle_img, wed_toggle_img, thu_toggle_img, fri_toggle_img, sat_toggle_img;
    int sun = 0, mon = 0, tue = 0, wed = 0, thu = 0, fri = 0, sat = 0, p = 0, d = 0, pos;
    ImageView pickUp_img, dropoff_img;
    TextView submit_txt, time_txt;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    TimePickerDialog timePickerDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView sun_openning, sun_clossing, mon_openning, mon_clossing, tue_openning, tue_clossing, wed_openning, wed_clossing,
            thu_openning, thu_clossing, fri_openning, fri_clossing, sat_openning, sat_clossing;
    String day;
    HttpEntity resEntity;
    String message;
    Dialog dialog2;
    String daysNAme[] = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.working_hour_second_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        init();
        for (int i = 0; i < 7; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(GlobalConstant.day, daysNAme[i]);
            map.put(GlobalConstant.opening_time, "9:00 AM");
            map.put(GlobalConstant.closing_time, "6:30 PM");
            map.put(GlobalConstant.status, "clossed");
            list.add(map);
        }
        dialogWindow();
        profileMethod();
    }

    public void init() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sun_openning = (TextView) findViewById(R.id.sun_openning);
        sun_clossing = (TextView) findViewById(R.id.sun_clossing);

        mon_openning = (TextView) findViewById(R.id.mon_openning);
        mon_clossing = (TextView) findViewById(R.id.mon_clossing);

        tue_openning = (TextView) findViewById(R.id.tue_openning);
        tue_clossing = (TextView) findViewById(R.id.tue_clossing);

        wed_openning = (TextView) findViewById(R.id.wed_openning);
        wed_clossing = (TextView) findViewById(R.id.wed_clossing);

        thu_openning = (TextView) findViewById(R.id.thu_openning);
        thu_clossing = (TextView) findViewById(R.id.thu_clossing);

        fri_openning = (TextView) findViewById(R.id.fri_openning);
        fri_clossing = (TextView) findViewById(R.id.fri_clossing);

        sat_openning = (TextView) findViewById(R.id.sat_openning);
        sat_clossing = (TextView) findViewById(R.id.sat_clossing);

        sun_openning.setOnClickListener(this);
        sun_clossing.setOnClickListener(this);

        mon_openning.setOnClickListener(this);
        mon_clossing.setOnClickListener(this);

        tue_openning.setOnClickListener(this);
        tue_clossing.setOnClickListener(this);

        wed_openning.setOnClickListener(this);
        wed_clossing.setOnClickListener(this);

        thu_openning.setOnClickListener(this);
        thu_clossing.setOnClickListener(this);

        fri_openning.setOnClickListener(this);
        fri_clossing.setOnClickListener(this);

        sat_openning.setOnClickListener(this);
        sat_clossing.setOnClickListener(this);


        sun_layout = (LinearLayout) findViewById(R.id.sun_layout);
        mon_layout = (LinearLayout) findViewById(R.id.mon_layout);
        tue_layout = (LinearLayout) findViewById(R.id.tue_layout);
        wed_layout = (LinearLayout) findViewById(R.id.wed_layout);
        thu_layout = (LinearLayout) findViewById(R.id.thu_layout);
        fri_layout = (LinearLayout) findViewById(R.id.fri_layout);
        sat_layout = (LinearLayout) findViewById(R.id.sat_layout);

        sun_closs_txt = (TextView) findViewById(R.id.sun_closs_txt);
        mon_closs_txt = (TextView) findViewById(R.id.mon_closs_txt);
        tue_closs_txt = (TextView) findViewById(R.id.tue_closs_txt);
        wed_closs_txt = (TextView) findViewById(R.id.wed_closs_txt);
        thu_closs_txt = (TextView) findViewById(R.id.thu_closs_txt);
        fri_closs_txt = (TextView) findViewById(R.id.fri_closs_txt);
        sat_closs_txt = (TextView) findViewById(R.id.sat_closs_txt);

        sun_toggle_img = (ImageView) findViewById(R.id.sun_toggle_img);
        mon_toggle_img = (ImageView) findViewById(R.id.mon_toggle_img);

        tue_toggle_img = (ImageView) findViewById(R.id.tue_toggle_img);

        wed_toggle_img = (ImageView) findViewById(R.id.wed_toggle_img);

        thu_toggle_img = (ImageView) findViewById(R.id.thu_toggle_img);

        fri_toggle_img = (ImageView) findViewById(R.id.fri_toggle_img);

        sat_toggle_img = (ImageView) findViewById(R.id.sat_toggle_img);

        sun_toggle_img.setOnClickListener(this);
        mon_toggle_img.setOnClickListener(this);
        tue_toggle_img.setOnClickListener(this);
        wed_toggle_img.setOnClickListener(this);
        thu_toggle_img.setOnClickListener(this);
        fri_toggle_img.setOnClickListener(this);
        sat_toggle_img.setOnClickListener(this);

        pickUp_img = (ImageView) findViewById(R.id.pickUp_img);

        dropoff_img = (ImageView) findViewById(R.id.dropoff_img);
        pickUp_img.setOnClickListener(this);
        dropoff_img.setOnClickListener(this);
        submit_txt = (TextView) findViewById(R.id.submit_txt);
        submit_txt.setText("Update");
        submit_txt.setOnClickListener(this);

    }

    public void timePicker(TextView time_txt) {
        // Get Current Time
        this.time_txt = time_txt;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        timePickerDialog = new TimePickerDialog(this,
                this, mHour, mMinute, false);

        timePickerDialog.show();
    }

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        time_txt.setText(getTime(hourOfDay, minute));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sun_toggle_img:

                pos = 0;
                if (sun == 0) {
                    sun = 1;
                    sun_closs_txt.setVisibility(View.GONE);
                    sun_layout.setVisibility(View.VISIBLE);
                    sun_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "sunday";
                    updateData("open", sun_openning.getText().toString(), sun_clossing.getText().toString());
                } else {
                    sun = 0;
                    sun_closs_txt.setVisibility(View.VISIBLE);
                    sun_layout.setVisibility(View.GONE);
                    sun_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");

                }
                break;
            case R.id.mon_toggle_img:

                pos = 1;
                if (mon == 0) {
                    mon = 1;
                    mon_closs_txt.setVisibility(View.GONE);
                    mon_layout.setVisibility(View.VISIBLE);
                    mon_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "monday";
                    updateData("open", mon_openning.getText().toString(), mon_clossing.getText().toString());

                } else {
                    mon = 0;
                    mon_closs_txt.setVisibility(View.VISIBLE);
                    mon_layout.setVisibility(View.GONE);
                    mon_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");


                }
                break;
            case R.id.tue_toggle_img:

                pos = 2;
                if (tue == 0) {
                    tue = 1;
                    tue_closs_txt.setVisibility(View.GONE);
                    tue_layout.setVisibility(View.VISIBLE);
                    tue_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "tuesday";
                    updateData("open", tue_openning.getText().toString(), tue_clossing.getText().toString());

                } else {
                    tue = 0;
                    tue_closs_txt.setVisibility(View.VISIBLE);
                    tue_layout.setVisibility(View.GONE);
                    tue_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");
                }
                break;
            case R.id.wed_toggle_img:

                pos = 3;
                if (wed == 0) {
                    wed = 1;
                    wed_closs_txt.setVisibility(View.GONE);
                    wed_layout.setVisibility(View.VISIBLE);
                    wed_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "wednesday";
                    updateData("open", wed_openning.getText().toString(), wed_clossing.getText().toString());

                } else {
                    wed = 0;
                    wed_closs_txt.setVisibility(View.VISIBLE);
                    wed_layout.setVisibility(View.GONE);
                    wed_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");
                }
                break;
            case R.id.thu_toggle_img:

                pos = 4;
                if (thu == 0) {
                    thu = 1;
                    thu_closs_txt.setVisibility(View.GONE);
                    thu_layout.setVisibility(View.VISIBLE);
                    thu_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "thursday";
                    updateData("open", thu_openning.getText().toString(), thu_clossing.getText().toString());

                } else {
                    thu = 0;
                    thu_closs_txt.setVisibility(View.VISIBLE);
                    thu_layout.setVisibility(View.GONE);
                    thu_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");
                }
                break;
            case R.id.fri_toggle_img:

                pos = 5;
                if (fri == 0) {
                    fri = 1;
                    fri_closs_txt.setVisibility(View.GONE);
                    fri_layout.setVisibility(View.VISIBLE);
                    fri_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "friday";
                    updateData("open", fri_openning.getText().toString(), fri_clossing.getText().toString());

                } else {
                    fri = 0;
                    fri_closs_txt.setVisibility(View.VISIBLE);
                    fri_layout.setVisibility(View.GONE);
                    fri_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");
                }
                break;
            case R.id.sat_toggle_img:

                pos = 6;
                if (sat == 0) {
                    sat = 1;
                    sat_closs_txt.setVisibility(View.GONE);
                    sat_layout.setVisibility(View.VISIBLE);
                    sat_toggle_img.setImageResource(R.drawable.toggle_on);
                    day = "saturday";
                    updateData("open", sat_openning.getText().toString(), sat_clossing.getText().toString());

                } else {
                    sat = 0;
                    sat_closs_txt.setVisibility(View.VISIBLE);
                    sat_layout.setVisibility(View.GONE);
                    sat_toggle_img.setImageResource(R.drawable.toggle_off);
                    day = "";
                    updateData("clossed", "", "");
                }
                break;
            case R.id.pickUp_img:
                if (p == 0) {
                    p = 1;
                    pickUp_img.setImageResource(R.drawable.pickup);
                } else {
                    p = 0;
                    pickUp_img.setImageResource(R.drawable.pickup_unselect);
                }
                break;
            case R.id.dropoff_img:
                if (d == 0) {
                    d = 1;
                    dropoff_img.setImageResource(R.drawable.dropoff);
                } else {
                    d = 0;
                    dropoff_img.setImageResource(R.drawable.dropoff_unselect);
                }
                break;
            case R.id.submit_txt:
                dialogWindow();
                //editprofile();
                new Thread(null, address_request, "")
                        .start();
                Log.e("time value", list.toString());
                break;
            case R.id.sun_openning:
                timePicker(sun_openning);
                break;
            case R.id.sun_clossing:
                timePicker(sun_clossing);
                break;
            case R.id.mon_openning:
                timePicker(mon_openning);
                break;
            case R.id.mon_clossing:
                timePicker(mon_clossing);
                break;

            case R.id.tue_openning:
                timePicker(tue_openning);
                break;
            case R.id.tue_clossing:
                timePicker(tue_clossing);
                break;

            case R.id.wed_openning:
                timePicker(wed_openning);
                break;
            case R.id.wed_clossing:
                timePicker(wed_clossing);
                break;

            case R.id.thu_openning:
                timePicker(thu_openning);
                break;
            case R.id.thu_clossing:
                timePicker(thu_clossing);
                break;

            case R.id.fri_openning:
                timePicker(fri_openning);
                break;
            case R.id.fri_clossing:
                timePicker(fri_clossing);
                break;
            case R.id.sat_openning:
                timePicker(sat_openning);
                break;
            case R.id.sat_clossing:
                timePicker(sat_clossing);
                break;
        }
    }

    public void updateData(String status, String openning, String clossing) {

        list.get(pos).put(GlobalConstant.day, day);
        list.get(pos).put(GlobalConstant.status, status);
        list.get(pos).put(GlobalConstant.opening_time, openning);
        list.get(pos).put(GlobalConstant.closing_time, clossing);

    }

    Runnable address_request = new Runnable() {
        String res = "false";


        @Override
        public void run() {
            try {

                res = doFileUpload();
            } catch (Exception e) {

            }
            Message msg = new Message();
            msg.obj = res;
            address_request_Handler.sendMessage(msg);
        }
    };

    Handler address_request_Handler = new Handler() {
        public void handleMessage(Message msg) {
            String res = (String) msg.obj;
            dialog2.dismiss();
            if (res.equalsIgnoreCase("true")) {
                // terms_dialog.dismiss();
                Toast.makeText(WorkingHoursUpdateActivity.this, message, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(WorkingHoursUpdateActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        }

    };

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

    // ------------------------------------------------------upload
    // method---------------
    private String doFileUpload() {
        String success = "false";

        String urlString = GlobalConstant.PROFILE_URL;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart(GlobalConstant.id, new StringBody(CommonUtils.UserID(WorkingHoursUpdateActivity.this)));
            JSONArray installedList = new JSONArray();


            for (int i = 0; i < list.size(); i++) {
                try {
                    if (list.get(i).get(GlobalConstant.status).equalsIgnoreCase("open")) {
                        JSONObject installedPackage = new JSONObject();
                        installedPackage.put(GlobalConstant.day, list.get(i).get(GlobalConstant.day));
                        installedPackage.put(GlobalConstant.opening_time, list.get(i).get(GlobalConstant.opening_time));
                        installedPackage.put(GlobalConstant.closing_time, list.get(i).get(GlobalConstant.closing_time));
                        installedList.put(installedPackage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            String dataToSend = installedList.toString();
            Log.e("data array", dataToSend);
            reqEntity.addPart(GlobalConstant.availability, new StringBody(dataToSend));

            reqEntity.addPart(GlobalConstant.repair_at_shop, new StringBody(String.valueOf(p)));
            reqEntity.addPart(GlobalConstant.repair_on_location, new StringBody(String.valueOf(d)));

               /* reqEntity.addPart("event_dates[0][event_end_date]", new StringBody("2017-10-3"));
                Log.e("event_dates[0][event_end_date]", "2017-10-3");*/


            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            final String response_str = EntityUtils.toString(resEntity);
            Log.e("response_str", response_str);
            if (resEntity != null) {
                JSONObject obj = new JSONObject(response_str);
                String status = obj.getString("status");
                if (status.equalsIgnoreCase("1")) {
                    success = "true";
                    message = obj.getString("message");


                } else {
                    success = "false";
                    message = obj.getString("message");
                }

                /*JSONObject resp = new JSONObject(response_str);
                String status = resp.getString("status");
                String message = resp.getString("message");
                if (status.equals("1")) {

                    success = "true";

                } else {

                    success = "false";

                }
*/
            }
        } catch (Exception ex) {
        }
        return success;
    }

    private void profileMethod() {

// Request a string response from the provided URL.
        String profileUrl= GlobalConstant.PROFILE_URL + "?id=" + CommonUtils.UserID(WorkingHoursUpdateActivity.this);
        Log.e("profile url",profileUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,profileUrl,
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
                                JSONObject data = obj.getJSONObject("data");

                                if (data.getString(GlobalConstant.repair_at_shop).equalsIgnoreCase("1")) {
                                    pickUp_img.setImageResource(R.drawable.pickup);
                                    p = 1;
                                } else {
                                    pickUp_img.setImageResource(R.drawable.pickup_unselect);
                                    p = 0;
                                }
                                if (data.getString(GlobalConstant.repair_on_location).equalsIgnoreCase("1")) {
                                    dropoff_img.setImageResource(R.drawable.dropoff);
                                    d = 1;
                                } else {
                                    dropoff_img.setImageResource(R.drawable.dropoff_unselect);
                                    d = 0;
                                }
                                JSONArray availability = data.getJSONArray(GlobalConstant.availability);
                                for (int i = 0; i < availability.length(); i++) {
                                    JSONObject availObj = availability.getJSONObject(i);
                                    for (int j = 0; j < list.size(); j++) {
                                        if (list.get(j).get(GlobalConstant.day).equalsIgnoreCase(availObj.getString(GlobalConstant.day))) {
                                            list.get(j).put(GlobalConstant.opening_time,availObj.getString(GlobalConstant.opening_time));
                                            list.get(j).put(GlobalConstant.closing_time,availObj.getString(GlobalConstant.closing_time));
                                            list.get(j).put(GlobalConstant.status,"open");
                                        }
                                    }
                                }
                                showValue();
                                Log.e("list value",list.toString());
                            } else {
                                Toast.makeText(WorkingHoursUpdateActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
    public void showValue(){
        for (int k=0;k<list.size();k++){
            if(k==0){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    sun=1;
                    sun_toggle_img.setImageResource(R.drawable.toggle_on);
                    sun_layout.setVisibility(View.VISIBLE);
                    sun_closs_txt.setVisibility(View.GONE);
                    sun_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    sun_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    sun=0;
                    sun_toggle_img.setImageResource(R.drawable.toggle_off);
                    sun_layout.setVisibility(View.GONE);
                    sun_closs_txt.setVisibility(View.VISIBLE);
                    sun_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    sun_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }

            }else  if(k==1){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    mon=1;
                    mon_toggle_img.setImageResource(R.drawable.toggle_on);
                    mon_layout.setVisibility(View.VISIBLE);
                    mon_closs_txt.setVisibility(View.GONE);
                    mon_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    mon_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    mon=0;
                    mon_toggle_img.setImageResource(R.drawable.toggle_off);
                    mon_layout.setVisibility(View.GONE);
                    mon_closs_txt.setVisibility(View.VISIBLE);
                    mon_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    mon_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }
            }
            else  if(k==2){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    tue=1;
                    tue_toggle_img.setImageResource(R.drawable.toggle_on);
                    tue_layout.setVisibility(View.VISIBLE);
                    tue_closs_txt.setVisibility(View.GONE);
                    tue_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    tue_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    tue=0;
                    tue_toggle_img.setImageResource(R.drawable.toggle_off);
                    tue_layout.setVisibility(View.GONE);
                    tue_closs_txt.setVisibility(View.VISIBLE);
                    tue_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    tue_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }
            }
            else  if(k==3){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    wed=1;
                    wed_toggle_img.setImageResource(R.drawable.toggle_on);
                    wed_layout.setVisibility(View.VISIBLE);
                    wed_closs_txt.setVisibility(View.GONE);
                    wed_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    wed_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    wed=0;
                    wed_toggle_img.setImageResource(R.drawable.toggle_off);
                    wed_layout.setVisibility(View.GONE);
                    wed_closs_txt.setVisibility(View.VISIBLE);
                    wed_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    wed_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }
            }
            else  if(k==4){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    thu=1;
                    thu_toggle_img.setImageResource(R.drawable.toggle_on);
                    thu_layout.setVisibility(View.VISIBLE);
                    thu_closs_txt.setVisibility(View.GONE);
                    thu_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    thu_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    thu=0;
                    thu_toggle_img.setImageResource(R.drawable.toggle_off);
                    thu_layout.setVisibility(View.GONE);
                    thu_closs_txt.setVisibility(View.VISIBLE);
                    thu_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    thu_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }
            }
            else  if(k==5){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    fri=1;
                    fri_toggle_img.setImageResource(R.drawable.toggle_on);
                    fri_layout.setVisibility(View.VISIBLE);
                    fri_closs_txt.setVisibility(View.GONE);
                    fri_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    fri_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    fri=0;
                    fri_toggle_img.setImageResource(R.drawable.toggle_off);
                    fri_layout.setVisibility(View.GONE);
                    fri_closs_txt.setVisibility(View.VISIBLE);
                    fri_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    fri_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }
            }
            else  if(k==6){
                if(list.get(k).get(GlobalConstant.status).equalsIgnoreCase("open")){
                    sat=1;
                    sat_toggle_img.setImageResource(R.drawable.toggle_on);
                    sat_layout.setVisibility(View.VISIBLE);
                    sat_closs_txt.setVisibility(View.GONE);
                    sat_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    sat_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }else{
                    sat=0;
                    sat_toggle_img.setImageResource(R.drawable.toggle_off);
                    sat_layout.setVisibility(View.GONE);
                    sat_closs_txt.setVisibility(View.VISIBLE);
                    sat_openning.setText(list.get(k).get(GlobalConstant.opening_time));
                    sat_clossing.setText(list.get(k).get(GlobalConstant.closing_time));
                }
            }

        }

    }
}
