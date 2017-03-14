package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by worksdelight on 02/03/17.
 */

public class BookAppoinmentActivity extends Activity implements OnDateSelectedListener, OnMonthChangedListener {
    MaterialCalendarView mcv;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    TextView book_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_appoinment_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        book_btn=(TextView)findViewById(R.id.book_btn);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        String date = formattedDate.split("-")[0];
        String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[2];
        // Toast.makeText(this,date+"-"+month+"-"+year,Toast.LENGTH_SHORT).show();
        mcv = (MaterialCalendarView) findViewById(R.id.calendarView);
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);

        mcv.state().edit()

                .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date)))
                .setMaximumDate(CalendarDay.from(2023, 12, 31))

                .commit();
        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BookAppoinmentActivity.this, ShoppingcartActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Toast.makeText(BookAppoinmentActivity.this,getSelectedDatesString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }
    private String getSelectedDatesString() {

           CalendarDay date1 = mcv.getSelectedDate();

              return FORMATTER.format(date1.getDate());
    }
}
