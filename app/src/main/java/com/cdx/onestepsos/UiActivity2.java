package com.cdx.onestepsos;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by CDX on 2017/4/15.
 */

public class UiActivity2 extends Activity{
    private Button btn_sos;
    private ImageView img_bluetooth_state;
    private TextView tv_bluetooth_state;
    private ProgressFragement progressFragement;
    private ContactsFragment contactsFragment;
    private boolean isContactsFragment = true;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case BluetoothTools.ACTION_CONNECT_SUCCESS:
                    img_bluetooth_state.setImageResource(R.drawable.gou);
                    tv_bluetooth_state.setText("已连接");
                    tv_bluetooth_state.setTextColor(Color.GREEN);
                    break;
                case BluetoothTools.ACTION_DATA_TO_GAME:
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    progressFragement = new ProgressFragement();
                    transaction.replace(R.id.fragmentlayout,progressFragement);
                    if(isContactsFragment) {
                        Log.i("CDX","iscontactsfragmet");
                        transaction.addToBackStack(null);
                        isContactsFragment = false;
                    }else{
                        Log.i("CDX","isnotcontactsfragment");
                        transaction.disallowAddToBackStack();
                    }
                    transaction.commitAllowingStateLoss();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isContactsFragment = true;
        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout2);
        contactsFragment = new ContactsFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentlayout, contactsFragment);
        transaction.commit();
        //开启蓝牙服务
        Intent intent = new Intent(UiActivity2.this,BluetoothServerService.class);
        startService(intent);

        //广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
        registerReceiver(broadcastReceiver,intentFilter);

        img_bluetooth_state = (ImageView) findViewById(R.id.img_bluetooth_state);
        tv_bluetooth_state = (TextView) findViewById(R.id.tv_bluetooth_state);
        btn_sos = (Button) findViewById(R.id.btn_sos);
        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
                sendBroadcast(intent);
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            Bundle bundle = data.getBundleExtra("picPath");
            String pic1 = bundle.getString("picPathBack");
            String pic2 = bundle.getString("picPathFront");
            Log.i("CDX","前摄:"+pic1+"\n 后摄:" + pic2);
            progressFragement.setImg_photo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
