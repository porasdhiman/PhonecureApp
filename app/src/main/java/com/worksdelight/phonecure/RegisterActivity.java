package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
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

public class RegisterActivity extends Activity implements View.OnClickListener {
    RelativeLayout facebook_layout, twitter_layout;
    TextView sign_up_btn;
    LinearLayout sign_in_btn;
    Dialog dialog2;
    EditText password_view, name_view, email_view;
    Global global;
    //--------------facebook variable--------------
    CallbackManager callbackManager;
    LoginButton Login_TV;
    String token;
    Button facebook_btn;
    String username_mString = "", email_mString = "", id_mString = "",user_image="";
    SocialAuthAdapter adapter;
    Profile profileMap;
    int REQUEST_CHECK_SETTINGS = 100;
SharedPreferences sp,sp1;
    SharedPreferences.Editor ed,ed1;

    ImageView img;
    int i=0;
    int imgArray[]={R.drawable.line1,R.drawable.line2,R.drawable.line3,R.drawable.line4,R.drawable.line5,R.drawable.line6,R.drawable.line7,R.drawable.line8,R.drawable.line9,R.drawable.line10,R.drawable.line11,R.drawable.line12};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.register_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp=getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed=sp.edit();
        sp1=getSharedPreferences("register", Context.MODE_PRIVATE);
        ed1=sp1.edit();
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        adapter = new SocialAuthAdapter(new ResponseListener());
        callbackManager = CallbackManager.Factory.create();
        Login_TV = (LoginButton) findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email"));
        fbMethod();
        img=(ImageView) findViewById(R.id.logo_img);
        new CountDownTimer(100,10) {

            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                img.setImageResource(imgArray[i]);
                i++;
                if(i== imgArray.length-1) i=0;
                start();
            }
        }.start();
        password_view = (EditText) findViewById(R.id.password_view);
        name_view = (EditText) findViewById(R.id.name_view);

        email_view = (EditText) findViewById(R.id.email_view);

        facebook_layout = (RelativeLayout) findViewById(R.id.facebook_layout);
        twitter_layout = (RelativeLayout) findViewById(R.id.twiter_layout);
        sign_in_btn = (LinearLayout) findViewById(R.id.sign_in_linear_layout);
        sign_up_btn = (TextView) findViewById(R.id.sign_up_btn);
        facebook_layout.setOnClickListener(this);
        twitter_layout.setOnClickListener(this);
        sign_in_btn.setOnClickListener(this);
        sign_up_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_layout:
                Login_TV.performClick();


                break;
            case R.id.twiter_layout:
                adapter.authorize(RegisterActivity.this, SocialAuthAdapter.Provider.TWITTER);


                break;
            case R.id.sign_in_linear_layout:

                Intent s = new Intent(this, LoginActivity.class);
                startActivity(s);
                finish();
                break;
            case R.id.sign_up_btn:
                if (email_view.getText().length() == 0) {
                    email_view.setError("Please enter email");

                } else if (password_view.getText().length() == 0) {
                    password_view.setError("Please enter password");


                } else if (name_view.getText().length() == 0) {
                    name_view.setError("Please enter name");

                } else if (!CommonUtils.isEmailValid(email_view.getText().toString())) {
                    email_view.setError("Please enter valid email");

                } else {
                    dialogWindow();
                    registerMethod();
                }


                break;
        }
    }

    @Override
    public void onBackPressed() {

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

                        Log.e("date", object.toString());
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
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {
                finish();
                Toast.makeText(getApplicationContext(), "GPS is enabled please restart app", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void registerMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.SIGNUP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONObject data=obj.getJSONObject("data");
                                ed.putString(GlobalConstant.USERID,data.getString(GlobalConstant.id));
                                ed.putString("type","app");
                                ed.putString(GlobalConstant.name,data.getString(GlobalConstant.name));
                                ed.putString(GlobalConstant.email,data.getString(GlobalConstant.email));
                                ed.putString(GlobalConstant.type,data.getString(GlobalConstant.type));
                                ed.putString(GlobalConstant.image,data.getString(GlobalConstant.image));
                                ed.commit();
                                ed1.clear();
                                ed1.commit();


                                Intent s = new Intent(RegisterActivity.this, WalkThroughtOneActivity.class);
                                startActivity(s);
                                ed1.putString("type","register");
                                ed1.commit();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put(GlobalConstant.name, name_view.getText().toString());
                params.put(GlobalConstant.email, email_view.getText().toString());
                params.put(GlobalConstant.password, password_view.getText().toString());
                params.put(GlobalConstant.device_token, global.getDeviceToken());
                params.put(GlobalConstant.type, "user");
                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());
                params.put(GlobalConstant.device_type, "android");


                Log.e("Parameter for register", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //--------------------Social api method---------------------------------
    private void socialMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("success");
                            if (status.equalsIgnoreCase("1")) {

                            } else {
                                Toast.makeText(RegisterActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

                params.put("device_type", "android");

                Log.e("Parameter for social", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //--------------------Facebook Social api method---------------------------------
    private void FacebooksocialMethod() {


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
                                JSONObject data=obj.getJSONObject("data");
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





                                Intent s = new Intent(RegisterActivity.this, WalkThroughtOneActivity.class);
                                startActivity(s);
                                ed1.putString("type","register");
                                ed1.commit();
                                finish();
                            } else {
                                LoginManager.getInstance().logOut();
                                Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Intent f = new Intent(RegisterActivity.this, WalkThroughtOneActivity.class);
                                startActivity(f);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
