package com.cdx.onestepsos.Setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cdx.onestepsos.ConnectServer.HttpConnectionThread;
import com.cdx.onestepsos.R;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by CDX on 2017/4/20.
 */

public class UserInformationDialog extends Dialog implements View.OnClickListener{
    private EditText et_user_information_mobile;
    private EditText et_user_information_name;
    private EditText et_user_information_age;
    private EditText et_user_information_default_location;
    private EditText et_user_information_medical_history;
    private RadioGroup rg_gender;
    private RadioButton rb_gender_male;
    private RadioButton rb_gender_female;
    private Button btn_user_information_update;
    private Button btn_user_information_save;
    private Button btn_user_information_cancel;
    private Activity context;
    private boolean register;
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
        et_user_information_mobile = (EditText) findViewById(R.id.et_user_information_mobile);
        et_user_information_name  = (EditText) findViewById(R.id.et_user_information_name);
        rg_gender = (RadioGroup) findViewById(R.id.rg_gender);
        rb_gender_male = (RadioButton) findViewById(R.id.rb_gender_male);
        rb_gender_female = (RadioButton) findViewById(R.id.rb_gender_female);

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
            et_user_information_mobile.setText(sp.getString("mobile",null));
            et_user_information_age.setText(sp.getString("age",null));
            et_user_information_name.setText(sp.getString("name",null));
            String gender = sp.getString("gender",null);
            if(gender.equals("女")){
                rb_gender_female.setChecked(true);
            }
            et_user_information_default_location.setText(sp.getString("default_location",null));
            et_user_information_medical_history.setText(sp.getString("medical_history",null));
            register = true;
            etsSetUnEnable();
        }else{
            btn_user_information_cancel.setEnabled(false);
            btn_user_information_save.setText("注册");
            register = false;
        }
    }

    private void etsSetUnEnable() {
        et_user_information_mobile.setEnabled(false);
        et_user_information_age.setEnabled(false);
        et_user_information_name.setEnabled(false);
       // et_user_information_gender.setEnabled(false);
        rg_gender.setEnabled(false);
        et_user_information_default_location.setEnabled(false);
        et_user_information_medical_history.setEnabled(false);
    }
    private void etsSetEnable(){
        et_user_information_mobile.setEnabled(true);
        et_user_information_age.setEnabled(true);
        et_user_information_name.setEnabled(true);
        rg_gender.setEnabled(true);
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
                if(et_user_information_mobile.getText().toString().equals("")) {
                    et_user_information_mobile.setHint("手机号码不能为空");
                    et_user_information_mobile.setHintTextColor(Color.RED);
                    break;
                }
                SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String mobile=et_user_information_mobile.getText().toString();
                String name =et_user_information_name.getText().toString();
                String age = et_user_information_age.getText().toString();
                String gender;
                if(rg_gender.getCheckedRadioButtonId() == rb_gender_male.getId()){
                    gender = "男";
                }else{
                    gender = "女";
                }
                String default_location = et_user_information_default_location.getText().toString();
                String medical_history = et_user_information_medical_history.getText().toString();
                //if(!register){
                    TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
                    String IEMI = TelephonyMgr.getDeviceId();
                    Log.i("CDX","IEMI = "+IEMI);
                    String content = null;

                    content = "ID="+IEMI+"&Number="+mobile+"&Name="+name+"&Gender="+gender+"&Age="+age+"&Address="+default_location
                                +"&MedicalHistory="+medical_history;

                    HttpConnectionThread thread = new HttpConnectionThread(content,HttpConnectionThread.USER_REGISTER);
                    thread.start();
              //  }else{


               // }
                editor.putString("mobile",mobile);
                editor.putString("name",name);
                editor.putString("age",age);
                editor.putString("gender",gender);
                editor.putString("default_location",default_location);
                editor.putString("medical_history",medical_history);
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
