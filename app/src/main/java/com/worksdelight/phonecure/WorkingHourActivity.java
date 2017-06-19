package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 19/04/17.
 */

public class WorkingHourActivity extends Activity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    ImageView back, pickUp_img, dropoff_img;
    TextView everday_txt, weekday_txt, weekend_txt, openning_txt, clossing_txt, submit_txt;
    TextView mon_txt, tue_txt, wed_txt, thu_txt, fri_txt, sat_txt, sun_txt;
    String minTimehour, minTimeminute, maxTimehour, maxTimeminute;
    TimePickerDialog timePickerDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int t = 0, p = 0, d = 0;
    String weekMstring = "mon,tue,wed,thu,fri,sat,sun";
    Dialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.working_hour_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pickUp_img = (ImageView) findViewById(R.id.pickUp_img);
        dropoff_img = (ImageView) findViewById(R.id.dropoff_img);
        everday_txt = (TextView) findViewById(R.id.everday_txt);
        weekday_txt = (TextView) findViewById(R.id.weekday_txt);

        weekend_txt = (TextView) findViewById(R.id.weekend_txt);
        openning_txt = (TextView) findViewById(R.id.openning_txt);
        clossing_txt = (TextView) findViewById(R.id.clossing_txt);
        submit_txt = (TextView) findViewById(R.id.submit_txt);

        mon_txt = (TextView) findViewById(R.id.mon_txt);
        tue_txt = (TextView) findViewById(R.id.tue_txt);

        wed_txt = (TextView) findViewById(R.id.wed_txt);
        thu_txt = (TextView) findViewById(R.id.thu_txt);
        fri_txt = (TextView) findViewById(R.id.fri_txt);
        sat_txt = (TextView) findViewById(R.id.sat_txt);
        sun_txt = (TextView) findViewById(R.id.sun_txt);
        everday_txt.setOnClickListener(this);
        weekday_txt.setOnClickListener(this);
        weekend_txt.setOnClickListener(this);

        openning_txt.setOnClickListener(this);
        clossing_txt.setOnClickListener(this);
        submit_txt.setOnClickListener(this);

        mon_txt.setOnClickListener(this);
        tue_txt.setOnClickListener(this);
        wed_txt.setOnClickListener(this);

        thu_txt.setOnClickListener(this);
        fri_txt.setOnClickListener(this);
        sat_txt.setOnClickListener(this);
        sun_txt.setOnClickListener(this);

        pickUp_img.setOnClickListener(this);
        dropoff_img.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.mon_txt:

                break;
            case R.id.tue_txt:

                break;
            case R.id.wed_txt:

                break;
            case R.id.thu_txt:

                break;
            case R.id.fri_txt:

                break;
            case R.id.sat_txt:

                break;
            case R.id.sun_txt:

                break;

            case R.id.openning_txt:
                timePicker();
                t = 0;
                break;
            case R.id.clossing_txt:
                timePicker();
                t = 1;
                break;
            case R.id.submit_txt:
                if (openning_txt.getText().toString().length() == 0) {
                    Toast.makeText(WorkingHourActivity.this, "Please enter opening time", Toast.LENGTH_SHORT).show();
                } else if (clossing_txt.getText().toString().length() == 0) {
                    Toast.makeText(WorkingHourActivity.this, "Please enter closing time", Toast.LENGTH_SHORT).show();
                } else {
                    dialogWindow();
                    workingHourMethod();
                }
                break;
            case R.id.everday_txt:
                everday_txt.setTextColor(getResources().getColor(R.color.main_color));
                weekday_txt.setTextColor(getResources().getColor(R.color.mainTextColor));
                weekend_txt.setTextColor(getResources().getColor(R.color.mainTextColor));


                mon_txt.setBackgroundResource(R.drawable.circle_background);
                tue_txt.setBackgroundResource(R.drawable.circle_background);
                wed_txt.setBackgroundResource(R.drawable.circle_background);
                thu_txt.setBackgroundResource(R.drawable.circle_background);
                fri_txt.setBackgroundResource(R.drawable.circle_background);
                sat_txt.setBackgroundResource(R.drawable.circle_background);
                sun_txt.setBackgroundResource(R.drawable.circle_background);
                mon_txt.setTextColor(Color.parseColor("#ffffff"));
                tue_txt.setTextColor(Color.parseColor("#ffffff"));
                wed_txt.setTextColor(Color.parseColor("#ffffff"));
                thu_txt.setTextColor(Color.parseColor("#ffffff"));
                fri_txt.setTextColor(Color.parseColor("#ffffff"));
                sat_txt.setTextColor(Color.parseColor("#ffffff"));
                sun_txt.setTextColor(Color.parseColor("#ffffff"));

                weekMstring = "mon,tue,wed,thu,fri,sat,sun";
                break;
            case R.id.weekday_txt:
                everday_txt.setTextColor(getResources().getColor(R.color.mainTextColor));
                weekday_txt.setTextColor(getResources().getColor(R.color.main_color));
                weekend_txt.setTextColor(getResources().getColor(R.color.mainTextColor));

                mon_txt.setBackgroundResource(R.drawable.circle_background);
                tue_txt.setBackgroundResource(R.drawable.circle_background);
                wed_txt.setBackgroundResource(R.drawable.circle_background);
                thu_txt.setBackgroundResource(R.drawable.circle_background);
                fri_txt.setBackgroundResource(R.drawable.circle_background);
                sat_txt.setBackgroundResource(R.drawable.default_circle_background);
                sun_txt.setBackgroundResource(R.drawable.default_circle_background);
                mon_txt.setTextColor(Color.parseColor("#ffffff"));
                tue_txt.setTextColor(Color.parseColor("#ffffff"));
                wed_txt.setTextColor(Color.parseColor("#ffffff"));
                thu_txt.setTextColor(Color.parseColor("#ffffff"));
                fri_txt.setTextColor(Color.parseColor("#ffffff"));
                sat_txt.setTextColor(getResources().getColor(R.color.headerText));
                sun_txt.setTextColor(getResources().getColor(R.color.headerText));

                weekMstring = "mon,tue,wed,thu,fri";
                break;
            case R.id.weekend_txt:
                everday_txt.setTextColor(getResources().getColor(R.color.mainTextColor));
                weekday_txt.setTextColor(getResources().getColor(R.color.mainTextColor));
                weekend_txt.setTextColor(getResources().getColor(R.color.main_color));
                mon_txt.setBackgroundResource(R.drawable.default_circle_background);
                tue_txt.setBackgroundResource(R.drawable.default_circle_background);
                wed_txt.setBackgroundResource(R.drawable.default_circle_background);
                thu_txt.setBackgroundResource(R.drawable.default_circle_background);
                fri_txt.setBackgroundResource(R.drawable.default_circle_background);
                sat_txt.setBackgroundResource(R.drawable.circle_background);
                sun_txt.setBackgroundResource(R.drawable.circle_background);
                mon_txt.setTextColor(getResources().getColor(R.color.headerText));
                tue_txt.setTextColor(getResources().getColor(R.color.headerText));
                wed_txt.setTextColor(getResources().getColor(R.color.headerText));
                thu_txt.setTextColor(getResources().getColor(R.color.headerText));
                fri_txt.setTextColor(getResources().getColor(R.color.headerText));
                sat_txt.setTextColor(Color.parseColor("#ffffff"));
                sun_txt.setTextColor(Color.parseColor("#ffffff"));
                weekMstring = "sat,sun";
                break;
        }
    }

    public void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        timePickerDialog = new TimePickerDialog(this,
                this, mHour, mMinute, false);

        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (t == 0) {
            openning_txt.setText(hourOfDay + ":" + minute);

        } else {
            clossing_txt.setText(hourOfDay + ":" + minute);

        }

    }

    //--------------------Facebook Social api method---------------------------------
    private void workingHourMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONObject data = obj.getJSONObject("data");

                                Intent f = new Intent(WorkingHourActivity.this, TechniciansDevice.class);
                                f.putExtra("type", "0");
                                startActivity(f);
                                finish();
                            } else {
                                Toast.makeText(WorkingHourActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.id, CommonUtils.UserID(WorkingHourActivity.this));
                params.put(GlobalConstant.availability, weekMstring);

                params.put(GlobalConstant.opening_time, openning_txt.getText().toString());
                params.put(GlobalConstant.closing_time, clossing_txt.getText().toString());
                params.put(GlobalConstant.pick_up, String.valueOf(p));
                params.put(GlobalConstant.drop_off, String.valueOf(d));


                Log.e("Parameter for Working", params.toString());
                return params;
            }

        };
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


}
