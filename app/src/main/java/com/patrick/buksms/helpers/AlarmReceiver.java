package com.patrick.buksms.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.patrick.buksms.app.ApplicationController;
import com.patrick.buksms.services.sendSmsService;

import java.util.logging.Logger;

/**
 * Created by ivan on 10/30/15.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String EXTRA_SERVICE = "extra-service";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("startuptest", "startup boot receiver completed");
            BootListener bootListener = getBootListener();
            if (bootListener != null) {
                bootListener.onBootCompleted(context, intent);
            }
            return;
        }
        startWakefulService(context, new Intent(context, sendSmsService.class));

    }

    private BootListener getBootListener() {
        return ApplicationController.getInstance();
    }




}