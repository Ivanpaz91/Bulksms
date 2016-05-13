package com.patrick.buksms.events;

import android.content.Context;

/**
 * Created by ivan on 10/23/15.
 */
public class SmsFireEvent  {

    public Context context;
    public String phone;
    public String body;

    public SmsFireEvent(Context context,String phone, String body) {

        this.phone = phone;
        this.context = context;
        this.body = body;
    }
}
