package com.cdx.onestepsos;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    Toast.makeText(getActivity(),"定位成功", Toast.LENGTH_LONG).show();
                    img_location.setImageResource(R.drawable.gou);
                    Bundle bundle = msg.getData();//定位 经纬度地址
                    //发送短信
                    ArrayList<Contact> contacts = getContacts();
                    for(int i = 0 ; i < contacts.size() ; i ++){
                        SendMessage sendMessage = new SendMessage(contacts.get(i).getMobile(),
                                "<紧急求助!!!＞我遇到了困难,需要您的帮助,我现在的位置是"+bundle.get("地点").toString()
                        );
                        sendMessage.Send();
                    }
                    Log.i("CDX","发送短信完成");
                    //拍照
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
