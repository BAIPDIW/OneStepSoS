package com.cdx.onestepsos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by CDX on 2017/4/20.
 */

public class UserInformationDialog extends Dialog implements View.OnClickListener{

    private EditText et_user_information_name;
    private EditText et_user_information_gender;
    private EditText et_user_information_age;
    private EditText et_user_information_default_location;
    private EditText et_user_information_medical_history;

    private Button btn_user_information_update;
    private Button btn_user_information_save;
    private Button btn_user_information_cancel;
    private Activity context;
    public UserInformationDialog(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_information_layout);
        setCancelable(false);
        et_user_information_name  = (EditText) findViewById(R.id.et_user_information_name);
        et_user_information_gender = (EditText) findViewById(R.id.et_user_information_gender);
        et_user_information_age = (EditText) findViewById(R.id.et_user_information_age);
        et_user_information_default_location = (EditText) findViewById(R.id.et_user_information_default_location);
        et_user_information_medical_history = (EditText) findViewById(R.id.et_user_information_medical_history);
        btn_user_information_cancel = (Button) findViewById(R.id.btn_user_information_cancel);
        btn_user_information_save = (Button) findViewById(R.id.btn_user_information_save);
        btn_user_information_update = (Button) findViewById(R.id.btn_user_information_update);
        btn_user_information_save.setOnClickListener(this);
        btn_user_information_update.setOnClickListener(this);
        btn_user_information_cancel.setOnClickListener(this);
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if(sp.getBoolean("isSetting",false)){
            et_user_information_age.setText(sp.getString("age",null));
            et_user_information_name.setText(sp.getString("name",null));
            et_user_information_gender.setText(sp.getString("gender",null));
            et_user_information_default_location.setText(sp.getString("default_location",null));
            et_user_information_medical_history.setText(sp.getString("medical_history",null));
            etsSetUnEnable();
        }else{

        }
    }

    private void etsSetUnEnable() {
        et_user_information_age.setEnabled(false);
        et_user_information_name.setEnabled(false);
        et_user_information_gender.setEnabled(false);
        et_user_information_default_location.setEnabled(false);
        et_user_information_medical_history.setEnabled(false);
    }
    private void etsSetEnable(){
        et_user_information_age.setEnabled(true);
        et_user_information_name.setEnabled(true);
        et_user_information_gender.setEnabled(true);
        et_user_information_default_location.setEnabled(true);
        et_user_information_medical_history.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_user_information_update:
                etsSetEnable();
                break;
            case R.id.btn_user_information_save:
                SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name",et_user_information_name.getText().toString());
                editor.putString("age",et_user_information_age.getText().toString());
                editor.putString("gender",et_user_information_gender.getText().toString());
                editor.putString("default_location",et_user_information_default_location.getText().toString());
                editor.putString("medical_history",et_user_information_medical_history.getText().toString());
                editor.putBoolean("isSetting",true);
                editor.commit();
                etsSetUnEnable();
                dismiss();
                break;
            case R.id.btn_user_information_cancel:
                dismiss();
                break;
        }
    }
}
