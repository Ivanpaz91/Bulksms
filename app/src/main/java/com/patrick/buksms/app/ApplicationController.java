package com.patrick.buksms.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.patrick.buksms.helpers.BootListener;
import com.patrick.buksms.model.Contact;
import com.patrick.buksms.services.sendSmsService;

import java.util.ArrayList;

/**
 * Created by ivan on 10/17/15.
 */
public class ApplicationController extends com.activeandroid.app.Application implements BootListener {

    private static ApplicationController instance;
    public ArrayList<Contact> contacts = new ArrayList<>();
    Context context1;
    Boolean isServiceForceStopped;
    static String SERVICE_STATUS = "service_status";
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        instance = this;
        context1 = this;
    }
    public  Context getContext(){
        return context1;
    }
    public static synchronized ApplicationController getInstance() {

        return instance;
    }
    public static boolean isActivityVisible(){
        return activityVisible;
    }
    public static void activityResumed(){
        activityVisible = true;
    }
    public static void isActivityPaused(){
        activityVisible = false;
    }

    private static boolean activityVisible;

    @Override
    public void onBootCompleted(Context context, Intent intent) {
        if(!isServiceForceStopped()){
            scheduleServices();
        }

    }

    public void scheduleServices() {
        new sendSmsSer
        vice().scheduleService(instance);
    }
    public boolean isServiceForceStopped(){
        Boolean flag = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.contains(SERVICE_STATUS)){
           flag = prefs.getBoolean(SERVICE_STATUS,false);
        }
        return flag;
    }

    public void setServiceForceStopped(Boolean flag){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(SERVICE_STATUS,flag);
        prefs.edit().apply();
    }
}
