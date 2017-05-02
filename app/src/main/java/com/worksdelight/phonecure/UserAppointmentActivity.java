package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by worksdelight on 24/04/17.
 */

public class UserAppointmentActivity extends Activity {
    ImageView back_img;
    ImageView user_view;
    TextView name_txt, address_txt, date_txt, cancel_request_txt, total_price,close_date_txt;
    ListView service_list;
    Global global;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ScrollView main_scroll;
    Dialog dialog2;
    String booking_id;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    String statusValue,filePath,invoice;
    File pdfFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_information);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        init();
    }

    public void init() {
        main_scroll = (ScrollView) findViewById(R.id.main_scroll);

        back_img = (ImageView) findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                finish();
            }
        });
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        user_view=(ImageView) findViewById(R.id.user_view);
        total_price = (TextView) findViewById(R.id.total_price);
        // back_img.setColorFilter(back_img.getContext().getResources().getColor(R.color.main_color), PorterDuff.Mode.SRC_ATOP);
        cancel_request_txt = (TextView) findViewById(R.id.cancel_request_txt);
        name_txt = (TextView) findViewById(R.id.name_txt);
        address_txt = (TextView) findViewById(R.id.address_txt);
        date_txt = (TextView) findViewById(R.id.date_txt);
        close_date_txt=(TextView)findViewById(R.id.close_date_txt);
        service_list = (ListView) findViewById(R.id.service_list);
        if (getIntent().getExtras().getString("type").equalsIgnoreCase("0")) {

            try {
                JSONObject obj = global.getCompletedaar().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
                booking_id=obj.getString(GlobalConstant.id);
                invoice=obj.getString(GlobalConstant.invoice);

                JSONObject objUser = obj.getJSONObject(GlobalConstant.technician_detail);
                name_txt.setText(cap(objUser.getString(GlobalConstant.name)));
                String url = GlobalConstant.TECHNICIANS_IMAGE_URL + objUser.getString(GlobalConstant.image);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(name_txt.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
                if (objUser.getString(GlobalConstant.image).equalsIgnoreCase("")) {

                    user_view.setImageDrawable(drawable);
                } else {
                    Picasso.with(this).load(GlobalConstant.TECHNICIANS_IMAGE_URL + objUser.getString(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into(user_view);


                    //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
                }
                JSONObject objDetail = obj.getJSONObject(GlobalConstant.user_detail);
                JSONObject shipping_address = objDetail.getJSONObject("shipping_address");
                address_txt.setText(shipping_address.getString(GlobalConstant.ship_address) + "," + shipping_address.getString(GlobalConstant.ship_city));
                JSONArray booking_item_arr = obj.getJSONArray(GlobalConstant.booking_items);
                for (int i = 0; i < booking_item_arr.length(); i++) {
                    JSONObject bookinObj = booking_item_arr.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(GlobalConstant.id, bookinObj.getString(GlobalConstant.id));
                    map.put(GlobalConstant.price, bookinObj.getString(GlobalConstant.price));
                    map.put(GlobalConstant.name, bookinObj.getString(GlobalConstant.name));
                    list.add(map);
                }


                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat d = new SimpleDateFormat("dd MMM yyyy");

                Date convertedDate = null;
                try {
                    convertedDate = inputFormat.parse(obj.getString(GlobalConstant.date));
                    String s = formatter.format(convertedDate);
                    date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                    close_date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                total_price.setText("$" + String.valueOf(Float.parseFloat(obj.getString(GlobalConstant.total_amount))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cancel_request_txt.setText("Download Invoice");
            cancel_request_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadProfileImage(GlobalConstant.PDF_DOWNLOAD_URL+invoice, UserAppointmentActivity.this);

                }
            });
        } else {

            try {
                JSONObject obj = global.getPendingaar().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
                booking_id=obj.getString(GlobalConstant.id);

                statusValue=obj.getString(GlobalConstant.status);
                JSONObject objUser = obj.getJSONObject(GlobalConstant.technician_detail);
                name_txt.setText(cap(objUser.getString(GlobalConstant.name)));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(name_txt.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
                if (objUser.getString(GlobalConstant.image).equalsIgnoreCase("")) {

                    user_view.setImageDrawable(drawable);
                } else {
                    Picasso.with(this).load(GlobalConstant.TECHNICIANS_IMAGE_URL + objUser.getString(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into(user_view);


                    //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
                }
                JSONObject objDetail = obj.getJSONObject(GlobalConstant.user_detail);
                JSONObject shipping_address = objDetail.getJSONObject("shipping_address");
                address_txt.setText(shipping_address.getString(GlobalConstant.ship_address) + "," + shipping_address.getString(GlobalConstant.ship_city));
                JSONArray booking_item_arr = obj.getJSONArray(GlobalConstant.booking_items);
                for (int i = 0; i < booking_item_arr.length(); i++) {
                    JSONObject bookinObj = booking_item_arr.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(GlobalConstant.id, bookinObj.getString(GlobalConstant.id));
                    map.put(GlobalConstant.price, bookinObj.getString(GlobalConstant.price));
                    map.put(GlobalConstant.name, bookinObj.getString(GlobalConstant.name));
                    list.add(map);
                }

                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat d = new SimpleDateFormat("dd MMM yyyy");

                Date convertedDate = null;
                try {
                    convertedDate = inputFormat.parse(obj.getString(GlobalConstant.date));
                    String s = formatter.format(convertedDate);
                    date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                    close_date_txt.setText(s + " " + formatdate2(obj.getString(GlobalConstant.date)) + " " + obj.getString(GlobalConstant.time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                total_price.setText("$" + String.valueOf(Float.parseFloat(obj.getString(GlobalConstant.total_amount))));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(statusValue.equalsIgnoreCase("pending")){
                cancel_request_txt.setVisibility(View.VISIBLE);
                cancel_request_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogWindow();
                        ComAdnDelMethod();
                    }
                });
            }else{
                cancel_request_txt.setText("Order "+statusValue);
            }
        }

        service_list.setAdapter(new CompletedAdapter(this));
        CommonUtils.getListViewSize(service_list);
        main_scroll.smoothScrollBy(0, 0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UserAppointmentActivity.this,HistoryActivity.class);
        startActivity(i);
        finish();
    }

    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat d = new SimpleDateFormat("MMM dd,yyyy");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }

    class CompletedAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;


        CompletedAdapter(Context c/*, ArrayList<HashMap<String, String>> list*/) {
            this.c = c;
            //this.list = list;
            inflatore = LayoutInflater.from(c);
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                holder = new Holder();

                view = inflatore.inflate(R.layout.appointment_item_layout, null);
                holder.service_name = (TextView) view.findViewById(R.id.service_name);
                holder.service_time = (TextView) view.findViewById(R.id.service_time);
                holder.service_price = (TextView) view.findViewById(R.id.service_price);

                view.setTag(holder);


            } else {
                holder = (Holder) view.getTag();
            }
            holder.service_name.setText(list.get(i).get(GlobalConstant.name));
            holder.service_price.setText("$" + String.valueOf(Float.parseFloat(list.get(i).get(GlobalConstant.price))));

            return view;
        }

        public class Holder {

            TextView service_name, service_time, service_price;

        }
    }

    //--------------------DEL And COMPLETED api method---------------------------------
    private void ComAdnDelMethod() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.COM_AND_DEL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //JSONObject data=obj.getJSONObject("data");
                                Intent i=new Intent(UserAppointmentActivity.this,HistoryActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(UserAppointmentActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.id, booking_id);
                params.put(GlobalConstant.USERID, CommonUtils.UserID(UserAppointmentActivity.this));
                params.put(GlobalConstant.status, "cancelled");


                Log.e("Parameter for cancel", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(UserAppointmentActivity.this);
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
    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    //-------------------------background proceess for pdf file------------------
    public void loadProfileImage(String file_url, Context c) {
        String splitPath[] = file_url.split("/");
        String targetFileName = splitPath[splitPath.length - 1];


        pdfFile = new File(Environment.getExternalStorageDirectory() + "/PhoneCure/" + targetFileName);
        if (pdfFile.exists()) {


            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(UserAppointmentActivity.this, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
            }
        } else {

            dialogWindow();
            new DownloadFileAsync().execute(file_url);

        }
    }


    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count = 0;
            int MEGABYTE = 1024;
            //Integer.parseInt(listing.get(p).get("file_size"));
            try {

                URL url = new URL(aurl[0]);
                String path = (String) aurl[0];
                String splitPath[] = path.split("/");

                String targetFileName = splitPath[splitPath.length - 1];
                File f = new File(Environment.getExternalStorageDirectory() + "/PhoneCure/");
                f.mkdir();

                pdfFile = new File(f, targetFileName);
                try {
                    pdfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filePath = pdfFile.toString();
//create storage directories, if they don't exist

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                // c.setDoOutput(true);
                c.connect();


                InputStream input = c.getInputStream();// new BufferedInputStream(url.openStream(), Integer.parseInt(listing.get(p).get("file_size")));


                FileOutputStream output = new FileOutputStream(pdfFile);
                int lenghtOfFile = c.getContentLength();



                byte data[] = new byte[MEGABYTE];

                long total = 0;

                while ((count = input.read(data)) != 0) {
                    total += count;

                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.e("ANDRO_ASYNC", progress[0]);

        }

        @Override
        protected void onPostExecute(String unused) {
            dialog2.dismiss();
            Log.e("file path", filePath);


            try
            {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(UserAppointmentActivity.this, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
