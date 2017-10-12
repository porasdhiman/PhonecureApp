package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
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
    String tech_txt[] = {getResources().getString(R.string.precision_tools), getResources().getString(R.string.diagnose), getResources().getString(R.string.always_available)};
    String exper_info[] = {getResources().getString(R.string.trained_service), getResources().getString(R.string.device_feature_reco), getResources().getString(R.string.tech_exp)};
    String expet_info_two[] = {getResources().getString(R.string.invest_txt), getResources().getString(R.string.put_an_end), "TO TAKE ON ANY SITUATION"};
    RelativeLayout bottom_back_started_layout, bottom_back_layout;
    TextView get_started_txt;
    ImageView indicatore_img;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    ArrayList<String> list = new ArrayList<>();
    CutomePagerAdapter adapter;
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        global = (Global) getApplicationContext();
        setContentView(R.layout.walkthrought_one_layout);
        init();
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

        } else {
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
