package com.cdx.onestepsos;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CDX on 2017/4/8.
 */

public class AddContactDialog extends Dialog{
    public final String CREATETABLE = "create table if not exists contactstb(_mobile varchar(11) primary key,name varchar(20) not null)";
    private Activity context;
    public EditText et_name;
    public EditText et_mobile;
    public Button btn_save_contacts;
    public Button btn_cancel_contcts;
    private View.OnClickListener onClickListener;
    public AddContactDialog(Activity context,View.OnClickListener onClickListener) {
        super(context);
        this.context =  context;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contacts_dialog);
        et_name = (EditText) findViewById(R.id.et_name);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        btn_save_contacts = (Button) findViewById(R.id.btn_save_contacts);
        btn_cancel_contcts = (Button) findViewById(R.id.btn_cancel_contacts);


        final Window dialogWindow = this.getWindow();
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);
        this.setCancelable(true);
        btn_save_contacts.setOnClickListener(onClickListener);
       /* btn_save_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将信息存储
                if(!et_mobile.getText().toString().trim().equals("") && !et_name.getText().toString().trim().equals("")){
                    String mobile = et_mobile.getText().toString().trim();
                    String name = et_name.getText().toString().trim();
                    ArrayList<Contact> contacts = getContacts();
                    Log.i("CDX",contacts.size()+"");
                    int i;
                    for(i = 0 ; i < contacts.size() ; i ++){
                        Log.i("CDX",contacts.get(i).getMobile());
                        if(contacts.get(i).getMobile().equals(mobile)){
                            Toast.makeText(context, "紧急电话重复,请重新输入", Toast.LENGTH_SHORT).show();
                            et_mobile.setText("");
                            et_mobile.requestFocus();
                            et_mobile.findFocus();
                            break;
                        }
                    }
                    if( i == contacts.size()){
                        ContentValues values = new ContentValues();
                        values.put("_mobile",mobile);
                        values.put("name",name);
                        SQLiteDatabase db = context.openOrCreateDatabase("user.db",MODE_PRIVATE,null);
                        db.execSQL(CREATETABLE);
                        db.insert("contactstb",null,values);
                        db.close();
                        dismiss();
                    }
                }else{
                    //信息不能为空
                    Toast.makeText(context,"紧急联系人信息不能为空请重新输入",Toast.LENGTH_LONG).show();
                }

            }
        });*/
        btn_cancel_contcts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     dismiss();
            }
        });
    }
    public ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = context.openOrCreateDatabase("user.db",MODE_PRIVATE,null);
        db.execSQL(CREATETABLE);
        Cursor cursor = db.rawQuery("select * from contactstb",null);
        if(cursor != null){
            while(cursor.moveToNext()){
                Contact contact = new Contact(cursor.getString(cursor.getColumnIndex("name")).toString(),cursor.getString(cursor.getColumnIndex("_mobile")).toString());
                contacts.add(contact);
            }
        }
        cursor.close();
        db.close();
        return contacts;
    }
}
