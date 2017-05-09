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
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.worksdelight.phonecure.R.id.calendarView;

/**
 * Created by worksdelight on 02/03/17.
 */

public class BookAppoinmentActivity extends Activity implements OnDateSelectedListener, OnMonthChangedListener, TimePickerDialog.OnTimeSetListener {
    MaterialCalendarView mcv;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    TextView book_btn, technicians_name;
    List<CalendarDay> calenderlist = new ArrayList<CalendarDay>();
    CalendarDay selectDate;
    Dialog PickerDialog;
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
    TextView time_txtView;
    String sendDate = "";
    CircleImageView user_img;
    String pos;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    String minTimehour, minTimeminute, maxTimehour, maxTimeminute;
    TimePickerDialog timePickerDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;

    int hour, minutes;
    ImageView back;
    NumberPicker minutePicker;
    private int TIME_PICKER_INTERVAL = 15;
    ImageView pickUp_img, dropoff_img;
    int p = 0;
    TextView distance_shop;
    int monthsDayes[] = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER};
    String weekDayes[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    ArrayList<String> availList = new ArrayList<>();
    String output = "";
    int item1, item2;
    ArrayList<String> hours = new ArrayList<>();
    ArrayList<String> minute = new ArrayList<>();
    Calendar mCalendarOpeningTime, mCalendarClosingTime, finalTime;
    String openTimeMode, closeTimeMode;
    String openTime = "", popenTime = "", closeTime = "", pclosetime = "";
    LinearLayout dropoff_layout, pickup_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        distance_shop = (TextView) findViewById(R.id.distance_shop);
        technicians_name = (TextView) findViewById(R.id.user_name);
        time_layout = (RelativeLayout) findViewById(R.id.time_layout);
        time_txtView = (TextView) findViewById(R.id.time_txtView);
        pos = getIntent().getExtras().getString("pos");
        technicians_name.setText(cap(global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.name)));
        distance_shop.setText(global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.distance));
        user_img = (CircleImageView) findViewById(R.id.user_img);
        String url = GlobalConstant.TECHNICIANS_IMAGE_URL + global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.image);
        if (url != null && !url.equalsIgnoreCase("null")
                && !url.equalsIgnoreCase("")) {
            imageLoader.displayImage(url, user_img, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);

                        }
                    });
        } else {
            user_img.setImageResource(R.drawable.user_back);
        }
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        String date = formattedDate.split("-")[0];
        final String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[2];

        // Toast.makeText(this,date+"-"+month+"-"+year,Toast.LENGTH_SHORT).show();
        mcv = (MaterialCalendarView) findViewById(calendarView);
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);

        mcv.state().edit()

                .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date)))
                .setMaximumDate(CalendarDay.from(Integer.parseInt(year), 12, 31))

                .commit();

        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendDate.equalsIgnoreCase("")) {
                    Toast.makeText(BookAppoinmentActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                } else if (time_txtView.getText().length() == 0) {
                    Toast.makeText(BookAppoinmentActivity.this, "Please select time", Toast.LENGTH_SHORT).show();
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

        time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
                time_txtView.setText("");
                /*SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");



                Date convertedDate = null;
                try {
                    convertedDate = inputFormat.parse(global.getSendDate());
                    String s = formatter.format(convertedDate);
                    dialogWindow1(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                // dialogWindow1();

            }
        });
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

        //  Log.e("monday date",getMondaysOfJanuary().toString());
    }

    public String dayName(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        String s = "";
        Date convertedDate = null;
        try {
            convertedDate = inputFormat.parse(date);
            s = formatter.format(convertedDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
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


        if (isValidFormat(getSelectedDatesString())) {
            // Toast.makeText(BookAppoinmentActivity.this, formatdate(getSelectedDatesString()), Toast.LENGTH_SHORT).show();
            sendDate = getSelectedDatesString();
            global.setSendDate(formatdate(sendDate));
            setOpeningAndClosingTimes(dayName(global.getSendDate()));
        } else {
            // Toast.makeText(BookAppoinmentActivity.this, formatdate2(getSelectedDatesString()), Toast.LENGTH_SHORT).show();
            sendDate = getSelectedDatesString();
            global.setSendDate(formatdate2(sendDate));
            setOpeningAndClosingTimes(dayName(global.getSendDate()));

        }


    }

    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    public boolean isValidFormat(String value) {
        Date date = null;
        boolean isTrue = false;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            date = sdf.parse(value);

            if (value.equals(sdf.format(date))) {
                isTrue = true;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return isTrue;
    }

    public String formatdate2(String fdate) {
        String datetime = null;

        DateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");

        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }

    public String formatdate(String fdate) {
        String datetime = null;

        DateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy");

        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        } catch (ParseException e) {

        }
        return datetime;


    }

    private String getSelectedDatesString() {

        CalendarDay date1 = mcv.getSelectedDate();

        return FORMATTER.format(date1.getDate());
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String fDate = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);

        if (isTimeWith_in_Interval(fDate, closeTime, openTime)) {

            time_txtView.setText(getTime(hourOfDay, minute));
            global.setSendTime(time_txtView.getText().toString());
        } else {
            Toast.makeText(BookAppoinmentActivity.this, "Technician Available between " + popenTime + " to " + pclosetime, Toast.LENGTH_SHORT).show();

        }


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

    private void setOpeningAndClosingTimes(String dayName) {

        JSONObject obj = null;
        try {
            obj = global.getCartData().getJSONObject(Integer.parseInt(pos));
            JSONArray avail_arr = obj.getJSONArray(GlobalConstant.availability);
            for (int i = 0; i < avail_arr.length(); i++) {
                JSONObject sun_obj = avail_arr.getJSONObject(i);
                if (dayName.equalsIgnoreCase(sun_obj.getString(GlobalConstant.day))) {
                    openTime = sun_obj.getString(GlobalConstant.opening_time);
                    closeTime = sun_obj.getString(GlobalConstant.closing_time);
                    popenTime = openTime;
                    pclosetime = closeTime;
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (openTime.contains("AM") || openTime.contains("PM")) {
            openTime = convertTo24Hour(openTime);
            minTimehour = openTime.split(":")[0];
            minTimeminute = openTime.split(":")[1];
            mCalendarOpeningTime = Calendar.getInstance();
            mCalendarOpeningTime.set(Calendar.HOUR, Integer.parseInt(minTimehour));
            mCalendarOpeningTime.set(Calendar.MINUTE, Integer.parseInt(minTimeminute));
        } else {
            minTimehour = openTime.split(":")[0];
            minTimeminute = openTime.split(":")[1];
            mCalendarOpeningTime = Calendar.getInstance();
            mCalendarOpeningTime.set(Calendar.HOUR, Integer.parseInt(minTimehour));
            mCalendarOpeningTime.set(Calendar.MINUTE, Integer.parseInt(minTimeminute));
        }

        if (closeTime.contains("AM") || closeTime.contains("PM")) {
            closeTime = convertTo24Hour(closeTime);
            maxTimehour = closeTime.split(":")[0];
            maxTimeminute = closeTime.split(":")[1];
            mCalendarClosingTime = Calendar.getInstance();
            mCalendarClosingTime.set(Calendar.HOUR, Integer.parseInt(maxTimehour));
            mCalendarClosingTime.set(Calendar.MINUTE, Integer.parseInt(maxTimeminute));
        } else {
            maxTimehour = closeTime.split(":")[0];
            maxTimeminute = closeTime.split(":")[1];
            mCalendarClosingTime = Calendar.getInstance();
            mCalendarClosingTime.set(Calendar.HOUR, Integer.parseInt(maxTimehour));
            mCalendarClosingTime.set(Calendar.MINUTE, Integer.parseInt(maxTimeminute));
        }

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

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(tme);
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

    public static String convertTo24Hour(String Time) {
        DateFormat f1 = new SimpleDateFormat("hh:mm a"); //11:00 pm
        Date d = null;
        try {
            d = f1.parse(Time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DateFormat f2 = new SimpleDateFormat("HH:mm");
        String x = f2.format(d); // "23:00"

        return x;
    }

    public void dialogWindow1() {
        PickerDialog = new Dialog(this);
        PickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        PickerDialog.setContentView(R.layout.wheel_dialog);
       /* int value = 0;
        for (int i=0;i<availList.size();i++){
            if(availList.get(i).equalsIgnoreCase(dayName)){
                value=i;
            }
        }
        JSONObject obj = null;
        try {
            obj = global.getCartData().getJSONObject(Integer.parseInt(pos));
            JSONArray avail_arr = obj.getJSONArray(GlobalConstant.availability);

                JSONObject sun_obj = avail_arr.getJSONObject(value);
                String openTime=convertTo24Hour(sun_obj.getString(GlobalConstant.opening_time));
            String closeTime=convertTo24Hour(sun_obj.getString(GlobalConstant.closing_time));

                int h = Integer.parseInt(openTime.split(":")[0]);
            int m = Integer.parseInt(openTime.split(":")[1]);
            int h1 = Integer.parseInt(closeTime.split(":")[0]);
            int m1 = Integer.parseInt(closeTime.split(":")[1]);
            for (int k=0;k<h;k++)

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        TextView cancel_txtView = (TextView) PickerDialog.findViewById(R.id.cancel_txtView);
        TextView done_txtView = (TextView) PickerDialog.findViewById(R.id.done_txtView);
        final LoopView loopView = (LoopView) PickerDialog.findViewById(R.id.loop_view);
        final LoopView loopView1 = (LoopView) PickerDialog.findViewById(R.id.loop_view1);

        loopView.setCanLoop(false);

        loopView.setTextSize(16);

        hours = getListHour();

        loopView.setDataList(hours);


        loopView1.setCanLoop(false);

        loopView1.setTextSize(16);
        minute = getListMinute();
        loopView1.setDataList(minute);
        Log.e("value", hours.toString() + " " + minute.toString());
        loopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                item1 = item;
            }
        });
        loopView1.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                item2 = item;
            }
        });
        done_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerDialog.dismiss();
                time_txtView.setText(getTime(Integer.parseInt(hours.get(item1).split(":")[1]), Integer.parseInt(minute.get(item2).split(":")[0])));
                global.setSendTime(time_txtView.getText().toString());

            }
        });
        cancel_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerDialog.dismiss();
            }
        });
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        PickerDialog.show();
    }

    public ArrayList<String> getListHour() {
        ArrayList<String> list1 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            int l = i + 1;
            list1.add("hour:" + l);
        }
        return list1;
    }

    public ArrayList<String> getListMinute() {
        ArrayList<String> list1 = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            int l = i + 1;
            list1.add(l + ":min");
        }


        return list1;
    }
}
