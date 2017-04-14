package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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


/**
 * Created by worksdelight on 10/04/17.
 */

public class TechniciansRegister extends Activity {
    EditText name_ed, email_ed, vat_ed, org_ed, address_ed, password_ed;
    ImageView tech_img;
    Dialog camgllry, dialog2;
    String selectedImagePath = "", message;
    TextView see_text, register_txtView;
    Global global;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    boolean isVat = false;
    HttpEntity resEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technicians_profile_layout);
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        name_ed = (EditText) findViewById(R.id.name_ed);
        email_ed = (EditText) findViewById(R.id.email_ed);
        vat_ed = (EditText) findViewById(R.id.vat_ed);
        org_ed = (EditText) findViewById(R.id.org_ed);
        password_ed = (EditText) findViewById(R.id.password_ed);
        see_text = (TextView) findViewById(R.id.see_text);
        address_ed = (EditText) findViewById(R.id.address_ed);
        tech_img = (ImageView) findViewById(R.id.tech_img);
        register_txtView = (TextView) findViewById(R.id.register_txtView);
        tech_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailog();
            }
        });
        vat_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    dialogWindow();
                    vatApiMethod(vat_ed.getText().toString());
                }

            }
        });

        see_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TechniciansRegister.this, WebActivity.class);
                startActivity(i);
            }
        });
        vat_ed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                see_text.setVisibility(View.GONE);
                return false;

            }
        });
        register_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_ed.getText().length() == 0) {
                    name_ed.setError("Please enter name");
                } else if (email_ed.getText().length() == 0) {
                    email_ed.setError("Please enter email");


                } else if (password_ed.getText().length() == 0) {
                    password_ed.setError("Please enter password");

                } else if (!CommonUtils.isEmailValid(email_ed.getText().toString())) {
                    email_ed.setError("Please enter valid email");
                } else if (vat_ed.getText().length() == 0) {
                    vat_ed.setError("Please enter vat number");
                } else if (org_ed.getText().length() == 0) {
                    org_ed.setError("Please enter organization");
                } else if (address_ed.getText().length() == 0) {
                    address_ed.setError("Please enter address");
                } else if (selectedImagePath.equalsIgnoreCase("")) {
                    Toast.makeText(TechniciansRegister.this, "Please select image", Toast.LENGTH_SHORT).show();
                } else if (isVat == false) {
                    Toast.makeText(TechniciansRegister.this, "Please enter valid VAT no.", Toast.LENGTH_SHORT).show();
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
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("valid");
                            if (status.equalsIgnoreCase("true")) {
                                org_ed.setText(obj.getString("company_name"));
                                address_ed.setText(obj.getString("company_address"));
                                isVat = true;
                            } else {
                                vat_ed.setError("Please enter valid VAT no.");
                                see_text.setVisibility(View.VISIBLE);
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
            reqEntity.addPart(GlobalConstant.address, new StringBody(address_ed.getText().toString()));

            reqEntity.addPart(GlobalConstant.device_token, new StringBody(global.getDeviceToken()));
            reqEntity.addPart(GlobalConstant.type, new StringBody("technician"));
            reqEntity.addPart(GlobalConstant.latitude, new StringBody(global.getLat()));
            reqEntity.addPart(GlobalConstant.longitude, new StringBody(global.getLong()));
            reqEntity.addPart(GlobalConstant.device_type, new StringBody("android"));

            post.setEntity(reqEntity);

            Log.e("params",  selectedImagePath + " " + name_ed.getText().toString()
                    + " " + email_ed.getText().toString() + " " + password_ed.getText().toString() + " " + vat_ed.getText().toString()+" "+org_ed.getText().toString()
                    +" "+address_ed.getText().toString()+" "+global.getDeviceToken()+" "+global.getLong()+" "+global.getLat()+" android"+" technician");
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();

            final String response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                JSONObject obj = new JSONObject(response_str);
                String status = obj.getString("status");
                if (status.equalsIgnoreCase("1")) {
                    success = "true";
                    message = obj.getString("msg");
                    JSONObject data = obj.getJSONObject("data");
                    ed.putString(GlobalConstant.USERID, data.getString(GlobalConstant.id));
                    ed.putString("type", "app");
                    ed.putString("user name", data.getString(GlobalConstant.name));
                    ed.putString("email", data.getString(GlobalConstant.email));
                    ed.putString(GlobalConstant.type, data.getString(GlobalConstant.type));

                    ed.commit();
                    Intent s = new Intent(TechniciansRegister.this, TechniciansDevice.class);
                    startActivity(s);
                    finish();

                } else {
                    success = "false";
                    message = obj.getString("msg");
                }

            }
        } catch (Exception ex) {
        }
        return success;
    }


}
