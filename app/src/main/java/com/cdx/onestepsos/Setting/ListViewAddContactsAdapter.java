package com.cdx.onestepsos.Setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cdx.onestepsos.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CDX on 2017/4/19.
 */

public class ListViewAddContactsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Contact> contacts;
    private LayoutInflater inflater;
    private static HashMap<Integer,Boolean> isSelected;
    public ListViewAddContactsAdapter(ArrayList<Contact> contacts,Context context){
        super();
        this.context = context;
        this.contacts = contacts;
        this.inflater=LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initData();
    }

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ListViewAddContactsAdapter.isSelected = isSelected;
    }
    private void initData() {
        for (int i = 0; i < contacts.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
        initData();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.lv_add_contacts_from_phone_item,null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_add_contacts_from_phone_name);
        TextView tv_mobile = (TextView) view.findViewById(R.id.tv_add_contacts_from_phone_mobile);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_add_contact_selected);
        tv_name.setText( contacts.get(position).getName().toString());
        tv_mobile.setText(contacts.get(position).getMobile().toString());

        checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                }
            }
        });
        checkBox.setChecked(getIsSelected().get(position));
        return view;
    }
}
