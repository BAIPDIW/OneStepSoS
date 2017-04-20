package com.cdx.onestepsos;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CDX on 2017/4/15.
 */

public class ContactsFragment extends Fragment implements View.OnClickListener{
    public static final String CREATETABLE = "create table if not exists contactstb(_mobile varchar(15) primary key,name varchar(20) not null)";
    private Button btn_add_contacts;
    private ListView lv_contacts;
    private ListViewContactAdapter listViewContactAdapter;
    private LinearLayout ll_contacts;
    private TextView tv_request_add_contacts;
    private AddContactDialog addContactDialog;
    private AddContactFromPhoneDialog addContactFromPhoneDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment_layout,container,false);
        btn_add_contacts = (Button) view.findViewById(R.id.btn_add_contacts);
        ll_contacts = (LinearLayout) view.findViewById(R.id.ll_contacts);
        lv_contacts = (ListView) view.findViewById(R.id.lv_contacts);
        tv_request_add_contacts = (TextView) view.findViewById(R.id.tv_request_add_contacts);
        listViewContactAdapter = new ListViewContactAdapter(getActivity(),getContacts());
        if(getContacts().size() == 0){
            tv_request_add_contacts.setVisibility(View.VISIBLE);
             ll_contacts.setVisibility(View.GONE);
        }else{
            tv_request_add_contacts.setVisibility(View.GONE);
            ll_contacts.setVisibility(View.VISIBLE);
        }
        lv_contacts.setAdapter(listViewContactAdapter);
        btn_add_contacts.setOnClickListener(this);
        lv_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //长按  编辑 删除
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
                final String[] options = {"删除", "取消"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Contact contact = (Contact)parent.getItemAtPosition(position);
                                SQLiteDatabase db = getActivity().openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                                db.execSQL(ContactsFragment.CREATETABLE);
                                //Log.i("CDX",contact.getMobile());
                                db.delete("contactstb","_mobile = ?",new String[] {contact.getMobile()});
                                db.close();
                                listViewContactAdapter.setContacts(getContacts());
                                if(getContacts().size() == 0) {
                                    tv_request_add_contacts.setVisibility(View.VISIBLE);
                                    ll_contacts.setVisibility(View.GONE);
                                }else{
                                    ll_contacts.setVisibility(View.VISIBLE);
                                    tv_request_add_contacts.setVisibility(View.GONE);
                                }
                                listViewContactAdapter.notifyDataSetChanged();

                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        return view;
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = getActivity().openOrCreateDatabase("user.db", MODE_PRIVATE, null);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_contacts:
                //addContactDialog = new AddContactDialog(getActivity(), this);
                //addContactDialog.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
                final String[] options = {"从手机联系人添加", "手动输入"};
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                               /*ArrayList<Contact> contacts = getContactsFromPhone();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("请选择联系人");
                                String[] contactsName = new String[contacts.size()];
                                for(int i  = 0 ;i < contacts.size() ;i ++){
                                    contactsName[i] = contacts.get(i).getName().toString()+"    "+contacts.get(i).getMobile();
                                }
                                alertDialog.setMultiChoiceItems(contactsName,null,null);
                                alertDialog.setPositiveButton("确认",null);
                                alertDialog.setNegativeButton("取消",null);
                                alertDialog.show();*/
                                addContactFromPhoneDialog = new AddContactFromPhoneDialog(getActivity(),ContactsFragment.this);
                                addContactFromPhoneDialog.show();
                                break;
                            case 1:
                                addContactDialog = new AddContactDialog(getActivity(),ContactsFragment.this);
                                addContactDialog.show();
                                break;
                        }
                    }
                });
                builder.show();
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
                                Toast.makeText(getActivity(), "紧急电话重复,请重新输入", Toast.LENGTH_SHORT).show();
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
                            SQLiteDatabase db = getActivity().openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                            db.execSQL(ContactsFragment.CREATETABLE);
                            db.insert("contactstb", null, values);
                            db.close();
                            listViewContactAdapter.setContacts(getContacts());
                            if(getContacts().size() == 0) {
                                ll_contacts.setVisibility(View.GONE);
                                tv_request_add_contacts.setVisibility(View.VISIBLE);
                            }else{
                                ll_contacts.setVisibility(View.VISIBLE);
                                tv_request_add_contacts.setVisibility(View.GONE);
                            }
                            listViewContactAdapter.notifyDataSetChanged();
                            addContactDialog.dismiss();
                        }
                    } else {
                        //信息不能为空
                        Toast.makeText(getActivity(), "紧急联系人信息不能为空请重新输入", Toast.LENGTH_LONG).show();
                    }
                }
            case R.id.btn_add_contacts_from_phone_confirm:
                if(addContactFromPhoneDialog != null) {
                    HashMap<Integer, Boolean> isSelected = ListViewAddContactsAdapter.getIsSelected();
                    ArrayList<Contact> contacts = addContactFromPhoneDialog.getShowContacts();
                    SQLiteDatabase db = getActivity().openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                    db.execSQL(ContactsFragment.CREATETABLE);
                    ArrayList<Contact> addedContacts = getContacts();//已经加过的联系人
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < contacts.size(); i++) {
                        if (isSelected.get(i)) {
                            int j;
                            for (j = 0; j < addedContacts.size(); j++) {
                                if (addedContacts.get(j).getMobile().equals(contacts.get(i).getMobile())) {
                                    break;
                                }
                            }
                            if(j == addedContacts.size()) {
                                values.clear();
                                values.put("_mobile", contacts.get(i).getMobile());
                                values.put("name", contacts.get(i).getName());
                                db.insert("contactstb", null, values);
                            }else{
                                Log.i("CDX","已存在");
                                continue;
                            }
                        }
                    }
                    db.close();
                    listViewContactAdapter.setContacts(getContacts());
                    if (getContacts().size() == 0) {
                        ll_contacts.setVisibility(View.GONE);
                        tv_request_add_contacts.setVisibility(View.VISIBLE);
                    } else {
                        ll_contacts.setVisibility(View.VISIBLE);
                        tv_request_add_contacts.setVisibility(View.GONE);
                    }
                    listViewContactAdapter.notifyDataSetChanged();
                    addContactFromPhoneDialog.dismiss();
                }
                break;
            case R.id.btn_add_contacts_from_phone_cancel:
                if(addContactFromPhoneDialog != null)
                    addContactFromPhoneDialog.dismiss();
                break;
        }
    }
    private ArrayList<Contact> getContactsFromPhone(){
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        ContentResolver resolver = getActivity().getContentResolver();
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
}
