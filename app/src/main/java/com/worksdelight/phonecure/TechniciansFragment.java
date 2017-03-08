package com.worksdelight.phonecure;

import android.content.Context;
import android.content.Intent;
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

import static com.worksdelight.phonecure.R.id.setting_layout;

/**
 * Created by worksdelight on 28/02/17.
 */

public class TechniciansFragment extends Fragment {
    ListView repair_listView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.technicians_layout, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();

        repair_listView = (ListView)v.findViewById(R.id.repair_listView);

        repair_listView.setAdapter(new ListViewAdapter(getActivity()) {
        });
        repair_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent j=new Intent(getActivity(),TechniciansDetailActivity.class);
                startActivity(j);
            }
        });
        return v;
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

        class Holder {
            ImageView cancel, chat, message, call;
            LinearLayout setting_layout, setting_call_layout;

        }
    }
}




