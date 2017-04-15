package com.worksdelight.phonecure;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 12/04/17.
 */

public class PandingFragment extends Fragment {
    ListView completed_listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.completed_layout, container, false);
        completed_listView = (ListView) v.findViewById(R.id.completed_listView);
        completed_listView.setAdapter(new ShoppingAdapter(getActivity()));
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
        ShoppingcartActivity.ShoppingAdapter.Holder h;

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
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public View generateView(final int position, ViewGroup parent) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.pending_layout_item, null);
            SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    v.findViewById(R.id.delete_img).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();

                            notifyDataSetChanged();
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
            });


            return v;
        }

        @Override
        public void fillValues(final int position, View convertView) {

           /* if(convertView==null){
                h=new Holder();

                h.name_view=(TextView)convertView.findViewById(R.id.name_view);
                h.price=(TextView)convertView.findViewById(R.id.price);
                h.person_count=(TextView)convertView.findViewById(R.id.person_count);
                h.add_view=(ImageView) convertView.findViewById(R.id.add);
                h.minus=(ImageView) convertView.findViewById(R.id.minus);
                h.service_view=(ImageView) convertView.findViewById(R.id.service_view);
                convertView.setTag(h);
                h.add_view.setTag(h);
                h.minus.setTag(h);
                h.person_count.setTag(h);
            }else{
                h = (Holder) convertView.getTag();
            }
           // h.name_view.setText(deviceListing.get(position).get(GlobalConstant.name));
            h.person_count.setText(deviceListing.get(position).get(GlobalConstant.count));
            url = GlobalConstant.SUB_CAETGORY_IMAGE_URL + deviceListing.get(position).get(GlobalConstant.icon);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, h.service_view, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                h.service_view.setImageResource(0);
            }


            h.add_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    h=(Holder)view.getTag();
                    int j=Integer.parseInt(h.person_count.getText().toString());

                        j=j+1;
                        h.person_count.setText(String.valueOf(j));
                        deviceListing.get(position).put(GlobalConstant.count,String.valueOf(j));


                }
            });
            h.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    h=(Holder)view.getTag();
                    int j=Integer.parseInt(h.person_count.getText().toString());
                    if(j==0){
                        h.person_count.setText(String.valueOf(0));
                        Toast.makeText(mContext,"less then zero not allowed",Toast.LENGTH_SHORT).show();
                        deviceListing.get(position).put(GlobalConstant.count,String.valueOf(j));

                    }else{
                        j=j-1;
                        h.person_count.setText(String.valueOf(j));
                        deviceListing.get(position).put(GlobalConstant.count,String.valueOf(j));

                    }
                }
            });*/

        }

        @Override
        public int getCount() {
            return 10;
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
            TextView name_view, price, person_count;
            ImageView add_view, minus, service_view;
        }
    }
}
