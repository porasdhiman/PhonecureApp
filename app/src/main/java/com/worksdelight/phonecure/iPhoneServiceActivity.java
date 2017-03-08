package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.device_image.setImageResource(img[i]);
            holder.device_name.setText(text[i]);
            return view;
        }

        class Holder {
            ImageView device_image;
            TextView device_name;
        }
    }
}
