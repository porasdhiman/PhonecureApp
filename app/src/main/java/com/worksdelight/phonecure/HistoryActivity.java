package com.worksdelight.phonecure;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by worksdelight on 12/04/17.
 */

public class HistoryActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{
    private int previousPage=0;
    PagerSlidingTabStrip tabsStrip;

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    LinearLayout mTabsLinearLayout;
ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        //tabsStrip.setTextColorResource(R.drawable.selectore);

        tabsStrip.setViewPager(viewPager);
        tabsStrip.setOnPageChangeListener(this);
        ((LinearLayout)tabsStrip.getChildAt(0)).getChildAt(0).setSelected(true);
        setUpTabStrip(0);
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
                tv.setTextColor(getResources().getColor(R.color.bt_very_light_gray));
            }
        }
    }
}
