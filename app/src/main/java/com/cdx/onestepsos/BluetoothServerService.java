package com.cdx.onestepsos;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Created by CDX on 2017/3/28.
 */

public class BluetoothServerService extends Service{

    //蓝牙适配器
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //蓝牙通讯线程
    private BluetoothCommunThread communThread;

    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("CDX","BluetoothServerService is on create");
        if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();    //打开蓝牙
        }
        //开启后台连接线程
        handler = new Handler();
        new BluetoothServerConnThread(serviceHandler).start();
        super.onCreate();
    }

    //接收其他线程消息的Handler
    private Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
                    //连接成功
                    //开启通讯线程
                    Log.i("CDX","连接成功开启通信线程");
                    communThread = new BluetoothCommunThread(serviceHandler, (BluetoothSocket)msg.obj);
                    communThread.start();
                    sendBroadcast(new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS));
                    break;
                case BluetoothTools.MESSAGE_CONNECT_ERROR:
                    //连接错误
                    Log.i("CDX","socket连接出错");
                    break;

                case BluetoothTools.MESSAGE_READ_OBJECT:
                    //读取到数据
                    //发送数据广播（包含数据对象）
                    Intent intent1 = new Intent(getBaseContext(),UiActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
                            sendBroadcast(intent);
                        }
                    },2000);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("CDX","BluetoothServerService is ondestroy");
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BluetoothServerService.this,BluetoothServerService.class);//service被kill时重新启动
                startService(intent);
            }
        },2000);*/

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


}
