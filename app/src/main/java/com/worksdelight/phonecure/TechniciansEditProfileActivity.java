package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
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
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by worksdelight on 20/04/17.
 */

public class TechniciansEditProfileActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    ImageView back;
    RelativeLayout main_layout;

    EditText name_ed, email_ed, vat_ed;
    TextView org_ed, address_ed;
    CircleImageView tech_img;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    String selectedImagePath = "";
    Dialog camgllry, dialog2;
    ImageView camer_click;
    TextView edit_txt, update_profile;
    String message;
    HttpEntity resEntity;
    boolean isVat = false;

    String lat, lng;
    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    boolean clickValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technician_edit_profile_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        buildGoogleApiClient();
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
        camer_click = (ImageView) findViewById(R.id.camer_click);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hideSoftKeyboard(TechniciansEditProfileActivity.this);
                finish();
            }
        });
        main_layout = (RelativeLayout) findViewById(R.id.main_layout);
        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent w = new Intent(TechniciansEditProfileActivity.this, WorkingHoursUpdateActivity.class);
                startActivity(w);
            }
        });
        update_profile = (TextView) findViewById(R.id.update_profile);
        edit_txt = (TextView) findViewById(R.id.edit_txt);
        tech_img = (CircleImageView) findViewById(R.id.tech_img);
        name_ed = (EditText) findViewById(R.id.name_ed);
        email_ed = (EditText) findViewById(R.id.email_ed);
        vat_ed = (EditText) findViewById(R.id.vat_ed);
        org_ed = (TextView) findViewById(R.id.org_ed);

        name_ed.setEnabled(false);
        email_ed.setEnabled(false);
        vat_ed.setEnabled(false);
        mAutocompleteView.setEnabled(false);
        name_ed.setText(sp.getString(GlobalConstant.name, ""));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(name_ed.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
        if (sp.getString(GlobalConstant.image, "").contains("storage")) {
            if(sp.getString(GlobalConstant.image, "").equalsIgnoreCase("")){
                tech_img.setImageDrawable(drawable);
            }else{
                Picasso.with(this).load(new File(sp.getString(GlobalConstant.image, ""))).placeholder(drawable).transform(new CircleTransform()).into(tech_img);

            }
        } else {
            Picasso.with(this).load(GlobalConstant.TECH_IMAGE_URL+sp.getString(GlobalConstant.image, "")).placeholder(drawable).transform(new CircleTransform()).into(tech_img);


            //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
        }







        email_ed.setText(sp.getString(GlobalConstant.email, ""));
        vat_ed.setText(sp.getString(GlobalConstant.vat_number, ""));

        mAutocompleteView.setText(sp.getString(GlobalConstant.address, ""));
        org_ed.setText(sp.getString(GlobalConstant.organization, ""));
        lat = sp.getString(GlobalConstant.address_latitude, "");
        lng = sp.getString(GlobalConstant.address_longitude, "");

        camer_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailog();
            }
        });
        name_ed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                name_ed.setSelection(name_ed.getText().length());
                return false;
            }
        });
        vat_ed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                vat_ed.setSelection(vat_ed.getText().length());
                return false;
            }
        });
        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mAutocompleteView.setSelection(mAutocompleteView.getText().length());
                return false;
            }
        });
        edit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_ed.setSelection(name_ed.getText().length());
                edit_txt.setVisibility(View.GONE);
                email_ed.setTextColor(getResources().getColor(R.color.mainTextColor));

                org_ed.setTextColor(getResources().getColor(R.color.mainTextColor));

                name_ed.setEnabled(true);
                vat_ed.setEnabled(true);
                mAutocompleteView.setEnabled(true);
                update_profile.setVisibility(View.VISIBLE);
                edit_txt.setVisibility(View.GONE);
            }
        });
        vat_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    vatApiMethod(vat_ed.getText().toString());
                }

            }
        });
        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

        org_ed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });
        vat_ed.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {

                        case KeyEvent.KEYCODE_ENTER:
                            if (vat_ed.getText().toString().length() > 0) {

                                vatApiMethod(vat_ed.getText().toString());
                            }
                            return true;

                        default:
                            break;
                    }
                }
                return false;
            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_ed.getText().length() == 0) {
                    Toast.makeText(TechniciansEditProfileActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();

                } else if (vat_ed.getText().length() == 0) {
                    Toast.makeText(TechniciansEditProfileActivity.this, "Please enter vat no.", Toast.LENGTH_SHORT).show();
                } else if (mAutocompleteView.getText().length() == 0) {
                    Toast.makeText(TechniciansEditProfileActivity.this, "your vat no. is invalid", Toast.LENGTH_SHORT).show();

                } else if (org_ed.getText().length() == 0) {
                    Toast.makeText(TechniciansEditProfileActivity.this, "your vat no. is invalid", Toast.LENGTH_SHORT).show();

                } else {
                    dialogWindow();
                    //editprofile();
                    new Thread(null, address_request, "")
                            .start();
                }
            }
        });
    }

    public void dailog() {
        camgllry = new Dialog(TechniciansEditProfileActivity.this);
        camgllry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        camgllry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        camgllry.setContentView(R.layout.camera_dialog);

        camgllry.show();

        onclick();

    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void onclick() {
        LinearLayout camera, gallery;

        camera = (LinearLayout) camgllry.findViewById(R.id.camera_layout);
        gallery = (LinearLayout) camgllry.findViewById(R.id.gallery_layout);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 1);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {


                    onSelectFromGalleryResult(data);
                    camgllry.dismiss();
                    edit_txt.setVisibility(View.GONE);
                    email_ed.setTextColor(getResources().getColor(R.color.mainTextColor));

                    org_ed.setTextColor(getResources().getColor(R.color.mainTextColor));

                    name_ed.setEnabled(true);
                    vat_ed.setEnabled(true);
                    update_profile.setVisibility(View.VISIBLE);
                    edit_txt.setVisibility(View.GONE);

                }
            }
        } else if (requestCode == 1) {
            onCaptureImageResult(data);
            camgllry.dismiss();
            edit_txt.setVisibility(View.GONE);
            email_ed.setTextColor(getResources().getColor(R.color.mainTextColor));

            org_ed.setTextColor(getResources().getColor(R.color.mainTextColor));

            name_ed.setEnabled(true);
            vat_ed.setEnabled(true);
            update_profile.setVisibility(View.VISIBLE);
            edit_txt.setVisibility(View.GONE);
        } else {

        }


    }

    //-------------------------------image-upload-------------------------------------------
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(TechniciansEditProfileActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //profilepic.setImageBitmap(bm);
        Uri uri = getImageUri(TechniciansEditProfileActivity.this, bm);
        try {
            clickValue = true;
            selectedImagePath = getFilePath(TechniciansEditProfileActivity.this, uri);
            Picasso.with(TechniciansEditProfileActivity.this).load(new File(selectedImagePath)).into(tech_img);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    private void onCaptureImageResult(Intent data) {
        // Uri uri=data.getData();
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // profilepic.setImageBitmap(thumbnail);
        Uri uri = getImageUri(TechniciansEditProfileActivity.this, thumbnail);
        try {
            clickValue = true;
            selectedImagePath = getFilePath(TechniciansEditProfileActivity.this, uri);
            Picasso.with(TechniciansEditProfileActivity.this).load(new File(selectedImagePath)).into(tech_img);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    Runnable address_request = new Runnable() {
        String res = "false";


        @Override
        public void run() {
            try {

                res = doFileUpload();
            } catch (Exception e) {

            }
            Message msg = new Message();
            msg.obj = res;
            address_request_Handler.sendMessage(msg);
        }
    };

    Handler address_request_Handler = new Handler() {
        public void handleMessage(Message msg) {
            String res = (String) msg.obj;
            dialog2.dismiss();
            if (res.equalsIgnoreCase("true")) {
                // terms_dialog.dismiss();
                Toast.makeText(TechniciansEditProfileActivity.this, message, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(TechniciansEditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        }

    };

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

    private String doFileUpload() {
        String success = "false";

        String urlString = GlobalConstant.PROFILE_URL;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            MultipartEntity reqEntity = new MultipartEntity();
            if (clickValue == true) {
                File file1 = new File(selectedImagePath);
                FileBody bin1 = new FileBody(file1);
                reqEntity.addPart(GlobalConstant.image, bin1);
            }
            reqEntity.addPart(GlobalConstant.id, new StringBody(CommonUtils.UserID(TechniciansEditProfileActivity.this)));

            reqEntity.addPart(GlobalConstant.name, new StringBody(name_ed.getText().toString()));


            reqEntity.addPart(GlobalConstant.vat_number, new StringBody(vat_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.organization, new StringBody(org_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.address, new StringBody(mAutocompleteView.getText().toString()));
            reqEntity.addPart(GlobalConstant.address_latitude, new StringBody(lat));
            reqEntity.addPart(GlobalConstant.address_longitude, new StringBody(lng));

            post.setEntity(reqEntity);

            Log.e("params", selectedImagePath + " " + name_ed.getText().toString()
                    + " " + vat_ed.getText().toString() + " " + org_ed.getText().toString()
                    + " " + mAutocompleteView.getText().toString() + lat + " " + lng);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();

            final String response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Log.e("response str", response_str);
                JSONObject obj = new JSONObject(response_str);
                String status = obj.getString("status");
                if (status.equalsIgnoreCase("1")) {
                    success = "true";
                    message = obj.getString("message");
                    JSONObject data = obj.getJSONObject("data");
                    ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                    if (clickValue == true) {
                        ed.putString(GlobalConstant.image, selectedImagePath);

                    }
                    ed.putString(GlobalConstant.vat_number, data.getString(GlobalConstant.vat_number));
                    ed.putString(GlobalConstant.organization, data.getString(GlobalConstant.organization));
                    ed.putString(GlobalConstant.address, data.getString(GlobalConstant.address));
                    ed.putString(GlobalConstant.address_latitude, data.getString(GlobalConstant.address_latitude));
                    ed.putString(GlobalConstant.address_longitude, data.getString(GlobalConstant.address_longitude));
                    ed.commit();

                } else {
                    success = "false";
                    message = obj.getString("message");
                }

            }
        } catch (Exception ex) {
        }
        return success;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            lat = locationAddress.split(" ")[0];
            lng = locationAddress.split(" ")[1];


        }
    }

    //--------------------Vat api method---------------------------------
    private void vatApiMethod(String vatNumber) {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.VAT_INFO_URL + vatNumber,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.


                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("valid");
                            if (status.equalsIgnoreCase("true")) {
                                org_ed.setText(obj.getString("company_name"));
                                mAutocompleteView.setText(obj.getString("company_address"));
                                isVat = true;
                                GeocodingLocation locationAddress = new GeocodingLocation();
                                locationAddress.getAddressFromLocation(mAutocompleteView.getText().toString(),
                                        getApplicationContext(), new GeocoderHandler());
                            } else {
                                vat_ed.setError("Please enter valid VAT no.");
                                org_ed.setText("");
                                mAutocompleteView.setText("");
                                isVat = false;
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
