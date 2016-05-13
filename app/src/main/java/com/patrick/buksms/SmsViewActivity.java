package com.patrick.buksms;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.patrick.buksms.helpers.SmsHelper;
import com.patrick.buksms.model.SimpleContact;
import com.patrick.buksms.model.SmsMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmsViewActivity extends AppCompatActivity {
    SwipeMenuListView inboxListView;
    List<Map<String, String>> data;
    List<SimpleContact> totalContacts;

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<SmsMsg> msgs = SmsHelper.getSMS(this);

        data = new ArrayList<Map<String, String>>();
        for (SmsMsg item : msgs) {
            for(SimpleContact contact : totalContacts){
                if(item.phone.contains(contact.phone)){
                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("name", contact.name);
                    datum.put("phoneNumber", contact.phone);
                    datum.put("message", item.data);

                    data.add(datum);
                    break;
                }
            }

        }

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "message"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        inboxListView.setAdapter(adapter);
        if(data.size() == 0){
            Toast.makeText(SmsViewActivity.this, "No messages", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(SmsViewActivity.this, String.valueOf(data.size()) + " messages received", Toast.LENGTH_SHORT).show();
        }

    }
    public void removeLogClick(View view){
        SimpleContact.deletAll();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_view);
        inboxListView = (SwipeMenuListView)findViewById(R.id.listView_Inbox);

        try {
            totalContacts = SimpleContact.getAll();
        }
        catch (Exception e){};

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };

// set creator
        inboxListView.setMenuCreator(creator);

        inboxListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open


                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", data.get(position).get("phoneNumber"));
                        smsIntent.putExtra("sms_body",data.get(position).get("message"));
                        startActivity(smsIntent);


                        break;
                    case 1:
                        // delete

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        inboxListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sms_view, menu);
        return true;
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
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
