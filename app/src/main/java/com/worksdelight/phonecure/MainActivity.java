package com.worksdelight.phonecure;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

/**
 * Created by worksdelight on 28/02/17.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    ImageView notification_img, message_img;
    Global global;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    CallbackManager callbackManager;
    AlertDialog builder;
    Button title_bar_left_menu;
    private Animation mEnterAnimation, mExitAnimation;

    public static final int OVERLAY_METHOD = 1;

    //--------------Location lat long variable---------

    private Location mLastLocation, myLocation;
    LocationManager mLocationManager;
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */

    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.main_layout);
        callbackManager = CallbackManager.Factory.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        buildGoogleApiClient();
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        global = (Global) getApplicationContext();
        setUpMenu();
        if (savedInstanceState == null)
            changeFragment(new HomeFragment(), "Phonecure");

    }

    private void setUpMenu() {

        // attach to current activity;
        //notification_img = (ImageView) findViewById(R.id.notification_img);
        message_img = (ImageView) findViewById(R.id.message_img);

        resideMenu = new ResideMenu(this, sp.getString(GlobalConstant.image, ""), sp.getString(GlobalConstant.name, ""));
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.layer_back);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome = new ResideMenuItem(this, "HOME");
        // drug = new ResideMenuItem(this, "Drug");
        technicians = new ResideMenuItem(this, "MY ORDERS");
        services = new ResideMenuItem(this, "TECHNICIANS");
        dashboard = new ResideMenuItem(this, "PRIVACY POLICY");
        itemProfile = new ResideMenuItem(this, "PROFILE");
       // new_cure = new ResideMenuItem(this, "ABOUT US");
       // logout = new ResideMenuItem(this, "LOGOUT");


        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        // drug.setOnClickListener(this);
        technicians.setOnClickListener(this);
        services.setOnClickListener(this);
        dashboard.setOnClickListener(this);
       // new_cure.setOnClickListener(this);
        //logout.setOnClickListener(this);


        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(drug, ResideMenu.DIRECTION_LEFT);

        resideMenu.addMenuItem(services, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(technicians, ResideMenu.DIRECTION_LEFT);
       // resideMenu.addMenuItem(new_cure, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(dashboard, ResideMenu.DIRECTION_LEFT);


        //resideMenu.addMenuItem(logout, ResideMenu.DIRECTION_LEFT);
        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        //resideMenu.setDirectionDisable(ResideMenu.DIRECTION_LEFT);
        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        title_bar_left_menu=(Button) findViewById(R.id.title_bar_left_menu);
        title_bar_left_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

       /* ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription("Slide menu")

                        .setGravity(Gravity.BOTTOM)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(title_bar_left_menu);
        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription("Notification")

                        .setGravity(Gravity.BOTTOM)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(message_img);
         *//* setup enter and exit animation *//*
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(new Pointer().setColor(getResources().getColor(R.color.main_color)).setGravity(Gravity.CENTER))
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();


        ChainTourGuide.init(this).playInSequence(sequence);*/
        header_txt = (TextView) findViewById(R.id.header_txt);
        message_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,NotifyActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome) {
            changeFragment(new HomeFragment(), "Phonecure");

        } else if (view == itemProfile) {
            changeFragment(new ProfileFragment(), "Profile");
        } /*else if (view == drug) {
            changeFragment(new DrugFragment(), "Drug");
        }*/ /*else if (view == technicians) {
            changeFragment(new TechniciansFragment(), "Technicians");
        }*/ else if (view == services) {
            changeFragment(new FavoriteFragment(), "Favorites");
        } else if (view == technicians) {
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(i);
            resideMenu.clearIgnoredViewList();
        } else if (view == dashboard) {
            changeFragment(new DashBoradFragment(), "Privacy Policy");
        } /*else if (view == new_cure) {
            changeFragment(new NewCureFragment(), "About us");
        }*/ /*else if (view == logout) {
            //global.getSocialAuthAdpater().signOut(this,SocialAuthAdapter.Provider.TWITTER.toString());
            if (sp.getString("type", "app").equalsIgnoreCase("app")) {
                ed.clear();
                ed.commit();
                global.setDateList(null);
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                ed.clear();
                ed.commit();
                global.setDateList(null);
                LoginManager.getInstance().logOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }


        }*/


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

    private void changeFragment(Fragment targetFragment, String titel) {
        if (titel.equalsIgnoreCase("Phonecure")) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            SpannableString str1 = new SpannableString(titel);
            str1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 5, str1.length(), str1.length() - 1);
            builder.append(str1);
            header_txt.setText(builder, TextView.BufferType.SPANNABLE);
            header_txt.setTextSize(22);
        } else {
            header_txt.setTextColor(Color.parseColor("#ffffff"));
            header_txt.setText(titel);
            header_txt.setTextSize(18);
        }

        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu() {

        return resideMenu;
    }

    public void visibilityMethod() {
       // notification_img.setVisibility(View.GONE);
        message_img.setVisibility(View.GONE);
    }

    public void homevisibilityMethod() {
        //notification_img.setVisibility(View.VISIBLE);
        message_img.setVisibility(View.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        setUpMenu();

    }

    @Override
    public void onBackPressed() {

        builder = new AlertDialog.Builder(MainActivity.this).setMessage("Do You Want To Exit?")
                .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();

                    }

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        builder.dismiss();
                    }
                }).show();

    }

    //---------------------------Location lat long method-----------------
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }




    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Toast.makeText(SplashActivity.this, "" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            global.setLat(String.valueOf(mLastLocation.getLatitude()));
            global.setLong(String.valueOf(mLastLocation.getLongitude()));
            Log.e("lat long", global.getLat() + " " + global.getLong());


        } else {


        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

}
