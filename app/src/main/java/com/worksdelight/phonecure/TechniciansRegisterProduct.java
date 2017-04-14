package com.worksdelight.phonecure;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by worksdelight on 14/04/17.
 */

public class TechniciansRegisterProduct extends Activity {
    ImageView back;
    TextView service_txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technicians_register_product_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        init();
    }
    public void init(){
        back=(ImageView)findViewById(R.id.back);
        service_txtView=(TextView) findViewById(R.id.service_txtView);
    }
}
