package com.worksdelight.phonecure;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 10/04/17.
 */

public class AddressActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    ImageView back;
    EditText first_name_ed, last_name_ed, address_ed, city_ed, zip_ed, phone_ed;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Global global;
    Dialog dialog2;
    TextView submit_btn;
String lat,lng;
    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    TextView edit_txt;
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
        buildGoogleApiClient();
        back = (ImageView) findViewById(R.id.back);
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
        first_name_ed = (EditText) findViewById(R.id.first_name_ed);
        last_name_ed = (EditText) findViewById(R.id.last_name_ed);
        phone_ed = (EditText) findViewById(R.id.phone_ed);
        submit_btn = (TextView) findViewById(R.id.submit_btn);
        first_name_ed.setEnabled(false);
        last_name_ed.setEnabled(false);
        phone_ed.setEnabled(false);
        mAutocompleteView.setEnabled(false);
        edit_txt=(TextView)findViewById(R.id.edit_txt);
        edit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first_name_ed.setEnabled(true);
                last_name_ed.setEnabled(true);
                phone_ed.setEnabled(true);
                mAutocompleteView.setEnabled(true);
                submit_btn.setVisibility(View.VISIBLE);
                edit_txt.setVisibility(View.GONE);
            }
        });
        //address_ed = (EditText) findViewById(R.id.address_ed);
      /*  city_ed = (EditText) findViewById(R.id.city_ed);
        zip_ed = (EditText) findViewById(R.id.zip_ed);*/

        if (!sp.getString("first name", "").equalsIgnoreCase("")) {
            first_name_ed.setText(cap(sp.getString("first name", "")));
        }
        if (!sp.getString("last name", "").equalsIgnoreCase("")) {
            last_name_ed.setText(cap(sp.getString("last name", "")));
        }
        if (!sp.getString(GlobalConstant.address, "").equalsIgnoreCase("")) {
            mAutocompleteView.setText(cap(sp.getString(GlobalConstant.address, "")));
        }
        lat=sp.getString(GlobalConstant.latitude,"");
        lng=sp.getString(GlobalConstant.longitude,"");
        /*if (!sp.getString("city", "").equalsIgnoreCase("")) {
            city_ed.setText(sp.getString("city", ""));
        }
        if (!sp.getString("zip", "").equalsIgnoreCase("")) {
            zip_ed.setText(sp.getString("zip", ""));
        }*/
        if (!sp.getString(GlobalConstant.phone, "").equalsIgnoreCase("")) {
            phone_ed.setText(cap(sp.getString(GlobalConstant.phone, "")));
        }

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (first_name_ed.length() == 0) {
                    first_name_ed.setError("Please enter first name");
                } else if (last_name_ed.length() == 0) {
                    last_name_ed.setError("Please enter last name");
                } else if (mAutocompleteView.length() == 0) {
                    mAutocompleteView.setError("Please enter address");
                } /*else if (city_ed.length() == 0) {
                    city_ed.setError("Please enter city");
                } else if (zip_ed.length() == 0) {
                    zip_ed.setError("Please enter Zip");
                }*/ else if (phone_ed.length() == 0) {
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
                Intent i=new Intent();
                setResult(0);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent();
        setResult(0);
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

                                ed.putString("first name", first_name_ed.getText().toString());
                                ed.putString("last name", last_name_ed.getText().toString());
                                ed.putString(GlobalConstant.address, mAutocompleteView.getText().toString());
                               ed.putString(GlobalConstant.latitude,lat);
                                ed.putString(GlobalConstant.longitude,lng);
                                ed.putString(GlobalConstant.phone, phone_ed.getText().toString());
                                ed.commit();
                                Toast.makeText(AddressActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                               /* Intent i = new Intent(AddressActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);*/
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

                params.put(GlobalConstant.ship_address, mAutocompleteView.getText().toString());
                params.put(GlobalConstant.latitude, lat);

                params.put(GlobalConstant.longitude, lng);
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
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
