package com.worksdelight.phonecure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by worksdelight on 12/04/17.
 */

public class PandingFragment extends Fragment {
    ListView completed_listView;
    Global global;
    JSONArray arr;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    Dialog dialog2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.completed_layout, container, false);
        completed_listView = (ListView) v.findViewById(R.id.completed_listView);
        global = (Global) getActivity().getApplicationContext();
        for (int i = 0; i < global.getPendingaar().length(); i++) {
            try {
                JSONObject obj = global.getPendingaar().getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.id, obj.getString(GlobalConstant.id));
                map.put(GlobalConstant.total_amount, obj.getString(GlobalConstant.total_amount));
                map.put(GlobalConstant.date, obj.getString(GlobalConstant.date));
                JSONObject user_detail = obj.getJSONObject(GlobalConstant.user_detail);
                map.put(GlobalConstant.name, user_detail.getString(GlobalConstant.name));
                list.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        completed_listView.setAdapter(new ShoppingAdapter(getActivity()));
        completed_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detail = new Intent(getActivity(), AppointmentActivity.class);
                detail.putExtra("pos", String.valueOf(i));
                detail.putExtra("type", "1");
                startActivity(detail);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < global.getPendingaar().length(); i++) {
            try {
                JSONObject obj = global.getPendingaar().getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.id, obj.getString(GlobalConstant.id));
                map.put(GlobalConstant.total_amount, obj.getString(GlobalConstant.total_amount));
                map.put(GlobalConstant.date, obj.getString(GlobalConstant.date));
                JSONObject user_detail = obj.getJSONObject(GlobalConstant.user_detail);
                map.put(GlobalConstant.name, user_detail.getString(GlobalConstant.name));
                list.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        completed_listView.setAdapter(new ShoppingAdapter(getActivity()));
    }

    public static PandingFragment newInstance(String text) {

        PandingFragment f = new PandingFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    public class ShoppingAdapter extends BaseSwipeAdapter {

        private Context mContext;
        Holder holder;

        ArrayList<HashMap<String, String>> serviceListing = new ArrayList<>();
        int priceValue;

        public ShoppingAdapter(Context mContext/*, ArrayList<HashMap<String, String>> serviceListing*/) {

            this.mContext = mContext;

            // this.serviceListing = serviceListing;
          /*  imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)

                    .showStubImage(0)        //	Display Stub Image
                    .showImageForEmptyUri(0)    //	If Empty image found
                    .cacheInMemory()
                    .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
            initImageLoader();*/
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public View generateView(final int position, ViewGroup parent) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.pending_layout_item, null);
            SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    v.findViewById(R.id.delete_img).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
                            dialogWindow();
                            ComAdnDelMethod(list.get(position).get(GlobalConstant.id));
                            notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {

                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });


            return v;
        }

        @Override
        public void fillValues(final int position, View convertView) {


            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView delivered_date_txt = (TextView) convertView.findViewById(R.id.delivered_date_txt);

            TextView price_txt = (TextView) convertView.findViewById(R.id.price_txt);

            price_txt.setText("$" + list.get(position).get(GlobalConstant.total_amount));

            delivered_date_txt.setText(formatdate2(list.get(position).get(GlobalConstant.date)));


            name.setText(list.get(position).get(GlobalConstant.name));




          /*  holder.setting_layout.setVisibility(View.GONE);
            holder.name.setText(list.get(i).get(GlobalConstant.name));
            holder.location.setText(list.get(i).get(GlobalConstant.distance) + "Km Away");
            holder.rating_value.setText(list.get(i).get(GlobalConstant.average_rating));
            // holder.specilist.setText(list.get(i).get(GlobalConstant.average_rating));
            String url = GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(i).get(GlobalConstant.image);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, holder.tech_view, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                holder.tech_view.setImageResource(R.drawable.user_back);
            }
*/

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class Holder {
            ImageView cancel, chat, message, call;
            LinearLayout setting_layout, setting_call_layout;
            CircleImageView tech_view;
            TextView name, delivered_date_txt, price_txt, rating_value;
        }
    }

    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat d = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }


    //--------------------DEL And COMPLETED api method---------------------------------
    private void ComAdnDelMethod(final String id) {


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
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).get(GlobalConstant.id).equalsIgnoreCase(id)) {
                                        list.remove(i);
                                    }
                                }
                                completed_listView.setAdapter(new ShoppingAdapter(getActivity()));

                            } else {
                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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

                params.put(GlobalConstant.id, id);
                params.put(GlobalConstant.status, "completed");


                Log.e("Parameter for cancel", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(getActivity());
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
