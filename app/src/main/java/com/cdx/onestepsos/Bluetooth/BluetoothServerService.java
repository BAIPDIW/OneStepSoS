package com.cdx.onestepsos.Bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cdx.onestepsos.Surface.UiActivity;

/**
 * Created by CDX on 2017/5/17.
 */

public class BluetoothServerService extends Service{

    //蓝牙适配器
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        Log.i("CDX","BluetoothServerService is on create");
        if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        handler = new Handler();
        new BluetoothConnectedThread(serviceHandler).start();
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.EXTRA_STATE);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver,intentFilter);
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1);
            switch(state){
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_OFF:
                    if(!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_ON:
                    new BluetoothConnectedThread(serviceHandler).start();
                    break;
            }

        }
    };
    //接收其他线程消息的Handler
    private Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.i("CDX","连接出错");
                    break;
                case 1:
                    new BluetoothCommunicateThread(serviceHandler, (BluetoothSocket)msg.obj).start();
                   break;
                case 2://收到求救信号
                    Log.i("CDX","收到求救信号");
                    new BluetoothConnectedThread(serviceHandler).start();
                    Intent intent1 = new Intent(getBaseContext(),UiActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent("SIGN_SOS_FROM_CLIENT");
                            sendBroadcast(intent);
                        }
                    },1000);
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.i("CDX","BluetoothService onDestroy");
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
