package com.patrick.buksms.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.patrick.buksms.app.ApplicationController;
import com.patrick.buksms.model.Contact;
import com.patrick.buksms.model.ScheduleContact;

import java.util.ArrayList;

/**
 * Created by ivan on 10/30/15.
 */
public class sendSmsService extends WakefulRepeatableService {

    public sendSmsService() {
        super("sendSmsService");
    }


    @Override
    protected void executeBackgroundService(Intent intent) throws Exception {
        ScheduleContact contact = ScheduleContact.getOne();
        if(contact != null){
            sendSMS(contact.phone, contact.message);
            contact.delete();

            ScheduleContact nextContact = ScheduleContact.getOne();
            if(nextContact == null){
                this.reschedule = false;
            }else{
                this.reschedule = true;
                this.scheduleService(ApplicationController.getInstance());
            }
        }else {
            this.reschedule = false;
        }
//        if(contacts.size() > 0){
//            Log.d("info",String.valueOf(contacts.size()));
//            sendSMS(contacts.get(contacts.size() - 1).phone, contacts.get(contacts.size() - 1).message);
//            ScheduleContact.deleteOne(contacts.size());
//        }

    }

    @Override
    protected long getRepeatingInterval() {
        return 36000;
    }

    // We can move it to super class using reflection, if Proguard is not being used.
    protected Intent getServiceIntent(Context context) {
        return new Intent(context,
                sendSmsService.class);
    }


    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(sendSmsService.this, "Service Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(sendSmsService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }
}