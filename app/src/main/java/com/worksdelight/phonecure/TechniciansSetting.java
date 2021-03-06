package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

/**
 * Created by worksdelight on 15/04/17.
 */

public class TechniciansSetting extends Activity {
    ImageView back;
    RelativeLayout sign_out_layout, change_password_layout, share_layout;
    SharedPreferences sp,spNotify;
    SharedPreferences.Editor ed,edNotify;
    Global global;
    Dialog dialog2;
    CallbackManager callbackManager;
    LinearLayout main_layout;
    ImageView notify_on_off;
    boolean isEnabled=false;
    AlertDialog builder;
    RelativeLayout notification_layout,deactivate_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.technicians_setting_layout);
        callbackManager = CallbackManager.Factory.create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        notification_layout=(RelativeLayout)findViewById(R.id.notification_layout) ;
        notification_layout.setVisibility(View.GONE);
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        spNotify=getSharedPreferences("NotifyType",Context.MODE_PRIVATE);
        edNotify=spNotify.edit();
        isEnabled=spNotify.getBoolean("isEnabled",false);
        notify_on_off=(ImageView)findViewById(R.id.notify_on_off);
        if(isEnabled==false){
            notify_on_off.setImageResource(R.drawable.toogle2);
        }else{
            notify_on_off.setImageResource(R.drawable.toggle);
        }
        global = (Global) getApplicationContext();

        notify_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnabled==false){
                    builder = new AlertDialog.Builder(TechniciansSetting.this).setMessage("Do You Want To Disable Notification?")
                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    edNotify.putBoolean("isEnabled",true);
                                    isEnabled=true;
                                    edNotify.commit();
                                    notify_on_off.setImageResource(R.drawable.toggle);
                                    builder.dismiss();
                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    builder.dismiss();
                                }
                            }).show();




                }else{
                    builder = new AlertDialog.Builder(TechniciansSetting.this).setMessage("Do You Want To Enable Notification?")
                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    isEnabled=false;
                                    edNotify.putBoolean("isEnabled",false);
                                    edNotify.commit();
                                    notify_on_off.setImageResource(R.drawable.toogle2);
                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    builder.dismiss();
                                }
                            }).show();


                }
            }
        });
        main_layout=(LinearLayout) findViewById(R.id.main_layout);
        Fonts.overrideFonts(this, main_layout);
        back = (ImageView) findViewById(R.id.back_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sign_out_layout = (RelativeLayout) findViewById(R.id.sign_out_layout);
        change_password_layout = (RelativeLayout) findViewById(R.id.change_password_layout);
        share_layout = (RelativeLayout) findViewById(R.id.share_layout);
        deactivate_layout = (RelativeLayout) findViewById(R.id.deactivate_layout);
        deactivate_layout.setVisibility(View.GONE);
        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.share_text)+" https://play.google.com/store/apps/details?id=com.worksdelight.phonecure&hl=en");
                startActivity(shareIntent);
            }
        });
        change_password_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TechniciansSetting.this, ChangePasswordActivity.class);
                startActivity(i);
            }
        });
        sign_out_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (sp.getString("type", "app").equalsIgnoreCase("app")) {
                    ed.clear();
                    ed.commit();
                    global.setDateList(null);
                    Intent i = new Intent(TechniciansSetting.this, LoginActivity.class);
                    if (Build.VERSION.SDK_INT >= 11) {
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else {
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                    startActivity(i);
                } else {
                    ed.clear();
                    ed.commit();
                    global.setDateList(null);
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(TechniciansSetting.this, LoginActivity.class);
                    if (Build.VERSION.SDK_INT >= 11) {
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else {
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                    startActivity(i);
                }



            }
        });
        deactivate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWindow();
            }
        });
    }

    //---------------------------Progrees Dialog-----------------------
    public void dialogWindow() {
        dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.deactivate_dialog_layout);
        TextView yes_txtView = (TextView) dialog2.findViewById(R.id.yes_txtView);
        TextView no_txtView = (TextView) dialog2.findViewById(R.id.no_txtView);
        TextView deactivat_txt = (TextView) dialog2.findViewById(R.id.deactivat_txt);

        String title = deactivat_txt.getText().toString();
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1 = new SpannableString(title);
        str1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 32, 42, str1.length() - 1);
        builder.append(str1);
        deactivat_txt.setText(builder, TextView.BufferType.SPANNABLE);

        yes_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
            /*    Intent i = new Intent(TechniciansSetting.this, DeactivationActivity.class);
                startActivity(i);*/
            }
        });
        no_txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
            }
        });
        // progress_dialog=ProgressDialog.show(LoginActivity.this,"","Loading...");
        dialog2.show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
