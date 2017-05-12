package com.cdx.onestepsos.Setting;

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

import com.cdx.onestepsos.Surface.ContactsFragment;
import com.cdx.onestepsos.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CDX on 2017/4/8.
 */

public class AddContactDialog extends Dialog{

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        this.setCancelable(false);
        btn_save_contacts.setOnClickListener(onClickListener);
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
        db.execSQL(ContactsFragment.CREATETABLE);
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
