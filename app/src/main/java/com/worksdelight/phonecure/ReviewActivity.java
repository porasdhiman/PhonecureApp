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
import android.widget.ListView;

/**
 * Created by worksdelight on 06/03/17.
 */

public class ReviewActivity extends Activity {
    ListView review_listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.review_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        review_listView=(ListView)findViewById(R.id.review_listView);
        //img.setColorFilter(img.getContext().getResources().getColor(R.color.fb_back), PorterDuff.Mode.SRC_ATOP);
        review_listView.setAdapter(new ReviewAdapter(this));
    }

    class ReviewAdapter extends BaseAdapter{
        Context c;
        LayoutInflater inflatore;
        ReviewAdapter(Context c){
            this.c=c;
            inflatore=(LayoutInflater.from(c));

        }

        @Override
        public int getCount() {
            return 20;
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
            view=inflatore.inflate(R.layout.review_list_item,null);
            return view;
        }
    }
}
