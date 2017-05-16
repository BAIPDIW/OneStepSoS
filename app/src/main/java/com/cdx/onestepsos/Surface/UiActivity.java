package com.cdx.onestepsos.Surface;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.cdx.onestepsos.Bluetooth.BluetoothServerService;
import com.cdx.onestepsos.Bluetooth.BluetoothTools;
import com.cdx.onestepsos.ConnectServer.HttpConnectionThread;
import com.cdx.onestepsos.MessageAndDial.Dial;
import com.cdx.onestepsos.MessageAndDial.DialContentObserver;
import com.cdx.onestepsos.R;
import com.cdx.onestepsos.Setting.Contact;
import com.cdx.onestepsos.Setting.UserInformationDialog;
import com.cdx.onestepsos.Voice.Speech;

import java.util.ArrayList;

/**
 * Created by CDX on 2017/4/15.
 */

public class UiActivity extends Activity {
    private Button btn_sos;
    private ImageButton imgbtn_user_settnig;
    private ImageView img_bluetooth_state;
    private TextView tv_bluetooth_state;
    private ProgressFragement progressFragement;
    private ContactsFragment contactsFragment;
    private boolean isContactsFragment = true;
    private Speech speech;
    private String longtitute;
    private String latitute;
    private Long startTime;
    int dialCount;
    DialContentObserver dialContentObserver;
    private boolean isDialed;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothTools.ACTION_CONNECT_SUCCESS:
                    img_bluetooth_state.setImageResource(R.drawable.gou);
                    tv_bluetooth_state.setText("已连接");
                    tv_bluetooth_state.setTextColor(Color.GREEN);
                    speech.TextToSpeech("蓝牙已连接");
                    break;
                case BluetoothTools.ACTION_DATA_TO_GAME:
                    startTime = System.currentTimeMillis();
                    speech.TextToSpeech("请帮助我!!!请帮助我!!!");
                    img_bluetooth_state.setImageResource(R.drawable.gou);
                    tv_bluetooth_state.setText("已连接");
                    tv_bluetooth_state.setTextColor(Color.GREEN);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    progressFragement = new ProgressFragement();
                    transaction.replace(R.id.fragmentlayout, progressFragement);
                    if (isContactsFragment) {
                        transaction.addToBackStack(null);
                        isContactsFragment = false;
                    } else {

                        transaction.disallowAddToBackStack();
                    }
                    transaction.commitAllowingStateLoss();
                    break;
                case "SOS":
                    speech.TextToSpeech("请帮助我!!!请帮助我!!!");
                    startTime = System.currentTimeMillis();
                    FragmentManager fm2 = getFragmentManager();
                    FragmentTransaction transaction2 = fm2.beginTransaction();
                    progressFragement = new ProgressFragement();
                    transaction2.replace(R.id.fragmentlayout, progressFragement);
                    if (isContactsFragment) {
                        transaction2.addToBackStack(null);
                        isContactsFragment = false;
                    } else {

                        transaction2.disallowAddToBackStack();
                    }
                    transaction2.commitAllowingStateLoss();
                    break;
                case "ALL_COMPLETE_START_DIAL":
                    Dial dial = new Dial(UiActivity.this);
                    dialCount = 0;
                    ArrayList<Contact> contacts = getContacts();
                    dial.call(contacts.get(dialCount).getMobile());
                    dialContentObserver = new DialContentObserver(UiActivity.this,new myHandler(),startTime);
                    dialContentObserver.SetMobile(contacts.get(dialCount).getMobile());
                    getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,true,dialContentObserver);
                    break;
                case "SMS_SEND_SUCCESS":
                    Log.i("CDX","短信发送成功");
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
                    }
                    break;
                case 1://拨打电话接通

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
        //开启蓝牙服务
        Intent intent = new Intent(UiActivity.this, BluetoothServerService.class);
        startService(intent);

        //广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
        intentFilter.addAction("SOS");
        intentFilter.addAction("ALL_COMPLETE_START_DIAL");
        intentFilter.addAction("SMS_SEND_SUCCESS");
        registerReceiver(broadcastReceiver, intentFilter);

        isDialed = false;
        img_bluetooth_state = (ImageView) findViewById(R.id.img_bluetooth_state);
        tv_bluetooth_state = (TextView) findViewById(R.id.tv_bluetooth_state);
        btn_sos = (Button) findViewById(R.id.btn_sos);
        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("SOS");
                sendBroadcast(intent);
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
            progressFragement.setImg_photo();
            speech.TextToSpeech("拍照完成");

            TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            if(pic1 != null) {
                HttpConnectionThread thread = new HttpConnectionThread(tm.getDeviceId(), pic1);
                thread.start();
            }
            if(pic2 != null){
                HttpConnectionThread thread = new HttpConnectionThread(tm.getDeviceId(), pic2);
                thread.start();
            }

            Intent intent = new Intent("ALL_COMPLETE_START_DIAL");
            sendBroadcast(intent);
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
