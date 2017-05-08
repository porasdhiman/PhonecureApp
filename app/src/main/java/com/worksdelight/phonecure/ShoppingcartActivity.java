package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by worksdelight on 08/03/17.
 */

public class ShoppingcartActivity extends Activity {
    ListView cart_list;
    ScrollView main_scrollView;
    TextView checkout_btn, total_price, cart_value_info,clear;
    Global global;
    List<String> selectList;
    ArrayList<HashMap<String, String>> listing = new ArrayList<>();
    ArrayList<HashMap<String, String>> serviceList = new ArrayList<>();
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    HashMap<String, String> listmap = new HashMap<>();
    String url;
    ImageView back;
    ArrayList<HashMap<String, String>> priceList = new ArrayList<>();
    float pricevalue=0.0f;
    AlertDialog builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoping_cart_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        init();

    }

    public void init() {
        cart_list = (ListView) findViewById(R.id.cart_list);
        cart_value_info = (TextView) findViewById(R.id.cart_value_info);
        main_scrollView = (ScrollView) findViewById(R.id.main_scrollView);
        clear=(TextView)findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(ShoppingcartActivity.this).setMessage("Do You Want To Clear Cart Data?")
                        .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(ShoppingcartActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }

                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                builder.dismiss();
                            }
                        }).show();


            }
        });
        checkout_btn = (TextView) findViewById(R.id.checkout_btn);
        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.setNewListing(serviceList);
                Intent i = new Intent(ShoppingcartActivity.this, AlmostdoneActivity.class);
                i.putExtra("pos", getIntent().getExtras().getString("pos"));
              //  i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                i.putExtra("total_price", total_price.getText().toString().replace("$", ""));
                startActivity(i);
            }
        });

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        total_price = (TextView) findViewById(R.id.total_price);
        Log.e("postion",getIntent().getExtras().getString("pos"));
        try {
            JSONObject obj=global.getCartData().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
            JSONArray servicesArr = obj.getJSONArray("technician_services");

            for (int j = 0; j < servicesArr.length(); j++) {
                JSONObject serviceObj = servicesArr.getJSONObject(j);
                HashMap<String, String> serviceMap = new HashMap<>();
                serviceMap.put(GlobalConstant.id, serviceObj.getString(GlobalConstant.id));
                serviceMap.put(GlobalConstant.name, serviceObj.getString(GlobalConstant.name));
                serviceMap.put(GlobalConstant.icon, serviceObj.getString(GlobalConstant.icon));
                serviceMap.put(GlobalConstant.price, serviceObj.getString(GlobalConstant.price));
                serviceMap.put(GlobalConstant.count,serviceObj.getString(GlobalConstant.quantity));

                serviceList.add(serviceMap);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("service_list",serviceList.toString());
        cart_value_info.setText(serviceList.size()+" Item in your cart");
        for (int i=0;i<serviceList.size();i++){
            if(serviceList.get(i).get(GlobalConstant.price).contains(".")){
                pricevalue=pricevalue+Float.parseFloat(serviceList.get(i).get(GlobalConstant.price));

            }else{
                pricevalue=pricevalue+Float.parseFloat(serviceList.get(i).get(GlobalConstant.price));

            }
        }
        total_price.setText("$" + pricevalue);
        cart_list.setAdapter(new ShoppingAdapter(this));
        CommonUtils.getListViewSize(cart_list);
        main_scrollView.smoothScrollTo(0, 0);
    }



    public class ShoppingAdapter extends BaseSwipeAdapter {

        private Context mContext;
        Holder h;


        float priceValue;

        public ShoppingAdapter(Context mContext) {

            this.mContext = mContext;


            imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)

                    .showStubImage(0)        //	Display Stub Image
                    .showImageForEmptyUri(0)    //	If Empty image found
                    .cacheInMemory()
                    .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
            initImageLoader();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public View generateView(final int position, ViewGroup parent) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.cart_list_item, null);
            SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
            swipeLayout.setRightSwipeEnabled(false);
            swipeLayout.setLeftSwipeEnabled(false);
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
                            serviceList.remove(position);
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
            TextView t = (TextView) convertView.findViewById(R.id.name_view);
            final TextView person_count = (TextView) convertView.findViewById(R.id.person_count);
            ImageView service_view = (ImageView) convertView.findViewById(R.id.service_view);
            ImageView add_view = (ImageView) convertView.findViewById(R.id.plus);
            ImageView minus = (ImageView) convertView.findViewById(R.id.minus);
            final TextView price = (TextView) convertView.findViewById(R.id.price);
            price.setText("$" + String.valueOf(Float.parseFloat(serviceList.get(position).get(GlobalConstant.price))));
            t.setText(serviceList.get(position).get(GlobalConstant.name));
            person_count.setText(serviceList.get(position).get(GlobalConstant.count));


            priceValue = Float.parseFloat(serviceList.get(position).get(GlobalConstant.price));


            url = GlobalConstant.SUB_CAETGORY_IMAGE_URL + serviceList.get(position).get(GlobalConstant.icon);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, service_view, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                service_view.setImageResource(0);
            }
            add_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int j = Integer.parseInt(person_count.getText().toString());

                    j = j + 1;
                    person_count.setText(String.valueOf(j));
                    serviceList.get(position).put(GlobalConstant.count, String.valueOf(j));
                    String priceFirstCat = price.getText().toString().replace("$", "");
                    price.setText("$" + String.valueOf(Float.parseFloat(priceFirstCat) + priceValue));
                    serviceList.get(position).put(GlobalConstant.price, price.getText().toString().replace("$", ""));
                    float pricevalue = 0;
                    for (int k = 0; k < serviceList.size(); k++) {
                        pricevalue = pricevalue + Float.parseFloat(serviceList.get(k).get(GlobalConstant.price));
                    }
                    total_price.setText("$" + pricevalue);
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int j = Integer.parseInt(person_count.getText().toString());
                    if (j == 1) {
                        person_count.setText(String.valueOf(1));
                        Toast.makeText(mContext, "less then one not allowed", Toast.LENGTH_SHORT).show();
                        serviceList.get(position).put(GlobalConstant.count, String.valueOf(j));
                        price.setText("$" + priceValue);
                        serviceList.get(position).put(GlobalConstant.price, price.getText().toString().replace("$", ""));
                        float pricevalue = 0;
                        for (int k = 0; k < serviceList.size(); k++) {
                            pricevalue = pricevalue + Float.parseFloat(serviceList.get(k).get(GlobalConstant.price));
                        }
                        total_price.setText("$" + pricevalue);
                    } else {
                        j = j - 1;
                        person_count.setText(String.valueOf(j));
                        serviceList.get(position).put(GlobalConstant.count, String.valueOf(j));
                        String priceFirstCat = price.getText().toString().replace("$", "");
                        price.setText("$" + String.valueOf(Float.parseFloat(priceFirstCat) - priceValue));
                        serviceList.get(position).put(GlobalConstant.price, price.getText().toString().replace("$", ""));
                        float pricevalue = 0;
                        for (int k = 0; k < serviceList.size(); k++) {
                            pricevalue = pricevalue + Float.parseFloat(serviceList.get(k).get(GlobalConstant.price));
                        }
                        total_price.setText("$" + pricevalue);
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return serviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return serviceList.get(position);
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

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
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
