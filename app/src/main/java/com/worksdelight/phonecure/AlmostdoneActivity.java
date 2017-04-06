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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.LineItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.worksdelight.phonecure.GlobalConstant.phone;
import static com.worksdelight.phonecure.GlobalConstant.zip;

/**
 * Created by worksdelight on 08/03/17.
 */

public class AlmostdoneActivity extends Activity {
    private static final String SERVER_BASE = "http://worksdelight.com"; // Replace with your own server
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken;
    TextView btn_start;
    EditText first_name_ed, last_name_ed, address_ed, city_ed, zip_ed, phone_ed;
    TextView total_txt;
    BraintreeFragment mBraintreeFragment;
    Dialog dialog2;
    Global global;
    String payment_method_nonce = "";
    ImageView back;
    TextView second_count_img, third_count_img;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    ImageView second_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        // getToken();
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        second_count_img = (TextView) findViewById(R.id.second_count_img);
        third_count_img = (TextView) findViewById(R.id.third_count_img);
        second_line = (ImageView) findViewById(R.id.second_line);
        first_name_ed = (EditText) findViewById(R.id.first_name_ed);
        last_name_ed = (EditText) findViewById(R.id.last_name_ed);
        address_ed = (EditText) findViewById(R.id.address_ed);
        city_ed = (EditText) findViewById(R.id.city_ed);
        zip_ed = (EditText) findViewById(R.id.zip_ed);
        phone_ed = (EditText) findViewById(R.id.phone_ed);
        btn_start = (TextView) findViewById(R.id.btn_start);
        total_txt = (TextView) findViewById(R.id.total_txt);
        total_txt.setText("$" + getIntent().getExtras().getString("total_price"));
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
        btn_start.setOnClickListener(new View.OnClickListener() {
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
                } else if (phone_ed.length() != 10) {
                    phone_ed.setError("Please enter correct Phone number");
                } else {

                    DropInRequest dropInRequest = new DropInRequest()
                            .tokenizationKey("sandbox_dgtkrsvt_szxk5km2k4fmrx4t")
                            .amount("$" + getIntent().getExtras().getString("total_price"))
                            .androidPayCart(getAndroidPayCart())
                            .paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
                    //.clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIyZTY4MzZhOTU5MTk1NTNjOWY5YzZiZGY0YWIwMWIxOGJhMmFmYWE5MTA4M2I5YTJhYTdhMWU4MTIyMjAyNmEwfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yOFQwNDo1NzoyNi44MDgwNjM5NjArMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=");
                    startActivityForResult(dropInRequest.getIntent(AlmostdoneActivity.this), REQUEST_CODE);
                   /* PaymentRequest paymentRequest = new PaymentRequest()
                            .tokenizationKey("sandbox_dgtkrsvt_szxk5km2k4fmrx4t")
                           // .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIzNDliMmMzMTYyODI1NDEwZGFjZWZiNDlmNzY5YmQ1YmZiZTk5YTA5Y2M0MzJlMDRhOWMzOWMxMmJkNjZjYTJmfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yM1QwNTowOTo1MC43ODc0ODMyMDIrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=")
                            .amount("$" + getIntent().getExtras().getString("total_price"))

                            .primaryDescription("Awesome payment")
                            .secondaryDescription("Using the Client SDK")
                            .submitButtonText("Pay").androidPayCart(getAndroidPayCart());
                  //  paymentRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));

                    startActivityForResult(paymentRequest.getIntent(AlmostdoneActivity.this), REQUEST_CODE);*/
                }
            }
        });
    }

    private Cart getAndroidPayCart() {
        return Cart.newBuilder()
                .setCurrencyCode("USD")
                .setTotalPrice(getIntent().getExtras().getString("total_price"))
                .addLineItem(LineItem.newBuilder()
                        .setCurrencyCode("USD")
                        .setDescription("Description")
                        .setQuantity("1")
                        .setUnitPrice(getIntent().getExtras().getString("total_price"))
                        .setTotalPrice(getIntent().getExtras().getString("total_price"))
                        .build())
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
                payment_method_nonce = result.getPaymentMethodNonce().getNonce();
                ViewGroup.LayoutParams params = second_count_img.getLayoutParams();
                params.height = 50;
                params.width = 50;
                second_count_img.setLayoutParams(params);
                second_count_img.setBackgroundResource(R.drawable.step_one_right_light);
                /*ViewGroup.LayoutParams params1 = third_count_img.getLayoutParams();
                params1.height = 50;
                params1.height = 50;
                third_count_img.setLayoutParams(params1);*/
                third_count_img.setBackgroundResource(R.drawable.step_one_right_light);
                second_line.setBackgroundResource(R.drawable.linew_white);

                dialogWindow();
                bookingMethod();
                // Toast.makeText(AlmostdoneActivity.this,result.getPaymentMethodNonce().getNonce(),Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }

      /*  if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
            payment_method_nonce = paymentMethodNonce.getNonce();
            dialogWindow();
            bookingMethod();
            *//*RequestParams requestParams = new RequestParams();
            requestParams.put("payment_method_nonce", paymentMethodNonce.getNonce());
            requestParams.put("amount", "10.00");

            client.post(SERVER_BASE + "/payment", requestParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                   // Toast.makeText(PaymentInfoActivity.this, responseString, Toast.LENGTH_LONG).show();
                    Log.e("respnse string",responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                   // Toast.makeText(PaymentInfoActivity.this, responseString, Toast.LENGTH_LONG).show();
                    Log.e("respnse string",responseString);
                }
            });*//*
        }*/

    }

    private void getToken() {
        client.get(SERVER_BASE + "/token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // findViewById(R.id.btn_start).setEnabled(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                clientToken = responseString;
                //findViewById(R.id.btn_start).setEnabled(true);
            }
        });
    }

    void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce);
        client.post(SERVER_BASE + "/payment", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(AlmostdoneActivity.this, responseString, Toast.LENGTH_LONG).show();
                Log.e("respnse string", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(AlmostdoneActivity.this, "1", Toast.LENGTH_LONG).show();
                Log.e("respnse string", responseString);
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
                                global.setDateList(null);
                                Intent i = new Intent(AlmostdoneActivity.this, AfterPaymentActivity.class);
                                startActivity(i);


                            } else {
                                Toast.makeText(AlmostdoneActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.id, CommonUtils.UserID(AlmostdoneActivity.this));

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


    //--------------------search api method---------------------------------
    private void bookingMethod() {
        //Map<String, String> params = new HashMap<String, String>();

        JSONObject params = new JSONObject();

        try {
            params.put(GlobalConstant.USERID, CommonUtils.UserID(AlmostdoneActivity.this));
            params.put(GlobalConstant.technician_id, global.getDateList().get(Integer.parseInt(getIntent().getExtras().getString("pos"))).get(GlobalConstant.id));

            params.put(GlobalConstant.firstname, first_name_ed.getText().toString());
            params.put(GlobalConstant.lastname, last_name_ed.getText().toString());

            params.put(GlobalConstant.address, address_ed.getText().toString());
            params.put(GlobalConstant.city, city_ed.getText().toString());

            params.put(zip, zip_ed.getText().toString());
            params.put(phone, phone_ed.getText().toString());

            params.put(GlobalConstant.date, global.getSendDate());
            params.put(GlobalConstant.time, global.getSendTime());
            params.put(GlobalConstant.total_amount, getIntent().getExtras().getString("total_price"));
            params.put(GlobalConstant.payment_method_nonce, payment_method_nonce);
            JSONArray installedList = new JSONArray();
//ArrayList<HashMap<String,String>> installedList=new ArrayList<>();
            if (getIntent().getExtras().getString("selected_id").contains(",")) {
                ArrayList<String> selectList = new ArrayList<>(Arrays.asList(getIntent().getExtras().getString("selected_id").split(",")));
                for (int i = 0; i < selectList.size(); i++) {
                    JSONObject installedPackage = new JSONObject();

                    // HashMap<String,String> installedPackage = new HashMap<>();
                    try {
                        installedPackage.put(GlobalConstant.id, selectList.get(i));
                        installedPackage.put(GlobalConstant.quantity, global.getNewListing().get(i).get(GlobalConstant.count));
                        installedPackage.put(GlobalConstant.price, global.getPrcieListing().get(i).get(GlobalConstant.price));
                        installedList.put(installedPackage);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } else {


                JSONObject installedPackage = new JSONObject();
                //  HashMap<String,String> installedPackage = new HashMap<>();

                try {
                    installedPackage.put(GlobalConstant.id, getIntent().getExtras().getString("selected_id"));
                    installedPackage.put(GlobalConstant.quantity, global.getNewListing().get(0).get(GlobalConstant.count));
                    installedPackage.put(GlobalConstant.price, global.getPrcieListing().get(0).get(GlobalConstant.price));
                    installedList.put(installedPackage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            // String dataToSend = installedList.toString();
            params.put(GlobalConstant.booking_items, installedList);
            Log.e("params valye", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, GlobalConstant.BOOKING_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        dialog2.dismiss();
                        try {


                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                ed.putString("first name", first_name_ed.getText().toString());
                                ed.putString("last name", last_name_ed.getText().toString());
                                ed.putString("address", address_ed.getText().toString());
                                ed.putString("city", city_ed.getText().toString());
                                ed.putString("zip", zip_ed.getText().toString());
                                ed.putString("phone", phone_ed.getText().toString());
                                ed.commit();
                                global.setDateList(null);
                                profileMethod();

                            } else {
                                Toast.makeText(AlmostdoneActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog2.dismiss();
                Toast.makeText(AlmostdoneActivity.this, error.toString(), Toast.LENGTH_SHORT).show();


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
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
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

}
