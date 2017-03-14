package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by worksdelight on 07/03/17.
 */

public class iPhoneServiceActivity extends Activity {
    ListView service_list;
    int imgArray[] = {R.drawable.backcover, R.drawable.battey, R.drawable.camera, R.drawable.charger, R.drawable.home_btn, R.drawable.microphone,R.drawable.ios_txt};
    String txtArray[] = {"Backcover", "Battery", "Front camera", "Dock charger", "Home Button", "Microphone","Software"};
ImageView search_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iphone_service_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        init();
    }
    public void init() {

        service_list = (ListView) findViewById(R.id.service_list);
        service_list.setAdapter(new DeviceAdapter(iPhoneServiceActivity.this,imgArray,txtArray));
        search_img=(ImageView)findViewById(R.id.search_img);

        service_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    //--------------------Adapter class-----------------
    class DeviceAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflator;
        Holder holder = null;
        int img[];
        String text[];

        DeviceAdapter(Context c, int img[], String text[]) {
            this.c = c;
            this.img = img;
            this.text = text;
            inflator = LayoutInflater.from(c);

        }

        @Override
        public int getCount() {
            return img.length;
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
            holder = new Holder();
            if (view == null) {

                view = inflator.inflate(R.layout.iphone_service_list_item, null);
                holder.device_image = (ImageView) view.findViewById(R.id.device_icon);
                holder.device_name = (TextView) view.findViewById(R.id.device_name);
                holder.select_img = (ImageView) view.findViewById(R.id.select_img);
                holder.unselect_img = (ImageView) view.findViewById(R.id.unselect_img);
                view.setTag(holder);
                holder.select_img.setTag(holder);
                holder.unselect_img.setTag(holder);
                holder.device_name.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.device_image.setImageResource(img[i]);
            holder.device_name.setText(text[i]);
            holder.select_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (Holder) v.getTag();
                    holder.select_img.setVisibility(View.GONE);
                    holder.unselect_img.setVisibility(View.VISIBLE);
                }
            });
            holder.unselect_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (Holder) v.getTag();
                    holder.select_img.setVisibility(View.VISIBLE);
                    holder.unselect_img.setVisibility(View.GONE);
                }
            });
            holder.device_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (Holder) v.getTag();
                    Intent map=new Intent(iPhoneServiceActivity.this,MapsActivity.class);
                    startActivity(map);
                }
            });

            return view;
        }

        class Holder {
            ImageView device_image,select_img,unselect_img;
            TextView device_name;
        }
    }
}
