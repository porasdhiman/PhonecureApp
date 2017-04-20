package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by worksdelight on 28/02/17.
 */

public class ForgotActivity extends Activity {
    Dialog dialog2;
    TextView reset_password_view;
    EditText email_view;
    LinearLayout sign_up_btn_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_layout);
        init();

    }
public void init(){
    sign_up_btn_layout=(LinearLayout)findViewById(R.id.sign_up_btn_layout);
    reset_password_view=(TextView)findViewById(R.id.reset_password_view);
    email_view=(EditText)findViewById(R.id.email_view);
    reset_password_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(email_view.getText().length()==0){
                Toast.makeText(ForgotActivity.this,"Please enter email",Toast.LENGTH_SHORT).show();

            }else if(!CommonUtils.isEmailValid(email_view.getText().toString())) {
                Toast.makeText(ForgotActivity.this,"Please enter valid email",Toast.LENGTH_SHORT).show();
            }
            else{
                dialogWindow();
                forgotMethod();
            }

        }
    });
    sign_up_btn_layout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i=new Intent(ForgotActivity.this,RegisterActivity.class);
            startActivity(i);
            finish();
        }
    });
}
    //--------------------Social api method---------------------------------
    private void forgotMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.FORGOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                Toast.makeText(ForgotActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                finish();
                            } else {
                                Toast.makeText(ForgotActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.email, email_view.getText().toString());

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
