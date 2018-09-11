package com.example.vend.newmlkitdemoapp;

import android.support.multidex.MultiDexApplication;


public class NewMLKitDemoApplciation extends MultiDexApplication{
    private static NewMLKitDemoApplciation retumApplication;

    public void onCreate() {
        super.onCreate();
        retumApplication = this;

    }

    public static NewMLKitDemoApplciation getInstance() {
        return retumApplication;
    }
}
