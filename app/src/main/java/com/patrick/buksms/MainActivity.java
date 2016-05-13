package com.patrick.buksms;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.filippudak.ProgressPieView.ProgressPieView;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.melnykov.fab.FloatingActionButton;
import com.patrick.buksms.app.ApplicationController;
import com.patrick.buksms.helpers.CsvHelper;
import com.patrick.buksms.helpers.SmsHelper;
import com.patrick.buksms.model.Contact;
import com.patrick.buksms.model.ScheduleContact;
import com.patrick.buksms.model.SmsMsg;
import com.patrick.buksms.services.sendSmsService;

import java.util.ArrayList;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.ExFilePickerParcelObject;

public class MainActivity extends Activity {

    private static final String TAG = "FileChooserActivity";
    private ArrayList<Contact> contacts = new ArrayList<>();
    private static final int EX_FILE_PICKER_RESULT = 0;
    Context context;

    AnimatedCircleLoadingView animatedCircleLoadingView;
   // private ProgressPieView mProgressPieViewXml;
    ImageView inboxBtn;
    ImageView logoView;
    ArrayList<ScheduleContact> scheduleContacts = new ArrayList<>();

    private int mInterval = 36000;
    private Handler mHandler;
    TextView mStatusText;
    FloatingActionButton addBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);
        addBtn = (FloatingActionButton) findViewById(R.id.fab);
        animatedCircleLoadingView = (AnimatedCircleLoadingView)findViewById(R.id.circle_loading_view);
        inboxBtn = (ImageView)findViewById(R.id.inboxBtn);
        logoView = (ImageView)findViewById(R.id.image_logo);
       // mProgressPieViewXml = (ProgressPieView)findViewmIntervalById(R.id.progressPieViewXml);
        mStatusText = (TextView)findViewById(R.id.textView_status_message);
        mHandler = new Handler();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleContacts = ScheduleContact.getAll();
                if (scheduleContacts.size() == 0) {
                    logoView.setVisibility(View.INVISIBLE);
                    animatedCircleLoadingView.setVisibility(View.VISIBLE);
                    showChooser();
                } else {
                    if(ApplicationController.getInstance().isServiceForceStopped()){
                        setImageDrawableToBtn(addBtn,R.drawable.ic_stop);
                        ApplicationController.getInstance().setServiceForceStopped(false);
                        ApplicationController.getInstance().scheduleServices();

                    }else{
                        setImageDrawableToBtn(addBtn,R.drawable.ic_stop);
                        ApplicationController.getInstance().setServiceForceStopped(true);
                        stopService(new Intent(context, sendSmsService.class));
                    }
                    Toast.makeText(MainActivity.this, "Messages are being sent, please wait", Toast.LENGTH_SHORT).show();
                }

            }
        });

        inboxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SmsViewActivity.class));

            }
        });

    }

    private void onButtonOnclick() {


    }
    public void showStatusBar(){
      //  mProgressPieViewXml.setVisibility(View.VISIBLE);
      //  mProgressPieViewXml.setText(Integer.toString(scheduleContacts.size()) + "messages left");
        Toast.makeText(MainActivity.this, Integer.toString(scheduleContacts.size()) + " messages left", Toast.LENGTH_SHORT).show();
      //  mProgressPieViewXml.setOnProgressListener(new ProgressPieView.OnProgressListener() {
//            @Override
//            public void onProgressChanged(int progress, mIntervalint max) {
//                if (!mProgressPieViewXml.isTextShowing()) {
//                    mProgressPieViewXml.setShowText(true);
//                    mProgressPieViewXml.setShowImage(falmIntervalse);
//                }
//            }
//
//            @Override
//            public void onProgressCompleted() {
//                if (!mProgressPieViewXml.isImageShowing()) {
//                    mProgressPieViewXml.setShowImage(true);
//                }
//                mProgressPieViewXml.setShowText(false);
//                //mProgressPieViewXml.setImageResource(R.drawable.ic_action_accept);
//            }
//        });mInterval
    }
    private void showChooser() {
        Intent intent = new Intent(getApplicationContext(), ru.bartwell.exfilepicker.ExFilePickerActivity.class);
        intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true);
        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[]{"csv"});
