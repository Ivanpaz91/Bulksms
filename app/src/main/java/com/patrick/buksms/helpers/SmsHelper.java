package com.patrick.buksms.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.patrick.buksms.model.SimpleContact;
import com.patrick.buksms.model.SmsMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 10/18/15.
 */
public class SmsHelper {
    public static void deleteSMS(Context context, String message, String number) {
        try {

            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address",
                            "person", "date", "body" }, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    if (message.equals(body) && address.equals(number)) {

                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
           // mLogger.logError("Could not delete SMS from inbox: " + e.getMessage());
        }
    }

    public  static ArrayList<SmsMsg> getSMS(Context context){
        ArrayList<SmsMsg> sms = new ArrayList<SmsMsg>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            sms.add(new SmsMsg(address,body));

        }
        return sms;

    }
}
