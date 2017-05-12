package com.cdx.onestepsos.Setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdx.onestepsos.R;

import java.util.ArrayList;

/**
 * Created by CDX on 2017/4/8.
 */

public class ListViewContactAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Contact> contacts;
    private LayoutInflater inflater;
    public ListViewContactAdapter(Context context, ArrayList<Contact> contacts){
        super();
        this.context = context;
        this.contacts = contacts;
        this.inflater=LayoutInflater.from(context);
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.lv_contacts_item,null);
        TextView tv_name = (TextView) view.findViewById(R.id.lv_contacts_item_name);
        TextView tv_mobile = (TextView) view.findViewById(R.id.lv_contacts_item_mobile);
        tv_name.setText( contacts.get(position).getName().toString());
        tv_mobile.setText(contacts.get(position).getMobile().toString());
        return view;

    }
}
