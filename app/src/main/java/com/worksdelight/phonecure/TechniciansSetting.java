package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

/**
 * Created by worksdelight on 15/04/17.
 */

public class TechniciansSetting extends Activity {
    ImageView back;
    RelativeLayout sign_out_layout;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.technicians_setting_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp=getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed=sp.edit();
        global=(Global) getApplicationContext();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sign_out_layout=(RelativeLayout)findViewById(R.id.sign_out_layout);
        sign_out_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getString("type", "app").equalsIgnoreCase("app")) {
                    ed.clear();
                    ed.commit();
                    global.setDateList(null);
                    Intent i = new Intent(TechniciansSetting.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    ed.clear();
                    ed.commit();
                    global.setDateList(null);
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(TechniciansSetting.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
