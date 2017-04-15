package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    TextView user_name,user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.technicians_setting_layout);
        sp=getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed=sp.edit();
        global=(Global) getApplicationContext();
        user_name=(TextView)findViewById(R.id.user_name);
        user_email=(TextView)findViewById(R.id.user_email);
        user_name.setText(sp.getString("user name",""));
        user_email.setText(sp.getString("email",""));
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
