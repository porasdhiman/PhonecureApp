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

/**
 * Created by worksdelight on 20/04/17.
 */

public class NotifyActivity extends Activity {
    ImageView back;
    ListView message_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        message_listView = (ListView) findViewById(R.id.message_listView);
        message_listView.setAdapter(new MessageAdapter(this));
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
            return 5;
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
            return view;
        }
    }
}
