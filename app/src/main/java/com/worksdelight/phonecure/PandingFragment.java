package com.worksdelight.phonecure;

import android.app.AlertDialog;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.squareup.picasso.Picasso;
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

/**
 * Created by worksdelight on 12/04/17.
 */

public class PandingFragment extends Fragment {
    ListView completed_listView;
    Global global;
    JSONArray arr;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    Dialog dialog2,ratingDialog;
    ImageView back_img;
    String com_star = "1", time_star = "1", service_star = "1", skill_star = "1",user_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.completed_layout, container, false);
        completed_listView = (ListView) v.findViewById(R.id.completed_listView);
        global = (Global) getActivity().getApplicationContext();
        back_img = (ImageView) v.findViewById(R.id.back_img);
        for (int i = 0; i < global.getPendingaar().length(); i++) {
            try {
                JSONObject obj = global.getPendingaar().getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.id, obj.getString(GlobalConstant.id));
                map.put(GlobalConstant.total_amount, obj.getString(GlobalConstant.total_amount));
                map.put(GlobalConstant.date, obj.getString(GlobalConstant.date));
                map.put(GlobalConstant.status, obj.getString(GlobalConstant.status));
                JSONObject user_detail = obj.getJSONObject(GlobalConstant.user_detail);
                map.put(GlobalConstant.USERID, user_detail.getString(GlobalConstant.id));
                map.put(GlobalConstant.name, user_detail.getString(GlobalConstant.name));
                map.put(GlobalConstant.image, user_detail.getString(GlobalConstant.image));

                list.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (list.size() > 0) {
            completed_listView.setVisibility(View.VISIBLE);
            back_img.setVisibility(View.GONE);
            completed_listView.setAdapter(new ShoppingAdapter(getActivity()));
        } else {
            completed_listView.setVisibility(View.GONE);
            back_img.setVisibility(View.VISIBLE);
        }

        completed_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detail = new Intent(getActivity(), AppointmentActivity.class);
                detail.putExtra("pos", String.valueOf(i));
                detail.putExtra("type", "1");
                startActivity(detail);
                getActivity().finish();
            }
        });
        return v;
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
        AlertDialog builder;
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
        public int getViewTypeCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;

        }

        @Override
        public View generateView(final int position, ViewGroup parent) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.pending_layout_item, null);
            SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
            if (list.get(position).get(GlobalConstant.status).equalsIgnoreCase("pending")) {
                swipeLayout.setRightSwipeEnabled(false);
                /*swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {

                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        v.findViewById(R.id.delete_img).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
                                builder = new AlertDialog.Builder(mContext).setMessage("Do You Want To Mark as Done?")
                                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialogWindow();
                                                ComAdnDelMethod(list.get(position).get(GlobalConstant.id),list.get(position).get(GlobalConstant.USERID));
                                                notifyDataSetChanged();
                                                builder.dismiss();
                                            }

                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub

                                                builder.dismiss();
                                            }
                                        }).show();

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
                });*/
            } else {
                swipeLayout.setRightSwipeEnabled(false);
            }

            return v;
        }

        @Override
        public void fillValues(final int position, View convertView) {


            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView delivered_date_txt = (TextView) convertView.findViewById(R.id.delivered_date_txt);

            TextView price_txt = (TextView) convertView.findViewById(R.id.price_txt);
            TextView status_txt = (TextView) convertView.findViewById(R.id.status_txt);
            ImageView tech_img = (ImageView) convertView.findViewById(R.id.tech_view);
            if (list.get(position).get(GlobalConstant.status).equalsIgnoreCase("pending")) {
                status_txt.setTextColor(getResources().getColor(R.color.main_color));

            } else {
                status_txt.setTextColor(Color.parseColor("#ff0000"));
                status_txt.setText(cap(list.get(position).get(GlobalConstant.status)));


            }

            price_txt.setText(global.getCurencySymbol() + list.get(position).get(GlobalConstant.total_amount));

            delivered_date_txt.setText(formatdate2(list.get(position).get(GlobalConstant.date)));


            name.setText(cap(list.get(position).get(GlobalConstant.name)));
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color
            int color1 = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(name.getText().toString().substring(0, 1).toUpperCase(), color1);
            if (list.get(position).get(GlobalConstant.image).equalsIgnoreCase("")) {

                tech_img.setImageDrawable(drawable);
            } else {
                Picasso.with(mContext).load(GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(position).get(GlobalConstant.image)).placeholder(drawable).transform(new CircleTransform()).into(tech_img);


                //profilepic.setImageURI(Uri.fromFile(new File(preferences.getString(GlobalConstants.IMAGE, ""))));
            }


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
            ImageView tech_view;
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
    private void ComAdnDelMethod(final String id,final String user_id) {


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
                                /*for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).get(GlobalConstant.id).equalsIgnoreCase(id)) {
                                        list.remove(i);
                                    }
                                }*/
                                ratingWindow(user_id);

                                // completed_listView.setAdapter(new ShoppingAdapter(getActivity()));

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
                params.put(GlobalConstant.USERID, CommonUtils.UserID(getActivity()));
                params.put(GlobalConstant.status, "completed");


                Log.e("Parameter for comp", params.toString());
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

    //--------------------Category api method---------------------------------
    private void categoryMethod() {

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GlobalConstant.BOOKINGINFO_URL + "&" + GlobalConstant.USERID + "=" + CommonUtils.UserID(getActivity()),
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
                                JSONArray copletedArr = data.getJSONArray(GlobalConstant.completed);
                                JSONArray pendingArr = data.getJSONArray(GlobalConstant.pending);

                                global.setCompletedaar(copletedArr);
                                global.setPendingaar(pendingArr);
                                list.clear();
                                for (int i = 0; i < global.getPendingaar().length(); i++) {
                                    try {
                                        JSONObject obj1 = global.getPendingaar().getJSONObject(i);
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put(GlobalConstant.id, obj1.getString(GlobalConstant.id));
                                        map.put(GlobalConstant.total_amount, obj1.getString(GlobalConstant.total_amount));
                                        map.put(GlobalConstant.date, obj1.getString(GlobalConstant.date));
                                        map.put(GlobalConstant.status, obj1.getString(GlobalConstant.status));
                                        JSONObject user_detail = obj1.getJSONObject(GlobalConstant.user_detail);
                                        map.put(GlobalConstant.USERID, user_detail.getString(GlobalConstant.id));
                                        map.put(GlobalConstant.name, user_detail.getString(GlobalConstant.name));
                                        map.put(GlobalConstant.image, user_detail.getString(GlobalConstant.image));

                                        list.add(map);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                if (list.size() > 0) {
                                    completed_listView.setVisibility(View.VISIBLE);
                                    back_img.setVisibility(View.GONE);
                                    completed_listView.setAdapter(new ShoppingAdapter(getActivity()));
                                } else {
                                    completed_listView.setVisibility(View.GONE);
                                    back_img.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    //---------------------------Progrees Dialog-----------------------
    public void ratingWindow(final String user_id) {
        ratingDialog = new Dialog(getActivity());
        ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingDialog.setCanceledOnTouchOutside(false);
        ratingDialog.setCancelable(false);
        ratingDialog.setContentView(R.layout.rateing_dialog_layout);
        TextView submit_rating = (TextView) ratingDialog.findViewById(R.id.submit_rating);
        ratingStarinit(ratingDialog);
        submit_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWindow();
                ratingMethod(user_id);
            }
        });
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        ratingDialog.show();
    }

    public void ratingStarinit(Dialog d) {
        final ImageView com_star1 = (ImageView) d.findViewById(R.id.com_star1);
        final ImageView com_star2 = (ImageView) d.findViewById(R.id.com_star2);
        final ImageView com_star3 = (ImageView) d.findViewById(R.id.com_star3);
        final ImageView com_star4 = (ImageView) d.findViewById(R.id.com_star4);
        final ImageView com_star5 = (ImageView) d.findViewById(R.id.com_star5);

        final ImageView timing_star1 = (ImageView) d.findViewById(R.id.timing_star1);
        final ImageView timing_star2 = (ImageView) d.findViewById(R.id.timing_star2);
        final ImageView timing_star3 = (ImageView) d.findViewById(R.id.timing_star3);
        final ImageView timing_star4 = (ImageView) d.findViewById(R.id.timing_star4);
        final ImageView timing_star5 = (ImageView) d.findViewById(R.id.timing_star5);

        final ImageView service_star1 = (ImageView) d.findViewById(R.id.service_star1);
        final ImageView service_star2 = (ImageView) d.findViewById(R.id.service_star2);
        final ImageView service_star3 = (ImageView) d.findViewById(R.id.service_star3);
        final ImageView service_star4 = (ImageView) d.findViewById(R.id.service_star4);
        final ImageView service_star5 = (ImageView) d.findViewById(R.id.service_star5);


        final ImageView skill_star1 = (ImageView) d.findViewById(R.id.skill_star1);
        final ImageView skill_star2 = (ImageView) d.findViewById(R.id.skill_star2);
        final ImageView skill_star3 = (ImageView) d.findViewById(R.id.skill_star3);
        final ImageView skill_star4 = (ImageView) d.findViewById(R.id.skill_star4);
        final ImageView skill_star5 = (ImageView) d.findViewById(R.id.skill_star5);


        com_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "1";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.green_star);
                com_star3.setImageResource(R.drawable.green_star);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "2";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.green_star);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "3";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.green_star);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "4";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.star_fill);
                com_star5.setImageResource(R.drawable.green_star);
            }
        });
        com_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com_star = "5";
                com_star1.setImageResource(R.drawable.star_fill);
                com_star2.setImageResource(R.drawable.star_fill);
                com_star3.setImageResource(R.drawable.star_fill);
                com_star4.setImageResource(R.drawable.star_fill);
                com_star5.setImageResource(R.drawable.star_fill);
            }
        });
        timing_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "1";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.green_star);
                timing_star3.setImageResource(R.drawable.green_star);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "2";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.green_star);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "3";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.green_star);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "4";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.star_fill);
                timing_star5.setImageResource(R.drawable.green_star);
            }
        });
        timing_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_star = "5";
                timing_star1.setImageResource(R.drawable.star_fill);
                timing_star2.setImageResource(R.drawable.star_fill);
                timing_star3.setImageResource(R.drawable.star_fill);
                timing_star4.setImageResource(R.drawable.star_fill);
                timing_star5.setImageResource(R.drawable.star_fill);
            }
        });

        service_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "1";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.green_star);
                service_star3.setImageResource(R.drawable.green_star);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "2";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.green_star);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "3";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.green_star);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "4";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.star_fill);
                service_star5.setImageResource(R.drawable.green_star);
            }
        });
        service_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_star = "5";
                service_star1.setImageResource(R.drawable.star_fill);
                service_star2.setImageResource(R.drawable.star_fill);
                service_star3.setImageResource(R.drawable.star_fill);
                service_star4.setImageResource(R.drawable.star_fill);
                service_star5.setImageResource(R.drawable.star_fill);
            }
        });

        skill_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "1";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.green_star);
                skill_star3.setImageResource(R.drawable.green_star);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "2";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.green_star);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "3";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.green_star);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "4";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.star_fill);
                skill_star5.setImageResource(R.drawable.green_star);
            }
        });
        skill_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skill_star = "5";
                skill_star1.setImageResource(R.drawable.star_fill);
                skill_star2.setImageResource(R.drawable.star_fill);
                skill_star3.setImageResource(R.drawable.star_fill);
                skill_star4.setImageResource(R.drawable.star_fill);
                skill_star5.setImageResource(R.drawable.star_fill);
            }
        });

    }
    //--------------------DEL And COMPLETED api method---------------------------------
    private void ratingMethod(final String user_id) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.RATING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //JSONObject data=obj.getJSONObject("data");
                                ratingDialog.dismiss();
                                categoryMethod();
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


                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put(GlobalConstant.USERID, CommonUtils.UserID(getActivity()));
                params.put(GlobalConstant.favorite_user_id, user_id);
                params.put("rating", com_star);
                params.put("rating1", service_star);
                params.put("rating2", time_star);
                params.put("rating3", skill_star);


                Log.e("Parameter for rating", params.toString());
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
