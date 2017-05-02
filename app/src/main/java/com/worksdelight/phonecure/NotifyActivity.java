package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 20/04/17.
 */

public class NotifyActivity extends Activity {
    ImageView back;
    ListView message_listView;
    Dialog dialog2;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        message_listView = (ListView) findViewById(R.id.message_listView);
       // message_listView.setAdapter(new MessageAdapter(this));
        dialogWindow();
        categoryMethod();
    }

    //------------------------Type Adapter--------------------
    class MessageAdapter extends BaseAdapter {


        Context c;
        LayoutInflater inflatore;

        MessageAdapter(Context c) {
            this.c = c;
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
            view = inflatore.inflate(R.layout.message_list_item, null);
            ImageView user_img=(ImageView)view.findViewById(R.id.user_img);
            TextView message_txt=(TextView)view.findViewById(R.id.message_txt);
            TextView user_name=(TextView)view.findViewById(R.id.user_name);

            user_name.setText(list.get(i).get(GlobalConstant.name));
            message_txt.setText(list.get(i).get(GlobalConstant.notification));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(user_name.getText().toString().substring(0, 1).toUpperCase(), Color.parseColor("#F94444"));
            if (list.get(i).get(GlobalConstant.image).equalsIgnoreCase("")) {

                user_img.setImageDrawable(drawable);
            } else {
                Picasso.with(c).load(GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(i).get(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into(user_img);


                //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
            }
            return view;
        }
    }

    //--------------------Category api method---------------------------------
    private void categoryMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.NOTIFY_URL + CommonUtils.UserID(this),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONArray notifications = data.getJSONArray("notifications");
                                for (int i = 0; i < notifications.length(); i++) {
                                    JSONObject notiObj = notifications.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(GlobalConstant.id, notiObj.getString(GlobalConstant.id));
                                    map.put(GlobalConstant.name, notiObj.getString(GlobalConstant.name));
                                    map.put(GlobalConstant.image, notiObj.getString(GlobalConstant.image));
                                    map.put(GlobalConstant.notification, notiObj.getString(GlobalConstant.notification));

                                    list.add(map);
                                }
                                message_listView.setAdapter(new MessageAdapter(NotifyActivity.this));

                            } else {
                                Toast.makeText(NotifyActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
}
