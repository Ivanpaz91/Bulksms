package com.patrick.buksms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.service.carrier.CarrierMessagingService;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.dd.CircularProgressButton;
import com.patrick.buksms.app.ApplicationController;
import com.patrick.buksms.model.Contact;
import com.patrick.buksms.model.ScheduleContact;
import com.patrick.buksms.model.SimpleContact;
import com.patrick.buksms.services.sendSmsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactListActivity extends AppCompatActivity {
    List<Map<String, String>> data;
    EditText editText;
    TextView lengthText;
    CircularProgressButton sendBtn;
    ArrayList<Integer> messageStatus = new ArrayList<>();
    int count = 0;
    int total = 0;
    String message;
    Context context;

    public int index = 0;

    TextView statusText;

    boolean isSent = false;
    BroadcastReceiver rec;
    ProgressBar progressBar;

    boolean isTrigerred = false;
    @Override
    protected void onResume() {
        super.onResume();
//        ApplicationController.getInstance().activityResumed();
//        if(isSent == true){
//           // Toast.makeText(this,message,Toast.LENGTH_LONG).show();
//            isSent = false;
//            showAlert(message);
////            showStatus();
//            progressBar.setVisibility(View.VISIBLE);
//        }else{
//            progressBar.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list_activity);
        context = this;

        ListView listView = (ListView)findViewById(R.id.listView_contact);
        editText = (EditText)findViewById(R.id.editText1);
        lengthText = (TextView)findViewById(R.id.textView_length);
        sendBtn = (CircularProgressButton)findViewById(R.id.sendBtn);
        statusText = (TextView) findViewById(R.id.textView_status);
        progressBar = (ProgressBar) findViewById(R.id.view_progress);

        data = new ArrayList<Map<String, String>>();
        for (Contact item : ApplicationController.getInstance().contacts) {
            if((item.phoneNumber.length() == 11) || (item.phoneNumber.length() == 10)){
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("name", item.firstName + " " + item.lastName);
                datum.put("phone", item.phoneString);
                datum.put("phoneNumber", item.phoneNumber);
                datum.put("email",item.email);
                data.add(datum);
            }
        }
        if(data.size() == 0){
            Toast.makeText(this,"There is no valid phone number",Toast.LENGTH_LONG).show();
        }
        else{
            SimpleAdapter adapter = new SimpleAdapter(this, data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"name", "phone"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});


            listView.setAdapter(adapter);
        };
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lengthText.setText(String.valueOf(160  - editText.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTrigerred){
                    Toast.makeText(ContactListActivity.this, "Messages are being sent,please wait.", Toast.LENGTH_SHORT).show();
                }else{

                    final String message = editText.getText().toString();
                    if (message.isEmpty()) {
                        Toast.makeText(ContactListActivity.this, "No message content", Toast.LENGTH_SHORT).show();
                        editText.requestFocus();
                    } else {
                        isTrigerred = true;

//                        progressBar.setVisibility(View.VISIBLE);
                        saveToDB();
                        saveToSchedule(message);
                      //  final Handler handler = new Handler();
                      //  index = 0;

                        startService(new Intent(context, sendSmsService.class));


                        isTrigerred = false;
                        int minute = (int)Math.round(data.size() * 0.6);
                        Toast.makeText(context,Integer.toString(data.size()) + " messages added to queue.\n" +
                                " Total " + Integer.toString(minute) + " minutes needed.",Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(context, MainActivity.class));
                                        finish();
                                    }
                                },3000);
                            }
                        });
                    }
                }
            }});
    }

    private void showAlert(String title) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list_acitivity, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationController.getInstance().isActivityPaused();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if(rec != null){
            unregisterReceiver(rec);
        }

    }


    private void saveToDB() {
        ActiveAndroid.beginTransaction();
        try {
            for (Map<String,String> contact:data) {
                SimpleContact item = new SimpleContact();
                item.name = contact.get("name");
                item.email = contact.get("email");
                item.phone = contact.get("phoneNumber");
                item.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void saveToSchedule(String message) {
        ActiveAndroid.beginTransaction();
        try {
            for (Map<String,String> contact:data) {
                ScheduleContact item = new ScheduleContact();

                String phone = contact.get("phoneNumber");
                item.phone = phone;
                item.message = message;
                item.name = contact.get("name");
                item.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void resetStatus() {

        for (int k = 0;k < 5; k++){
            messageStatus.set(k, 0) ;
        }
        count = 0;
    }
}

