package com.cdx.onestepsos.Surface;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdx.onestepsos.Camera.CameraActivity;
import com.cdx.onestepsos.ConnectServer.HttpConnectionThread;
import com.cdx.onestepsos.Locate.Location;
import com.cdx.onestepsos.MessageAndDial.SendMessage;
import com.cdx.onestepsos.R;
import com.cdx.onestepsos.Setting.Contact;
import com.cdx.onestepsos.Voice.Speech;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CDX on 2017/4/15.
 */

public class ProgressFragement extends Fragment{
    private ImageView img_location;
    private ImageView img_send_message;
    private ImageView img_photo;

    private TextView tv_progress_location;
    private TextView tv_progress_send_message;
    private TextView tv_progress_photo;
    private Speech speech;
    private String longitude;//经度
    private String latitude;//纬度
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    img_location.setImageResource(R.drawable.gou);
                    Bundle bundle = msg.getData();//定位 经纬度地址
                    //发送短信
                    setLongitude(bundle.get("经度").toString());
                    setLatitude(bundle.get("纬度").toString());
                    ArrayList<Contact> contacts = getContacts();
                    for(int i = 0 ; i < contacts.size() ; i ++){
                        SendMessage sendMessage = new SendMessage(contacts.get(i).getMobile(),
                                "<紧急求助!!!＞我遇到了困难,需要您的帮助,我现在的位置是"+bundle.get("地点").toString()
                        );
                        sendMessage.Send();
                    }
                    Log.i("CDX","发送短信完成");
                   // speech.TextToSpeech("发送短信完成,开始拍照");
                   //拍照
                    TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);

                    String content = "ID="+tm.getDeviceId()+"&Longitude="+bundle.get("经度").toString()+"&Latitude="+bundle.get("纬度").toString();
                    HttpConnectionThread thread = new HttpConnectionThread(content,HttpConnectionThread.UPLOAD_LOCATION);
                    thread.start();

                    img_send_message.setImageResource(R.drawable.gou);
                    Intent intent = new Intent(getActivity(),CameraActivity.class);
                    getActivity().startActivityForResult(intent,1);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.progress_fragment_layout,container,false);
        img_location = (ImageView) view.findViewById(R.id.img_location);
        img_send_message = (ImageView) view.findViewById(R.id.img_send_message);
        img_photo= (ImageView) view.findViewById(R.id.img_photo);
        tv_progress_location = (TextView) view.findViewById(R.id.tv_progress_location);
        tv_progress_photo = (TextView) view.findViewById(R.id.tv_progress_photo);
        tv_progress_send_message = (TextView) view.findViewById(R.id.tv_progress_send_message);
        speech = new Speech(getActivity());
        //开启定位
        Location location = new Location();
        location.init(this.getActivity().getApplicationContext());
        location.set(tv_progress_location,myHandler);
        location.startLocation();

        return view;
    }
    public void setImg_photo(){
        img_photo.setImageResource(R.drawable.gou);
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = this.getActivity().openOrCreateDatabase("user.db", MODE_PRIVATE, null);
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

    public String getLongitude() {
        return longitude;
    }

    public String getLatitute() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
