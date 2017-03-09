package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by worksdelight on 27/02/17.
 */

public class WalkThroughtOneActivity extends Activity implements ViewPager.OnPageChangeListener {
    ImageView next_btn, back_btn;
    ViewPager pager;
    int img[] = {R.drawable.first_screen_logo, R.drawable.second_screen_logo, R.drawable.courier};
    String tech_txt[] = {"PRECISION TOOLS", "DIAGNOSE", "ALWAYS AVAILABLE"};
    String exper_info[] = {"WE ONLY FIND CERTIFIED REPAIR SERVICES", "THE EASY \"CAUSE LACTORE\"", "OUR TECHNICIANS ARE EXPERIENCED"};
    String expet_info_two[] = {"THAT INVEST IN THEMSELVES", "WILL SHOW YOU THE RIGHT SOLUTION", "TO TAKE ON ANY SITUATION"};
    RelativeLayout bottom_back_started_layout, bottom_back_layout;
    TextView get_started_txt;
    ImageView indicatore_img;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    ArrayList<String> list = new ArrayList<>();
    CutomePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.walkthrought_one_layout);
        init();
        Log.e("device info",getDeviceName()+" , "+getAndroidVersion()+" "+getDeviceId());
    }

    public void init() {
        pager = (ViewPager) findViewById(R.id.viewpager);

        bottom_back_started_layout = (RelativeLayout) findViewById(R.id.bottom_back_started_layout);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        get_started_txt = (TextView) findViewById(R.id.get_started_txt);


        pagerAdapterMethod();
        get_started_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WalkThroughtOneActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    public void pagerAdapterMethod() {


        adapter = new CutomePagerAdapter(WalkThroughtOneActivity.this, img, tech_txt, exper_info, expet_info_two);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(this);
        setUiPageViewController();

    }

    private void setUiPageViewController() {

        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselected_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 2) {
            bottom_back_started_layout.setVisibility(View.VISIBLE);

        }else{
            bottom_back_started_layout.setVisibility(View.GONE);

        }
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselected_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //-------device info---------------
    public String getDeviceName() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String getAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private String getDeviceId() {
        String deviceId = "";
        final TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(getApplicationContext()
                    .getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }
}
