package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.astuetz.PagerSlidingTabStrip;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by worksdelight on 15/04/17.
 */

public class TechniciansHistory extends FragmentActivity implements ViewPager.OnPageChangeListener{
    private int previousPage=0;
    PagerSlidingTabStrip tabsStrip;

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    LinearLayout mTabsLinearLayout;
    ImageView back;
    TextView header_txt;
    Dialog dialog2;
    Global global;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global=(Global)getApplicationContext();
        dialogWindow();
        categoryMethod();
       viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        //tabsStrip.setTextColorResource(R.drawable.selectore);
        header_txt=(TextView)findViewById(R.id.header_txt);
        header_txt.setText("Appointments");

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ((LinearLayout)tabsStrip.getChildAt(0)).getChildAt(previousPage).setSelected(false);
        //set the selected page to state_selected = true
        ((LinearLayout)tabsStrip.getChildAt(0)).getChildAt(position).setSelected(true);
        //remember the current page
        previousPage=position;
        setUpTabStrip(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void setUpTabStrip(int pos) {

        //your other customizations related to tab strip...blahblah
        // Set first tab selected
        mTabsLinearLayout = ((LinearLayout) tabsStrip.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);

            if (i == pos) {
                tv.setTextColor(Color.WHITE);
            } else {
                tv.setTextColor(getResources().getColor(R.color.headerText));
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         * else{
		 *
		 * }
		 */
        super.onActivityResult(requestCode, resultCode, data);
    }

    //--------------------Category api method---------------------------------
    private void categoryMethod() {
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.BOOKINGINFO_URL + "&" + GlobalConstant.USERID + "=" + CommonUtils.UserID(TechniciansHistory.this),
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
                                JSONArray copletedArr = data.getJSONArray(GlobalConstant.completed);
                                JSONArray pendingArr = data.getJSONArray(GlobalConstant.pending);

                                global.setCompletedaar(copletedArr);
                                global.setPendingaar(pendingArr);

                            } else {
                                Toast.makeText(TechniciansHistory.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                            viewPager.setAdapter(new FragmentPageAdapterForTechnicians(getSupportFragmentManager()));

                            tabsStrip.setViewPager(viewPager);
                            tabsStrip.setOnPageChangeListener(TechniciansHistory.this);
                            ((LinearLayout)tabsStrip.getChildAt(0)).getChildAt(0).setSelected(true);
                            setUpTabStrip(0);
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
        RequestQueue requestQueue = Volley.newRequestQueue(TechniciansHistory.this);
        requestQueue.add(stringRequest);
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(TechniciansHistory.this);
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

