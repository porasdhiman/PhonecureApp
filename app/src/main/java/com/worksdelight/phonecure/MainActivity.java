package com.worksdelight.phonecure;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

/**
 * Created by worksdelight on 28/02/17.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem drug;
    private ResideMenuItem technicians;
    private ResideMenuItem services;
    private ResideMenuItem new_cure;
    private ResideMenuItem dashboard;
    private ResideMenuItem logout;
    private ResideMenuItem itemProfile;
TextView header_txt;
    ImageView notification_img,message_img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.main_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        setUpMenu();
        if( savedInstanceState == null )
            changeFragment(new HomeFragment(),"PhoneCure");
    }
    private void setUpMenu() {

        // attach to current activity;
        notification_img=(ImageView)findViewById(R.id.notification_img) ;
        message_img=(ImageView)findViewById(R.id.message_img) ;
        resideMenu = new ResideMenu(this,R.drawable.user_back,"PORAS DHIMAN","balance:$100000");
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.layer_back);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this,"Home");
        drug     = new ResideMenuItem(this,"Drug");
        technicians     = new ResideMenuItem(this,"Technicians");
        services     = new ResideMenuItem(this,"services");
        dashboard     = new ResideMenuItem(this,"Dashboard");
        itemProfile  = new ResideMenuItem(this,"profile");
        new_cure  = new ResideMenuItem(this,"New cure");
        logout  = new ResideMenuItem(this,"Logout");


        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        drug.setOnClickListener(this);
        technicians.setOnClickListener(this);
        services.setOnClickListener(this);
        dashboard.setOnClickListener(this);
        new_cure.setOnClickListener(this);
        logout.setOnClickListener(this);


        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(drug, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(technicians, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(services, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(dashboard, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(new_cure, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(logout, ResideMenu.DIRECTION_LEFT);
        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_LEFT);
        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        header_txt=(TextView)findViewById(R.id.header_txt);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new HomeFragment(),"PhoneCure");
        }else if (view == itemProfile){
            changeFragment(new ProfileFragment(),"Profile");
        }else if (view == drug){
            changeFragment(new DrugFragment(),"Drug");
        }else if (view == technicians){
            changeFragment(new TechniciansFragment(),"Technicians");
        }else if (view == services){
            changeFragment(new ServiceFragment(),"Services");
        }
        else if (view == dashboard){
            changeFragment(new DashBoradFragment(),"Dashboard");
        }

        else if (view == new_cure){
            changeFragment(new NewCureFragment(),"New Cure");
        }
        else if (view == logout){
            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }


        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {

        }

        @Override
        public void closeMenu() {

        }
    };

    private void changeFragment(Fragment targetFragment,String titel){
        header_txt.setText(titel);
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){

        return resideMenu;
    }
    public void visibilityMethod(){
        notification_img.setVisibility(View.GONE);
        message_img.setVisibility(View.GONE);
    }
    public void homevisibilityMethod(){
        notification_img.setVisibility(View.VISIBLE);
        message_img.setVisibility(View.VISIBLE);
    }
}
