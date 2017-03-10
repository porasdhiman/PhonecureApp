package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import static com.worksdelight.phonecure.R.id.setting_layout;


/**
 * Created by worksdelight on 01/03/17.
 */

public class RepairActivity extends Activity {
    ListView repair_listView;


TextView top_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iphone_repaire_layout);
        repair_listView = (ListView) findViewById(R.id.repair_listView);

        repair_listView.setAdapter(new ListViewAdapter(this));
        top_txt=(TextView)findViewById(R.id.top_txt);
        top_txt.setVisibility(View.GONE);

    }




    class RepairAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;

        RepairAdapter(Context c) {
            this.c = c;
            inflatore = LayoutInflater.from(c);
        }
        @Override
        public int getViewTypeCount() {
            return 10;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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
            if (view == null) {
                holder = new Holder();

                view = inflatore.inflate(R.layout.repair_list_item, null);
                holder.call = (ImageView) view.findViewById(R.id.call);
                holder.message = (ImageView) view.findViewById(R.id.message);

                holder.chat = (ImageView) view.findViewById(R.id.chat);

                holder.cancel = (ImageView) view.findViewById(R.id.cancel);

                holder.setting_layout = (LinearLayout) view.findViewById(setting_layout);
                holder.setting_call_layout = (LinearLayout) view.findViewById(R.id.setting_call_layout);

                view.setTag(holder);
                holder.setting_layout.setTag(holder);
                holder.call.setTag(holder);
                holder.chat.setTag(holder);
                holder.message.setTag(holder);
                holder.cancel.setTag(holder);
                holder.setting_call_layout.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }
            holder.setting_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.setting_call_layout.setVisibility(View.VISIBLE);
                }
            });
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            return view;
        }

        public class Holder {
            ImageView cancel, chat, message, call;
            LinearLayout setting_layout, setting_call_layout;

        }
    }
}
