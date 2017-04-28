package com.worksdelight.phonecure;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by worksdelight on 24/04/17.
 */

public class UserCompletedFragment extends Fragment {
    ListView completed_listView;
    Dialog dialog2;
    Global global;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.completed_layout, container, false);
        completed_listView = (ListView) v.findViewById(R.id.completed_listView);

        global = (Global) getActivity().getApplicationContext();
        for (int i = 0; i < global.getCompletedaar().length(); i++) {
            try {
                JSONObject obj = global.getCompletedaar().getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(GlobalConstant.total_amount, obj.getString(GlobalConstant.total_amount));
                map.put(GlobalConstant.date, obj.getString(GlobalConstant.date));
                JSONObject user_detail = obj.getJSONObject(GlobalConstant.technician_detail);
                map.put(GlobalConstant.name, user_detail.getString(GlobalConstant.name));
                map.put(GlobalConstant.image, user_detail.getString(GlobalConstant.image));

                list.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        completed_listView.setAdapter(new CompletedAdapter(getActivity()));
        completed_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent detail = new Intent(getActivity(), UserAppointmentActivity.class);
                detail.putExtra("pos", String.valueOf(i));
                detail.putExtra("type", "0");
                startActivity(detail);
                getActivity().finish();
            }
        });
        return v;
    }

    public static UserCompletedFragment newInstance(String text) {

        UserCompletedFragment f = new UserCompletedFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


    class CompletedAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;

        com.nostra13.universalimageloader.core.ImageLoader imageLoader;
        DisplayImageOptions options;

        CompletedAdapter(Context c/*, ArrayList<HashMap<String, String>> list*/) {
            this.c = c;
            //this.list = list;
            imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)

                    .showStubImage(R.drawable.user_back)        //	Display Stub Image
                    .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                    .cacheInMemory()
                    .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
            initImageLoader();
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

                view = inflatore.inflate(R.layout.complete_list_item, null);
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.delivered_date_txt = (TextView) view.findViewById(R.id.delivered_date_txt);
                holder.download_img = (ImageView) view.findViewById(R.id.download_img);
                holder.price_txt = (TextView) view.findViewById(R.id.price_txt);
                holder.tech_view = (CircleImageView) view.findViewById(R.id.tech_view);
                view.setTag(holder);
                holder.download_img.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }


            holder.name.setText(cap(list.get(i).get(GlobalConstant.name)));
            holder.price_txt.setText("$" + list.get(i).get(GlobalConstant.total_amount));
            holder.delivered_date_txt.setText(formatdate2(list.get(i).get(GlobalConstant.date)));


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
            holder.download_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder=(Holder)view.getTag();
                }
            });
            return view;
        }

        public class Holder {
            ImageView cancel, chat, message, call;
            LinearLayout setting_layout, setting_call_layout;
            CircleImageView tech_view;
            TextView name, delivered_date_txt, price_txt, rating_value;
            ImageView download_img;

        }
    }

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getActivity().getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
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
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
