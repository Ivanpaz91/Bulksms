package com.patrick.buksms.model;

/**
 * Created by sb on 10/18/15.
 */
public class SmsMsg {

    public String phone;
    public String data;

    public SmsMsg(String address, String body) {

        phone = address;
        data = body;

    }
}

