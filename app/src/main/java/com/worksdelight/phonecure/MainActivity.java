package com.worksdelight.phonecure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 28/02/17.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    RelativeLayout title_bar_left_menu;
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
    Dialog ratingDialog, dialog2;

    //------------rating variable --------
    String com_star = "1", time_star = "1", service_star = "1", skill_star = "1", user_id;
    String userName_mString = "", imageName_mString = "", id_mString;

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
        ratingPendingMethod();
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
        drug = new ResideMenuItem(this, "SHARE");
        technicians = new ResideMenuItem(this, "MY ORDERS");
        services = new ResideMenuItem(this, "TECHNICIANS");
        dashboard = new ResideMenuItem(this, "PRIVACY POLICY");
        itemProfile = new ResideMenuItem(this, "PROFILE");
        new_cure = new ResideMenuItem(this, "FEEDBACK");
        // logout = new ResideMenuItem(this, "LOGOUT");


        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        drug.setOnClickListener(this);
        technicians.setOnClickListener(this);
        services.setOnClickListener(this);
        dashboard.setOnClickListener(this);
        new_cure.setOnClickListener(this);
        //logout.setOnClickListener(this);


        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);


        resideMenu.addMenuItem(services, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(technicians, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(drug, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(dashboard, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(new_cure, ResideMenu.DIRECTION_LEFT);

        //resideMenu.addMenuItem(logout, ResideMenu.DIRECTION_LEFT);
        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        //resideMenu.setDirectionDisable(ResideMenu.DIRECTION_LEFT);
        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        title_bar_left_menu = (RelativeLayout) findViewById(R.id.left_view);
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
                Intent i = new Intent(MainActivity.this, NotifyActivity.class);
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
        } else if (view == drug) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, download this app! http://google.com");
            startActivity(shareIntent);
        } /*else if (view == technicians) {
            changeFragment(new TechniciansFragment(), "Technicians");
        }*/ else if (view == services) {
            changeFragment(new FavoriteFragment(), "Favorite Technicians");
        } else if (view == technicians) {
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(i);
            resideMenu.clearIgnoredViewList();
        } else if (view == dashboard) {
            changeFragment(new DashBoradFragment(), "Privacy Policy");
        } else if (view == new_cure) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");

            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@phonecure.be"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            intent.putExtra(Intent.EXTRA_TEXT, "");

            startActivity(Intent.createChooser(intent, "Send Email"));

          /*  Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setData(Uri.parse("info@phonecure.be"));
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "info@phonecure.be" });
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivityForResult(sendIntent,0);*/

        } /*else if (view == logout) {
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

    //-----------------------Check if rating panding-----------
    private void ratingPendingMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.IF_RATING_PENDING + CommonUtils.UserID(MainActivity.this),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Log.e("pending response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONObject techDetails = data.getJSONObject("technician_detail");
                                id_mString=techDetails.getString(GlobalConstant.id);
                                userName_mString=techDetails.getString(GlobalConstant.name);
                                imageName_mString=techDetails.getString(GlobalConstant.image);

                                ratingWindow();
                            } else {
                                // Toast.makeText(TechniciansHomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
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
        ImageView user_image = (ImageView) ratingDialog.findViewById(R.id.user_img);
        TextView user_txt = (TextView) ratingDialog.findViewById(R.id.user_txt);

        user_txt.setText(userName_mString);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(user_txt.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
        if (imageName_mString.equalsIgnoreCase("")) {

            user_image.setImageDrawable(drawable);
        } else {
            Picasso.with(this).load(GlobalConstant.TECHNICIANS_IMAGE_URL + imageName_mString).placeholder(drawable).transform(new CircleTransform()).into(user_image);


            //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
        }
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

    //-------------------Rating method-------------------
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

                            } else {
                                ratingDialog.dismiss();
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


                params.put(GlobalConstant.USERID, CommonUtils.UserID(MainActivity.this));
                params.put(GlobalConstant.favorite_user_id, id_mString);
                params.put("rating", com_star);
                params.put("rating1", service_star);
                params.put("rating2", time_star);
                params.put("rating3", skill_star);


                Log.e("Parameter for rating", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

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
