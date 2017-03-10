package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by worksdelight on 01/03/17.
 */

public class LoginActivity extends Activity implements OnClickListener {
    RelativeLayout facebook_layout,twitter_layout;
    TextView sign_in_btn,sign_up_btn,sign_up_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        setContentView(R.layout.login_layout);
        init();
    }
    public void init(){
        facebook_layout=(RelativeLayout)findViewById(R.id.facebook_layout);
        twitter_layout=(RelativeLayout)findViewById(R.id.twiter_layout);
        sign_in_btn=(TextView) findViewById(R.id.sign_in_btn);
        sign_up_btn=(TextView) findViewById(R.id.sign_up_btn);
        sign_up_user=(TextView) findViewById(R.id.sign_up_user);
        facebook_layout.setOnClickListener(this);
        twitter_layout.setOnClickListener(this);
        sign_in_btn.setOnClickListener(this);
        sign_up_btn.setOnClickListener(this);
        sign_up_user.setOnClickListener(this);
        //-----------------Permission value----------------------
        String locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        String coarselocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        String cameraPermission = android.Manifest.permission.CAMERA;
        String wstorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String rstorage = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        String readPhoneState = android.Manifest.permission.READ_PHONE_STATE;
        String networkPermission = android.Manifest.permission.ACCESS_NETWORK_STATE;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasFinePermission = LoginActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarsePermission = LoginActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hascameraPermission = LoginActivity.this.checkSelfPermission(android.Manifest.permission.CAMERA);
            int haswstorage = LoginActivity.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasrstorage = LoginActivity.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasreadstorage = LoginActivity.this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            int hasaccessnetworkState = LoginActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            List<String> permissions = new ArrayList<String>();
            if (hasFinePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(locationPermission);
            }
            if (hasCoarsePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(coarselocationPermission);
            }
            if (hascameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(cameraPermission);
            }
            if (haswstorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(wstorage);
            }
            if (hasrstorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(rstorage);
            }
            if (hasaccessnetworkState != PackageManager.PERMISSION_GRANTED) {
                permissions.add(networkPermission);
            }
            if (hasreadstorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readPhoneState);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, 0);

            } else {



                // We already have permission, so handle as norma
                //Toast.makeText(MainActivity.this,gps.getLatitude()+""+   gps.getLongitude(),Toast.LENGTH_SHORT).show();
            }
        } else {



            // Toast.makeText(MainActivity.this,gps.getLatitude()+""+   gps.getLongitude(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED&&
                        perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted


                    // Toast.makeText(MainActivity.this,gps.getLatitude()+""+   gps.getLongitude(),Toast.LENGTH_SHORT).show();

                } else {
                    // Permission Denied
                    Toast.makeText(LoginActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.facebook_layout:
                Intent f=new Intent(this,WalkThroughtOneActivity.class);
                startActivity(f);
                finish();
                break;
            case R.id.twiter_layout:
                Intent t=new Intent(this,WalkThroughtOneActivity.class);
                startActivity(t);
                finish();

                break;
            case R.id.sign_in_btn:

                Intent s=new Intent(this,WalkThroughtOneActivity.class);
                startActivity(s);
                finish();
                break;
            case R.id.sign_up_btn:
                Intent su=new Intent(this,RegisterActivity.class);
                startActivity(su);
                finish();

                break;
            case R.id.sign_up_user:
                Intent us=new Intent(this,RegisterActivity.class);
                startActivity(us);
                finish();

                break;
        }
    }
}
