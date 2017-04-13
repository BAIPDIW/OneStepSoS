package com.cdx.onestepsos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by CDX on 2017/4/13.
 */

public class ProgressActivity extends Activity{
    private ProgressBar pb_location;
    private ProgressBar pb_send_message;
    private ProgressBar pb_take_photo_upload;

    private TextView tv_progress_location;
    private TextView tv_progress_send_message;
    private TextView tv_progress_photo;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){

            }
        }
    };
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    Toast.makeText(ProgressActivity.this,"定位成功",Toast.LENGTH_LONG).show();
                   Bundle bundle = msg.getData();//定位 经纬度地址
                    Log.i("CDX",bundle.get("地点").toString());
                    //发送短信
                    ArrayList<Contact> contacts = getContacts();
                    for(int i = 0 ; i < contacts.size() ; i ++){
                        SendMessage sendMessage = new SendMessage(contacts.get(i).getMobile(),
                                "<紧急求助!!!＞我遇到了困难,需要您的帮助,我现在的位置是"+bundle.get("地点").toString()
                        );
                        sendMessage.Send();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_layout);
        pb_location = (ProgressBar) findViewById(R.id.pb_location);
        pb_send_message = (ProgressBar) findViewById(R.id.pb_send_message);
        pb_take_photo_upload = (ProgressBar) findViewById(R.id.pb_take_photo_upload);
        tv_progress_location = (TextView) findViewById(R.id.tv_progress_location);
        tv_progress_photo = (TextView) findViewById(R.id.tv_progress_photo);
        tv_progress_send_message = (TextView) findViewById(R.id.tv_progress_send_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

       //开启定位
        Location location = new Location();
        location.init(getApplicationContext());
        location.set(tv_progress_location,pb_location,myHandler);
        location.startLocation();

    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists contactstb(_mobile varchar(11) primary key,name varchar(20) not null)");
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
