package com.cdx.onestepsos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by CDX on 2017/4/8.
 */

public class UiActivity extends Activity implements View.OnClickListener {
    private ImageView img_bluetooth_state;
    private TextView tv_bluetooth_state;
    private Button btn_add_contacts;
    private ListView lv_contacts;
    private ListViewContactAdapter listViewContactAdapter;
    private AddContactDialog addContactDialog;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
//                case BluetoothTools.ACTION_DATA_TO_GAME:
//                    /*Toast.makeText(UiActivity.this,"SOS",Toast.LENGTH_LONG).show();
//                    SendMessage sendMessage = new SendMessage("18850042915","SOS");
//                    sendMessage.Send();*/
//                    startActivity(new Intent(UiActivity.this,ProgressActivity.class));
//                    break;
                case BluetoothTools.ACTION_CONNECT_SUCCESS:
                    img_bluetooth_state.setImageResource(R.drawable.gou);
                    tv_bluetooth_state.setText("已连接");
                    tv_bluetooth_state.setTextColor(Color.GREEN);
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);
        //开启蓝牙服务
        Intent intent = new Intent(UiActivity.this,BluetoothServerService.class);
        startService(intent);

        //广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
        registerReceiver(broadcastReceiver,intentFilter);

        //初始化控件
        btn_add_contacts = (Button) findViewById(R.id.btn_add_contacts);
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        img_bluetooth_state = (ImageView) findViewById(R.id.img_bluetooth_state);
        tv_bluetooth_state = (TextView) findViewById(R.id.tv_bluetooth_state);
        listViewContactAdapter = new ListViewContactAdapter(this,getContacts());
        lv_contacts.setAdapter(listViewContactAdapter);
        btn_add_contacts.setOnClickListener(this);
        lv_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //长按  编辑 删除
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UiActivity.this,android.R.style.Theme_Holo_Light_Dialog);
                final String[] options = {"删除", "取消"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Contact contact = (Contact)parent.getItemAtPosition(position);
                                SQLiteDatabase db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                                db.execSQL("create table if not exists contactstb(_mobile varchar(11) primary key,name varchar(20) not null)");
                                //Log.i("CDX",contact.getMobile());
                                db.delete("contactstb","_mobile = ?",new String[] {contact.getMobile()});
                                db.close();
                                listViewContactAdapter.setContacts(getContacts());
                                listViewContactAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_contacts:
                addContactDialog = new AddContactDialog(this, this);
                addContactDialog.show();
                break;
            case R.id.btn_cancel_contacts:
                if(addContactDialog != null)
                    addContactDialog.dismiss();
                break;
            case R.id.btn_save_contacts:
                if (addContactDialog != null) {
                    if (!addContactDialog.et_mobile.getText().toString().trim().equals("") && !addContactDialog.et_name.getText().toString().trim().equals("")) {
                        String mobile = addContactDialog.et_mobile.getText().toString().trim();
                        String name = addContactDialog.et_name.getText().toString().trim();
                        ArrayList<Contact> contacts = getContacts();
                        int i;
                        for (i = 0; i < contacts.size(); i++) {
                            if (contacts.get(i).getMobile().equals(mobile)) {
                                Toast.makeText(this, "紧急电话重复,请重新输入", Toast.LENGTH_SHORT).show();
                                addContactDialog.et_mobile.setText("");
                                addContactDialog.et_mobile.requestFocus();
                                addContactDialog.et_mobile.findFocus();
                                break;
                            }
                        }
                        if (i == contacts.size()) {
                            ContentValues values = new ContentValues();
                            values.put("_mobile", mobile);
                            values.put("name", name);
                            SQLiteDatabase db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                            db.execSQL(addContactDialog.CREATETABLE);
                            db.insert("contactstb", null, values);
                            db.close();
                            listViewContactAdapter.setContacts(getContacts());
                            listViewContactAdapter.notifyDataSetChanged();
                            addContactDialog.dismiss();
                        }
                    } else {
                        //信息不能为空
                        Toast.makeText(this, "紧急联系人信息不能为空请重新输入", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    public  ArrayList<Contact> getContacts() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
