package com.cdx.onestepsos;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by CDX on 2017/4/19.
 */

public class AddContactFromPhoneDialog extends Dialog{
    private Activity context;
    private ListView lv_show_contacts_from_phone;
    private Button btn_add_contacts_from_phone_confirm;
    private Button btn_add_contacts_from_phone_cancel;
    private EditText et_add_contacts_from_phone_mobile_search;
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> showedContacts;
    private View.OnClickListener onClickListener;
    public AddContactFromPhoneDialog(Activity context, View.OnClickListener onClickListener){
        super(context);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_contacts_from_phone);
        final Window dialogWindow = this.getWindow();
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.8
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);
        this.setCancelable(false);
        lv_show_contacts_from_phone = (ListView) findViewById(R.id.lv_show_contacts_from_phone);
        contacts = getContactsFromPhone();
        showedContacts = getContactsFromPhone();
        final ListViewAddContactsAdapter listViewAddContactsAdapter = new ListViewAddContactsAdapter(contacts,context);
        lv_show_contacts_from_phone.setAdapter(listViewAddContactsAdapter);
        btn_add_contacts_from_phone_confirm= (Button) findViewById(R.id.btn_add_contacts_from_phone_confirm);
        btn_add_contacts_from_phone_confirm.setOnClickListener(onClickListener);
        btn_add_contacts_from_phone_cancel = (Button) findViewById(R.id.btn_add_contacts_from_phone_cancel);
        btn_add_contacts_from_phone_cancel.setOnClickListener(onClickListener);
        et_add_contacts_from_phone_mobile_search = (EditText) findViewById(R.id.et_add_contacts_from_phone_mobile_search);
        et_add_contacts_from_phone_mobile_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                   listViewAddContactsAdapter.setContacts(searchFromContacts(s.toString()));
                   listViewAddContactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    private ArrayList<Contact> getContactsFromPhone(){
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        String name;
        String mobile;
        if(phoneCursor != null){
            while(phoneCursor.moveToNext()){
                name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                mobile = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(mobile.startsWith("+86")){
                    mobile = mobile.substring(3);
                }
                contacts.add(new Contact(name,mobile));
            }
        }
        phoneCursor.close();
        return contacts;
    }

    public  ArrayList<Contact> searchFromContacts(String s){
       // ArrayList<Contact> contactsSearched = new ArrayList<Contact>();
        showedContacts.clear();
        for(int i = 0 ;i < contacts.size() ;i ++){
            if(contacts.get(i).getName().contains(s)){
                showedContacts.add(contacts.get(i));
            }
        }
        return showedContacts;
    }
    public ArrayList<Contact> getShowContacts(){
        return showedContacts;
    }
}
