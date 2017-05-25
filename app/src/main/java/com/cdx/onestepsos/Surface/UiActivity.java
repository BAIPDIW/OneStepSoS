package com.cdx.onestepsos.Surface;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.cdx.onestepsos.Bluetooth.BluetoothServerService;
import com.cdx.onestepsos.ConnectServer.HttpConnectionThread;
import com.cdx.onestepsos.ConnectServer.NetworkUtil;
import com.cdx.onestepsos.MessageAndDial.Dial;
import com.cdx.onestepsos.MessageAndDial.DialContentObserver;
import com.cdx.onestepsos.R;
import com.cdx.onestepsos.Setting.Contact;
import com.cdx.onestepsos.Setting.UserInformationDialog;
import com.cdx.onestepsos.Voice.Speech;
import com.cdx.onestepsos.Voice.SpeechService;

import java.util.ArrayList;

/**
 * Created by CDX on 2017/4/15.
 */

public class UiActivity extends Activity {
    private Button btn_sos;
    private ImageButton imgbtn_user_settnig;
    private ProgressFragment progressFragment;
    private ContactsFragment contactsFragment;
    private boolean isContactsFragment = true;
    private Speech speech;
    private Long startTime;
    private NetworkUtil networkUtil;
    private boolean isNetwork = true;
    private boolean bluetoothServiceOn;
    int dialCount;
    int photoCount ;
    int sendPhotoCount;
    private Intent speechIntent;
    DialContentObserver dialContentObserver;
    private boolean isDialed;
    private String dialedNumber = null;
    private ImageButton imgbtn_bluetooth_switch;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "SIGN_SOS_FROM_CLIENT":
                    startTime = System.currentTimeMillis();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    progressFragment = new ProgressFragment();
                    transaction.replace(R.id.fragmentlayout, progressFragment);
                    if (isContactsFragment) {
                        transaction.addToBackStack(null);
                        isContactsFragment = false;
                    } else {

                        transaction.disallowAddToBackStack();
                    }
                    transaction.commitAllowingStateLoss();
                    startService(speechIntent);
                    isNetwork = networkUtil.isNetwork(UiActivity.this);

                    break;
                case "SOS":
                   // speech.TextToSpeech("请帮助我!!!请帮助我!!!");
                    startTime = System.currentTimeMillis();
                    FragmentManager fm2 = getFragmentManager();
                    FragmentTransaction transaction2 = fm2.beginTransaction();
                    progressFragment = new ProgressFragment();
                    transaction2.replace(R.id.fragmentlayout, progressFragment);
                    if (isContactsFragment) {
                        transaction2.addToBackStack(null);
                        isContactsFragment = false;
                    } else {

                        transaction2.disallowAddToBackStack();
                    }
                    transaction2.commitAllowingStateLoss();
                    isNetwork = networkUtil.isNetwork(UiActivity.this);

