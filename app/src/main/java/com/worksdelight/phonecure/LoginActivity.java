package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.wang.avi.AVLoadingIndicatorView;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.worksdelight.phonecure.GlobalConstant.facebook_id;
import static com.worksdelight.phonecure.GlobalConstant.twitter_id;

/**
 * Created by worksdelight on 01/03/17.
 */

public class LoginActivity extends FragmentActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    RelativeLayout facebook_layout, twitter_layout;
    TextView sign_in_btn, sign_up_btn, sign_up_user, forgot_view;
    EditText password_view, email_view;
    //--------------facebook variable--------------
    CallbackManager callbackManager;
    LoginButton Login_TV;
    String token;
    Button facebook_btn;
    String username_mString = "", email_mString = "", id_mString = "";
    SocialAuthAdapter adapter;
    Profile profileMap;


    Global global;

    String user_image;
    Dialog dialog2, fb_dialog;
    SharedPreferences sp, sp1;
    SharedPreferences.Editor ed, ed1;
    ImageView img;
    int i = 0;
    private Animation mEnterAnimation, mExitAnimation;

    int imgArray[] = {R.drawable.line1, R.drawable.line2, R.drawable.line3, R.drawable.line4, R.drawable.line5, R.drawable.line6, R.drawable.line7, R.drawable.line8, R.drawable.line9, R.drawable.line10, R.drawable.line11, R.drawable.line12};
//--------------Location lat long variable---------

    private Location mLastLocation, myLocation;
    LocationManager mLocationManager;
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */

    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