//        intent.putExtra(ExFilePicker.SET_FILTER_EXCLUDE, new String[]{ "csv" });
        intent.putExtra(ExFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);

        startActivityForResult(intent, EX_FILE_PICKER_RESULT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        scheduleContacts = ScheduleContact.getAll();
        if(scheduleContacts.size() > 0){
            if (ApplicationController.getInstance().isServiceForceStopped()){
                setImageDrawableToBtn(addBtn,R.drawable.ic_play);

            } else {
                setImageDrawableToBtn(addBtn,R.drawable.ic_stop);
            }


            //addBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop,getTheme()));
            animatedCircleLoadingView.setVisibility(View.INVISIBLE);
            logoView.setVisibility(View.GONE);

            showStatusBar();
            mHandler = new Handler();
            startRepeatingTask();

            int minute = (int)Math.round(scheduleContacts.size() * 0.6);
            mStatusText.setText(Integer.toString(scheduleContacts.size()) + " messages left." +
                    "\n Total " + minute + " minutes to complete.");

        }else{

            setImageDrawableToBtn(addBtn,R.drawable.ic_add);
            animatedCircleLoadingView.setVisibility(View.VISIBLE);
            mStatusText.setText("");
            logoView.setVisibility(View.VISIBLE);
          //  mProgressPieViewXml.setVisibility(View.GONE);
        }


    }
    void setImageDrawableToBtn(FloatingActionButton btn, int res_id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setImageDrawable(getResources().getDrawable(res_id,getTheme()));
        }else {
            btn.setImageDrawable(getResources().getDrawable(res_id));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mHandler != null){
            stopRepeatingTast();
        }
    }

    private void startRepeatingTask() {
        mStatusChecker.run();
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            scheduleContacts = ScheduleContact.getAll();
            if(scheduleContacts.size() > 0){
                int minute = (int)Math.round(scheduleContacts.size() * 0.6);
                mStatusText.setText(Integer.toString(scheduleContacts.size()) + " messages left" +
                        "\n Total " + minute + " minutes left.");
                mHandler.postDelayed(mStatusChecker, mInterval);
            }

        }
    };



    void stopRepeatingTast(){
        mHandler.removeCallbacks(mStatusChecker);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EX_FILE_PICKER_RESULT) {
            if (data != null) {
                ExFilePickerParcelObject object = (ExFilePickerParcelObject) data.getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
                if (object.count > 0) {
                    // Here is object contains selected files names and path
                    ApplicationController.getInstance().contacts = CsvHelper.getContact(context, object);

                    showProgress();

                }

            }






//            animatedCircleLoadingView.startIndeterminate();
        }
    }

    private void showProgress() {
        logoView.setVisibility(View.INVISIBLE);

        animatedCircleLoadingView.startDeterminate();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < 51; i++) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //do your Ui task here
                            animatedCircleLoadingView.setPercent(finalI);
                        }
                    });

                    try {

                        Thread.sleep(40);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                for (int i = 51; i <= 100; i++) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //do your Ui task here
                            animatedCircleLoadingView.setPercent(finalI);
                        }
                    });

                    try {

                        Thread.sleep(14);


                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }


                //start Next Activity
                 Runnable intentStarter =  new Runnable() {

                    @Override
                    public void run() {

                        //do your Ui task here
                        try
                        {

                            Thread.sleep(4000);// sleeps 1 second
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if(ApplicationController.getInstance().contacts.size() !=0 ){
                            startActivity(new Intent(MainActivity.this, ContactListActivity.class));
                            ((MainActivity) context).finish();
                        }else{
                            animatedCircleLoadingView.setPercent(-1);
                            logoView.setVisibility(View.VISIBLE);
                        }

                    }
                };
                Thread name = new Thread(intentStarter);
                name.start();
            }

        });
        thread.start();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
