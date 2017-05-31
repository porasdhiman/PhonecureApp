package com.worksdelight.phonecure;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

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

    public ArrayList<HashMap<String, String>> getNewListing() {
        return newListing;
    }

    public void setNewListing(ArrayList<HashMap<String, String>> newListing) {
        this.newListing = newListing;
    }

    ArrayList<HashMap<String,String>> newListing=new ArrayList<>();

    public ArrayList<HashMap<String, String>> getPrcieListing() {
        return prcieListing;
    }

    public void setPrcieListing(ArrayList<HashMap<String, String>> prcieListing) {
        this.prcieListing = prcieListing;
    }

    ArrayList<HashMap<String,String>> prcieListing=new ArrayList<>();

    public SocialAuthAdapter getSocialAuthAdpater() {
        return socialAuthAdpater;
    }

    public void setSocialAuthAdpater(SocialAuthAdapter socialAuthAdpater) {
        this.socialAuthAdpater = socialAuthAdpater;
    }

    SocialAuthAdapter socialAuthAdpater;

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    String sendDate;
    String sendTime;

    public String getLogoutValue() {
        return logoutValue;
    }

    public void setLogoutValue(String logoutValue) {
        this.logoutValue = logoutValue;
    }

    public ArrayList<HashMap<String, String>> getOtherDeviceList() {
        return otherDeviceList;
    }

    public void setOtherDeviceList(ArrayList<HashMap<String, String>> otherDeviceList) {
        this.otherDeviceList = otherDeviceList;
    }

    ArrayList<HashMap<String,String>> otherDeviceList=new ArrayList<>();
    String logoutValue;

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    int  postion;

    public ArrayList<HashMap<String, String>> getAllDeviceList() {
        return allDeviceList;
    }

    public void setAllDeviceList(ArrayList<HashMap<String, String>> allDeviceList) {
        this.allDeviceList = allDeviceList;
    }

    ArrayList<HashMap<String,String>> allDeviceList=new ArrayList<>();

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    String colorId,subCatId;

    public JSONArray getAar() {
        return aar;
    }

    public void setAar(JSONArray aar) {
        this.aar = aar;
    }

    JSONArray aar;


    public JSONArray getCompletedaar() {
        return completedaar;
    }

    public void setCompletedaar(JSONArray completedaar) {
        this.completedaar = completedaar;
    }

    public JSONArray getPendingaar() {
        return pendingaar;
    }

    public void setPendingaar(JSONArray pendingaar) {
        this.pendingaar = pendingaar;
    }

    JSONArray completedaar;
    JSONArray pendingaar;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    String deviceId;

    public JSONArray getCartData() {
        return CartData;
    }

    public void setCartData(JSONArray cartData) {
        CartData = cartData;
    }

    JSONArray CartData;

    public JSONArray getOtherData() {
        return OtherData;
    }

    public void setOtherData(JSONArray otherData) {
        OtherData = otherData;
    }

    String dropOff="0";

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    public String getDropOff() {
        return dropOff;
    }

    public void setDropOff(String dropOff) {
        this.dropOff = dropOff;
    }

    String pickUp="0";

    JSONArray OtherData;

    public String getCurencySymbol() {
        return curencySymbol;
    }

    public void setCurencySymbol(String curencySymbol) {
        this.curencySymbol = curencySymbol;
    }

    String curencySymbol;

    public int getRegisterTechType() {
        return registerTechType;
    }

    public void setRegisterTechType(int registerTechType) {
        this.registerTechType = registerTechType;
    }

    int registerTechType=1;

    public String getPushNotificationType() {
        return pushNotificationType;
    }

    public void setPushNotificationType(String pushNotificationType) {
        this.pushNotificationType = pushNotificationType;
    }

    public String getPushValue() {
        return pushValue;
    }

    public void setPushValue(String pushValue) {
        this.pushValue = pushValue;
    }

    String pushNotificationType="",pushValue="0";

    public String getColor_id() {
        return color_id;
    }

    public void setColor_id(String color_id) {
        this.color_id = color_id;
    }

    String color_id;

    public String getEstimated_travel_time() {
        return estimated_travel_time;
    }

    public void setEstimated_travel_time(String estimated_travel_time) {
        this.estimated_travel_time = estimated_travel_time;
    }

    String estimated_travel_time;

    public ArrayList<JSONObject> getObjArray() {
        return objArray;
    }

    public void setObjArray(ArrayList<JSONObject> objArray) {
        this.objArray = objArray;
    }

    ArrayList<JSONObject> objArray=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());


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
