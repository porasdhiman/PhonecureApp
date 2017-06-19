package com.worksdelight.phonecure;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmIntentService extends IntentService {
    public int NOTIFICATION_ID;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String TAG = "PhoneCure";
    String datamessage, senderid, userreply, reciverid, identity_type = "0";
    Global global;
    Context context;
    SharedPreferences mSharedPreferences,spNotify;
    Editor mEditor;
    boolean isapp_inbackground_boolean;
    Bundle extras;
    PendingIntent contentIntent;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("in gcm", "we are in pushnotification");
        global = (Global) getApplicationContext();
        mSharedPreferences = getSharedPreferences("chat", Context.MODE_PRIVATE);
        extras = intent.getExtras();
        spNotify=getSharedPreferences("NotifyType",Context.MODE_PRIVATE);
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        NOTIFICATION_ID = (int) System.currentTimeMillis();
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("PhoneCure App", "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("PhoneCure App", "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                for (int i = 0; i < 3; i++) {
                    Log.i(TAG, "Working... " + (i + 1) + "/3 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                try {
                    JSONObject obj=new JSONObject(extras.getString("data"));
                    global.setPushValue("1");
                    global.setPushNotificationType(obj.getString("push_notification_type"));
                    Log.e("data",obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               /* Log.e("envago app", extras.toString());
                if (extras.getString("content_title").contains("message")) {
                    String id = extras.getString("data").split(":")[1].replace("\"", "").replace("}", "").trim();
                    String main_id = id.replace("action", "").replace(",", "").trim();
                    global.setEvent_id(main_id);
                    Log.e("id", global.getEvent_id());
                    if (mSharedPreferences.getBoolean("message", false) == true) {
                        MessageFragment m = new MessageFragment();
                        m.CallAPI(GcmIntentService.this);
                    } else {
                        sendNotification("Envago App", extras.getString("content_title"));

                    }

                } else {
                    sendNotification("Envago App", extras.getString("content_title"));
                }*/
                if(spNotify.getBoolean("isEnabled",false)==false) {
                    sendNotification("PhoneCure App", extras.getString("content_title"));
                }else{

                }

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String notificationTitle, String notificationMessage) {


        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);


            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), 0);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(notificationTitle).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setContentText(notificationMessage);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());


    }


}