RelativeLayout main_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.login_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        sp1 = getSharedPreferences("register", Context.MODE_PRIVATE);
        ed1 = sp1.edit();
        buildGoogleApiClient();
        init();
    }

    public void init() {

        main_layout=(RelativeLayout)findViewById(R.id.main_layout);
        Fonts.overrideFonts(this, main_layout);
        adapter = new SocialAuthAdapter(new ResponseListener());
        callbackManager = CallbackManager.Factory.create();
        Login_TV = (LoginButton) findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email"));
        fbMethod();
        global = (Global) getApplicationContext();
        img = (ImageView) findViewById(R.id.logo_img);

        new CountDownTimer(100, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                img.setImageResource(imgArray[i]);
                i++;
                if (i == imgArray.length - 1) i = 0;
                start();
            }
        }.start();
       /* try {
            GifDrawable gifFromResource = new GifDrawable( getResources(), R.drawable.logo );
            img.setImageDrawable(gifFromResource);

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        password_view = (EditText) findViewById(R.id.password_view);


        email_view = (EditText) findViewById(R.id.email_view);
        facebook_layout = (RelativeLayout) findViewById(R.id.facebook_layout);
        twitter_layout = (RelativeLayout) findViewById(R.id.twiter_layout);
        sign_in_btn = (TextView) findViewById(R.id.sign_in_btn);
        sign_up_btn = (TextView) findViewById(R.id.sign_up_btn);
        sign_up_user = (TextView) findViewById(R.id.sign_up_user);
        forgot_view = (TextView) findViewById(R.id.forgot_view);
        Fonts.overrideFonts1(this, forgot_view);
        facebook_layout.setOnClickListener(this);
        twitter_layout.setOnClickListener(this);
        twitter_layout.setVisibility(View.GONE);
        sign_in_btn.setOnClickListener(this);
        sign_up_btn.setOnClickListener(this);
        sign_up_user.setOnClickListener(this);
        forgot_view.setOnClickListener(this);


       /* ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription("Login with facebook")

                        .setGravity(Gravity.TOP)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(facebook_layout);
        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription("Register as a user")

                        .setGravity(Gravity.TOP)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(sign_up_user);
        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription("Register as a technician")

                        .setGravity(Gravity.TOP)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(sign_up_btn);
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(300);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(300);
        mExitAnimation.setFillAfter(true);
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(new Pointer().setColor(getResources().getColor(R.color.main_color)).setGravity(Gravity.CENTER))
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();


        ChainTourGuide.init(this).playInSequence(sequence);*/

    }


    //---------------------------facebook method------------------------------
    public void fbMethod() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                token = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        // Application code

                        Log.e("date", response.toString());
                        try {
                            username_mString = object.getString("name");
                            if (object.has("email")) {
                                email_mString = object.getString("email");
                            } else {
                                //  email = "";
                            }
                            id_mString = object.getString("id");

                            try {
                                if (android.os.Build.VERSION.SDK_INT > 9) {
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
                                    user_image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Log.e("profile image", user_image);
                                    /*URL fb_url = new URL(profilePicUrl);//small | noraml | large
                                    HttpsURLConnection conn1 = (HttpsURLConnection) fb_url.openConnection();
                                    HttpsURLConnection.setFollowRedirects(true);
                                    conn1.setInstanceFollowRedirects(true);
                                    Bitmap fb_img = BitmapFactory.decodeStream(conn1.getInputStream());
                                    //image.setImageBitmap(fb_img);*/
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            //gender = object.getString("gender");
                            //birthday = object.getString("birthday");
                            dialogWindow();
                            FacebooksocialMethod();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "picture.type(large),bio,id,name,link,gender,email, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                Toast.makeText(LoginActivity.this, selectedPlace.toString(), Toast.LENGTH_SHORT).show();
                // Do something with the place
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_layout:
                if (CommonUtils.getConnectivityStatus(LoginActivity.this)) {
                    Login_TV.performClick();
                } else {
                    CommonUtils.openInternetDialog(LoginActivity.this);
                }

                break;
            case R.id.twiter_layout:
                adapter.authorize(LoginActivity.this, SocialAuthAdapter.Provider.TWITTER);

                break;
            case R.id.sign_in_btn:

                if (email_view.getText().length() == 0) {
                    email_view.setError(getResources().getString(R.string.email_blank));

                } else if (password_view.getText().length() == 0) {
                    password_view.setError(getResources().getString(R.string.password_blank));


                } else if (!CommonUtils.isEmailValid(email_view.getText().toString())) {
                    email_view.setError(getResources().getString(R.string.email_valid));

                } else {

                    if (CommonUtils.getConnectivityStatus(LoginActivity.this)) {

                        dialogWindow();
                        loginMethod();
                    } else {
                        CommonUtils.openInternetDialog(LoginActivity.this);
                    }
                }

                break;
            case R.id.sign_up_btn:
                if (CommonUtils.getConnectivityStatus(LoginActivity.this)) {

                    Intent su = new Intent(this, TechniciansRegister.class);
                    su.putExtra("type","1");
                    startActivity(su);
                    finish();
                } else {
                    CommonUtils.openInternetDialog(this);
                }

                break;
            case R.id.sign_up_user:
                if (CommonUtils.getConnectivityStatus(LoginActivity.this)) {

                    Intent us = new Intent(this, RegisterActivity.class);
                    startActivity(us);
                    finish();
                } else {
                    CommonUtils.openInternetDialog(this);
                }
                break;
            case R.id.forgot_view:
                if (CommonUtils.getConnectivityStatus(LoginActivity.this)) {
                    Intent f = new Intent(this, ForgotActivity.class);
                    startActivity(f);
                } else {
                    CommonUtils.openInternetDialog(this);
                }

                break;
        }
    }

    private final class ResponseListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {

            token = adapter.getCurrentProvider().getAccessGrant().getKey();
            Log.e("token", token);
            adapter.getUserProfileAsync(new SocialAuthListener() {

                @Override
                public void onExecute(String s, Object o) {
                    profileMap = adapter.getUserProfile();
                    Log.e("info", profileMap.toString());
                    email_mString = profileMap.getEmail();
                    username_mString = profileMap.getFullName();

                    id_mString = profileMap.getValidatedId();
                    global.setSocialAuthAdpater(adapter);
                    dialogWindow();
                    twittersocialMethod();
                    Log.e("info", email_mString + "," + username_mString + "," + id_mString);

                }

                @Override
                public void onError(SocialAuthError socialAuthError) {

                }
            });
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onBack() {

        }
    }


    private void loginMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.LOGIN_URL,
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


                                ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                                ed.putString("type", "app");

                                ed.putString(GlobalConstant.type, data.getString(GlobalConstant.type));

                                if (data.getString(GlobalConstant.type).equalsIgnoreCase("user")) {
                                    ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                                    ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                                    ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                                    ed.putString(GlobalConstant.latitude, data.getString(GlobalConstant.latitude));
                                    ed.putString(GlobalConstant.longitude, data.getString(GlobalConstant.longitude));
                                    Object intervention = data.get("shipping_address");
                                    if (intervention instanceof JSONArray) {
                                        // It's an array

                                    } else {

                                        JSONObject shipping_address = data.getJSONObject("shipping_address");
                                        ed.putString("first name", shipping_address.getString("ship_firstname"));
                                        ed.putString("last name", shipping_address.getString("ship_lastname"));
                                        ed.putString(GlobalConstant.address, shipping_address.getString("ship_address"));

                                        ed.putString(GlobalConstant.phone, shipping_address.getString("ship_phone"));
                                        ed.putString("ship_lat", shipping_address.getString("ship_latitude"));
                                        ed.putString("ship_long", shipping_address.getString("ship_longitude"));
                                    }

                                    ed.commit();
                                    ed1.putString("type", "login");
                                    ed1.commit();
                                    Intent s = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(s);
                                    finish();
                                } else {
                                    ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                                    ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                                    ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                                    ed.putString(GlobalConstant.vat_number, data.getString(GlobalConstant.vat_number));
                                    ed.putString(GlobalConstant.organization, data.getString(GlobalConstant.organization));
                                    ed.putString(GlobalConstant.address, data.getString(GlobalConstant.address));
                                    ed.putString(GlobalConstant.address_latitude, data.getString(GlobalConstant.address_latitude));
                                    ed.putString(GlobalConstant.address_longitude, data.getString(GlobalConstant.address_longitude));
                                    ed.commit();
                                    ed1.putString("type", "login");
                                    ed1.putString("techDevice", "1");
                                    ed1.putString("techShowDevice", "1");
                                    ed1.putString("techService", "1");
                                    ed1.putString("techProduct", "1");
                                    ed1.putString("techOtherDevice", "1");
                                    ed1.commit();
                                    Intent s = new Intent(LoginActivity.this, TechniciansHomeActivity.class);
                                    startActivity(s);
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put(GlobalConstant.email, email_view.getText().toString());
                params.put(GlobalConstant.password, password_view.getText().toString());
                params.put(GlobalConstant.device_token, global.getDeviceToken());
                params.put(GlobalConstant.type, "user");
                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());
                params.put(GlobalConstant.device_type, "android");


                Log.e("Parameter for Login", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //--------------------Facebook Social api method---------------------------------
    private void FacebooksocialMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.FACEBOOK_URL,
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


                                ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                                ed.putString("type", "facebook");

                                ed.putString(GlobalConstant.type, data.getString(GlobalConstant.type));

                                if (data.getString(GlobalConstant.type).equalsIgnoreCase("user")) {
                                    ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                                    ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                                    ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                                    ed.putString(GlobalConstant.latitude, data.getString(GlobalConstant.latitude));
                                    ed.putString(GlobalConstant.longitude, data.getString(GlobalConstant.longitude));
                                    Object intervention = data.get("shipping_address");
                                    if (intervention instanceof JSONArray) {
                                        // It's an array

                                    } else {

                                        JSONObject shipping_address = data.getJSONObject("shipping_address");
                                        ed.putString("first name", shipping_address.getString("ship_firstname"));
                                        ed.putString("last name", shipping_address.getString("ship_lastname"));
                                        ed.putString(GlobalConstant.address, shipping_address.getString("ship_address"));

                                        ed.putString(GlobalConstant.phone, shipping_address.getString("ship_phone"));
                                        ed.putString("ship_lat", shipping_address.getString("ship_latitude"));
                                        ed.putString("ship_long", shipping_address.getString("ship_longitude"));
                                    }

                                    ed.commit();
                                    ed1.putString("type", "login");
                                    ed1.commit();
                                    Intent s = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(s);
                                    finish();
                                } else {
                                    ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                                    ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                                    ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                                    ed.putString(GlobalConstant.vat_number, data.getString(GlobalConstant.vat_number));
                                    ed.putString(GlobalConstant.organization, data.getString(GlobalConstant.organization));
                                    ed.putString(GlobalConstant.address, data.getString(GlobalConstant.address));
                                    ed.putString(GlobalConstant.address_latitude, data.getString(GlobalConstant.address_latitude));
                                    ed.putString(GlobalConstant.address_longitude, data.getString(GlobalConstant.address_longitude));
                                    ed.commit();
                                    ed1.putString("type", "login");
                                    ed1.putString("techDevice", "1");
                                    ed1.putString("techShowDevice", "1");
                                    ed1.putString("techService", "1");
                                    ed1.putString("techProduct", "1");
                                    ed1.putString("techOtherDevice", "1");
                                    ed1.commit();
                                    Intent s = new Intent(LoginActivity.this, TechniciansHomeActivity.class);
                                    startActivity(s);
                                    finish();
                                }
                            } else {
                                facebookdialogWindow();
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


                params.put(facebook_id, id_mString);
                params.put(GlobalConstant.device_token, global.getDeviceToken());

                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());
                params.put(GlobalConstant.device_type, "android");


                Log.e("Parameter for social", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //--------------------Facebook Social api method---------------------------------
    private void FacebookRegistersocialMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.FACEBOOK_REGISTER_URL,
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
                                ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                                ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                                ed.putString(GlobalConstant.latitude, data.getString(GlobalConstant.latitude));
                                ed.putString(GlobalConstant.longitude, data.getString(GlobalConstant.longitude));
                                ed.putString("type", "facebook");
                                ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                                ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                                ed.putString(GlobalConstant.type, data.getString(GlobalConstant.type));
                                ed.commit();
                                ed1.clear();
                                ed1.commit();


                                Intent s = new Intent(LoginActivity.this, WalkThroughtOneActivity.class);
                                startActivity(s);
                                ed1.putString("type", "register");
                                ed1.commit();
                                finish();
                            } else {

                                Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

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

                params.put(GlobalConstant.name, username_mString);
                params.put(GlobalConstant.email, email_mString);
                params.put(facebook_id, id_mString);
                params.put(GlobalConstant.device_token, global.getDeviceToken());
                params.put(GlobalConstant.type, "user");
                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());
                params.put(GlobalConstant.device_type, "android");
                params.put(GlobalConstant.image, user_image);


                Log.e("Parameter for social", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //--------------------Facebook Social api method---------------------------------
    private void twittersocialMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.TWITTER_URL,
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
                                ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                                ed.putString("type", "app");
                                ed.commit();
                                Intent f = new Intent(LoginActivity.this, WalkThroughtOneActivity.class);
                                startActivity(f);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                LoginManager.getInstance().logOut();
                                fb_dialog.dismiss();
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

                params.put(GlobalConstant.name, username_mString);
                params.put(GlobalConstant.email, "");
                params.put(twitter_id, id_mString);
                params.put(GlobalConstant.device_token, global.getDeviceToken());
                params.put(GlobalConstant.type, "user");
                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());
                params.put(GlobalConstant.device_type, "android");


                Log.e("Parameter for social", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //---------------------------Progrees Dialog-----------------------
    public void facebookdialogWindow() {
        fb_dialog = new Dialog(this);
        fb_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fb_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fb_dialog.setCanceledOnTouchOutside(false);
        fb_dialog.setCancelable(false);
        fb_dialog.setContentView(R.layout.facebook_layout_dialog);
        TextView user = (TextView) fb_dialog.findViewById(R.id.user_txtView);
        TextView tech = (TextView) fb_dialog.findViewById(R.id.tech_txtView);
        TextView cancel = (TextView) fb_dialog.findViewById(R.id.cancel_txtView);
        user.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWindow();
                FacebookRegistersocialMethod();
            }
        });
        tech.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, TechniciansRegister.class);
                i.putExtra("type","0");
                i.putExtra("fb_id",id_mString);
                i.putExtra("email",email_mString);
                i.putExtra("name",username_mString);
                i.putExtra("image",user_image);
                startActivity(i);
                finish();

            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                fb_dialog.dismiss();
            }
        });
        fb_dialog.show();
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
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
