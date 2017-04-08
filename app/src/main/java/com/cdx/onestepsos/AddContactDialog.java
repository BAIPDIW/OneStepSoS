package com.cdx.onestepsos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by CDX on 2017/4/8.
 */

public class AddContactDialog extends Dialog{
    private Activity context;
    private EditText et_name;
    private EditText et_mobile;
    private Button btn_save_contacts;
    private Button btn_cancel_contcts;
    public AddContactDialog(Activity context) {
        super(context);
        this.context =  context;
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
        btn_save_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //将信息存储
                SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if(!et_name.getText().toString().trim().equals("") && !et_mobile.getText().toString().trim().equals("")){
                    editor.putString("ContactName1",et_name.getText().toString().trim());
                    editor.putString("ContactMobile1",et_mobile.getText().toString().trim());
                    editor.commit();
                    Toast.makeText(context,"editor commit success",Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });
        btn_cancel_contcts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     dismiss();

            }
        });
    }
}