                    startService(speechIntent);
                    break;
                case "ALL_COMPLETE_START_DIAL":
                    Dial dial = new Dial(UiActivity.this);
                    dialCount = 0;
                    ArrayList<Contact> contacts = getContacts();
                    if(contacts.size() > 0) {
                        dial.call(contacts.get(dialCount).getMobile());
                        dialContentObserver = new DialContentObserver(UiActivity.this, new myHandler(), startTime);
                        dialContentObserver.SetMobile(contacts.get(dialCount).getMobile());
                        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, dialContentObserver);
                    }
                    break;
                case "SMS_SEND_SUCCESS":
                    Log.i("CDX","短信发送成功");
                    break;
                case "STOP_SOS":
                    stopService(speechIntent);
                    TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String content = "ID="+tm.getDeviceId()+"&Phone="+dialedNumber+"&Message="+"1";
                    HttpConnectionThread thread = new HttpConnectionThread(content,HttpConnectionThread.STOP);
                    thread.start();
                    dialedNumber = "0";
                    break;

            }
        }
    };
    public class myHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0://拨打电话未接通
                    ArrayList<Contact> contacts = getContacts();
                    Dial dial = new Dial(UiActivity.this);
                    if(++dialCount < contacts.size()) {
                        dial.call(contacts.get(dialCount).getMobile());
                        dialContentObserver.SetMobile(contacts.get(dialCount).getMobile());
                    }else{
                        dialedNumber = "0";
                    }
                    break;
                case 1://拨打电话接通
                    progressFragment.setImg_dial();

                    Bundle bundle = msg.getData();
                    dialedNumber = bundle.get("mobile").toString();

                    break;
                case 2:
                    sendPhotoCount += 1;
                    if(sendPhotoCount == photoCount){
                        Intent intent = new Intent("ALL_COMPLETE_START_DIAL");
                        sendBroadcast(intent);
                    }
                    break;
            }
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isContactsFragment = true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);
        contactsFragment = new ContactsFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentlayout, contactsFragment);
        transaction.commit();
        speechIntent= new Intent(UiActivity.this, SpeechService.class);
        //开启蓝牙服务
        final Intent intent = new Intent(UiActivity.this,BluetoothServerService.class);
        startService(intent);
        bluetoothServiceOn = true;

        //广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SOS");
        intentFilter.addAction("ALL_COMPLETE_START_DIAL");
        intentFilter.addAction("SMS_SEND_SUCCESS");
        intentFilter.addAction("SIGN_SOS_FROM_CLIENT");
        intentFilter.addAction("STOP_SOS");
        registerReceiver(broadcastReceiver, intentFilter);
        networkUtil = new NetworkUtil();
        isDialed = false;
        btn_sos = (Button) findViewById(R.id.btn_sos);
        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("SOS");
                sendBroadcast(intent);
            }
        });
        imgbtn_bluetooth_switch = (ImageButton) findViewById(R.id.imgbtn_bluetooth_switch);
        imgbtn_bluetooth_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(bluetoothServiceOn){
                   imgbtn_bluetooth_switch.setImageResource(R.drawable.bluetooth_off);
                   bluetoothServiceOn = false;
                   stopService(intent);
                   BluetoothAdapter.getDefaultAdapter().disable();
               }else{
                   startService(intent);
                   BluetoothAdapter.getDefaultAdapter().enable();
                   imgbtn_bluetooth_switch.setImageResource(R.drawable.bluetooth_on);
                   bluetoothServiceOn = true;
               }
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        imgbtn_user_settnig = (ImageButton) findViewById(R.id.imgbtn_user_setting);
        imgbtn_user_settnig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserInformationDialog(UiActivity.this).show();
            }
        });

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if (!sp.getBoolean("isSetting", false)) {
            new UserInformationDialog(UiActivity.this).show();
        }

        speech = new Speech(UiActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            Bundle bundle = data.getBundleExtra("picPath");
            String pic1 = bundle.getString("picPathBack");
            String pic2 = bundle.getString("picPathFront");
            progressFragment.setImg_photo();

            photoCount = 0;
            sendPhotoCount = 0;
            if(pic1 != null){
                photoCount += 1;
            }
            if(pic2 != null){
                photoCount += 1;
            }
            TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            if(pic1 != null) {
                HttpConnectionThread thread = new HttpConnectionThread(tm.getDeviceId(), pic1,new myHandler());
                thread.start();
            }
            if(pic2 != null){
                HttpConnectionThread thread = new HttpConnectionThread(tm.getDeviceId(), pic2,new myHandler());
                thread.start();
            }
           if(!networkUtil.isNetwork(UiActivity.this)) {
               Intent intent = new Intent("ALL_COMPLETE_START_DIAL");
               sendBroadcast(intent);
           }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
        db.execSQL(ContactsFragment.CREATETABLE);
        Cursor cursor = db.rawQuery("select * from contactstb", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact(cursor.getString(cursor.getColumnIndex("name")).toString(), cursor.getString(cursor.getColumnIndex("_mobile")).toString());
                contacts.add(contact);
            }
        }
        cursor.close();
        db.close();
        return contacts;
    }
}
