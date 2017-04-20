package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 10/04/17.
 */

public class AddressActivity extends Activity {
    ImageView back;
    EditText first_name_ed, last_name_ed, address_ed, city_ed, zip_ed, phone_ed;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Global global;
    Dialog dialog2;
    TextView submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        back = (ImageView) findViewById(R.id.back);
        first_name_ed = (EditText) findViewById(R.id.first_name_ed);
        last_name_ed = (EditText) findViewById(R.id.last_name_ed);
        address_ed = (EditText) findViewById(R.id.address_ed);
        city_ed = (EditText) findViewById(R.id.city_ed);
        zip_ed = (EditText) findViewById(R.id.zip_ed);
        phone_ed = (EditText) findViewById(R.id.phone_ed);
        if (!sp.getString("first name", "").equalsIgnoreCase("")) {
            first_name_ed.setText(sp.getString("first name", ""));
        }
        if (!sp.getString("last name", "").equalsIgnoreCase("")) {
            last_name_ed.setText(sp.getString("last name", ""));
        }
        if (!sp.getString("address", "").equalsIgnoreCase("")) {
            address_ed.setText(sp.getString("address", ""));
        }
        if (!sp.getString("city", "").equalsIgnoreCase("")) {
            city_ed.setText(sp.getString("city", ""));
        }
        if (!sp.getString("zip", "").equalsIgnoreCase("")) {
            zip_ed.setText(sp.getString("zip", ""));
        }
        if (!sp.getString("phone", "").equalsIgnoreCase("")) {
            phone_ed.setText(sp.getString("phone", ""));
        }
        submit_btn = (TextView) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (first_name_ed.length() == 0) {
                    first_name_ed.setError("Please enter first name");
                } else if (last_name_ed.length() == 0) {
                    last_name_ed.setError("Please enter last name");
                } else if (address_ed.length() == 0) {
                    address_ed.setError("Please enter address");
                } else if (city_ed.length() == 0) {
                    city_ed.setError("Please enter city");
                } else if (zip_ed.length() == 0) {
                    zip_ed.setError("Please enter Zip");
                } else if (phone_ed.length() == 0) {
                    phone_ed.setError("Please enter Phone number");
                } else {
                    dialogWindow();
                    profileMethod();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //--------------------search api method---------------------------------
    private void profileMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("responsefff", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                Toast.makeText(AddressActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                ed.putString("first name", first_name_ed.getText().toString());
                                ed.putString("last name", last_name_ed.getText().toString());
                                ed.putString("address", address_ed.getText().toString());
                                ed.putString("city", city_ed.getText().toString());
                                ed.putString("zip", zip_ed.getText().toString());
                                ed.putString("phone", phone_ed.getText().toString());
                                ed.commit();


                            } else {
                                Toast.makeText(AddressActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.id, CommonUtils.UserID(AddressActivity.this));

                params.put(GlobalConstant.ship_firstname, first_name_ed.getText().toString());
                params.put(GlobalConstant.ship_lastname, last_name_ed.getText().toString());

                params.put(GlobalConstant.ship_address, address_ed.getText().toString());
                params.put(GlobalConstant.ship_city, city_ed.getText().toString());

                params.put(GlobalConstant.ship_zip, zip_ed.getText().toString());
                params.put(GlobalConstant.ship_phone, phone_ed.getText().toString());


                Log.e("Parameter for profile", params.toString());
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
