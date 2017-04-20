package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
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
 * Created by worksdelight on 07/04/17.
 */

public class ChangePasswordActivity extends Activity {
    EditText old_password_ed, new_password_ed, confirm_password_ed;
    TextView reset_password_view;
    ImageView back;
Dialog dialog2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        init();
    }

    public void init() {
        back = (ImageView) findViewById(R.id.back);
        old_password_ed = (EditText) findViewById(R.id.old_password_ed);
        new_password_ed = (EditText) findViewById(R.id.new_password_ed);
        confirm_password_ed = (EditText) findViewById(R.id.confirm_password_ed);
        reset_password_view = (TextView) findViewById(R.id.reset_password_view);
        reset_password_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (old_password_ed.length() == 0) {
                    old_password_ed.setError("Please Enter Password");
                } else if (new_password_ed.length() == 0) {
                    new_password_ed.setError("Please Enter Your New Password");
                } else if (confirm_password_ed.length() == 0) {
                    confirm_password_ed.setError("Please Enter Your Confirm Password");
                } else if (!confirm_password_ed.getText().toString().equalsIgnoreCase(new_password_ed.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "Password Do Not Match", Toast.LENGTH_SHORT).show();
                } else {
                    dialogWindow();
                    changePassMethod();
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

    private void changePassMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.CHANGEPASSWORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {


                                Toast.makeText(ChangePasswordActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                old_password_ed.setText("");
                                new_password_ed.setText("");
                                confirm_password_ed.setText("");

                            } else {
                                Toast.makeText(ChangePasswordActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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


                params.put(GlobalConstant.USERID, CommonUtils.UserID(ChangePasswordActivity.this));
                params.put(GlobalConstant.old_password, old_password_ed.getText().toString());
                params.put(GlobalConstant.new_password, new_password_ed.getText().toString());
                params.put(GlobalConstant.confirm_password, confirm_password_ed.getText().toString());


                Log.e("Parameter Change pass", params.toString());
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
