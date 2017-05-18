package com.worksdelight.phonecure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by worksdelight on 23/03/17.
 */

public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // --------------code for gcm
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "166286978746";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regId;
    Context context;
    String TAG = "Device Token Log";
    Global global;
    AlertDialog alert;
//--------------Location lat long variable---------

    private Location mLastLocation, myLocation;
    LocationManager mLocationManager;
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */

    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        sp = getSharedPreferences(GlobalConstant.PREF_NAME, Context.MODE_PRIVATE);
        global = (Global) getApplicationContext();
        buildGoogleApiClient();
        //----------Check play service---------------------------
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);
            Log.e(TAG, " Google Play Services APK found.");
            if (regId.isEmpty()) {

                registerInBackground();
            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }
        Locale current = getResources().getConfiguration().locale;
        global.setCurencySymbol(Currency.getInstance(current).getSymbol(current));
        Log.e("locale", Currency.getInstance(current).getSymbol(current));
     /*   global.setLat("30.701990");
        global.setLong("76.682625");*/
        //-----------------Permission value----------------------
        String locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        String coarselocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        String cameraPermission = android.Manifest.permission.CAMERA;
        String wstorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String rstorage = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        String readPhoneState = android.Manifest.permission.READ_PHONE_STATE;
        String networkPermission = android.Manifest.permission.ACCESS_NETWORK_STATE;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasFinePermission = SplashActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarsePermission = SplashActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int hascameraPermission = SplashActivity.this.checkSelfPermission(android.Manifest.permission.CAMERA);
            int haswstorage = SplashActivity.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasrstorage = SplashActivity.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasreadstorage = SplashActivity.this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            int hasaccessnetworkState = SplashActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
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


                if (checkGPSStatus()) {
                  //  if (global.getPushValue().equalsIgnoreCase("0")) {
                        Handler splashhandler = new Handler();
                        splashhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                if (CommonUtils.UserID(SplashActivity.this).equalsIgnoreCase("")) {

                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                    finish();
                                } else {
                                    if (sp.getString(GlobalConstant.type, "technician").equalsIgnoreCase("user")) {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                        finish();
                                    } else {
                                        Intent intent = new Intent(SplashActivity.this, TechniciansHomeActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                        finish();
                                    }
                                }
                            }
                        }, 3000);

                        global.setDeviceName(getDeviceName());
                        Log.e("device info", getDeviceName() + " , " + getAndroidVersion() + " " + getDeviceId());

                    /*} else {
                        Handler splashhandler = new Handler();
                        splashhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (sp.getString(GlobalConstant.type, "technician").equalsIgnoreCase("user")) {
                                    if(global.getPushNotificationType().equalsIgnoreCase("booking_completted")||global.getPushNotificationType().equalsIgnoreCase("booking_cancelled")){
                                        Intent intent = new Intent(SplashActivity.this, HistoryActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                        finish();
                                    }
                                } else {
                                    Intent intent = new Intent(SplashActivity.this, TechniciansHomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                    finish();
                                }





                            }
                        }, 3000);

                        global.setDeviceName(getDeviceName());
                        Log.e("device info", getDeviceName() + " , " + getAndroidVersion() + " " + getDeviceId());
                    }*/
                    // We already have permission, so handle as norma
                }  //Toast.makeText(MainActivity.this,gps.getLatitude()+""+   gps.getLongitude(),Toast.LENGTH_SHORT).show();
            }
        } else {

            if (checkGPSStatus()) {
                global.setDeviceName(getDeviceName());
                Log.e("device info", getDeviceName() + " , " + getAndroidVersion() + " " + getDeviceId());
                Handler splashhandler = new Handler();
                splashhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (CommonUtils.UserID(SplashActivity.this).equalsIgnoreCase("")) {

                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                            finish();
                        } else {
                            if (sp.getString(GlobalConstant.type, "technician").equalsIgnoreCase("user")) {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, TechniciansHomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                finish();
                            }
                        }
                    }
                }, 3000);
            }

        }
        // Toast.makeText(MainActivity.this,gps.getLatitude()+""+   gps.getLongitude(),Toast.LENGTH_SHORT).show();


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
                        perms.get(android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                    if (checkGPSStatus()) {
                        global.setDeviceName(getDeviceName());
                        Log.e("device info", getDeviceName() + " , " + getAndroidVersion() + " " + getDeviceId());
                        Handler splashhandler = new Handler();
                        splashhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                if (CommonUtils.UserID(SplashActivity.this).equalsIgnoreCase("")) {

                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                    finish();
                                } else {
                                    if (sp.getString(GlobalConstant.type, "technician").equalsIgnoreCase("user")) {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                        finish();
                                    } else {
                                        Intent intent = new Intent(SplashActivity.this, TechniciansHomeActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                        finish();
                                    }
                                }
                            }
                        }, 3000);
                        // Toast.makeText(MainActivity.this,gps.getLatitude()+""+   gps.getLongitude(),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    finish();
                    Toast.makeText(SplashActivity.this, "Internet not working", Toast.LENGTH_SHORT);
                }

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void hashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.worksdelight.phonecure", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //textInstructionsOrLink = (TextView)findViewById(R.id.textstring);
                //textInstructionsOrLink.setText(sign);
                Log.e("Hash Key", sign);
                Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("nope", "nope");
        } catch (NoSuchAlgorithmException e) {
        }
    }

    //------------------------------ Device Token-------------------
    private String getRegistrationId(Context context) {

        // TODO Auto-generated method stub

        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        Log.e("Tag", registrationId);

        return registrationId;

    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                // TODO Auto-generated method stub

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    global.setDeviceToken(regId);
                    msg = "Device registered, registration ID=" + regId;

                    Log.e("getdeviceid", global.getDeviceToken());

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);

    }


    private boolean checkPlayServices() {
        // TODO Auto-generated method stub
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;

    }

    //-------device info---------------
    public String getDeviceName() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String getAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private String getDeviceId() {
        String deviceId = "";
        final TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(getApplicationContext()
                    .getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    //---------------------------Location lat long method-----------------
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    public boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        /*try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }*/
        if (!gps_enabled /*&& !network_enabled*/) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setMessage("Your Location Services are not enabled for PhoneCure");
            dialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.dismiss();
                }
            });
            alert = dialog.create();
            alert.show();
        } else {

        }
        return gps_enabled/* && network_enabled*/;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            locatioMethod();
            global.setDeviceName(getDeviceName());
            Log.e("device info", getDeviceName() + " , " + getAndroidVersion() + " " + getDeviceId());
            locatioMethod();
            Handler splashhandler = new Handler();
            splashhandler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (CommonUtils.UserID(SplashActivity.this).equalsIgnoreCase("")) {

                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        finish();
                    }
                }
            }, 2000);
        } else {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Toast.makeText(SplashActivity.this, "" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            global.setLat(String.valueOf(mLastLocation.getLatitude()));
            global.setLong(String.valueOf(mLastLocation.getLongitude()));
            Log.e("lat long", global.getLat() + " " + global.getLong());


        } else {


        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    public void locatioMethod() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Toast.makeText(SplashActivity.this, "" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            global.setLat(String.valueOf(mLastLocation.getLatitude()));
            global.setLong(String.valueOf(mLastLocation.getLongitude()));
            Log.e("lat long", global.getLat() + " " + global.getLong());


        } else {

           /* mLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                buildAlertMessageNoGPS();
            }*/

        }
    }

}
