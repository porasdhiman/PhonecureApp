package com.worksdelight.phonecure;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by worksdelight on 02/03/17.
 */

public class TechniciansDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        setContentView(R.layout.technicians_detail_layout_second);
    }
}
