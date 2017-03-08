package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by worksdelight on 01/03/17.
 */

public class RegisterActivity extends Activity implements View.OnClickListener{
    RelativeLayout facebook_layout,twitter_layout;
    TextView sign_in_btn,sign_up_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        setContentView(R.layout.register_layout);
        init();
    }
    public void init(){
        facebook_layout=(RelativeLayout)findViewById(R.id.facebook_layout);
        twitter_layout=(RelativeLayout)findViewById(R.id.twiter_layout);
        sign_in_btn=(TextView) findViewById(R.id.sign_in_btn);
        sign_up_btn=(TextView) findViewById(R.id.sign_up_btn);
        facebook_layout.setOnClickListener(this);
        twitter_layout.setOnClickListener(this);
        sign_in_btn.setOnClickListener(this);
        sign_up_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.facebook_layout:
                Intent f=new Intent(this,MainActivity.class);
                startActivity(f);
                finish();
                break;
            case R.id.twiter_layout:
                Intent t=new Intent(this,MainActivity.class);
                startActivity(t);
                finish();

                break;
            case R.id.sign_in_btn:

                Intent s=new Intent(this,LoginActivity.class);
                startActivity(s);
                finish();
                break;
            case R.id.sign_up_btn:
                Intent su=new Intent(this,MainActivity.class);
                startActivity(su);
                finish();

                break;
        }
    }
}
