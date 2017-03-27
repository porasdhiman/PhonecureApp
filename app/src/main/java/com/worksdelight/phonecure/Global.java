package com.worksdelight.phonecure;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.brickred.socialauth.android.SocialAuthAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by worksdelight on 10/03/17.
 */

public class Global extends Application {

    public ArrayList<HashMap<String, String>> getSearchList() {
        return searchList;
    }

    public void setSearchList(ArrayList<HashMap<String, String>> searchList) {
        this.searchList = searchList;
    }

    ArrayList<HashMap<String,String>> searchList=new ArrayList<>();

    public ArrayList<HashMap<String, String>> getDateList() {
        return dateList;
    }

    public void setDateList(ArrayList<HashMap<String, String>> dateList) {
        this.dateList = dateList;
    }

    ArrayList<HashMap<String,String>> dateList=new ArrayList<>();

    public ArrayList<HashMap<String, String>> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<HashMap<String, String>> serviceList) {
        this.serviceList = serviceList;
    }

    ArrayList<HashMap<String,String>> serviceList=new ArrayList<>();

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    String deviceName;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    String deviceToken;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    String lat;

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    String Long;

    public SocialAuthAdapter getSocialAuthAdpater() {
        return socialAuthAdpater;
    }

    public void setSocialAuthAdpater(SocialAuthAdapter socialAuthAdpater) {
        this.socialAuthAdpater = socialAuthAdpater;
    }

    SocialAuthAdapter socialAuthAdpater;
    @Override
    public void onCreate() {
        super.onCreate();


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())

                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000) // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .enableLogging() // Not necessary in common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
