package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.LineItem;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.worksdelight.phonecure.GlobalConstant.phone;

//import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by worksdelight on 08/03/17.
 */

public class AlmostdoneActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String SERVER_BASE = "http://worksdelight.com"; // Replace with your own server
    private static final int REQUEST_CODE = Menu.FIRST;
    // private AsyncHttpClient client = new AsyncHttpClient();
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

    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    String lat, lng;
LinearLayout main_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        buildGoogleApiClient();
        main_layout=(LinearLayout) findViewById(R.id.main_layout);
        Fonts.overrideFonts(this, main_layout);
        // getToken();
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        //-------------------------------Call AutocompleteTxtView-----------------
        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.address_ed);

        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(14);
                return view;
            }
        };

        mAutocompleteView.setThreshold(1);

        mAutocompleteView.setAdapter(mAdapter);
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


        phone_ed = (EditText) findViewById(R.id.phone_ed);
        btn_start = (TextView) findViewById(R.id.btn_start);
        total_txt = (TextView) findViewById(R.id.total_txt);
        total_txt.setText("€" + getIntent().getExtras().getString("total_price"));
        if (!sp.getString("first name", "").equalsIgnoreCase("")) {
            first_name_ed.setText(sp.getString("first name", ""));
        }
        if (!sp.getString("last name", "").equalsIgnoreCase("")) {
            last_name_ed.setText(sp.getString("last name", ""));
        }
        if (!sp.getString(GlobalConstant.address, "").equalsIgnoreCase("")) {
            mAutocompleteView.setText(sp.getString(GlobalConstant.address, ""));
        }

        if (!sp.getString(GlobalConstant.phone, "").equalsIgnoreCase("")) {
            phone_ed.setText(sp.getString(GlobalConstant.phone, ""));
        }
        lat = sp.getString("ship_lat", "");
        lng = sp.getString("ship_long", "");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (first_name_ed.length() == 0) {
                    first_name_ed.setError("Please enter first name");
                } else if (last_name_ed.length() == 0) {
                    last_name_ed.setError("Please enter last name");
                } else if (mAutocompleteView.length() == 0) {
                    mAutocompleteView.setError("Please enter address");
                } else if (phone_ed.length() == 0) {
                    phone_ed.setError("Please enter Phone number");
                } else if (lat.equalsIgnoreCase("")) {
                    Toast.makeText(AlmostdoneActivity.this, "Please enter valid location name", Toast.LENGTH_SHORT).show();

                } else {

                    DropInRequest dropInRequest = new DropInRequest()
                            .tokenizationKey("sandbox_dgtkrsvt_szxk5km2k4fmrx4t")
                            .amount("€" + getIntent().getExtras().getString("total_price"))
                            .androidPayCart(getAndroidPayCart())
                            .paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
                    //.clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIyZTY4MzZhOTU5MTk1NTNjOWY5YzZiZGY0YWIwMWIxOGJhMmFmYWE5MTA4M2I5YTJhYTdhMWU4MTIyMjAyNmEwfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yOFQwNDo1NzoyNi44MDgwNjM5NjArMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=");
                    startActivityForResult(dropInRequest.getIntent(AlmostdoneActivity.this), REQUEST_CODE);
                   /* PaymentRequest paymentRequest = new PaymentRequest()
                            .tokenizationKey("sandbox_dgtkrsvt_szxk5km2k4fmrx4t")
                           // .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIzNDliMmMzMTYyODI1NDEwZGFjZWZiNDlmNzY5YmQ1YmZiZTk5YTA5Y2M0MzJlMDRhOWMzOWMxMmJkNjZjYTJmfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yM1QwNTowOTo1MC43ODc0ODMyMDIrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=")
                            .amount("€" + getIntent().getExtras().getString("total_price"))

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

    /*private void getToken() {
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
    }*/

    /*void postNonceToServer(String nonce) {
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
*/
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

                params.put(GlobalConstant.ship_address, mAutocompleteView.getText().toString());
                params.put("ship_latitude", lat);

                params.put("ship_longitude", lng);
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

            params.put(GlobalConstant.address, mAutocompleteView.getText().toString());
            params.put(GlobalConstant.latitude, lat);

            params.put(GlobalConstant.longitude, lng);
            params.put(phone, phone_ed.getText().toString());

            params.put(GlobalConstant.repair_at_shop, global.getPickUp());
            params.put(GlobalConstant.repair_on_location, global.getDropOff());

            params.put(GlobalConstant.date, global.getSendDate());
            params.put(GlobalConstant.time, global.getSendTime());
            params.put(GlobalConstant.selected_color_id, global.getColor_id());
            params.put(GlobalConstant.estimated_travel_time, global.getEstimated_travel_time());

            params.put(GlobalConstant.total_amount, getIntent().getExtras().getString("total_price"));
            params.put(GlobalConstant.payment_method_nonce, payment_method_nonce);
            JSONArray installedList = new JSONArray();
//ArrayList<HashMap<String,String>> installedList=new ArrayList<>();

            for (int i = 0; i < global.getNewListing().size(); i++) {
                JSONObject installedPackage = new JSONObject();

                // HashMap<String,String> installedPackage = new HashMap<>();
                try {
                    installedPackage.put(GlobalConstant.id, global.getNewListing().get(i).get(GlobalConstant.id));
                    installedPackage.put(GlobalConstant.quantity, global.getNewListing().get(i).get(GlobalConstant.count));
                    installedPackage.put(GlobalConstant.price, global.getNewListing().get(i).get(GlobalConstant.price));
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
                        Log.e("response", response.toString());
                        dialog2.dismiss();
                        try {


                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                ed.putString("first name", first_name_ed.getText().toString());
                                ed.putString("last name", last_name_ed.getText().toString());
                                ed.putString(GlobalConstant.address, mAutocompleteView.getText().toString());

                                ed.putString(GlobalConstant.phone, phone_ed.getText().toString());
                                ed.putString("ship_lat", lat);
                                ed.putString("ship_long", lng);
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
                Log.e("error", error.toString());


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
        loaderView.setIndicatorColor(getResources().getColor(R.color.main_color));
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");

        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }

    //-------------------------------Autolocation Method------------------------
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             * Retrieve the place ID of the selected item from the Adapter. The
			 * adapter stores each Place suggestion in a PlaceAutocomplete
			 * object from which we read the place ID.
			 */

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            //  Log.i("TAG", "placeid: " + global.getPlace_id());
            Log.i("TAG", "Autocomplete item selected: " + item.description);

			/*
             * Issue a request to the Places Geo Data API to retrieve a Place
			 * object with additional details about the place.
			 */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            //Toast.makeText(getApplicationContext(), "Clicked: " + item.description, Toast.LENGTH_SHORT).show();
            Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);

        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the
     * first place result in the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {

                Log.e("Tag", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            final CharSequence thirdPartyAttribution = places.getAttributions();
            CharSequence attributions = places.getAttributions();


            //------------Place.getLatLng use for get Lat long According to select location name-------------------
            String latlong = place.getLatLng().toString().split(":")[1];
            String completeLatLng = latlong.substring(1, latlong.length() - 1);
            // Toast.makeText(MapsActivity.this,completeLatLng,Toast.LENGTH_SHORT).show();
            lat = completeLatLng.split(",")[0];
            lat = lat.substring(1, lat.length());
            lng = completeLatLng.split(",")[1];

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id, CharSequence address,
                                              CharSequence phoneNumber, Uri websiteUri) {
        Log.e("Tag", res.getString(R.string.place_details, name, id, address, phoneNumber, websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber, websiteUri));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state
        // and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mAdapter.setGoogleApiClient(mGoogleApiClient);


        Log.i("search", "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mAdapter.setGoogleApiClient(null);
        Log.e("search", "Google Places API connection suspended.");
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();
    }

}
