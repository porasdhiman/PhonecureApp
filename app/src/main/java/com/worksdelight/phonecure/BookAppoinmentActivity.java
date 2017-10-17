package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bruce.pickerview.LoopScrollListener;
import com.bruce.pickerview.LoopView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.worksdelight.phonecure.R.id.calendarView;

/**
 * Created by worksdelight on 02/03/17.
 */

public class BookAppoinmentActivity extends Activity implements OnDateSelectedListener, OnMonthChangedListener, TimePickerDialog.OnTimeSetListener {
    MaterialCalendarView mcv;

    TextView book_btn;
    List<CalendarDay> calenderlist = new ArrayList<CalendarDay>();
    CalendarDay selectDate;

    private Collection<CalendarDay> calendarDays = new Collection<CalendarDay>() {
        @Override
        public boolean add(CalendarDay object) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends CalendarDay> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean contains(Object object) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NonNull
        @Override
        public Iterator<CalendarDay> iterator() {
            return null;
        }

        @Override
        public boolean remove(Object object) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(T[] array) {
            return null;
        }
    };
    Global global;
    List<String> list = new ArrayList<>();
    RelativeLayout time_layout;

    String sendDate = "";

    String pos;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    String minTimehour, minTimeminute, maxTimehour, maxTimeminute;
    TimePickerDialog timePickerDialog;
    private int mHour, mMinute;

    ImageView back;

