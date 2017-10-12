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
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.makeramen.roundedimageview.RoundedImageView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.worksdelight.phonecure.GlobalConstant.address;
import static com.worksdelight.phonecure.GlobalConstant.facebook_id;


/**
 * Created by worksdelight on 10/04/17.
 */

public class TechniciansRegister extends FragmentActivity implements View.OnClickListener, View.OnTouchListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    EditText name_ed, email_ed, vat_ed, password_ed, no_of_emp_ed;
    RoundedImageView tech_img;
    Dialog camgllry, dialog2;
    String selectedImagePath = "", message;
    TextView see_text, register_txtView;
    EditText org_ed;
    Global global;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    boolean isVat = false;
    HttpEntity resEntity;
    ImageView back;
    CallbackManager callbackManager;
    LoginButton Login_TV;
    String token;
    Button facebook_btn;
    String username_mString = "", email_mString = "", id_mString = "", user_image = "";
    String type = "app",selectedType="shop";
    RelativeLayout facebook_layout;

    String lat, lng;
    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    LinearLayout main_layout, individual_layout, shop_layout;
    RelativeLayout number_of_layout;
    ImageView individual_selected_img, shop_selected_img;
    int s = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.technicians_profile_layout);
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
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        Fonts.overrideFonts(this, main_layout);
        callbackManager = CallbackManager.Factory.create();
        Login_TV = (LoginButton) findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email"));
        fbMethod();
        buildGoogleApiClient();
        //-------------------------------Call AutocompleteTxtView-----------------
        individual_selected_img = (ImageView) findViewById(R.id.individual_selected_img);
        shop_selected_img = (ImageView) findViewById(R.id.shop_selected_img);
        individual_layout = (LinearLayout) findViewById(R.id.individual_layout);
        shop_layout = (LinearLayout) findViewById(R.id.shop_layout);
        no_of_emp_ed = (EditText) findViewById(R.id.no_of_emp_ed);
        number_of_layout = (RelativeLayout) findViewById(R.id.number_of_layout);
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
        facebook_layout = (RelativeLayout) findViewById(R.id.facebook_layout);
        name_ed = (EditText) findViewById(R.id.name_ed);
        email_ed = (EditText) findViewById(R.id.email_ed);
        vat_ed = (EditText) findViewById(R.id.vat_ed);
        org_ed = (EditText) findViewById(R.id.org_ed);
        password_ed = (EditText) findViewById(R.id.password_ed);
        see_text = (TextView) findViewById(R.id.see_text);
        // address_ed = (TextView) findViewById(R.id.address_ed);
        tech_img = (RoundedImageView) findViewById(R.id.tech_img);
        register_txtView = (TextView) findViewById(R.id.register_txtView);
        back.setOnClickListener(this);
        tech_img.setOnClickListener(this);

       /* vat_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    vatApiMethod(vat_ed.getText().toString());
                } else {
                    vat_ed.setText("");
                }

            }
        });*/
        org_ed.setOnTouchListener(this);
        mAutocompleteView.setOnTouchListener(this);
        see_text.setOnClickListener(this);
        vat_ed.setOnTouchListener(this);
        facebook_layout.setOnClickListener(this);
        register_txtView.setOnClickListener(this);
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
        mAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lat = "";
                lng = "";
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (getIntent().getExtras().getString("type").equalsIgnoreCase("0")) {
            type = "facebook";
            id_mString = getIntent().getExtras().getString("fb_id");
            name_ed.setText(getIntent().getExtras().getString("name"));
            email_ed.setText(getIntent().getExtras().getString("email"));
            username_mString = getIntent().getExtras().getString("name");
            email_mString = getIntent().getExtras().getString("email");
            password_ed.setHint("Not required");
            Picasso.with(TechniciansRegister.this).load(getIntent().getExtras().getString("image")).into(tech_img);
            user_image = getIntent().getExtras().getString("image");
            name_ed.setEnabled(false);
            email_ed.setEnabled(false);
            password_ed.setEnabled(false);

            tech_img.setEnabled(false);
        }
        individual_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s = 1;
                shop_selected_img.setImageResource(R.drawable.unselected_radio);
                individual_selected_img.setImageResource(R.drawable.selected_radio);
                number_of_layout.setVisibility(View.GONE);
                selectedType="technician";
            }
        });
        shop_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s = 0;
                shop_selected_img.setImageResource(R.drawable.selected_radio);
                individual_selected_img.setImageResource(R.drawable.unselected_radio);
                number_of_layout.setVisibility(View.VISIBLE);
                selectedType="shop";
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.org_ed:

                break;
            case R.id.address_ed:

                break;
            case R.id.vat_ed:
                vat_ed.setFocusable(true);
                see_text.setVisibility(View.GONE);

                break;

        }
        return false;
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
            // Toast.makeText(TechniciansRegister.this,locationAddress.toString(),Toast.LENGTH_SHORT).show();
            //lat = locationAddress.split(" ")[0];
            // lng = locationAddress.split(" ")[1];


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_txtView:
                if (type.equalsIgnoreCase("app")) {
                    if(s==0){
                        if (name_ed.getText().length() == 0) {
                            name_ed.setError(getResources().getString(R.string.name_blank));
                        } else if (email_ed.getText().length() == 0) {
                            email_ed.setError(getResources().getString(R.string.email_blank));


                        } else if (password_ed.getText().length() == 0) {
                            password_ed.setError(getResources().getString(R.string.password_blank));

                        } else if (!CommonUtils.isEmailValid(email_ed.getText().toString())) {
                            email_ed.setError(getResources().getString(R.string.email_valid));
                        } else if (vat_ed.getText().length() == 0) {
                            vat_ed.setError(getResources().getString(R.string.vat_valid));
                        } else if (org_ed.getText().length() == 0) {
                            org_ed.setError("Please enter organization");
                        } else if (mAutocompleteView.getText().length() == 0) {
                            mAutocompleteView.setError(getResources().getString(R.string.address_blank));
                        } else if (selectedImagePath.equalsIgnoreCase("")) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.image_valid), Toast.LENGTH_SHORT).show();
                        }  else if (lat.equalsIgnoreCase("")) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.address_valid), Toast.LENGTH_SHORT).show();
                        }else if (no_of_emp_ed.getText().toString().length()==0) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.no_of_emp_blank), Toast.LENGTH_SHORT).show();
                        } else {

                            dialogWindow();
                            //editprofile();
                            new Thread(null, address_request, "")
                                    .start();
                        }
                    }else{
                        if (name_ed.getText().length() == 0) {
                            name_ed.setError(getResources().getString(R.string.name_blank));
                        } else if (email_ed.getText().length() == 0) {
                            email_ed.setError(getResources().getString(R.string.email_blank));


                        } else if (password_ed.getText().length() == 0) {
                            password_ed.setError(getResources().getString(R.string.password_blank));

                        } else if (!CommonUtils.isEmailValid(email_ed.getText().toString())) {
                            email_ed.setError(getResources().getString(R.string.email_valid));
                        } else if (vat_ed.getText().length() == 0) {
                            vat_ed.setError(getResources().getString(R.string.vat_valid));
                        } else if (org_ed.getText().length() == 0) {
                            org_ed.setError("Please enter organization");
                        } else if (mAutocompleteView.getText().length() == 0) {
                            mAutocompleteView.setError(getResources().getString(R.string.address_blank));
                        } else if (selectedImagePath.equalsIgnoreCase("")) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.image_valid), Toast.LENGTH_SHORT).show();
                        } else if (lat.equalsIgnoreCase("")) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.address_valid), Toast.LENGTH_SHORT).show();
                        } else {

                            dialogWindow();
                            //editprofile();
                            new Thread(null, address_request, "")
                                    .start();
                        }
                    }

                } else {
                    if(s==0){
                         if (vat_ed.getText().length() == 0) {
                             vat_ed.setError(getResources().getString(R.string.vat_valid));
                        } else if (org_ed.getText().length() == 0) {
                            org_ed.setError("Please enter organization");
                        } else if (mAutocompleteView.getText().length() == 0) {
                             mAutocompleteView.setError(getResources().getString(R.string.address_blank));
                        } else if (lat.equalsIgnoreCase("")) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.address_valid), Toast.LENGTH_SHORT).show();
                        }else if (no_of_emp_ed.getText().toString().length()==0) {
                             Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.no_of_emp_blank), Toast.LENGTH_SHORT).show();
                        } else {

                            dialogWindow();
                            FacebooksocialMethod();
                        }
                    }else{
                        if (vat_ed.getText().length() == 0) {
                            vat_ed.setError(getResources().getString(R.string.vat_valid));
                        } else if (org_ed.getText().length() == 0) {
                            org_ed.setError("Please enter organization");
                        } else if (mAutocompleteView.getText().length() == 0) {
                            mAutocompleteView.setError(getResources().getString(R.string.address_blank));
                        }  else if (lat.equalsIgnoreCase("")) {
                            Toast.makeText(TechniciansRegister.this, getResources().getString(R.string.address_valid), Toast.LENGTH_SHORT).show();
                        } else {

                            dialogWindow();
                            FacebooksocialMethod();
                        }
                    }

                }
                break;
            case R.id.tech_img:
                dailog();
                break;
            case R.id.see_text:
                Intent i = new Intent(TechniciansRegister.this, WebActivity.class);
                startActivity(i);
                break;
            case R.id.back:
                Intent k = new Intent(TechniciansRegister.this, LoginActivity.class);
                k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(k);
                break;
            case R.id.facebook_layout:
                Login_TV.performClick();


                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent k = new Intent(TechniciansRegister.this, LoginActivity.class);
        k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(k);
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
                            type = "facebook";
                            name_ed.setText(username_mString);
                            email_ed.setText(email_mString);
                            Picasso.with(TechniciansRegister.this).load(user_image).into(tech_img);
                            name_ed.setEnabled(false);
                            email_ed.setEnabled(false);
                            password_ed.setEnabled(false);

                            tech_img.setEnabled(false);
                            //gender = object.getString("gender");
                            //birthday = object.getString("birthday");


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


    public void dailog() {
        camgllry = new Dialog(TechniciansRegister.this);
        camgllry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        camgllry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        camgllry.setContentView(R.layout.camera_dialog);

        camgllry.show();

        onclick();

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {


                    onSelectFromGalleryResult(data);
                    camgllry.dismiss();

                }
            }
        } else if (requestCode == 1) {
            onCaptureImageResult(data);
            camgllry.dismiss();
        } else {

        }


    }

    //-------------------------------image-upload-------------------------------------------
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(TechniciansRegister.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //profilepic.setImageBitmap(bm);
        Uri uri = getImageUri(TechniciansRegister.this, bm);
        try {
            selectedImagePath = getFilePath(TechniciansRegister.this, uri);
            Picasso.with(TechniciansRegister.this).load(new File(selectedImagePath)).into(tech_img);


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
        Uri uri = getImageUri(TechniciansRegister.this, thumbnail);
        try {
            selectedImagePath = getFilePath(TechniciansRegister.this, uri);
            Picasso.with(TechniciansRegister.this).load(new File(selectedImagePath)).into(tech_img);


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
                                org_ed.setFocusable(true);
                                //see_text.setVisibility(View.VISIBLE);
                                org_ed.setEnabled(true);
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


    // ------------------------------------------------------upload
    // method---------------
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
                Toast.makeText(TechniciansRegister.this, message, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(TechniciansRegister.this, message, Toast.LENGTH_SHORT).show();
            }

        }

    };

    private String doFileUpload() {
        String success = "false";

        String urlString = GlobalConstant.TECHSIGNUP_URL;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            MultipartEntity reqEntity = new MultipartEntity();
            if (selectedImagePath.length() > 0) {
                File file1 = new File(selectedImagePath);
                FileBody bin1 = new FileBody(file1);
                reqEntity.addPart(GlobalConstant.image, bin1);
            }


            reqEntity.addPart(GlobalConstant.name, new StringBody(name_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.email, new StringBody(email_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.password, new StringBody(password_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.vat_number, new StringBody(vat_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.organization, new StringBody(org_ed.getText().toString()));
            reqEntity.addPart(GlobalConstant.address, new StringBody(mAutocompleteView.getText().toString()));
            reqEntity.addPart("address_latitude", new StringBody(lat));
            reqEntity.addPart("address_longitude", new StringBody(lng));
            reqEntity.addPart(GlobalConstant.device_token, new StringBody(global.getDeviceToken()));
            reqEntity.addPart(GlobalConstant.type, new StringBody(selectedType));
            reqEntity.addPart(GlobalConstant.latitude, new StringBody(global.getLat()));
            reqEntity.addPart(GlobalConstant.longitude, new StringBody(global.getLong()));

            if(s==0){
                reqEntity.addPart(GlobalConstant.number_of_employees, new StringBody(no_of_emp_ed.getText().toString()));

            }
            reqEntity.addPart(GlobalConstant.device_type, new StringBody("android"));

            post.setEntity(reqEntity);

            Log.e("params", selectedImagePath + " " + name_ed.getText().toString()
                    + " " + email_ed.getText().toString() + " " + password_ed.getText().toString() + " " + vat_ed.getText().toString() + " " + org_ed.getText().toString()
                    + " " + mAutocompleteView.getText().toString() + " " + global.getDeviceToken() + " " + global.getLong() + " " + global.getLat() + " android" + " technician" + lat + lng);
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
                    ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                    ed.putString("type", "app");
                    ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                    ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                    ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                    ed.putString(GlobalConstant.vat_number, data.getString(GlobalConstant.vat_number));
                    ed.putString(GlobalConstant.organization, data.getString(GlobalConstant.organization));
                    ed.putString(address, data.getString(address));
                    ed.putString(GlobalConstant.type, data.getString(GlobalConstant.type));

                    ed.commit();
                    Intent s = new Intent(TechniciansRegister.this, WoorkingHourSecondActivity.class);
                    startActivity(s);
                    finish();

                } else {
                    success = "false";
                    message = obj.getString("message");
                }

            }
        } catch (Exception ex) {
            dialog2.dismiss();
        }
        return success;
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
                                JSONObject data = obj.getJSONObject("data");
                                ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                                ed.putString(GlobalConstant.image, data.getString(GlobalConstant.image));
                                ed.putString(GlobalConstant.latitude, data.getString(GlobalConstant.latitude));
                                ed.putString(GlobalConstant.longitude, data.getString(GlobalConstant.longitude));
                                ed.putString("type", "facebook");
                                ed.putString(GlobalConstant.name, data.getString(GlobalConstant.name));
                                ed.putString(GlobalConstant.email, data.getString(GlobalConstant.email));
                                ed.putString(GlobalConstant.type, data.getString(GlobalConstant.type));
                                ed.putString(GlobalConstant.vat_number, data.getString(GlobalConstant.vat_number));
                                ed.putString(GlobalConstant.organization, data.getString(GlobalConstant.organization));

                                ed.putString(address, data.getString(address));

                                ed.commit();
                                Intent s = new Intent(TechniciansRegister.this, WoorkingHourSecondActivity.class);
                                startActivity(s);
                                finish();
                            } else {
                                LoginManager.getInstance().logOut();
                                Toast.makeText(TechniciansRegister.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put(GlobalConstant.type, "technician");
                params.put(GlobalConstant.latitude, global.getLat());
                params.put(GlobalConstant.longitude, global.getLong());
                params.put(GlobalConstant.device_type, "android");
                params.put(GlobalConstant.image, user_image);
                params.put(GlobalConstant.vat_number, vat_ed.getText().toString());
                params.put(GlobalConstant.organization, org_ed.getText().toString());
                params.put(GlobalConstant.address, mAutocompleteView.getText().toString());
                if(s==0){
                    ed.putString(GlobalConstant.number_of_employees, no_of_emp_ed.getText().toString());
                }
                params.put("address_latitude", lat);
                params.put("address_longitude", lng);


                Log.e("Parameter for social", params.toString());
                return params;
            }

        };
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
