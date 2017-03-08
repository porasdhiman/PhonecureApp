package com.worksdelight.phonecure;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by worksdelight on 06/03/17.
 */

public class AppointmentActivity extends Activity {
    ImageView back_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_information);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }
    public void init(){
        back_img=(ImageView)findViewById(R.id.back_img);
        back_img.setColorFilter(back_img.getContext().getResources().getColor(R.color.main_color), PorterDuff.Mode.SRC_ATOP);
    }
}