    ImageView pickUp_img, dropoff_img;
    int p = 0;
    TextView distance_shop;
    int monthsDayes[] = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER};
    String weekDayes[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    ArrayList<String> availList = new ArrayList<>();

    Calendar mCalendarOpeningTime, mCalendarClosingTime, finalTime;
    String openTime = "", popenTime = "", closeTime = "", pclosetime = "";
    LinearLayout dropoff_layout, pickup_layout;
    int dayOfWeek;
    TextView est_time;
    int y;
    Dialog dialog2;
    String formattedDate;
    TimeZone tz;
    FlowLayout time_slot_flowView;
    ArrayList<HashMap<String, String>> timeSlotArr = new ArrayList<>();
    int k=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  String languageToLoad  = "fa"; // your language

        setContentView(R.layout.book_appoinment_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        global = (Global) getApplicationContext();


        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)

                .showStubImage(R.drawable.user_back)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.user_back)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        initImageLoader();
        pickup_layout = (LinearLayout) findViewById(R.id.pickup_layout);
        dropoff_layout = (LinearLayout) findViewById(R.id.dropoff_layout);
        time_slot_flowView = (FlowLayout) findViewById(R.id.time_slot_flowView);
        pickUp_img = (ImageView) findViewById(R.id.pickUp_img);
        dropoff_img = (ImageView) findViewById(R.id.dropoff_img);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        book_btn = (TextView) findViewById(R.id.book_btn);
        est_time = (TextView) findViewById(R.id.est_time);
        distance_shop = (TextView) findViewById(R.id.distance_shop);


        pos = getIntent().getExtras().getString("pos");

        distance_shop.setText(global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.distance) + " " + getResources().getString(R.string.km_away));

        est_time.setText(getDurationString(Integer.parseInt(global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.estimated_travel_time))) + " " + getResources().getString(R.string.hours));


        tz = TimeZone.getDefault();
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c.getTime());
        sendDate = formattedDate;
        String date = formattedDate.split("-")[2];
        final String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[0];

        // Toast.makeText(this,date+"-"+month+"-"+year,Toast.LENGTH_SHORT).show();
        mcv = (MaterialCalendarView) findViewById(calendarView);
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);
        y = Integer.parseInt(year);
        mcv.setSelectedDate(c.getTime());
        mcv.state().edit()

                .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date)))
                .setMaximumDate(CalendarDay.from(Integer.parseInt(year), 12, 31))
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.setSendDate(sendDate);
                if (sendDate.equalsIgnoreCase("") ) {
                    Toast.makeText(BookAppoinmentActivity.this, getResources().getString(R.string.booking_date_time_valid), Toast.LENGTH_SHORT).show();
                }else if(global.getTimeSlot().equalsIgnoreCase("")){
                    Toast.makeText(BookAppoinmentActivity.this, getResources().getString(R.string.booking_date_time_valid), Toast.LENGTH_SHORT).show();

                }
                else if (global.getPickUp().equalsIgnoreCase("0") && global.getDropOff().equalsIgnoreCase("0")) {
                    Toast.makeText(BookAppoinmentActivity.this, getResources().getString(R.string.repair_option_valid), Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(BookAppoinmentActivity.this, ShoppingcartActivity.class);
                    // i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                    i.putExtra("pos", String.valueOf(pos));
                    startActivity(i);
                }

            }
        });

        JSONObject obj = null;
        try {
            obj = global.getCartData().getJSONObject(Integer.parseInt(pos));
            JSONArray avail_arr = obj.getJSONArray(GlobalConstant.availability);
            for (int i = 0; i < avail_arr.length(); i++) {
                JSONObject sun_obj = avail_arr.getJSONObject(i);
                if (sun_obj.getString(GlobalConstant.status).equalsIgnoreCase("closed")) {
                    availList.add(sun_obj.getString(GlobalConstant.day));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int k = 0; k < weekDayes.length; k++) {
            for (int i = 0; i < availList.size(); i++) {
                if (weekDayes[k].equalsIgnoreCase(availList.get(i))) {
                    for (int l = 0; l < monthsDayes.length; l++) {

                        list.addAll(getDayNameInYear(l, k + 1));


                    }
                }
            }

        }
        for (int i = 0; i < list.size(); i++) {
          /*  String date1=list.get(i).split("-")[2];
            String year1=list.get(i).split("-")[0];
            String month1=list.get(i).split("-")[1];*/

            selectDate = new CalendarDay();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = format.parse(list.get(i));
                selectDate = CalendarDay.from(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calenderlist.add(selectDate);

        }
        Log.e("calender list", calenderlist.toString());
        mcv.addDecorators(new EventDecorator(calenderlist));


        if (global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.repair_at_shop).equalsIgnoreCase("1") && global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.repair_on_location).equalsIgnoreCase("1")) {

            pickUp_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    pickUp_img.setImageResource(R.drawable.pickup);
                    dropoff_img.setImageResource(R.drawable.dropoff_unselect);
                    global.setPickUp("1");
                    global.setDropOff("0");


                }
            });

            dropoff_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    pickUp_img.setImageResource(R.drawable.pickup_unselect);
                    dropoff_img.setImageResource(R.drawable.dropoff);
                    global.setPickUp("0");
                    global.setDropOff("1");

                }
            });


        } else if (global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.repair_at_shop).equalsIgnoreCase("1")) {

            pickUp_img.setImageResource(R.drawable.pickup);
            dropoff_img.setImageResource(R.drawable.dropoff_unselect);
            global.setPickUp("1");
            global.setDropOff("0");
            dropoff_layout.setAlpha(0.5f);


        } else if (global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.repair_on_location).equalsIgnoreCase("1")) {
            pickUp_img.setImageResource(R.drawable.pickup_unselect);
            dropoff_img.setImageResource(R.drawable.dropoff);
            global.setPickUp("0");
            global.setDropOff("1");
            pickup_layout.setAlpha(0.5f);


        }
        dialogWindow();
        slotMethod();
        //  Log.e("monday date",getMondaysOfJanuary().toString());
    }

    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");

        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number % 10 == 0) {
            return "" + number;
        }

        return String.valueOf(number);
    }


    private List<String> getDayNameInYear(int months, int days) {
        ArrayList<String> d = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.MONTH, months); // month starts by 0 = january
        cal.set(Calendar.DAY_OF_WEEK, days);

        cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        int month = cal.get(Calendar.MONTH);

        while (cal.get(Calendar.MONTH) == month) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(cal.getTime());
            d.add(formattedDate);
            cal.add(Calendar.DAY_OF_MONTH, 7);
        }
        return d;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        boolean value = false;

        //Toast.makeText(BookAppoinmentActivity.this, getSelectedDatesString(), Toast.LENGTH_SHORT).show();

        // Toast.makeText(BookAppoinmentActivity.this, formatdate2(getSelectedDatesString()), Toast.LENGTH_SHORT).show();
        sendDate = getSelectedDatesString();
        global.setSendDate(sendDate);

        setOpeningAndClosingTimes(dayOfWeek - 1);
        if(timeSlotArr.size()>0){
            timeSlotArr.clear();
        }
        dialogWindow();
        slotMethod();


    }

    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }


    private String getSelectedDatesString() {
        String datetime = null;
        CalendarDay date1 = mcv.getSelectedDate();
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date1.getDate());
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        datetime = d.format(date1.getDate());

        return datetime;
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String fDate = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);


    }

    public boolean isTimeWith_in_Interval(String valueToCheck, String endTime, String startTime) {
        boolean isBetween = false;
        try {
            Date time1 = new SimpleDateFormat("HH:mm").parse(endTime);

            Date time2 = new SimpleDateFormat("HH:mm").parse(startTime);

            Date d = new SimpleDateFormat("HH:mm").parse(valueToCheck);

            if (time1.after(d) && time2.before(d)) {
                isBetween = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isBetween;
    }

    class EventDecorator implements DayViewDecorator {

        //private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(/*int color, */Collection<CalendarDay> dates) {
            // this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {

            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);

            // view.addSpan(new DotSpan(7, color));

        }

    }

    private void setOpeningAndClosingTimes(int dayName) {

        JSONObject obj = null;
        try {
            obj = global.getCartData().getJSONObject(Integer.parseInt(pos));
            JSONArray avail_arr = obj.getJSONArray(GlobalConstant.availability);
            JSONObject sun_obj = avail_arr.getJSONObject(dayName);


            openTime = sun_obj.getString(GlobalConstant.opening_time);
            closeTime = sun_obj.getString(GlobalConstant.closing_time);
            popenTime = openTime;
            pclosetime = closeTime;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("time", openTime + " " + closeTime);
        minTimehour = openTime.split(":")[0];
        minTimeminute = openTime.split(":")[1];
        mCalendarOpeningTime = Calendar.getInstance();
        mCalendarOpeningTime.set(Calendar.HOUR, Integer.parseInt(minTimehour));
        mCalendarOpeningTime.set(Calendar.MINUTE, Integer.parseInt(minTimeminute));


        maxTimehour = closeTime.split(":")[0];
        maxTimeminute = closeTime.split(":")[1];
        mCalendarClosingTime = Calendar.getInstance();
        mCalendarClosingTime.set(Calendar.HOUR, Integer.parseInt(maxTimehour));
        mCalendarClosingTime.set(Calendar.MINUTE, Integer.parseInt(maxTimeminute));

    }

    public void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        timePickerDialog = new TimePickerDialog(this,
                this, mHour, mMinute, false);
        timePickerDialog.show();

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

    //--------------------Category api method---------------------------------
    private void slotMethod() {
        int calculateEstimateTime = 0;
        try {
            JSONObject obj = global.getCartData().getJSONObject(Integer.parseInt(pos));
            JSONArray techServiceArr = obj.getJSONArray("technician_services");
            for (int i = 0; i < techServiceArr.length(); i++) {
                JSONObject obj1 = techServiceArr.getJSONObject(i);
                calculateEstimateTime = calculateEstimateTime + Integer.parseInt(obj1.getString(GlobalConstant.expected_time));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = GlobalConstant.GET_TIME_SLOT_URL + GlobalConstant.id + "=" + global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.id) + "&" + GlobalConstant.date + "=" + sendDate +
                "&" + GlobalConstant.expected_time + "=" + String.valueOf(calculateEstimateTime) +
                "&" + GlobalConstant.estimated_travel_time + "=" + global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.estimated_travel_time) + "&timezone=" + tz.getID();
        Log.e("url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialog2.dismiss();

                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray data = obj.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject timeObj = data.getJSONObject(i);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(GlobalConstant.start, timeObj.getString(GlobalConstant.start));
                                    map.put(GlobalConstant.status, timeObj.getString(GlobalConstant.status));

                                    timeSlotArr.add(map);
                                }
                                addLayouts(timeSlotArr);


                            } else {
                                Toast.makeText(BookAppoinmentActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.progrees_login);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) dialog2.findViewById(R.id.loader_view);
        loaderView.setIndicatorColor(getResources().getColor(R.color.main_color));
        loaderView.show();

        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

    //-------------------------------------Time slot dyanmic view------------------------------------
    private void addLayouts(final ArrayList<HashMap<String, String>> list) {

        time_slot_flowView.removeAllViews();

        for (k=0; k < list.size(); k++) {


            View view = this.getLayoutInflater().inflate(R.layout.time_slot_item, null);
            final TextView textView = (TextView) view.findViewById(R.id.time_txt);
            // final ImageView cancel_img = (ImageView) view.findViewById(R.id.cancel_img);
            textView.setId(k);
            textView.setTag(k);
            time_slot_flowView.addView(view);
            textView.setText(list.get(k).get(GlobalConstant.start));
            if (list.get(k).get(GlobalConstant.status).equalsIgnoreCase("0")) {
                textView.setTextColor(getResources().getColor(R.color.LightGrey));
                textView.setBackgroundResource(R.drawable.dark_grey_border);

            } else {
                textView.setTextColor(getResources().getColor(R.color.Black));
                textView.setBackgroundResource(R.drawable.black_border);

            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list.get((Integer) view.getTag()).get(GlobalConstant.status).equalsIgnoreCase("0")) {


                    } else {


                        global.setTimeSlot(list.get((Integer) view.getTag()).get(GlobalConstant.start));
                        Log.e("time slot", global.getTimeSlot());
                        for (k=0; k < list.size(); k++) {
                            if((Integer) view.getTag()==k){
                                Log.e("view postion", "" + (Integer) view.getTag());
                                ((TextView) findViewById((Integer) view.getTag())).setTextColor(getResources().getColor(R.color.main_color));
                                ((TextView) findViewById((Integer) view.getTag())).setBackgroundResource(R.drawable.green_border);
                            }else{
                                if (list.get(k).get(GlobalConstant.status).equalsIgnoreCase("0")) {
                                    ((TextView) findViewById(k)).setTextColor(getResources().getColor(R.color.LightGrey));
                                    ((TextView) findViewById(k)).setBackgroundResource(R.drawable.dark_grey_border);

                                }
                                else {
                                    ((TextView) findViewById(k)).setTextColor(getResources().getColor(R.color.Black));
                                    ((TextView) findViewById(k)).setBackgroundResource(R.drawable.black_border);

                                }
                            }

                        }
                    }


                }
            });


        }
    }

}
