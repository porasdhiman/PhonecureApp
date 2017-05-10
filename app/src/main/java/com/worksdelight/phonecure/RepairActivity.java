package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.worksdelight.phonecure.R.id.setting_layout;


/**
 * Created by worksdelight on 01/03/17.
 */

public class RepairActivity extends Activity {
    ListView repair_listView;


    TextView top_txt;
    Global global;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iphone_repaire_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();
        back = (ImageView) findViewById(R.id.back);
        repair_listView = (ListView) findViewById(R.id.repair_listView);
        repair_listView.setAdapter(new RepairAdapter(this, global.getDateList()));
        top_txt = (TextView) findViewById(R.id.top_txt);
        top_txt.setVisibility(View.GONE);
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        repair_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RepairActivity.this, TechniciansDetailActivity.class);
                intent.putExtra("pos", String.valueOf(i));
                //intent.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                intent.putExtra("type", "0");
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    class RepairAdapter extends BaseAdapter {
        Context c;
        LayoutInflater inflatore;
        Holder holder;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        RepairAdapter(Context c, ArrayList<HashMap<String, String>> list) {
            this.c = c;
            this.list = list;
            inflatore = LayoutInflater.from(c);
        }

        @Override
        public int getViewTypeCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return list.size();
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
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.location = (TextView) view.findViewById(R.id.location);

                holder.specilist = (TextView) view.findViewById(R.id.specilist);

                holder.rating_value = (TextView) view.findViewById(R.id.rating_value);
                holder.tech_view = (CircleImageView) view.findViewById(R.id.tech_view);

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
            holder.name.setText(list.get(i).get(GlobalConstant.name));
            holder.location.setText(list.get(i).get(GlobalConstant.distance) + "Km Away");
            holder.rating_value.setText(list.get(i).get(GlobalConstant.average_rating));
            // holder.specilist.setText(list.get(i).get(GlobalConstant.average_rating));
            String url = GlobalConstant.TECHNICIANS_IMAGE_URL + list.get(i).get(GlobalConstant.image);
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, holder.tech_view, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                            }
                        });
            } else {
                holder.tech_view.setImageResource(R.drawable.user_back);
            }

          /*  holder.setting_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.VISIBLE);
                }
            });*/
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Holder) view.getTag();
                    holder.setting_call_layout.setVisibility(View.GONE);
                }
            });
            return view;
        }

        public class Holder {
            ImageView cancel, chat, message, call;
            LinearLayout setting_layout, setting_call_layout;
            CircleImageView tech_view;
            TextView name, location, specilist, rating_value;

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
}
