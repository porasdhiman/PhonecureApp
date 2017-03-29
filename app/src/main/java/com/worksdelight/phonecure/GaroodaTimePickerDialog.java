package com.worksdelight.phonecure;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.Calendar;


public class GaroodaTimePickerDialog extends TimePickerDialog {

    private int minHour = -1;
    private int minMinute = -1;

    private int maxHour = 25;
    private int maxMinute = 61;

    private int currentHour = 0;
    private int currentMinute = 0;

    private Calendar calendar = Calendar.getInstance();
    //private DateFormat dateFormat;

    public GaroodaTimePickerDialog(Context context, OnTimeSetListener callBack, int hour, int minute,
                                   boolean is24HourView) {
        super(context, callBack, hour, minute, is24HourView);

        currentHour = hour;
        currentMinute = minute;
        //dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            Class<?> superclass = getClass().getSuperclass();
            Field mTimePickerField = superclass.getDeclaredField("mTimePicker");
            mTimePickerField.setAccessible(true);
            TimePicker mTimePicker = (TimePicker) mTimePickerField.get(this);
            mTimePicker.setOnTimeChangedListener(this);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }







    public void setMin(int hour, int minute) {
        minHour = hour;
        minMinute = minute;

        Calendar min = Calendar.getInstance();
        Calendar existing = Calendar.getInstance();

        min.set(Calendar.HOUR_OF_DAY, minHour);
        min.set(Calendar.MINUTE, minMinute);

        existing.set(Calendar.HOUR_OF_DAY, currentHour);
        existing.set(Calendar.MINUTE, currentMinute);

        if (existing.before(min)) {
            currentHour = minHour;
            currentMinute = minMinute;
            updateTime(currentHour, currentMinute);
        }
    }

    public void setMax(int hour, int minute) {
        maxHour = hour;
        maxMinute = minute;

        Calendar max = Calendar.getInstance();
        Calendar existing = Calendar.getInstance();

        max.set(Calendar.HOUR_OF_DAY, maxHour);
        max.set(Calendar.MINUTE, maxMinute);

        existing.set(Calendar.HOUR_OF_DAY, currentHour);
        existing.set(Calendar.MINUTE, currentMinute);

        if (existing.after(max)) {
            currentHour = maxHour;
            currentMinute = maxMinute;
            updateTime(currentHour, currentMinute);
        }

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        boolean validTime = true;
        if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)){
            validTime = false;
        }

        if (hourOfDay  > maxHour || (hourOfDay == maxHour && minute > maxMinute)){
            validTime = false;
        }

        if (validTime) {
            currentHour = hourOfDay;
            currentMinute = minute;
        }

        updateTime(currentHour, currentMinute);
        updateDialogTitle(view, currentHour, currentMinute);
    }

    private void updateDialogTitle(TimePicker timePicker, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        //String title = dateFormat.format(calendar.getTime());
        //setTitle(title);
    }
}
