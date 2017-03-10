package com.worksdelight.phonecure;

import android.app.Application;

/**
 * Created by worksdelight on 10/03/17.
 */

public class Global extends Application {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    String deviceName;
}
