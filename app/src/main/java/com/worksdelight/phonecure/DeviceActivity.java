package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by worksdelight on 07/03/17.
 */

public class DeviceActivity extends Activity implements View.OnClickListener {
    TextView device_txtView, types_txtView;
    ListView device_listView/*, types_listView*/;
    int imgArray[] = {R.drawable.apple_big_logo, R.drawable.android_logo, R.drawable.windows_logo, R.drawable.tablet_logo, R.drawable.portable_logo, R.drawable.game_logo};
    RelativeLayout type_view_include;
    String txtArray[] = {"Apple Device", "Android Device", "Window Device", "Tablet Device", "Portable Device", "Game Console"};
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        init();
    }

    public void init() {
        //device_txtView = (TextView) findViewById(R.id.device_txtView);
       // types_txtView = (TextView) findViewById(R.id.types_txtView);
        device_listView = (ListView) findViewById(R.id.device_listView);
        //types_listView = (ListView) findViewById(R.id.types_listView);
        type_view_include = (RelativeLayout) findViewById(R.id.type_view_include);
        back = (ImageView) findViewById(R.id.back);
        device_listView.setAdapter(new DeviceAdapter(DeviceActivity.this, imgArray, txtArray));
        getListViewSize(device_listView);
        //device_txtView.setOnClickListener(this);
        //types_txtView.setOnClickListener(this);
        back.setOnClickListener(this);
        device_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent iPhone=new Intent(DeviceActivity.this,iPhoneServiceActivity.class);
                iPhone.putExtra("device_type",txtArray[i]);
                startActivity(iPhone);
            }
        });
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.device_txtView:
                device_listView.setVisibility(View.VISIBLE);
                type_view_include.setVisibility(View.GONE);
                types_txtView.setBackgroundResource(R.drawable.white_btn_back);
                device_txtView.setBackgroundResource(R.drawable.green_btn);
                device_txtView.setTextColor(Color.parseColor("#ffffff"));
                types_txtView.setTextColor(Color.parseColor("#FFE6E4E4"));

                break;
            case R.id.types_txtView:
                device_listView.setVisibility(View.GONE);
                type_view_include.setVisibility(View.VISIBLE);
                device_txtView.setBackgroundResource(R.drawable.white_btn_back);
                types_txtView.setBackgroundResource(R.drawable.green_btn);
                types_txtView.setTextColor(Color.parseColor("#ffffff"));
                device_txtView.setTextColor(Color.parseColor("#FFE6E4E4"));
                break;
            case R.id.back:
                finish();
                break;
        }
*/
    }

    //------------------------Device adapter--------------------------------

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

                view = inflator.inflate(R.layout.device_item_layout, null);
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

    //------------------------Type Adapter--------------------
    class TypeAdapter extends BaseAdapter {


        Context c;

        TypeAdapter(Context c) {
            this.c = c;

        }

        @Override
        public int getCount() {
            return 0;
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
            return null;
        }
    }

    //----------------------------------ListView scrolloing method----------
    public void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

}
