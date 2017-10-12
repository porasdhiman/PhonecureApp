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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.worksdelight.phonecure.R.id.person_count;
import static com.worksdelight.phonecure.R.id.service_view;
import static java.lang.Float.parseFloat;

/**
 * Created by worksdelight on 08/03/17.
 */

public class ShoppingcartActivity extends Activity {
    ListView cart_list;
    ScrollView main_scrollView;
    TextView checkout_btn, total_price, cart_value_info, clear;
    Global global;
    List<String> selectList;
    ArrayList<HashMap<String, String>> listing = new ArrayList<>();
    ArrayList<HashMap<String, String>> serviceList = new ArrayList<>();
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    HashMap<String, String> listmap = new HashMap<>();
    String url;
    ImageView back;
    TextView other_price;
    ArrayList<HashMap<String, String>> priceList = new ArrayList<>();
    float pricevalue = 0.0f;
    AlertDialog builder;
    float other_Charges;
    LinearLayout main_layout;

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
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        Fonts.overrideFonts(this, main_layout);
        cart_list = (ListView) findViewById(R.id.cart_list);
        other_price = (TextView) findViewById(R.id.other_price);
        cart_value_info = (TextView) findViewById(R.id.cart_value_info);
        main_scrollView = (ScrollView) findViewById(R.id.main_scrollView);
        clear = (TextView) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(ShoppingcartActivity.this).setMessage(getResources().getString(R.string.clear_mes))
                        .setCancelable(false).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(ShoppingcartActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }

                        }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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
                float finalTotel = Float.parseFloat(total_price.getText().toString().replace("€", ""));
                Intent i = new Intent(ShoppingcartActivity.this, AlmostdoneActivity.class);
                i.putExtra("pos", getIntent().getExtras().getString("pos"));
                //  i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                i.putExtra("total_price", String.valueOf(finalTotel));
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
        Log.e("postion", getIntent().getExtras().getString("pos"));
        try {
            JSONObject obj = global.getCartData().getJSONObject(Integer.parseInt(getIntent().getExtras().getString("pos")));
            JSONArray servicesArr = obj.getJSONArray("technician_services");

            for (int j = 0; j < servicesArr.length(); j++) {
                JSONObject serviceObj = servicesArr.getJSONObject(j);
                HashMap<String, String> serviceMap = new HashMap<>();
                serviceMap.put(GlobalConstant.id, serviceObj.getString(GlobalConstant.id));
                serviceMap.put(GlobalConstant.name, serviceObj.getString(GlobalConstant.name));
                serviceMap.put(GlobalConstant.icon, serviceObj.getString(GlobalConstant.icon));
                serviceMap.put(GlobalConstant.price, serviceObj.getString(GlobalConstant.price));
                serviceMap.put(GlobalConstant.count, serviceObj.getString(GlobalConstant.quantity));

                serviceList.add(serviceMap);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("service_list", serviceList.toString());
        cart_value_info.setText(serviceList.size() + " "+getResources().getString(R.string.cart_value));
        for (int i = 0; i < serviceList.size(); i++) {
            HashMap<String, String> priceMap = new HashMap<>();
            priceMap.put("price", serviceList.get(i).get(GlobalConstant.price));
            priceList.add(priceMap);
            if (serviceList.get(i).get(GlobalConstant.price).contains(".")) {
                pricevalue = pricevalue + parseFloat(serviceList.get(i).get(GlobalConstant.price));

            } else {
                pricevalue = pricevalue + parseFloat(serviceList.get(i).get(GlobalConstant.price));

            }
        }

        other_Charges = Float.parseFloat(global.getDateList().get(Integer.parseInt(getIntent().getExtras().getString("pos"))).get(GlobalConstant.other_charges));
        if (global.getDropOff().equalsIgnoreCase("1")) {
            other_price.setText("€" + 20.0);
            pricevalue = pricevalue + 20.0f;

            total_price.setText("€" + pricevalue);
        }
        if (global.getPickUp().equalsIgnoreCase("1")) {
            other_price.setText("€" + 0);
            pricevalue = pricevalue;

            total_price.setText("€" + pricevalue);
        }

        cart_list.setAdapter(new ShoppingAdapter(this));
        CommonUtils.getListViewSize(cart_list);
        main_scrollView.smoothScrollTo(0, 0);
    }


    public class ShoppingAdapter extends BaseAdapter {
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
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return serviceList.size();
        }

        @Override
        public int getCount() {
            return serviceList.size();
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
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                h = new Holder();
                view = LayoutInflater.from(mContext).inflate(R.layout.cart_list_item, null);
                h.main_layout_cart = (LinearLayout) view.findViewById(R.id.main_layout_cart);


                h.name_view = (TextView) view.findViewById(R.id.name_view);
                h.price = (TextView) view.findViewById(R.id.price);
                h.person_count = (TextView) view.findViewById(person_count);
                h.add_view = (ImageView) view.findViewById(R.id.plus);
                h.minus = (ImageView) view.findViewById(R.id.minus);
                h.service_view = (ImageView) view.findViewById(service_view);
                view.setTag(h);
                h.add_view.setTag(h);
                h.minus.setTag(h);
                h.person_count.setTag(h);
            } else {
                h = (Holder) view.getTag();
            }
            Fonts.overrideFonts(mContext, h.main_layout_cart);

            h.price.setText("€" + String.valueOf(parseFloat(serviceList.get(position).get(GlobalConstant.price))));
            h.name_view.setText(serviceList.get(position).get(GlobalConstant.name));
            h.person_count.setText(serviceList.get(position).get(GlobalConstant.count));


            url = GlobalConstant.SUB_CAETGORY_IMAGE_URL + serviceList.get(position).get(GlobalConstant.icon);
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
                    priceValue = parseFloat(priceList.get(position).get(GlobalConstant.price));
                    h = (Holder) view.getTag();
                    int j = Integer.parseInt(h.person_count.getText().toString());

                    j = j + 1;
                    h.person_count.setText(String.valueOf(j));
                    serviceList.get(position).put(GlobalConstant.count, String.valueOf(j));
                    String priceFirstCat = h.price.getText().toString().replace("€", "");
                    h.price.setText("€" + String.valueOf(parseFloat(priceFirstCat) + priceValue));
                    serviceList.get(position).put(GlobalConstant.price, h.price.getText().toString().replace("€", ""));
                    float pricevalue = 0;
                    for (int k = 0; k < serviceList.size(); k++) {
                        pricevalue = pricevalue + parseFloat(serviceList.get(k).get(GlobalConstant.price));
                    }
                    if (global.getDropOff().equalsIgnoreCase("1")) {
                        pricevalue = pricevalue + 20.0f;
                        total_price.setText("€" + pricevalue);
                    } else {
                        total_price.setText("€" + pricevalue);
                    }


                }
            });
            h.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    priceValue = parseFloat(priceList.get(position).get(GlobalConstant.price));
                    h = (Holder) view.getTag();
                    int j = Integer.parseInt(h.person_count.getText().toString());
                    if (j == 1) {
                        h.person_count.setText(String.valueOf(1));
                        Toast.makeText(mContext, "less then one not allowed", Toast.LENGTH_SHORT).show();
                        serviceList.get(position).put(GlobalConstant.count, String.valueOf(j));
                        h.price.setText("€" + priceValue);
                        serviceList.get(position).put(GlobalConstant.price, h.price.getText().toString().replace("€", ""));
                        float pricevalue = 0;
                        for (int k = 0; k < serviceList.size(); k++) {
                            pricevalue = pricevalue + parseFloat(serviceList.get(k).get(GlobalConstant.price));
                        }
                        if (global.getDropOff().equalsIgnoreCase("1")) {

                            pricevalue = pricevalue + 20.0f;
                            total_price.setText("€" + pricevalue);
                        } else {
                            total_price.setText("€" + pricevalue);
                        }
                    } else {
                        j = j - 1;
                        h.person_count.setText(String.valueOf(j));
                        serviceList.get(position).put(GlobalConstant.count, String.valueOf(j));
                        String priceFirstCat = h.price.getText().toString().replace("€", "");
                        h.price.setText("€" + String.valueOf(parseFloat(priceFirstCat) - priceValue));
                        serviceList.get(position).put(GlobalConstant.price, h.price.getText().toString().replace("€", ""));
                        float pricevalue = 0;
                        for (int k = 0; k < serviceList.size(); k++) {
                            pricevalue = pricevalue + parseFloat(serviceList.get(k).get(GlobalConstant.price));
                        }
                        if (global.getDropOff().equalsIgnoreCase("1")) {

                            pricevalue = pricevalue + 20.0f;
                            total_price.setText("€" + pricevalue);
                        } else {
                            total_price.setText("€" + pricevalue);
                        }


                    }

                }
            });


            return view;
        }

        class Holder {
            TextView name_view, price, person_count;
            ImageView add_view, minus, service_view;
            LinearLayout main_layout_cart;
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
