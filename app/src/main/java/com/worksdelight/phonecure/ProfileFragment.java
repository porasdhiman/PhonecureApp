package com.worksdelight.phonecure;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

/**
 * Created by worksdelight on 27/02/17.
 */

public class ProfileFragment extends Fragment {
    TextView user_name, user_phone, user_email, location_name_txtView;
    RelativeLayout change_password_layout, sign_out_layout,address_layout;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Global global;
    CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View v = inflater.inflate(R.layout.user_profile_layout, container, false);
        callbackManager = CallbackManager.Factory.create();
        sp = getActivity().getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        ed = sp.edit();
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();
        global = (Global) getActivity().getApplicationContext();
        init(v);
        return v;
    }

    public void init(View v) {
        user_name = (TextView) v.findViewById(R.id.user_name);
        user_phone = (TextView) v.findViewById(R.id.user_phone);
        user_email = (TextView) v.findViewById(R.id.user_email);
        location_name_txtView = (TextView) v.findViewById(R.id.location_name_txtView);

        change_password_layout = (RelativeLayout) v.findViewById(R.id.change_password_layout);
        sign_out_layout = (RelativeLayout) v.findViewById(R.id.sign_out_layout);
        address_layout=(RelativeLayout)v.findViewById(R.id.address_layout);
        user_name.setText(cap(sp.getString("user name", "")));


        location_name_txtView.setText(sp.getString("address", ""));


        user_phone.setText(sp.getString("phone", ""));
        user_email.setText(sp.getString("email", ""));
        address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddressActivity.class);
                startActivity(i);
            }
        });
        change_password_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
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
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                } else {
                    ed.clear();
                    ed.commit();
                    global.setDateList(null);
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
    public String cap(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}