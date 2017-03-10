package com.worksdelight.phonecure;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by worksdelight on 10/03/17.
 */

public class HorizontalViewAdapter extends BaseAdapter {
    Context c;
    LayoutInflater inflatore;
    public HorizontalViewAdapter(Context c){
        this.c=c;
        inflatore=LayoutInflater.from(c);

    }
    @Override
    public int getCount() {
        return 10;
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
        view=inflatore.inflate(R.layout.map_pager_item,null);
        TextView book_appointment=(TextView)view.findViewById(R.id.book_appointment);
        book_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j=new Intent(c,BookAppoinmentActivity.class);
                c.startActivity(j);
            }
        });
        return view;
    }
}
