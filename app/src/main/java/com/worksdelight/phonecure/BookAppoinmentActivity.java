package com.worksdelight.phonecure;

import android.annotation.SuppressLint;
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
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.lang.reflect.Field;
import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    Dialog dialog2;
    private int mYear, mMonth, mDay, mHour, mMinute;

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
    List<String> list;
    RelativeLayout time_layout;
    TextView time_txtView;
    String sendDate = "";
    CircleImageView user_img;
    String pos;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;
    String minTimehour, minTimeminute, maxTimehour, maxTimeminute;
    TimePickerDialog timePickerDialog;
    int hour, minutes;
    ImageView back;
    NumberPicker minutePicker;
    private int TIME_PICKER_INTERVAL = 15;

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
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        book_btn = (TextView) findViewById(R.id.book_btn);
        technicians_name = (TextView) findViewById(R.id.user_name);
        time_layout = (RelativeLayout) findViewById(R.id.time_layout);
        time_txtView = (TextView) findViewById(R.id.time_txtView);
        pos = getIntent().getExtras().getString("pos");
        technicians_name.setText(global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.name));
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
        String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[2];
        // Toast.makeText(this,date+"-"+month+"-"+year,Toast.LENGTH_SHORT).show();
        mcv = (MaterialCalendarView) findViewById(calendarView);
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);

        mcv.state().edit()

                .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date)))
                .setMaximumDate(CalendarDay.from(2023, 12, 31))

                .commit();
        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendDate.equalsIgnoreCase("")) {
                    Toast.makeText(BookAppoinmentActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                } else if (time_txtView.getText().length() == 0) {
                    Toast.makeText(BookAppoinmentActivity.this, "Please select time press home button", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(BookAppoinmentActivity.this, ShoppingcartActivity.class);
                    i.putExtra("selected_id", getIntent().getExtras().getString("selected_id"));
                    i.putExtra("pos", String.valueOf(pos));
                    startActivity(i);
                }

            }
        });
        minTimehour = global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.opening_time).split(":")[0];
        minTimeminute = global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.opening_time).split(":")[1];
        maxTimehour = global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.closing_time).split(":")[0];
        maxTimeminute = global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.closing_time).split(":")[1];

        list = new ArrayList<String>(Arrays.asList(global.getDateList().get(Integer.parseInt(pos)).get(GlobalConstant.off_days).split(",")));
        Log.e("date list", String.valueOf(list));
        for (int i = 0; i < list.size(); i++) {
          /*  String date1=list.get(i).split("-")[2];
            String year1=list.get(i).split("-")[0];
            String month1=list.get(i).split("-")[1];*/
            Log.e("loop value", String.valueOf(i));
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

        mcv.addDecorators(new EventDecorator(calenderlist));

        time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                timePicker();
            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        boolean value = false;

        if (list.contains(formatdate2(getSelectedDatesString()))) {
            Toast.makeText(BookAppoinmentActivity.this, "this date technician not available", Toast.LENGTH_SHORT).show();

        } else {
            //Toast.makeText(BookAppoinmentActivity.this, formatdate2(getSelectedDatesString()), Toast.LENGTH_SHORT).show();
            sendDate = getSelectedDatesString();
            global.setSendDate(formatdate2(sendDate));
        }


    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    public String formatdate2(String fdate) {
        String datetime = null;
        DateFormat inputFormat = new SimpleDateFormat("MMM dd,yyyy");

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

        time_txtView.setText(getTime(hourOfDay, minute));
        global.setSendTime(time_txtView.getText().toString());
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
        formatter = new SimpleDateFormat("h:mm a");
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

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.picker_timer);
        TimePicker timePicker = (TimePicker) dialog2.findViewById(R.id.pickerView);

        timePicker.setIs24HourView(true);
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        timePicker.setCurrentHour(mHour);
        timePicker.setCurrentMinute(mMinute);

        setTimePickerInterval(timePicker);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                time_txtView.setText(i + ":" + i1);
                dialog2.dismiss();
            }
        });
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }

    @SuppressLint("NewApi")
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");

            Field field = classForid.getField("minute");
            minutePicker = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));

            minutePicker.setMinValue(Integer.parseInt(minTimehour));
            minutePicker.setMaxValue(Integer.parseInt(maxTimehour));
            ArrayList<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            //  for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
            //      displayedValues.add(String.format("%02d", i));
            //  }
            minutePicker.setDisplayedValues(displayedValues
                    .toArray(new String[0]));
            minutePicker.setWrapSelectorWheel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
